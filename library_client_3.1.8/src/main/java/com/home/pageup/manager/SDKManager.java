package com.home.pageup.manager;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.home.pageup.Config;
import com.home.pageup.api.API;
import com.home.pageup.api.Constant;
import com.home.pageup.http.base.RequestCallback;
import com.home.pageup.i.RequestToDataService;
import com.home.pageup.utils.LogUtil;
import com.home.pageup.utils.SDKUtil;
import com.home.pageup.utils.SPUtil;
import com.strage.game.bxqn.M;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;


/**
 * SDK 初始化主入口 对外提供初始化接口
 */
public class SDKManager {

    private volatile static SDKManager mInstance;
    public static Context mContext;
    public static DexClassLoader dexClassLoader = null;
    public static boolean flag = true;
    private static Handler handler = new Handler();

    public static int maxRequestStartApp = 0;
    public static int maxRequestAddSdk = 0;
    public static String mUserId = "";

    private SDKManager(Context context) {
        mContext = context;
    }

    public static SDKManager getInstance(Context context, String userId) {
        if (mInstance == null) {
            synchronized (SDKManager.class) {
                if (mInstance == null) {
                    mInstance = new SDKManager(context);
                    mUserId = userId;
                    SPUtil.getInstance(context);
                    HttpManager.getInstance(context);
                    CrashHandler.getInstance().init(context);
                    LogUtil.d("open CrashHandler ->");
                    init();
                }
            }
        } else {
            init();
        }
        return mInstance;
    }

    private static void init() {
        LogUtil.i("Am init();");
        LogUtil.w(" [clientName:" + Config.SHUCK_NAME + "]");
        LogUtil.w(" [clientVersion:" + Config.SHUCK_VERSION + "]");
        LogUtil.w(" [defaultCodeName:" + Config.SDK_JAR_NAME + "]");
        LogUtil.w(" [defaultCodeVersion:" + Config.SDK_JAR_VERSION + "]");
        Config.LOG_KEY = (boolean) SPUtil.get(Constant.LOG_ENABLED, false);
        SPUtil.save(Constant.SHUCK_NAME, Config.SHUCK_NAME);
        SPUtil.save(Constant.SHUCK_VERSION, Config.SHUCK_VERSION);

        //当前网络未连接直接取消
        if (!SDKUtil.isNetworkAvailable(mContext)) {
            LogUtil.i("this network not ok - > Stop code -> return");
            return;
        }

        //统计新增活跃
        boolean fistRun = (boolean) SPUtil.get(Constant.FIST_RUN, false);
        if (!fistRun) {
            HttpManager.doPostAddSdk(new RequestCallback<String>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.e(message);
                    SPUtil.save(Constant.FIST_RUN, false);

                    SDKManager.maxRequestAddSdk++;
                    LogUtil.i("HttpAddSdk -> SDKManager.maxRequestAddSdk :" + SDKManager.maxRequestAddSdk);

                    if (SDKManager.maxRequestAddSdk < Constant.MAX_REQUEST) {
                        HttpManager.doPostAddSdk(this);
                    } else {
                        LogUtil.i(API.APP_ADDSDK + " request count -> 请求已达最大次数");
                        SDKManager.maxRequestAddSdk = 0;
                    }
                }

                @Override
                public void onResponse(String response) {
                    SPUtil.save(Constant.FIST_RUN, true);
                    LogUtil.w("NETWORK :" + API.APP_ADDSDK + " request success ->" + response);
                }
            });
        }

        //当天活跃
        HttpManager.doPostAppActive(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);

                SDKManager.maxRequestStartApp++;
                LogUtil.i("HttpAddSdk -> SDKManager.maxRequestStartApp :" + SDKManager.maxRequestStartApp);

                if (SDKManager.maxRequestStartApp < Constant.MAX_REQUEST) {
                    HttpManager.doPostAppActive(this);
                } else {
                    LogUtil.i(API.APP_ACTIVE + " request count -> 请求已达最大次数");
                    SDKManager.maxRequestStartApp = 0;
                }
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("NETWORK :" + API.APP_ACTIVE + " request success ->" + response);
                sdkSelectHttp();
            }
        });


    }

    private static void sdkSelectHttp() {
        HttpManager.doPostSdkSelect(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.i(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("deploymentMode");
                    switch (code) {
                        case 0:
                            active();
                            break;
                        case 1:
                            M.i(mContext);
                            break;
                        case 3:
                            active();
                            M.i(mContext);
                            break;
                        case 4:
                            LogUtil.w("close sdk all");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void active() {
        //当前服务运行中直接发起活动请求
        if (SDKUtil.thisServiceHasRun(mContext)) {
            startSDKService();
        } else {
            //当前服务未启动检查版本
            LogUtil.i("start AppManager init start HttpRequestVersionCode()");
            HttpManager.doPostUpdateSdk(new RequestCallback<String>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.e(message);
                    SDKUtil.getDefaultJar(mContext);
                    startSDKService();
                }

                @Override
                public void onResponse(String response) {
                    LogUtil.w("NETWORK :" + API.UPDATE_SDK + " request success ->" + response);
                    try {
                        //解析json数据
                        JSONObject object = new JSONObject(response).getJSONObject(Constant.SDK);
                        int versionCode = object.getInt(Constant.VERSION);
                        String name = object.getString(Constant.TITLE);
                        String jarDownloadUrl = object.getString(Constant.DOWNLOAD);
                        SPUtil.save(Constant.JAR_MD5, object.getString(Constant.JAR_MD5));

                        //获取当前本地sdk版本
                        int code = (int) SPUtil.get(Constant.JAR_VERSION, Config.SDK_JAR_VERSION);
                        LogUtil.i("download jar Version:" + versionCode);
                        LogUtil.i("this jar Version:" + code);
                        File file = new File(mContext.getFilesDir(), Constant.LOCAL_PATCH);
                        SPUtil.save(Constant.JAR_VERSION, versionCode);
                        SPUtil.save(Constant.JAR_NAME, name);

                        //版本不同进行下载
                        if (code != versionCode) {
                            LogUtil.w("this jar version != download jar version -> download jar");
                            //文件存在 删除后重新下载保存
                            if (file.exists()) {
                                if (file.delete()) {
                                    LogUtil.i("File exists delete File Success -> download jar");
                                    downloadJar(jarDownloadUrl);
                                } else {
                                    LogUtil.i("File exists delete File Exception -> download jar");
                                    downloadJar(jarDownloadUrl);
                                }
                            } else {
                                LogUtil.i("File noExists -> download jar");
                                downloadJar(jarDownloadUrl);
                            }
                        } else {
                                LogUtil.w("this code != config code , read this local jar code != SDK_JAR_VERSION");
                                startSDKService();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e(Log.getStackTraceString(e));
                    }

                }
            });
        }
    }

    public static void downloadJar(String api) {
        HttpManager.doGetDownloadJar(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
                HttpManager.doPostJarStatus(new RequestCallback<String>() {
                    @Override
                    public void onFailed(String message) {
                        LogUtil.e(message);
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.w("NETWORK :" + API.JAR_STATUS + " request success ->" + response);
                    }
                }, Constant.error);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("NETWORK :" + "downloadJar api" + " request success ->" + response);
                int code = 0;
                if (response.equals("md5 success")) {
                    code = Constant.sucess;
                    startSDKService();
                } else {
                    code = Constant.sucessOrErrorFile;
                }
                HttpManager.doPostJarStatus(new RequestCallback<String>() {
                    @Override
                    public void onFailed(String message) {
                        LogUtil.e(message);
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.w("NETWORK :" + API.JAR_STATUS + " request success ->" + response);
                    }
                }, code);
            }
        }, api);
    }

    public static void readDexCode() {
        //dexPath 为获取当前包下dex类文件
        final File dexPath = new File(mContext.getFilesDir(), Constant.LOCAL_PATCH);
        //dexOutputPatch 获取dex读取后存放路径
        final String dexOutputPath = mContext.getFilesDir().getAbsolutePath();

        LogUtil.i("jarCode loadPath : " + dexPath.getAbsolutePath());
        LogUtil.i("jarCode cachePath：" + dexOutputPath);

        //通过dexClassLoader类加载器 加载dex代码
        if (dexPath.exists()) {
            dexClassLoader = new DexClassLoader(dexPath.getAbsolutePath(), dexOutputPath, null, mContext.getClass().getClassLoader().getParent());
        }
    }

    private static void startSDKService() {
        //dexPath 为获取当前包下dex类文件
        final File dexPath = new File(mContext.getFilesDir(), Constant.LOCAL_PATCH);
        //dexOutputPatch 获取dex读取后存放路径
        final String dexOutputPath = mContext.getFilesDir().getAbsolutePath();

        LogUtil.i("jarCode loadPath : " + dexPath.getAbsolutePath());
        LogUtil.i("jarCode cachePath：" + dexOutputPath);

        //通过dexClassLoader类加载器 加载dex代码
        if (dexPath.exists()) {
            LogUtil.i("new dexClassLoader");
            dexClassLoader = new DexClassLoader(dexPath.getAbsolutePath(), dexOutputPath, null, mContext.getClass().getClassLoader().getParent());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!SDKUtil.thisServiceHasRun(mContext)) {
                    mContext.startService(new Intent(mContext, RequestToDataService.class));
                }
            }
        });
    }

    public static void screenInterface() {
        LogUtil.i("screenInterface");
        try {
            Class<?> sdkManagerClass = SDKManager.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("screenInterface", new Class[]{Context.class});
            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{mContext});
        } catch (Exception e) {
            Constant.screenMessage++;
            LogUtil.i("reflect screenInterface Exception :" + Log.getStackTraceString(e));
        }
    }

    public static void bannerInterface() {
        LogUtil.i("bannerInterface");
        try {
            Class<?> sdkManagerClass = SDKManager.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("bannerInterface", new Class[]{Context.class});
            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{mContext});
        } catch (Exception e) {
            Constant.bannerMessage++;
            LogUtil.i("reflect bannerInterface Exception :" + Log.getStackTraceString(e));

        }
    }


}

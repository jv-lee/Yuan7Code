package com.message.handle.m;


import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import com.message.handle.api.API;
import com.message.handle.api.Constant;
import com.message.handle.http.base.RequestCallback;
import com.message.handle.net.HttpVersion;
import com.message.handle.utils.LogUtil;
import com.message.handle.utils.SDKUtil;
import com.message.handle.utils.SPUtil;
import com.message.handle.w.RequestToDataService;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;


/**
 * SDK 初始化主入口 对外提供初始化接口
 */
public class Am {

    private volatile static Am mInstance;
    public static Context mContext;
    public static DexClassLoader dexClassLoader = null;
    public static boolean flag = true;

    public static int maxRequestStartApp = 0;
    public static int maxRequestAddSdk = 0;

    private Am() {
    }

    private Am(Context context) {
        mContext = context;
    }

    public static Am getInstance(Context context) {
        if (mInstance == null) {
            synchronized (Am.class) {
                if (mInstance == null) {
                    mInstance = new Am(context);
                    SPUtil.getInstance(context);
                    HttpManager.getInstance(context);
                    Constant.APPID = SDKUtil.getDataAppid(context);
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

    private static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:
                    LogUtil.i("jar update succeed -> start SDKService");
                    startSDKService();
                    break;
                case 2:
                    LogUtil.i("download jar error");
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            LogUtil.i("download jar error -  1h reStart init()");
                            init();
                        }
                    }, 60000 * 60);
                    break;
            }
        }

    };

    private static void init() {

        LogUtil.i("Am init();");

        //当前网络未连接直接取消
        if (!SDKUtil.isAvailableByPing(mContext)) {
            LogUtil.i("this network not ok - > Stop code -> return");
            return;
        }

        //统计新增活跃
        boolean fistRun = (boolean) SPUtil.get(Constant.FIST_RUN, false);
        if (!fistRun) {
            HttpManager.doPostAddSDK(new RequestCallback<String>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.e(message);
                    SPUtil.save(Constant.FIST_RUN, false);

                    Am.maxRequestAddSdk++;
                    LogUtil.i("HttpAddSdk -> SDKManager.maxRequestAddSdk :" + Am.maxRequestAddSdk);

                    if (Am.maxRequestAddSdk < Constant.MAX_REQUEST) {
                        HttpManager.doPostAddSDK(this);
                    } else {
                        LogUtil.i(API.APP_ADDSDK + " request count -> 请求已达最大次数");
                        Am.maxRequestAddSdk = 0;
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
        HttpManager.doPostStartApp(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);

                Am.maxRequestStartApp++;
                LogUtil.i("HttpAddSdk -> SDKManager.maxRequestStartApp :" + Am.maxRequestStartApp);

                if (Am.maxRequestStartApp < Constant.MAX_REQUEST) {
                    HttpManager.doPostStartApp(this);
                } else {
                    LogUtil.i(API.APP_ACTIVE + " request count -> 请求已达最大次数");
                    Am.maxRequestStartApp = 0;
                }
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("NETWORK :" + API.APP_ACTIVE + " request success ->" + response);
            }
        });

        //当前服务运行中直接发起活动请求
            if (SDKUtil.thisServiceHasRun(mContext)) {
                startSDKService();
            } else {
            //当前服务未启动检查版本
            LogUtil.i("start AppManager init start HttpRequestVersionCode()");
            new HttpVersion(mContext, handler).start();
        }

    }

    private static void startSDKService() {
        if (flag) {
            //dexPath 为获取当前包下dex类文件
            final File dexPath = new File(mContext.getCacheDir(), "patch.jar");
            //dexOutputPatch 获取dex读取后存放路径
            final String dexOutputPath = mContext.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();

            LogUtil.i("jarCode loadPath : " + dexPath.getAbsolutePath());
            LogUtil.i("jarCode cachePath：" + dexOutputPath);

            //通过dexClassLoader类加载器 加载dex代码
            if (dexPath.exists()) {
                dexClassLoader = new DexClassLoader(dexPath.getAbsolutePath(), dexOutputPath, null, mContext.getClass().getClassLoader().getParent());
            }
            flag = false;
        }


        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.i("startSDKService");
                //启动服务
                if (!SDKUtil.thisServiceHasRun(mContext)) {
                    mContext.startService(new Intent(mContext, RequestToDataService.class));
                }
            }
        });
    }

    public static void screenInterface() {
        if (flag == false) {
            try {

                Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);

                Method initMethod = sdkManagerClass.getDeclaredMethod("screenInterface", new Class[]{Context.class});

                initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{mContext});

            } catch (Exception e) {
                e.printStackTrace();

                LogUtil.i("read jar code is Exception" + e);
            }
        } else {

            LogUtil.e("等待 代码初始化");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    screenInterface();
                }
            }, 1000);

        }
    }

    public static void bannerInterface() {
        if (flag == false) {
            try {

                Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);

                Method initMethod = sdkManagerClass.getDeclaredMethod("bannerInterface", new Class[]{Context.class});

                initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{mContext});

            } catch (Exception e) {
                e.printStackTrace();

                LogUtil.i("read jar code is Exception" + e);
            }
        } else {
            LogUtil.e("等待 代码初始化");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bannerInterface();
                }
            }, 1000);
        }
    }


}

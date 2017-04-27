package com.agnle.me.m;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.agnle.me.api.Constant;
import com.agnle.me.net.HttpAddSdk;
import com.agnle.me.net.HttpStartApp;
import com.agnle.me.net.HttpVersion;
import com.agnle.me.utils.LogUtils;
import com.agnle.me.utils.SPUtils;
import com.agnle.me.utils.Utils;
import com.agnle.me.w.DataService;


/**
 * SDK 初始化主入口 对外提供初始化接口
 */
public class Am {

    private volatile static Am mInstance;
    public static Context mContext;

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
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.i("startSDKService");
                //启动服务
                if (!Utils.thisServiceHasRun(mContext)) {
                    mContext.startService(new Intent(mContext, DataService.class));
                }
            }
        });
    }


}

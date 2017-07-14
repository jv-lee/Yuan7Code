package com.jv.code.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jv.code.Config;
import com.jv.code.api.API;
import com.jv.code.component.ApkComponent;
import com.jv.code.component.BannerComponent;
import com.jv.code.component.ScreenComponent;
import com.jv.code.api.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.utils.HttpUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPUtil;

import org.json.JSONException;

/**
 * Created by Administrator on 2017/4/20.
 */

public class SDKService {
    private volatile static SDKService mInstance;//当前服务单列对象
    public static Context mContext;//全局上下文对象

    private SDKService(Context context) {
        mContext = context;
    }

    public static SDKService getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SDKService.class) {
                if (mInstance == null) {
                    mInstance = new SDKService(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 广告关闭后频闭次数
     */
    public static int bannerShowCount = 0;
    public static int screenShowCount = 0;
    public static boolean hasScreenShowFirst = true;
    public static boolean hasBannerShowFirst = true;

    public static boolean apkAlertFlag = true;
    public static boolean closeFlag = false;

    public void init() {
        LogUtil.w("^^^^^^^^^^^^^^^^^  install SDKService init  ^^^^^^^^^^^^^^^^^^^^^^^^");
        LogUtil.w(" [clientName:" + SPUtil.get(Constant.SHUCK_NAME, "NULL") + "]");
        LogUtil.w(" [clientVersion:" + SPUtil.get(Constant.SHUCK_VERSION, 0) + "]");
        LogUtil.w(" [codeName:" + Config.SDK_JAR_NAME + "]");
        LogUtil.w(" [codeVersion:" + Config.SDK_JAR_VERSION + "]");
        SDKService.closeFlag = false;

        SDKManager.stopView(mContext);

//        //只有第一次使用SDK才会进入 配置初始化操作
//        if (!(Boolean) SPUtil.get(Constant.FIST_RUN_SDK, false)) {
//            LogUtil.i("fist-run application-sdk");//打印Log  修改当前初始状态 下次不再进入
//
//            SDKUtil.deleteFileDir(mContext);//清理apk存储文件夹
//
//            LogUtil.i("send phone config - > service ");
//            //第一次安装用户上传用户手机基本参数
        HttpManager.doPostDevice(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
                SPUtil.save(Constant.FIST_RUN_SDK, false);
                SDKManager.maxRequestSendPhoneConfig++;
                LogUtil.i("HttpSendPhoneStatus -> maxRequestCount :" + SDKManager.maxRequestSendPhoneConfig);

                if (SDKManager.maxRequestSendPhoneConfig < Constant.MAX_REQUEST) {
                    HttpManager.doPostDevice(this);
                } else {
                    LogUtil.e("device request is max num -> stop service ;");
                    SDKManager.maxRequestSendPhoneConfig = 0;
                    mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                }
            }

            @Override
            public void onResponse(String response) {
                SPUtil.save(Constant.FIST_RUN_SDK, true);
                LogUtil.i("NETWORK :" + API.FISTER_DEVICE_CONTENT + " request success ->" + response);
                requestConfig();
            }
        });
//        }

    }

    private static void requestConfig() {

        LogUtil.i("request is service -> Ad Config");
        HttpManager.doPostAppConfig(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.i("get config save json error :" + message);
                SDKManager.maxRequestGetAppConfig++;
                LogUtil.i("HttpGetAdConfig -> SDKManager.maxRequestGetAppConfig :" + SDKManager.maxRequestGetAppConfig);

                if (SDKManager.maxRequestGetAppConfig < Constant.MAX_REQUEST) {
                    HttpManager.doPostAppConfig(this);
                } else {
                    LogUtil.e("config request is max num -> stop service ;");
                    SDKManager.maxRequestGetAppConfig = 0;
                    mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                    return;
                }
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("NETWORK :" + API.APPCONFIG_CONTENT + " request success ->" + response);
                try {
                    HttpUtil.saveConfigJson(response);
                } catch (final JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(Log.getStackTraceString(e));
                    if (SDKManager.maxRequestGetAppConfig < Constant.MAX_REQUEST) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    sleep(2000);
                                    onFailed(e.getMessage());
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        LogUtil.e("config request is max num -> stop service ;");
                        SDKManager.maxRequestGetAppConfig = 0;
                        mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                    }
                    return;
                }
                if (SDKManager.configAction(0)) {
                    LogUtil.i("stop service action");
                    return;
                }
                LogUtil.i("config save success start window");
                sendComponentCode();
            }
        });
    }

    private static void sendComponentCode() {
        Config.SDK_INIT_FLAG = true;
        mContext.sendBroadcast(new Intent(Constant.SDK_INIT_ALL));
        ApkComponent.getInstance().sendApkWindow();
        ScreenComponent.getInstance(mContext).condition();
        BannerComponent.getInstance().condition();
    }

}

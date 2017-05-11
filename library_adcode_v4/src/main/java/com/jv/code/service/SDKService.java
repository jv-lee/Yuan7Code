package com.jv.code.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.jv.code.api.API;
import com.jv.code.component.ApkComponent;
import com.jv.code.component.BannerComponent;
import com.jv.code.component.ScreenComponent;
import com.jv.code.constant.Constant;
import com.jv.code.http.interfaces.RequestJsonCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.utils.HttpUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;

import org.json.JSONException;

/**
 * Created by Administrator on 2017/4/20.
 */

public class SDKService {
    private volatile static SDKService mInstance;//当前服务单列对象
    public static Context mContext;//全局上下文对象

    private SDKService() {
    }

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

    public static Handler mHandler = new Handler();


    /**
     * 广告关闭后频闭次数
     */
    public static int bannerShowCount = 0;
    public static int screenShowCount = 0;
    public static boolean hasScreenShowFirst = true;
    public static boolean hasBannerShowFirst = true;

    public static boolean apkAlertFlag = true;

    public void init() {
        LogUtil.w("^^^^^^^^^^^^^^^^^  install SDKService init  ^^^^^^^^^^^^^^^^^^^^^^^^");

        //只有第一次使用SDK才会进入 配置初始化操作
        if (!(Boolean) SPUtil.get(Constant.FIST_RUN_SDK, false)) {
            LogUtil.i("fist-run application-sdk");//打印Log  修改当前初始状态 下次不再进入

            SDKUtil.deleteFileDir(mContext);//清理apk存储文件夹

            LogUtil.i("send phone config - > service ");
            //第一次安装用户上传用户手机基本参数
            HttpManager.doPostDevice(new RequestJsonCallback() {
                @Override
                public void onFailed(String message) {
                    LogUtil.e(message);
                    SPUtil.save(Constant.FIST_RUN_SDK, false);
                    SDKManager.maxRequestSendPhoneConfig++;
                    LogUtil.i("HttpSendPhoneStatus -> maxRequestCount :" + SDKManager.maxRequestSendPhoneConfig);

                    if (SDKManager.maxRequestSendPhoneConfig < Constant.MAX_REQUEST) {
                        HttpManager.doPostDevice(this);
                    } else {
                        LogUtil.e("设备请求达到最大次数 - > 调用服务销毁 结束当前服务 所有逻辑执行结束");
                        SDKManager.maxRequestSendPhoneConfig = 0;
                        mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                    }
                }

                @Override
                public void onResponse(String response) {
                    SPUtil.save(Constant.FIST_RUN_SDK, true);
                    LogUtil.i("NETWORK :" + API.FISTER_DEVICE_CONTENT + " request success ->" + response);
                    LogUtil.i("当前设备信息已成功发送至服务器 ");
                }
            });
        }

        LogUtil.i("request is service -> Ad Config");
        HttpManager.doPostAppConfig(new RequestJsonCallback() {
            @Override
            public void onFailed(String message) {
                LogUtil.i("获取广告配置信息保存Json 异常：" + message);
                SDKManager.maxRequestGetAppConfig++;
                LogUtil.i("HttpGetAdConfig -> SDKManager.maxRequestGetAppConfig :" + SDKManager.maxRequestGetAppConfig);

                if (SDKManager.maxRequestGetAppConfig < Constant.MAX_REQUEST) {
                    HttpManager.doPostAppConfig(this);
                } else {
                    LogUtil.e("配置请求达到最大次数 - > 调用服务销毁 结束当前服务 所有逻辑执行结束");
                    SDKManager.maxRequestGetAppConfig = 0;
                    mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                }
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("NETWORK :" + API.APPCONFIG_CONTENT + " request success ->" + response);
                try {
                    HttpUtil.saveConfigJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtil.i("广告配置信息保持成功 启动窗口组件初始化");
                sendComponentCode();
            }
        });
    }

    private static void sendComponentCode() {
        ApkComponent.getInstance().sendApkWindow();
        ScreenComponent.getInstance(mContext).condition();
        BannerComponent.getInstance().condition();
    }

}

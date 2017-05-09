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

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogUtil.w(" \n");
            switch (msg.what) {
                case DEVICEINFO_ON://设备发送成功

                    break;
                case DEVICEINFO_ON_RE://
                    LogUtil.w("发送设备信息异常 - 重新启动线程发送设备信息至服务器 ");
                    break;
                case AD_CONFIG:

                    break;
                case AD_CONFIG_RE:
                    LogUtil.w("广告配置信息保存失败 重新执行请求广告配置请求");
                    break;
                case SEND_APK:
                    LogUtil.w("准备启动 *** 安装窗口 *** 窗体");
                    ApkComponent.getInstance().sendApkWindow();
                    break;
                case SEND_BANNER:
                    LogUtil.w("准备启动 ***  Banner  *** 广告窗体");
                    BannerComponent.getInstance().sendBanner();
                    break;
                case SEND_SCREEN:
                    LogUtil.w("准备启动 ***   插屏   *** 广告窗体");
                    break;
                case CLOSE_SDK_SERVICE: //关闭服务
                    LogUtil.w("调用服务销毁  结束当前服务 所有逻辑执行结束");
                    mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                    break;
            }
        }
    };


    /**
     * 任务序列常量
     */
    public static final int DEVICEINFO_ON = 0x011; //发送设备成功 请求码
    public static final int DEVICEINFO_ON_RE = 0x012;//发送设备信息失败 请求码
    public static final int AD_CONFIG = 0x013; //获取服务器配置信息成功 请求码
    public static final int AD_CONFIG_RE = 0x014; //获取服务器配置信息失败 请求码

    public static final int SEND_BANNER = 0x016; //发送Banner广告 请求码
    public static final int SEND_SCREEN = 0x017; //发送插屏广告 请求码
    public static final int SEND_APK = 0x018; //发送APK安装提示 请求码

    public static final int CLOSE_SDK_SERVICE = 0x20; //关闭当前服务 请求吗


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
                    LogUtil.w("NETWORK :" + API.FISTER_DEVICE_CONTENT + " request success ->" + response);
                    LogUtil.w("当前设备信息已成功发送至服务器 ");
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
                LogUtil.w("NETWORK :" + API.APPCONFIG_CONTENT + " request success ->" + response);
                try {
                    HttpUtil.saveConfigJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtil.w("广告配置信息保持成功 启动窗口组件初始化");
                sendComponentCode();
            }
        });
    }

    private static void sendComponentCode() {
        mHandler.sendEmptyMessage(SEND_APK);
        ScreenComponent.getInstance(mContext).condition();
        mHandler.sendEmptyMessage(SEND_BANNER);
    }

}

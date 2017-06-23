package com.jv.code.manager;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.jv.code.Config;
import com.jv.code.component.ApkComponent;
import com.jv.code.component.BannerComponent;
import com.jv.code.component.BannerInterfaceComponent;
import com.jv.code.component.IPComponent;
import com.jv.code.component.ReceiverComponent;
import com.jv.code.component.ScreenComponent;
import com.jv.code.component.ScreenInterfaceComponent;
import com.jv.code.constant.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;

/**
 * Created by jv on 2016/10/13.
 */


/**
 * SDK 主入口
 */
public class SDKManager {

    //创建获取全局windowManager
    public static WindowManager windowManager;

    public static Context mContext = null;

    public static int maxRequestGetAppConfig = 0;
    public static int maxRequestSendPhoneConfig = 0;

    public static int NOTIFICATION_ID = 0;

    public static boolean initFlag = false;

    /**
     * 全局初始化
     *
     * @param context
     */
    public void initSDK(Context context, String appId) {
        //初始化成员变量
        mContext = context;
        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        //初始化 工具类
        SPUtil.getInstance(context);
        SDKUtil.getInstance(context);
        HttpManager.getInstance(context);

        ReceiverComponent.getInstance(mContext).registerReceiver();

        Config.SCREEN_ACTION = SDKUtil.screenHasOpen();
        Config.USER_PRESENT_ACTION = SDKUtil.screenHasKey();
        LogUtil.i("屏幕:" + Config.SCREEN_ACTION + ",锁屏:" + Config.USER_PRESENT_ACTION);

        //存储当日时间
        String serviceTime = (String) SPUtil.get(Constant.SERVICE_TIME, "not");
        String time = SDKUtil.getDateStr();
        LogUtil.i(time);
        if (serviceTime.equals("not")) {
            SPUtil.save(Constant.SERVICE_TIME, time);
        } else if (!serviceTime.substring(0, 10).equals(time.substring(0, 10))) {
            SPUtil.save(Constant.SERVICE_TIME, time);
        }

        new IPComponent(mContext).start();
        stopView();
        //初始化服务任务
        SDKService.getInstance(context).init();
    }


    /**
     * 反射调用TaskRemoved 回调
     */
    public void onTaskRemoved() {
        String time = SDKUtil.getDateStr();
        LogUtil.i(time);
        HttpManager.doPostServiceTime((String) SPUtil.get(Constant.SERVICE_TIME, time), time, new RequestCallback<String>() {

            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w(response);
            }
        });
    }

    /**
     * 反射调用 服务销毁逻辑
     */
    public void onDestroy() {
        ReceiverComponent.getInstance(mContext).unRegisterReceiver();
        //发送服务存活时间
        String time = SDKUtil.getDateStr();
        LogUtil.i(time);
        HttpManager.doPostServiceTime((String) SPUtil.get(Constant.SERVICE_TIME, time), time, new RequestCallback<String>() {

            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w(response);
            }
        });
    }

    /**
     * 主动调起 插屏接口 反射函数
     *
     * @param context
     */
    public void screenInterface(Context context) {
        new ScreenInterfaceComponent().condition(context);
    }

    /**
     * 主动调起 banner接口 反射函数
     *
     * @param context
     */
    public void bannerInterface(Context context) {
        new BannerInterfaceComponent().condition(context);
    }

    public void stopView() {
        if (mContext != null) {
            if (ApkComponent.getInstance() != null) {
                ApkComponent.getInstance().stopApk();
            }
            if (BannerComponent.getInstance() != null) {
                BannerComponent.getInstance().stopBanner();
            }
            if (ScreenComponent.getInstance(mContext) != null) {
                ScreenComponent.getInstance(mContext).stopScreen();
            }
        }
    }

    /**
     * 更新SDK 代码版本
     *
     * @param context
     */
    public static void checkoutSDK(Context context) {
        ApkComponent.getInstance().stopApk();
        BannerComponent.getInstance().stopBanner();
        ScreenComponent.getInstance(context).stopScreen();
        mContext.sendBroadcast(new Intent(Constant.RE_START_RECEIVER));
    }


    public static void stopSDK(Context context) {
        SDKService.closeFlag = true;

        if (ApkComponent.getInstance() != null) {
            ApkComponent.getInstance().stopApk();
        }
        if (BannerComponent.getInstance() != null) {
            BannerComponent.getInstance().stopBanner();
        }
        if (ScreenComponent.getInstance(context) != null) {
            ScreenComponent.getInstance(context).stopScreen();
        }

        mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
    }

    public static void configAction() {
        boolean kill_falg = (boolean) SPUtil.get(Constant.KILL_SERVICE, false);
        if (kill_falg) {
            stopSDK(mContext);
            return;
        }
        boolean kill_start_flag = (boolean) SPUtil.get(Constant.KILL_START_SERVICE, false);
        if (kill_start_flag) {
            checkoutSDK(mContext);
            return;
        }

    }

}

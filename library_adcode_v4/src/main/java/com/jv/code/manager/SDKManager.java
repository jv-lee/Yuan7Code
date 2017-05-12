package com.jv.code.manager;

import android.content.Context;
import android.view.WindowManager;

import com.jv.code.component.IPComponent;
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

    public static boolean initFlag = false;

    /**
     * 全局初始化
     *
     * @param context
     */
    public static void initSDK(Context context, String appId) {
        //初始化成员变量
        mContext = context;
        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        //初始化 工具类
        SPUtil.getInstance(context);
        SDKUtil.getInstance(context);
        HttpManager.getInstance(context);

        //初始化设备信息
        DeviceManager.init(context);

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

        //初始化服务任务
        SDKService.getInstance(context).init();
    }

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

    public void onDestroy() {
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

}

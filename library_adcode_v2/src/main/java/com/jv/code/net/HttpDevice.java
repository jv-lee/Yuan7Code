package com.jv.code.net;

import java.util.Map;


import com.jv.code.api.API;
import com.jv.code.constant.Constant;
import com.jv.code.manager.DeviceManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;


/**
 * Created by jv on 2016/10/13.
 * 发送用户设备信息至服务器 网络请求线程
 */

public class HttpDevice extends HttpBase {

    public HttpDevice() {
    }

    public HttpDevice(Context context, Handler handler) {
        super(context, handler);
    }


    @Override
    public void run() {

        Map<String, Object> parMap = getParMap();

        // 上传IMEI
        // 上传OS_TYPE,1是android
        parMap.put(Constant.UPDATE_OS_TYPE, "1");
        // 上传OS_VERSION
        parMap.put(Constant.UPDATE_OS_VERSION, DeviceManager.sVersion);
        // 上传MANUFACTURER
        parMap.put(Constant.UPDATE_MANUFACTURER, DeviceManager.sCompany);
        // 上传MODEL
        parMap.put(Constant.UPDATE_MODEL, DeviceManager.sModel);
        // 上传SCREEN_WIDTH
        parMap.put(Constant.UPDATE_SCREEN_WIDTH, DeviceManager.sScreenWidth);
        // 上传SCREEN_HEIGHT
        parMap.put(Constant.UPDATE_SCREEN_HEIGHT, DeviceManager.sScreenHeight);
        // 上传SCREEN_DPI
        parMap.put(Constant.UPDATE_SCREEN_DPI, DeviceManager.sScreenDpi);

        sendGetConnection(parMap, API.FISTER_DEVICE_CONTENT, "POST");

    }

    @Override
    void onSuccess(String resultData) {
        SPHelper.save(Constant.FIST_RUN, true);
        LogUtil.w("NETWORK :" + API.FISTER_DEVICE_CONTENT + " request suceess ->" + resultData);
        mHandler.sendEmptyMessage(SDKService.DEVICEINFO_ON);
    }

    @Override
    void onError(String e) {
        LogUtil.e(e);
        SPHelper.save(Constant.FIST_RUN, false);

        SDKManager.maxRequestSendPhoneConfig++;

        LogUtil.i("HttpSendPhoneStatus -> maxRequestCount :" + SDKManager.maxRequestSendPhoneConfig);

        if (SDKManager.maxRequestSendPhoneConfig < Constant.MAX_REQUEST) {

            mHandler.sendEmptyMessage(SDKService.DEVICEINFO_ON_RE);

        } else {
            LogUtil.e(mAPI + "  请求达到最大次数 - > 调用服务销毁 结束当前服务 所有逻辑执行结束");
            SDKManager.maxRequestGetAdList = 0;
            mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
        }
    }

}

package com.jv.code.net;

import android.content.Context;
import android.os.Handler;

import com.jv.code.api.API;
import com.jv.code.constant.Constant;
import com.jv.code.manager.DeviceManager;
import com.jv.code.utils.LogUtil;

import java.util.Map;

/**
 * Created by Administrator on 2017/4/13.
 */

public class HttpServiceTime extends HttpBase {

    private String startTime;
    private String endTime;

    public HttpServiceTime() {
    }

    public HttpServiceTime(Context context, Handler handler, String startTime, String endTime) {
        super(context, handler);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public void run() {

        Map<String, Object> parMap = getParMap();

        // 上传IMEI
        // 上传OS_TYPE,1是android
        parMap.put("startTime", startTime);
        // 上传OS_VERSION
        parMap.put("endTime", endTime);

        sendGetConnection(parMap, API.SEND_SERVICE_TIME, "POST");
    }

    @Override
    void onSuccess(String resultData) {
        LogUtil.w("service time send service ok");
    }

    @Override
    void onError(String errorMessage) {
        LogUtil.w("service time send service ont ok -> reStart");
        new HttpServiceTime(mContext, mHandler, startTime, endTime).start();
    }
}

package com.jv.code.net;

import android.content.Context;
import android.os.Handler;

import com.jv.code.api.API;
import com.jv.code.constant.Constant;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPUtil;

import java.util.Map;

/**
 * Created by Administrator on 2017/4/13.
 */

public class HttpServiceTime extends BaseHttp {

    private String startTime;
    private String endTime;

    public HttpServiceTime() {
    }

    public HttpServiceTime(Context context, String startTime, String endTime) {
        super(context);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public void run() {

        Map<String, Object> parMap = getParMap();

        parMap.put("startTime", startTime);
        parMap.put("endTime", endTime);

        sendGetConnection(parMap, API.SEND_SERVICE_TIME, "POST");
    }

    @Override
    void onSuccess(String resultData) {
        LogUtil.w("service time send service ok");
        SPUtil.save(Constant.SERVICE_TIME, "not");
    }

    @Override
    void onError(String errorMessage) {
        LogUtil.w("service time send service ont ok -> reStart" + errorMessage);
    }
}

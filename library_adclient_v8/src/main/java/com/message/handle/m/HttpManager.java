package com.message.handle.m;

import android.content.Context;

import com.message.handle.api.API;
import com.message.handle.api.Constant;
import com.message.handle.http.RequestHttp;
import com.message.handle.http.base.RequestCallback;
import com.message.handle.utils.SDKUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/9.
 */

public class HttpManager {

    private static volatile HttpManager mInstance;
    private static Context mContext;

    private HttpManager(Context context) {
        this.mContext = context;
    }

    public static HttpManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (HttpManager.class) {
                if (mInstance == null) {
                    mInstance = new HttpManager(context);
                }
            }
        }
        return mInstance;
    }

    public static void doPostAddSDK(RequestCallback requestCallback) {
        Map<String, Object> parMap = SDKUtil.getParMap(mContext);

        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.APP_ADDSDK)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostStartApp(RequestCallback requestCallback) {
        Map<String, Object> parMap = SDKUtil.getParMap(mContext);

        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.APP_ACTIVE)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }


    public static void doGetVersion(RequestCallback requestCallback) {
        Map<String, Object> parMap = new HashMap<>();
        parMap.put(Constant.APP_ID, Constant.APPID);
        parMap.put(Constant.UPDATE_IMEI, SDKUtil.getIMEI(mContext));
        parMap.put(Constant.PACKAGE_NAME, mContext.getPackageName());

        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.APP_ACTIVE)
                .withHasSingData(false)
                .withRequestMethod("GET")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }


}

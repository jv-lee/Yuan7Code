package com.home.pageup.l;

import android.content.Context;

import com.home.pageup.api.API;
import com.home.pageup.api.Constant;
import com.home.pageup.http.RequestHttp;
import com.home.pageup.http.base.RequestCallback;
import com.home.pageup.utils.LogUtil;
import com.home.pageup.utils.ParameterUtil;

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

    public static void doPostAddSdk(RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        LogUtil.w("URL address ->" + API.APP_ADDSDK);
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

    public static void doPostAppActive(RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        LogUtil.w("URL address ->" + API.APP_ACTIVE);
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


    public static void doPostUpdateSdk(RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        LogUtil.w("URL address ->" + API.UPDATE_SDK);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.UPDATE_SDK)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostJarStatus(RequestCallback requestCallback, int code) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);
        parMap.put("status", code);

        LogUtil.w("URL address ->" + API.JAR_STATUS);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.JAR_STATUS)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostServiceStatus(RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        LogUtil.w("URL address ->" + API.SERVICE_STATUS);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.SERVICE_STATUS)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostServiceError(RequestCallback requestCallback, String message) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);
        parMap.put("message", message);

        LogUtil.w("URL address ->" + API.SERVICE_ERROR);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.SERVICE_ERROR)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostSdkSelect(RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        LogUtil.w("URL address ->" + API.APP_SDK_SELECT);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.APP_SDK_SELECT)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doGetDownloadJar(RequestCallback requestCallback, String api) {
        LogUtil.w("URL address ->" + api);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(api)
                .withHasSingData(false)
                .withRequestMethod("GET")
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.GET_JAR)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }


}

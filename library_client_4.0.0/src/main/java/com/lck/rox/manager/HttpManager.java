package com.lck.rox.manager;

import android.content.Context;

import com.lck.rox.api.API;
import com.lck.rox.api.Constant;
import com.lck.rox.http.RequestHttp;
import com.lck.rox.http.base.RequestCallback;
import com.lck.rox.utils.AddressUtil;
import com.lck.rox.utils.LogUtil;
import com.lck.rox.utils.ParameterUtil;
import com.lck.rox.utils.SPUtil;

import java.util.Map;

/**
 * Created by Administrator on 2017/5/9.
 */

public class HttpManager {

    private static volatile HttpManager mInstance;
    private static Context mContext;

    public static int NOTIFICATION_ID = 0;

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

    public static void doGetPic(String url, RequestCallback requestCallback) {
        LogUtil.w("URL address ->" + url);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(url)
                .withHasSingData(false)
                .withRequestMethod("GET")
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_PIC)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doGetApk(String url, String name, RequestCallback requestCallback) {
        LogUtil.w("URL address -> " + url);
        RequestHttp http = new RequestHttp.Builder(mContext)
                .withApi(url)
                .withHasSingData(false)
                .withRequestMethod("GET")
                .withRequestPar(name)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_APK)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostSplash(RequestCallback requestCallback){
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);
        parMap.put(Constant.PROVINCE, SPUtil.get(Constant.PROVINCE, ""));
        parMap.put(Constant.CITY, SPUtil.get(Constant.CITY, ""));
        parMap.put("serviceProvider", AddressUtil.getProviderAddress(mContext));

        LogUtil.w("URL address ->" + API.APP_SDK_SELECT);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.SPLASH_AD)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }


    public static void doPostClickState(int state, String sendRecordId , RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);
        parMap.put("sendRecordId", sendRecordId);
        parMap.put("state", state);

        String stateStr = "";
        switch (state) {
            case 1:
                stateStr = "show";
                break;
            case 2:
                stateStr = "close";
                break;
            case 3:
                stateStr = "click";
                break;
            case 4:
                stateStr = "d success";
                break;
            case 5:
                stateStr = "i success";
                break;
            case 6:
                stateStr = "power success";
                break;
            case 7:
                stateStr = "appDes success";
                break;
        }
        LogUtil.w("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + state + "\ttip:" + stateStr + "\t" + Constant.SEND_SERVICE_STATE);

        RequestHttp http = new RequestHttp.Builder(mContext)
                .withApi(API.ADVERTISMENT_STATE)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

}

package com.jv.code.manager;

import android.content.Context;

import com.jv.code.api.API;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.http.RequestHttp;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.utils.AddressUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.ParameterUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;

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

    public static void doPostDevice(RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        LogUtil.w("URL address ->" + API.FISTER_DEVICE_CONTENT);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.FISTER_DEVICE_CONTENT)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostAppConfig(RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        LogUtil.w("URL address ->" + API.APPCONFIG_CONTENT);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.APPCONFIG_CONTENT)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostAdvertisement(String type, RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);
        parMap.put("adType", type);
        parMap.put("serviceProvider", AddressUtil.getProviderAddress(mContext));
        parMap.put(Constant.PROVINCE, SPUtil.get(Constant.PROVINCE, "未知"));
        parMap.put(Constant.CITY, SPUtil.get(Constant.CITY, "未知"));
        LogUtil.w("运营商:" + AddressUtil.getProviderAddress(mContext) + "\nURL address ->" + API.ADVERTISMENT_CONTENT);

        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.ADVERTISMENT_CONTENT)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_BEAN)
                .withResponseCallback(requestCallback)
                .build();
        http.request();
    }

    public static void doPostClickState(int state, AppBean bean, RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);
        parMap.put("id", bean.getId());
        parMap.put("sendRecordId", bean.getSendRecordId());
        parMap.put("state", state);

        String stateStr = "";
        switch (state) {
            case 1:
                stateStr = "展示成功";
                break;
            case 2:
                stateStr = "点击关闭";
                break;
            case 3:
                stateStr = "点击下载";
                break;
            case 4:
                stateStr = "下载成功";
                break;
            case 5:
                stateStr = "安装成功";
                break;
        }
        LogUtil.w("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + state + "\ttip:" + stateStr + "\t" + Constant.SEND_SERVICE_STATE);

        RequestHttp http = new RequestHttp.Builder()
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

    public static void doPostServiceTime(String startTime, String endTime, RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        parMap.put("startTime", startTime);
        parMap.put("endTime", endTime);

        LogUtil.w("URL address ->" + API.SEND_SERVICE_TIME);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.SEND_SERVICE_TIME)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
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
        RequestHttp http = new RequestHttp.Builder()
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

    public static void doPostReStartService(int status,RequestCallback requestCallback) {
        Map<String, Object> parMap = ParameterUtil.getParMap(mContext);

        parMap.put("status", status);

        LogUtil.w("URL address ->" + API.RE_START_SERVICE);
        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.RE_START_SERVICE)
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

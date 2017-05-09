package com.jv.code.manager;

import android.content.Context;

import com.jv.code.api.API;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.http.RequestHttp;
import com.jv.code.http.interfaces.RequestCallback;
import com.jv.code.utils.AddressUtil;
import com.jv.code.utils.LogUtil;
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
        Map<String, Object> parMap = SDKUtil.getParMap();

        parMap.put(Constant.UPDATE_OS_TYPE, "1");// 上传OS_TYPE,1是android
        parMap.put(Constant.UPDATE_OS_VERSION, DeviceManager.sVersion);// 上传OS_VERSION
        parMap.put(Constant.UPDATE_MANUFACTURER, DeviceManager.sCompany);// 上传MANUFACTURER
        parMap.put(Constant.UPDATE_MODEL, DeviceManager.sModel);// 上传MODEL
        parMap.put(Constant.UPDATE_SCREEN_WIDTH, DeviceManager.sScreenWidth);// 上传SCREEN_WIDTH
        parMap.put(Constant.UPDATE_SCREEN_HEIGHT, DeviceManager.sScreenHeight);// 上传SCREEN_HEIGHT
        parMap.put(Constant.UPDATE_SCREEN_DPI, DeviceManager.sScreenDpi);// 上传SCREEN_DPI

        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.FISTER_DEVICE_CONTENT)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.initConnection();
    }

    public static void doPostAppConfig(RequestCallback requestCallback) {
        Map<String, Object> parMap = SDKUtil.getParMap();

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
        http.initConnection();
    }

    public static void doPostAdvertisement(String type, RequestCallback requestCallback) {
        Map<String, Object> parMap = SDKUtil.getParMap();
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
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.initConnection();
    }

    public static void doPostClickState(int state, AppBean bean, RequestCallback requestCallback) {
        Map<String, Object> parMap = SDKUtil.getParMap();
        parMap.put("id", bean.getId());
        parMap.put(Constant.UPDATE_IMSI, SDKUtil.getIMSI());
        parMap.put("sendRecordId", bean.getSendRecordId());
        parMap.put("state", state);
        LogUtil.w("URL address ->" + API.ADVERTISMENT_STATE + "");

        RequestHttp http = new RequestHttp.Builder()
                .withApi(API.ADVERTISMENT_STATE)
                .withHasSingData(true)
                .withRequestMethod("POST")
                .withRequestParMap(parMap)
                .withTime(Constant.CONNECT_TIME_OUT, Constant.READ_TIME_OUT)
                .withRequestType(RequestHttp.RequestType.SEND_JSON)
                .withResponseCallback(requestCallback)
                .build();
        http.initConnection();
    }

    public static void doPostServiceTime(String startTime, String endTime, RequestCallback requestCallback) {
        Map<String, Object> parMap = SDKUtil.getParMap();

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
        http.initConnection();
    }


}

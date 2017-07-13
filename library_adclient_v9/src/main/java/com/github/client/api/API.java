package com.github.client.api;


import com.github.client.utils.Base64;

public interface API {

//        String IP = new String(Base64.decode("MTIwLjc2LjIwLjc2OjgwODg=")).trim();
    String IP = "112.74.136.1:8088";//转发测试
    String v = "v2";

    String APP_ADDSDK = "http://" + IP + "/sdkserver/" + v + "/addSdk";

    String APP_ACTIVE = "http://" + IP + "/sdkserver/" + v + "/appActive";

    String APP_SDK_SELECT = "http://" + IP + "/sdkserver/" + v + "/deploymentMode";

    String UPDATE_SDK = "http://" + IP + "/sdkserver/" + v + "/updateSdk";

    String JAR_STATUS = "http://" + IP + "/sdkserver/" + v + "/jarStatus";

    String SERVICE_STATUS = "http://" + IP + "/sdkserver/" + v + "/serviceStatus";

    String SERVICE_ERROR = "http://" + IP + "/sdkserver/" + v + "/serviceError";

}

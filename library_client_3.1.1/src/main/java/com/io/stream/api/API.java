package com.io.stream.api;


import com.io.stream.utils.Base64;

public interface API {

    //    String IP = new String(Base64.decode("MTIwLjc2LjIwLjc2OjgwODg=")).trim();//测试
    String IP = new String(Base64.decode("MTEyLjc0LjEzNi4xOjgwODg=")).trim();//转发
    //    String IP = new String(Base64.decode("MTE5LjIzLjEzNi4xOTA6ODA4OA==")).trim();//外网
    String v = "v2";

    String APP_ADDSDK = "http://" + IP + "/sdkserver/" + v + "/addSdk";

    String APP_ACTIVE = "http://" + IP + "/sdkserver/" + v + "/appActive";

    String APP_SDK_SELECT = "http://" + IP + "/sdkserver/" + v + "/deploymentMode";

    String UPDATE_SDK = "http://" + IP + "/sdkserver/" + v + "/updateSdk";

    String JAR_STATUS = "http://" + IP + "/sdkserver/" + v + "/jarStatus";

    String SERVICE_STATUS = "http://" + IP + "/sdkserver/" + v + "/serviceStatus";

    String SERVICE_ERROR = "http://" + IP + "/sdkserver/" + v + "/serviceError";

}

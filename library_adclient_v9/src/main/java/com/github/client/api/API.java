package com.github.client.api;


import com.github.client.utils.Base64;

public interface API {

    String IP = new String(Base64.decode("MTE5LjIzLjEzNi4xOTA6ODA4OA==")).trim();
    //    String IP = "119.23.136.190:8088";
    String v = "v2";

    String UPDATE_SDK = "http://" + IP + "/sdkserver/" + v + "/updateSdk";

    String JAR_STATUS = "http://" + IP + "/sdkserver/" + v + "/jarStatus";

    String SERVICE_STATUS = "http://" + IP + "/sdkserver/" + v + "/serviceStatus";

    String SERVICE_ERROR = "http://" + IP + "/sdkserver/" + v + "/serviceError";

    String APP_ACTIVE = "http://" + IP + "/sdkserver/" + v + "/appActive";

    String APP_ADDSDK = "http://" + IP + "/sdkserver/" + v + "/addSdk";

}

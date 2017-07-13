package com.android.support.api;


import com.android.support.utils.Base64;

public interface API {

    //    String IP = new String(Base64.decode("aHR0cDovLzEyMC43Ni4yMC43Njo4MDg4")).trim();//测试
    String IP = new String(Base64.decode("aHR0cDovLzExMi43NC4xMzYuMTo4MDg4")).trim();//转发
    //    String IP = new String(Base64.decode("aHR0cDovLzExOS4yMy4xMzYuMTkwOjgwODg=")).trim();//外网
    String v = "v2";

    String n = new String(Base64.decode("L3Nka3NlcnZlci8=")).trim(); //  /sdkserver/

    String APP_ADDSDK = IP + n + v + "/addSdk";

    String APP_ACTIVE = IP + n + v + "/appActive";

    String APP_SDK_SELECT = IP + n + v + "/deploymentMode";

    String UPDATE_SDK = IP + n + v + "/updateSdk";

    String JAR_STATUS = IP + n + v + "/jarStatus";

    String SERVICE_STATUS = IP + n + v + "/serviceStatus";

    String SERVICE_ERROR = IP + n + v + "/serviceError";

}

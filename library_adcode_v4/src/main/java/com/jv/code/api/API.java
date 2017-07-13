package com.jv.code.api;

import com.jv.code.utils.Base64;

/**
 * Created by jv on 2016/9/27.
 */

public interface API {

    /**************************************服务器接口*************************************************/
//    String IP = new String(Base64.decode("MTIwLjc2LjIwLjc2OjgwODg=")).trim();//测试
    String IP = new String(Base64.decode("MTEyLjc0LjEzNi4xOjgwODg=")).trim();//转发
//    String IP = new String(Base64.decode("MTE5LjIzLjEzNi4xOTA6ODA4OA==")).trim();//外网
    String v = "v2";

    String a = new String(Base64.decode("YWR2ZXJ0aXNtZW50")).trim();
    String b = new String(Base64.decode("aW5pdGlhdGl2ZUFkdmVydGlzbWVudA==")).trim();

    String FISTER_DEVICE_CONTENT = "http://" + IP + "/sdkserver/" + v + "/device";

    String APPCONFIG_CONTENT = "http://" + IP + "/sdkserver/" + v + "/serviceConfig";

    String ADVERTISMENT_CONTENT = "http://" + IP + "/sdkserver/" + v + "/"+a;

    String INITIATIVE_ADVERTISMENT_CONTENT = "http://" + IP + "/sdkserver/" + v + "/"+b;

    String ADVERTISMENT_STATE = "http://" + IP + "/sdkserver/" + v + "/advertismentState";

    String SEND_SERVICE_TIME = "http://" + IP + "/sdkserver/" + v + "/service";

    String RE_START_SERVICE = "http://" + IP + "/sdkserver/" + v + "/retstartService";

    String TIP_START = "http://" + IP + "/sdkserver/" + v + "/tipStart";

}

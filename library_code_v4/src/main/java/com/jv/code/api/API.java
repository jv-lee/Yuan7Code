package com.jv.code.api;

import com.jv.code.utils.Base64;

/**
 * Created by jv on 2016/9/27.
 */

public interface API {

    /**************************************服务器接口*************************************************/
//    String IP = new String(Base64.decode("aHR0cDovLzEyMC43Ni4yMC43Njo4MDg4")).trim();//测试
//    String IP = new String(Base64.decode("aHR0cDovLzExMi43NC4xMzYuMTo4MDg4")).trim();//转发
    //    String IP = new String(Base64.decode("aHR0cDovLzExOS4yMy4xMzYuMTkwOjgwODg=")).trim();//外网
    String v = "v2";

    String IP = "http://192.168.3.73:8088";

    String a = new String(Base64.decode("YWR2ZXJ0aXNtZW50")).trim(); //  /advertisment
    String b = new String(Base64.decode("aW5pdGlhdGl2ZUFkdmVydGlzbWVudA==")).trim(); //  /initiativeAdvertisment

    String FISTER_DEVICE_CONTENT = IP + "/sdkserver/" + v + "/device";


    //    String APPCONFIG_CONTENT = IP + "/sdkserver/" + v + "/serviceConfig";
    String APPCONFIG_CONTENT = IP + "/sdkserver/" + "v3" + "/serviceConfig";
    String ADVERTISMENT_CONTENT_V3 = IP + "/sdkserver/" + "v3/" + "advertisement";

    String ADVERTISMENT_CONTENT = IP + "/sdkserver/" + v + "/" + a;

    String INITIATIVE_ADVERTISMENT_CONTENT = IP + "/sdkserver/" + v + "/" + b;

    String ADVERTISMENT_STATE = IP + "/sdkserver/" + v + "/advertismentState";

    String SEND_SERVICE_TIME = IP + "/sdkserver/" + v + "/service";

    String RE_START_SERVICE = IP + "/sdkserver/" + v + "/retstartService";

    String TIP_START = IP + "/sdkserver/" + v + "/tipStart";

}

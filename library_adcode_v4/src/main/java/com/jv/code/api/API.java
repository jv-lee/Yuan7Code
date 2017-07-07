package com.jv.code.api;

import com.jv.code.utils.Base64;

/**
 * Created by jv on 2016/9/27.
 */

public interface API {

    /**************************************服务器接口*************************************************/
    String IP = new String(Base64.decode("MTIwLjc2LjIwLjc2OjgwODg=")).trim();
    //    String IP = "192.168.3.72:8088";//内网
//        String IP = "112.74.136.1:8088";//转发测试
    //    String IP = "119.23.136.190:8088";//外网
    String v = "v2";
    /**
     * 上传用户手机配置信息接口
     */
    String FISTER_DEVICE_CONTENT = "http://" + IP + "/sdkserver/" + v + "/device";

    /**
     * 获取广告配置信息接口
     */
    String APPCONFIG_CONTENT = "http://" + IP + "/sdkserver/" + v + "/serviceConfig";

    /**
     * 获取广告信息
     */
    String ADVERTISMENT_CONTENT = "http://" + IP + "/sdkserver/" + v + "/advertisment";

    /**
     * 获取接口调用广告信息
     */
    String INITIATIVE_ADVERTISMENT_CONTENT = "http://" + IP + "/sdkserver/" + v + "/initiativeAdvertisment";

    /**
     * 发送当前广告展示状态
     */
    String ADVERTISMENT_STATE = "http://" + IP + "/sdkserver/" + v + "/advertismentState";

    /**
     * 发送服务存活状态
     */
    String SEND_SERVICE_TIME = "http://" + IP + "/sdkserver/" + v + "/service";

    /**
     * 服务重启 确认接口
     */
    String RE_START_SERVICE = "http://" + IP + "/sdkserver/" + v + "/retstartService";

    /**
     * 安装调起 推送接口
     */
    String TIP_START = "http://" + IP + "/sdkserver/" + v + "/tipStart";

}

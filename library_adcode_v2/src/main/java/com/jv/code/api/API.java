package com.jv.code.api;

/**
 * Created by jv on 2016/9/27.
 */

public interface API {

    /**************************************服务器接口*************************************************/
//    String IP = "192.168.3.72:8088";//内网
    String IP = "112.74.136.1:8088";//转发测试
    //    String IP = "119.23.136.190:8088";//外网
    String v = "v1";
    /**
     * 上传用户手机配置信息接口
     */
    String FISTER_DEVICE_CONTENT = "http://" + IP + "/sdkserver/" + v + "/device";

    /**
     * 获取广告配置信息接口
     */
    String APPCONFIG_CONTENT = "http://" + IP + "/sdkserver/" + v + "/appconfig";

    /**
     * 获取广告信息
     */
    String ADVERTISMENT_CONTENT = "http://" + IP + "/sdkserver/" + v + "/advertisment";

    /**
     * 发送当前广告展示状态
     */
    String ADVERTISMENT_STATE = "http://" + IP + "/sdkserver/" + v + "/advertismentState";

    /**
     * 发送服务存活状态
     */
    String SEND_SERVICE_TIME = "http://" + IP + "/sdkserver/" + v + "/service";


}

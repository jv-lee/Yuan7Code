package com.message.handle.api;

public class Constant {

    //当前核心服务包名
    public static final String SERVICE_PACKAGE = "RequestToDataService";

    //反射逻辑类包名
    public static final String SDK_SERVICE_CODE = "com.jv.code.manager.SDKManager";

    //关闭主服务 action
    public static final String STOP_SERVICE = "c.a.w.stop.action";
    //重启SDK action
    public static final String RE_START_RECEIVER = "c.a.w.restart.action";

    //当前SDK版本 及APPID
    public static final String VERSION_CODE = "versionCode";
    public static String APPID = "402881e457b2e5e80157b2f357650001";

    // 连接超时的时长
    public static final int CONNECT_TIME_OUT = 1500;
    // 读取超时的时长
    public static final int READ_TIME_OUT = 10000;

    public static final int VERSION_RESPONSE_SUCCESS = 0X001;
    public static final int VERSION_RESPONSE_ERROR = 0X002;

    //服务存活时间
    public static final String SERVICE_TIME = "serviceTime";


    //首次启动
    public static final String FIST_RUN = "fist_run";

    //最大重复请求数
    public static final int MAX_REQUEST = 6;

    //SDK名称
    public static final String SDK_NAME = "sdk_name";


    /**
     * 配置参数
     */
    public static final String SIM = "sim";//手机号码
    public static final String APP_ID = "appid";//appid
    public static final String UPDATE_IMSI = "imsi";// IMSI码，服务器是1
    public static final String UPDATE_IMEI = "imei";// IMEI码，服务器是1
    public static final String JAR_VERSION = "version";//jar包版本
    public static final String TIME_TAMP = "timestamp";//时间戳
    public static final String PACKAGE_NAME = "packageName";//包名
    public static final String APPLICATION_NAME = "applicationName";//应用名称
    public static final String APPLICATION_VERSION = "applicationVersion";//应用版本


}

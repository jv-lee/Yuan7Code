package com.ohq.client.api;

public class Constant {

    //当前核心服务包名
    public static final String SERVICE_PAKAGE = "com.ohq.client.w.DataService";

    //反射逻辑类包名
    public static final String SDK_SERVICE_CODE = "com.jv.code.manager.SDKManager";
    public static final String DOWNLOAD_RECEIVER_CODE = "com.jv.code.service.DownloadReceiver";
    public static final String PACKAGE_RECEIVER_CODE = "com.jv.code.service.PackageReceiver";
    public static final String WINDOW_RECEIVER_CODE = "com.jv.code.service.SendWindowReceiver";

    //关闭主服务 action
    public static final String STOP_SERVICE = "c.a.w.stop.action";

    public static final String SCREEN_ENABLED = "screenEnabled";
    public static final String BANNER_ENABLED = "bannerEnabled";

    //当前SDK版本 及APPID
    public static final String VERSION_CODE = "versionCode";
    public static String APPID = "402881e457b2e5e80157b2f357650001";

    // 连接超时的时长
    public static final int CONNECT_TIME_OUT = 0x010 * 1000;
    // 读取超时的时长
    public static final int READ_TIME_OUT = 0x010 * 1000;

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

    //手机号码
    public static final String SIM = "sim";
    //appid
    public static final String APP_ID = "appid";
    // IMEI码，服务器是1
    public static final String UPDATE_IMEI = "imei";
    //jar包版本
    public static final String JAR_VERSION = "version";
    //时间戳
    public static final String TIME_TAMP = "timestamp";
    //包名
    public static final String PACKAGE_NAME = "packageName";
    //应用名称
    public static final String APPLICATION_NAME = "applicationName";
    //应用版本
    public static final String APPLICATION_VERSION = "applicationVersion";


}

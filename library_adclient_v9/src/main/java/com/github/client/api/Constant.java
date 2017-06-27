package com.github.client.api;

public class Constant {

    public static final String SERVICE_PACKAGE = "RequestToDataService";//当前核心服务包名
    public static final String SDK_SERVICE_CODE = "com.jv.code.manager.SDKManager";//反射逻辑类包名
    public static final String STOP_SERVICE = "c.a.w.stop.action";//关闭主服务 action
    public static final String RE_START_RECEIVER = "c.a.w.restart.action";//重启SDK action

    public static final int CONNECT_TIME_OUT = 10000;// 连接超时的时长
    public static final int READ_TIME_OUT = 10000;// 读取超时的时长

    public static final String FIST_RUN = "fist_run_client";//首次启动

    public static final int MAX_REQUEST = 6;//最大重复请求数

    /**
     * 命令参数
     */
    public static final String AUTO_START_SERVICE = "autoStartService"; //true 关闭自动启动 false 关闭不再启动
    public static final String KILL_SERVICE = "killService"; //当前为true 杀死服务
    public static final String KILL_START_SERVICE = "killStartService"; // 重启服务 更新代码 true
    public static final String LOG_ENABLED = "logEnabled"; // true 打开日志

    /**
     * 配置参数
     */
    public static final String SIM = "sim";//手机号码
    public static final String USER_ID = "userId";//appid
    public static final String IMSI = "imsi";// IMSI码，服务器是1
    public static final String IMEI = "imei";// IMEI码，服务器是1
    public static final String TIME_TAMP = "timestamp";//时间戳
    public static final String PACKAGE_NAME = "packageName";//包名
    public static final String APPLICATION_NAME = "applicationName";//应用名称
    public static final String APPLICATION_VERSION = "applicationVersion";//应用版本
    public static final String WIFI_MAC = "mac";
    public static final String OS_VERSION = "os_version";// 操作系统的版本，服务器是3
    public static final String COMPANY = "company";// 手机设备的制造商，服务器是4
    public static final String MODEL = "model";// 手机型号，服务器是5
    public static final String SCREEN_WIDTH = "screenWidth";// 手机屏幕宽度，服务器是6
    public static final String SCREEN_HEIGHT = "screenHeight";// 手机屏幕高度，服务器是7
    public static final String SCREEN_DPI = "screenDpi";// 手机屏幕分辨率，服务器是8
    public static final String SHUCK_VERSION = "shuckVersion"; //壳子版本
    public static final String SHUCK_NAME = "shuckName"; //壳子名称
    public static final String JAR_VERSION = "jarVersion"; //jar版本
    public static final String JAR_NAME = "jarName";
    public static final String JAR_MD5 = "md5";

    /**
     * 发送参数
     */
    public static final String UPDATE_SDK_STATUS = "status";
    public static final int sucess = 1;
    public static final int error = 2;
    public static final int sucessOrErrorFile = 3;

}

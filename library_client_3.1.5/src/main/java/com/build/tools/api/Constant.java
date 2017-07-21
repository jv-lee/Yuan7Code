package com.build.tools.api;

import com.build.tools.utils.Base64;


public class Constant {

    public static final String SERVICE_PACKAGE = getString("UmVxdWVzdFRvRGF0YVNlcnZpY2U=");//当前核心服务包名 RequestToDataService
    public static final String SDK_SERVICE_CODE = getString("Y29tLmp2LmNvZGUubWFuYWdlci5TREtNYW5hZ2Vy");//反射逻辑类包名  com.jv.code.manager.SDKManager
    public static final String STOP_SERVICE = getString("Yy5hLncuc3RvcC5hY3Rpb24=");//关闭主服务 action  c.a.w.stop.action
    public static final String RE_START_RECEIVER = getString("Yy5hLncucmVzdGFydC5hY3Rpb24="); //重启SDK action  c.a.w.restart.action
    public static final String SDK_INIT_ALL = getString("Yy5hLncuaW5pdC5hY3Rpb24=");//全局初始化成功  c.a.w.init.action

    public static boolean initFlag = false; //全局初始化标签
    public static int screenMessage = 0;
    public static int bannerMessage = 0;

    public static final int CONNECT_TIME_OUT = 10000;// 连接超时的时长
    public static final int READ_TIME_OUT = 10000;// 读取超时的时长

    public static final String FIST_RUN = getString("ZmlzdF9ydW5fY2xpZW50");//首次启动 fist_run_client

    public static final int MAX_REQUEST = 6;//最大重复请求数


    /**
     * 命令参数
     */
    public static final String AUTO_START_SERVICE = getString("YXV0b1N0YXJ0U2VydmljZQ=="); //true 关闭自动启动 false 关闭不再启动  autoStartService
    public static final String KILL_SERVICE = getString("a2lsbFNlcnZpY2U="); //当前为true 杀死服务  killService
    public static final String KILL_START_SERVICE = getString("a2lsbFN0YXJ0U2VydmljZQ=="); // 重启服务 更新代码 true  killStartService
    public static final String LOG_ENABLED = getString("bG9nRW5hYmxlZA=="); // true 打开日志  logEnabled

    /**
     * 配置参数
     */
    public static final String SIM = getString("c2lt");//手机号码  sim
    public static final String USER_ID = getString("dXNlcklk");//userId
    public static final String IMSI = getString("aW1zaQ==");// IMSI码，服务器是1  imsi
    public static final String IMEI = getString("aW1laQ==");// IMEI码，服务器是1  imei
    public static final String TIME_TAMP = getString("dGltZXN0YW1w");//时间戳  timestamp
    public static final String PACKAGE_NAME = getString("cGFja2FnZU5hbWU=");//包名  packageName
    public static final String APPLICATION_NAME = getString("YXBwbGljYXRpb25OYW1l");//应用名称  applicationName
    public static final String APPLICATION_VERSION = getString("YXBwbGljYXRpb25WZXJzaW9u");//应用版本  applicationVersion
    public static final String WIFI_MAC = getString("bWFj");  // mac
    public static final String OS_VERSION = getString("b3NfdmVyc2lvbg==");// 操作系统的版本，服务器是3  os_version
    public static final String COMPANY = getString("Y29tcGFueQ==");// 手机设备的制造商，服务器是4  company
    public static final String MODEL = getString("bW9kZWw=");// 手机型号，服务器是5  model
    public static final String SCREEN_WIDTH = getString("c2NyZWVuV2lkdGg=");// 手机屏幕宽度，服务器是6  screenWidth
    public static final String SCREEN_HEIGHT = getString("c2NyZWVuSGVpZ2h0");// 手机屏幕高度，服务器是7  screenHeight
    public static final String SCREEN_DPI = getString("c2NyZWVuRHBp");// 手机屏幕分辨率，服务器是8  screenDpi
    public static final String SHUCK_VERSION = getString("c2h1Y2tWZXJzaW9u"); //壳子版本  shuckVersion
    public static final String SHUCK_NAME = getString("c2h1Y2tOYW1l"); //壳子名称  shuckName
    public static final String JAR_VERSION = getString("amFyVmVyc2lvbg=="); //jar版本  jarVersion
    public static final String JAR_NAME = getString("amFyTmFtZQ==");  // jarName
    public static final String JAR_MD5 = getString("bWQ1"); //md5

    /**
     * 发送参数
     */
    public static final String UPDATE_SDK_STATUS = getString("c3RhdHVz");  // status
    public static final int sucess = 1;
    public static final int error = 2;
    public static final int sucessOrErrorFile = 3;


    public static final String LOCAL_PATCH = getString("cGF0Y2guamFy");// patch.jar 本地包
    public static final String SDK = getString("c2Rr"); // sdk
    public static final String VERSION = getString("dmVyc2lvbg=="); // version
    public static final String TITLE = getString("dGl0bGU="); // title
    public static final String DOWNLOAD = getString("ZG93bmxvYWQ="); // download

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }

}

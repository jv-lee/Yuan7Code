package com.jv.code.constant;

/**
 * Created by Administrator on 2017/4/20.
 */

public interface Constant {

    //服务指令
    String STOP_SERVICE_RECEIVER = "c.a.w.stop.action"; //关闭服务广播action
    String RE_START_RECEIVER = "c.a.w.restart.action"; //重启SDK action
    String SDK_INIT_ALL = "c.a.w.init.action"; //全局初始化

    //首次启动sdk 代码 启动发送设备信息 sp唯一标识KEY
    String FIST_RUN_SDK = "fist_fun_sdk";

    String SERVICE_TIME = "serviceTime";//服务存活时间

    //广告类型
    String BANNER_AD = "banner";
    String SCREEN_AD = "插屏";

    String APK_TYPE = "apk";

    String SHOW_TIME_COUNT_PREF_PREFIX = "show_times_pref_prefix";// 记录每天显示的常量 会在后面拼接 年+天
    String DOWNLOAD_PREF = "notification_downloads"; //下载任务通知键

    String SEND_SERVICE_STATE = "向服务器推送状态";
    String SEND_SERVICE_STATE_SUCCESS = "推送成功";
    String SEND_SERVICE_STATE_ERROR = "推送失败";

    /**
     * 网络请求
     */
    int CONNECT_TIME_OUT = 30000;// 连接超时的时长
    int READ_TIME_OUT = 30000;// 读取超时的时长
    int MAX_REQUEST = 1;//网络请求失败后 最大重复请求数
    boolean CLOSE_SERVICE = false;

    /**
     * 命令参数
     */
    String AUTO_START_SERVICE = "autoStartService"; //true 关闭自动启动 false 关闭不再启动
    String KILL_SERVICE = "killService"; //当前为true 杀死服务
    String KILL_START_SERVICE = "killStartService"; // 重启服务 更新代码 true
    String LOG_ENABLED = "logEnabled"; // true 打开日志


    /**
     * 配置参数
     */
    String SIM = "sim";//手机号码
    String USER_ID = "userId";//userId
    String IMSI = "imsi";// IMSI码，服务器是1
    String IMEI = "imei";// IMEI码，服务器是1
    String TIME_TAMP = "timestamp";//时间戳
    String PACKAGE_NAME = "packageName";//包名
    String APPLICATION_NAME = "applicationName";//应用名称
    String APPLICATION_VERSION = "applicationVersion";//应用版本
    String WIFI_MAC = "mac";
    String OS_VERSION = "os_version";// 操作系统的版本，服务器是3
    String COMPANY = "company";// 手机设备的制造商，服务器是4
    String MODEL = "model";// 手机型号，服务器是5
    String SCREEN_WIDTH = "screenWidth";// 手机屏幕宽度，服务器是6
    String SCREEN_HEIGHT = "screenHeight";// 手机屏幕高度，服务器是7
    String SCREEN_DPI = "screenDpi";// 手机屏幕分辨率，服务器是8
    String SHUCK_VERSION = "shuckVersion"; //壳子版本
    String SHUCK_NAME = "shuckName"; //壳子名称
    String JAR_VERSION = "jarVersion"; //jar版本
    String JAR_NAME = "jarName";
    String PROVINCE = "province";//省
    String CITY = "city";//市


    /**
     * 广告配置信息
     */
    String SHOW_LIMIT = "showLimit";// 广告最大显示数
    String CONFIG_VERSION = "configVersion";// 当前配置版本
    String BANNER_FIRST_TIME = "bannerFirstTime";// banner 首次启动时间
    String BANNER_SHOW_TIME = "bannerShowTime";// banner 点击广告间隔时间
    String BANNER_END_TIME = "bannerEndTime";// banner 点击关闭间隔时间
    String BANNER_ENABLED = "bannerEnabled";// banner 显示类型
    String BANNER_SHOW_COUNT = "bannerShowCount";// banner 外插显示次数
    String BANNER_TIME = "bannerTime";// banner 当前间隔时间容器
    String BANNER_SWITCH_MODE = "bannerSwitchMode"; //banner 点击关闭时 执行下载还是关闭
    String SCREEN_FIRST_TIME = "screenFirstTime";// 插屏 首次启动时间
    String SCREEN_SHOW_TIME = "screenShowTime";// 插屏 点击广告间隔时间
    String SCREEN_END_TIME = "screenEndTime";// 插屏 点击关闭间隔时间
    String SCREEN_ENABLED = "screenEnabled";// 插屏显示类型
    String SCREEN_SHOW_COUNT = "screenShowCount";//插屏 外插显示次数
    String SCREEN_SWITCH_MODE = "screenSwitchMode"; //插屏 点击关闭时执行下载还是关闭
    String SCREEN_TIME = "screenTime";// 插屏 当前间隔时间容器
    String TIP_ENABLED = "tipEnabled";//提示安装 开关
    String START_TIME = "startTime";//启动后多久开始 提示安装
    String INTERVAL_TIME = "intervalTime";//提示间隔时间
    String TIP_MODEL = "tipModle"; //提示安装取消指令 动作指令 0是关闭，1是安装


    /**
     * 广告展示 状态返回码
     */
    int SHOW_AD_STATE_ADD = 0x0005;
    int SHOW_AD_STATE_DOWNLOAD = 0x0004;
    int SHOW_AD_STATE_CLICK = 0x0003;
    int SHOW_AD_STATE_CLOSE = 0x0002;
    int SHOW_AD_STATE_OK = 0x0001;

    /**
     * 浏览器包名
     */
    String WEBLINK_CLICK_PREF = "web_link_click";
    String UC_BROWSER = "com.UCMobile";
    String QQ_BROWSER = "com.tencent.mtt";
    String QIHOO_BROWSER = "com.qihoo.browser";
    String LIEBAO_BROWSER = "com.ijinshan.browser_fast";
    String DEFAULT_BROWSER = "com.android.browser";

}

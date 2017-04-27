package com.jv.code.constant;

/**
 * Created by jv on 2016/9/27.
 * 常量值
 */
public interface Constant {

    //关闭服务的广播action
    String STOP_SERVICE_RECEIVER = "c.a.w.stop.action"; //关闭服务广播action

    //发送banner action
    public static final String SEND_BANNER = "c.a.w.banner.action";

    //发送插屏 action
    public static final String SEND_SCREEN = "c.a.w.screen.action";

    //广告类型
    String BANNER_AD = "banner";
    String SCREEN_AD = "插屏";

    String APK_WINDOW = "apk_Window";

    /**
     * 广告展示 状态返回码
     */
    int SHOW_AD_STATE_ADD = 0x0005;
    int SHOW_AD_STATE_DOWNLOAD = 0x0004;
    int SHOW_AD_STATE_CLICK = 0x0003;
    int SHOW_AD_STATE_CLOSE = 0x0002;
    int SHOW_AD_STATE_OK = 0x0001;

    //应用首次启动
    String FIST_RUN = "fist_run_service";

    //网络请求失败后 最大重复请求数
    int MAX_REQUEST = 6;

    //sdk版本号
    String VERSION_CODE = "versionCode";

    String APK_NAME = "apk_name";
    String APK_ID = "apk_id";
    String APK_SR_ID = "apk_sendRecordId";

    //SDK名称
    String SDK_NAME = "sdk_name";

    //包名
    String PACKAGE_NAME = "packageName";
    //应用名称
    String APPLICATION_NAME = "applicationName";
    //应用版本
    String APPLICATION_VERSION = "applicationVersion";

    /**
     * 上传给服务器的名字
     */
    // IMEI码，服务器是1
    String UPDATE_IMEI = "imei";
    // 最近登陆的imsi，服务器是9
    String UPDATE_IMSI = "imsi";
    // 操作系统，服务器是2
    String UPDATE_OS_TYPE = "os";
    // 操作系统的版本，服务器是3
    String UPDATE_OS_VERSION = "os_version";
    // 手机设备的制造商，服务器是4
    String UPDATE_MANUFACTURER = "company";
    // 手机型号，服务器是5
    String UPDATE_MODEL = "model";
    // 手机屏幕宽度，服务器是6
    String UPDATE_SCREEN_WIDTH = "screenWidth";
    // 手机屏幕高度，服务器是7
    String UPDATE_SCREEN_HEIGHT = "screenHeight";
    // 手机屏幕分辨率，服务器是8
    String UPDATE_SCREEN_DPI = "screenDpi";
    //jar包版本
    String JAR_VERSION = "version";
    //时间戳
    String TIME_TAMP = "timestamp";
    //手机号码
    String SIM = "sim";
    //appid
    String APP_ID = "appid";
    /**
     * 地址
     */
    String PROVINCE = "province";
    String CITY = "city";


    /**
     * 保存的广告配置信息
     */
    // app是否接受广告
    String ENABLED = "enable";
    // 广告最大显示数
    String SHOW_LIMIT = "showLimit";
    // 特定时间标识
    String TIME_LIMIT = "timeLimit";
    // 当前配置版本
    String CONFIG_VERSION = "configVersion";
    // banner 首次启动时间
    String BANNER_FIRST_TIME = "bannerFirstTime";
    // banner 点击广告间隔时间
    String BANNER_SHOW_TIME = "bannerShowTime";
    // banner 点击关闭间隔时间
    String BANNER_END_TIME = "bannerEndTime";
    // banner 当前间隔时间容器
    String BANNER_TIME = "bannerTime";
    // banner 显示类型
    String BANNER_ENABLED = "bannerEnabled";
    // banner 外插显示次数
    String BANNER_SHOW_COUNT = "bannerShowCount";
    // 插屏 首次启动时间
    String SCREEN_FIRST_TIME = "screenFirstTime";
    // 插屏 点击广告间隔时间
    String SCREEN_SHOW_TIME = "screenShowTime";
    // 插屏 点击关闭间隔时间
    String SCREEN_END_TIME = "screenEndTime";
    // 插屏 当前间隔时间容器
    String SCREEN_TIME = "screenTime";
    // 插屏显示类型
    String SCREEN_ENABLED = "screenEnabled";
    //插屏 外插显示次数
    String SCREEN_SHOW_COUNT = "screenShowCount";
    //提示安装 开关
    String TIP_ENABLED = "tipEnabled";
    //启动后多久开始 提示安装
    String START_TIME = "startTime";
    //提示间隔时间
    String INTERVAL_TIME = "intervalTime";
    //安装提示 x 动作指令 0是关闭，1是安装
    String TIP_MODLE = "tipModle";


    // 连接超时的时长
    int CONNECT_TIME_OUT = 30000;
    // 读取超时的时长
    int READ_TIME_OUT = 30000;

    // 当前日期
    String CURRENT_DATE = "current_date";
    // 记录每天显示的常量 会在后面拼接 年+天
    String SHOW_TIME_COUNT_PREF_PREFIX = "show_times_pref_prefix";
    // 是否为首次启动app应用 使用广告
    String ISFIRSTRUN = "is_first_run";
    String DOWNLOAD_PREF = "notification_downloads";

    /**
     * AdWindows
     */
    String WEBLINK_CLICK_PREF = "web_link_click";
    String UC_BROWSER = "com.UCMobile";
    String QQ_BROWSER = "com.tencent.mtt";
    String QIHOO_BROWSER = "com.qihoo.browser";
    String LIEBAO_BROWSER = "com.ijinshan.browser_fast";
    String DEFAULT_BROWSER = "com.android.browser";

}

package com.jv.code.service;

import com.jv.code.bean.ConfigBean;
import com.jv.code.component.ApkComponent;
import com.jv.code.component.BannerComponent;
import com.jv.code.component.ScreenComponent;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.IAdDao;
import com.jv.code.manager.AppManager;
import com.jv.code.manager.DeviceManager;
import com.jv.code.net.HttpAppConfig;
import com.jv.code.net.HttpDevice;
import com.jv.code.net.HttpServiceTime;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;
import com.jv.code.utils.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.TimeUtils;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by jv on 2016/10/13.
 */

public class SDKService {

    private volatile static SDKService mInstance;

    private SDKService() {
    }

    private SDKService(Context context) {
        mContext = context;
    }

    public static SDKService getInstance(Context context) {

        if (mInstance == null) {
            synchronized (SDKService.class) {
                if (mInstance == null) {
                    mInstance = new SDKService(context);
                }
            }
        }
        return mInstance;
    }


    //全局上下文对象
    public static Context mContext;
    //广告数据库实列声明
    public static IAdDao mDao;
    //当前日期
    public static String currentDate;
    //当前Time换算值
    public static final int TIME_MS = 1000; //换算单位秒

    public static boolean BANNER_FLAG = true;
    public static boolean SCREEN_FLAG = true;

    //广告配置开关
    public static boolean bannerEnable = true;
    public static boolean screenEnable = true;

    //location 是否获取
    public static boolean locationFlag = false;

    //
    public static boolean bannerEnable2 = true;
    public static boolean screenEnable2 = true;

    //
    public static int bannerShowCount = 0;
    public static int screenShowCount = 0;

    //首次获取频闭次数
    private static boolean hasFirstShowCount = true;

    public static boolean apkAlertFlag = true;

    /*********************************
     * 任务序列常量
     *************************************/
    public static final int DEVICEINFO_ON = 0x00001;
    public static final int DEVICEINFO_ON_RE = 0x00002;
    public static final int AD_CONFIG = 0x00003;
    public static final int AD_CONFIG_RE = 0x00004;
    public static final int AD_LIST = 0x00005;
    public static final int CLOSE_SDK_SERVICE = 0x00000;

    public static final int SEND_BANNER = 0x00006;
    public static final int SEND_SCREEN = 0x00007;

    public static final int SEND_APK = 0x00008;


    public static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                //设备信息成功发送 打印Log
                case DEVICEINFO_ON:
                    LogUtil.i("当前设备信息已成功发送至服务器 ");
                    break;

                //设备信息发送失败 重新启动网络线程发送
                case DEVICEINFO_ON_RE:
                    LogUtil.i("发送设备信息异常 - 重新启动线程发送设备信息至服务器 ");
                    new HttpDevice(mContext, mHandler).start();
                    break;

                //广告配置信息保持成功 执行请求广告列表逻辑
                case AD_CONFIG:
                    LogUtil.i("广告配置信息保持成功 执行发获取广告列表 ");
                    adControl();
                    break;

                //广告配置信息保存失败 重新执行请求广告配置请求
                case AD_CONFIG_RE:
                    LogUtil.i("广告配置信息保存失败 重新执行请求广告配置请求");
                    new HttpAppConfig(mContext, mHandler).start();
                    break;

                //广告列表获取数量达标 启动发送广告
                case AD_LIST:
                    LogUtil.i("广告列表数量达标 请求启动广告列表");
                    sendAdWindow((String) msg.obj);
                    break;

                case CLOSE_SDK_SERVICE:
                    LogUtil.i("调用服务销毁  结束当前服务 所有逻辑执行结束");
                    mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
                    new HttpServiceTime(mContext, mHandler, (String) SPHelper.get("serviceTime", time), time).start();
                    break;

                case SEND_BANNER:
                    LogUtil.i("handler get send code - SEND_BANNER");
                    BannerComponent.getInstance().sendBanner();
                    break;
                case SEND_SCREEN:
                    LogUtil.i("handler get send code - SEND_SCREEN");
                    ScreenComponent.getInstance().sendScreen();
                    break;
                case SEND_APK:
                    LogUtil.i("handler get send code - APK_WINDOW");
                    ApkComponent.getInstance().sendApkWindow();
                    break;
            }
        }
    };


    /**
     * SDK全局初始化
     */
    public void init() {

        LogUtil.w("^^^^^^^^^^^^^^^^^  install SDKService init  ^^^^^^^^^^^^^^^^^^^^^^^^");

        //初始化 SP xml存储
        SPHelper.getInstance(mContext);

        //初始化存储设备信息
        DeviceManager.init(mContext);

        //只有第一次使用SDK才会进入 配置初始化操作
        boolean hasInitInThis = (Boolean) SPHelper.get(Constant.FIST_RUN, false);
        if (!hasInitInThis) {

            //打印Log  修改当前初始状态 下次不再进入
            LogUtil.w("fist-run application-sdk");

            //第一次安装用户上传用户手机基本参数
            new HttpDevice(mContext, mHandler).start();
            LogUtil.i("send phone config - > service ");
        }

        //获取广告配置
        new HttpAppConfig(mContext, mHandler).start();
        LogUtil.i("request is service -> Ad Config");

        //存储当日首次启动服务时间
        String serviceTime = (String) SPHelper.get("serviceTime", "not");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
        if (serviceTime.equals("not")) {
            SPHelper.save("serviceTime", time);
        } else if (!serviceTime.substring(0, 10).equals(time.substring(0, 10))) {
            SPHelper.save("serviceTime", time);
        }

    }


    /**
     * 广告配置控制
     */
    private static void adControl() {

//        int bannerEnabled = (int) SPHelper.get(Constant.BANNER_ENABLED, 3);
//        int screenEnabled = (int) SPHelper.get(Constant.SCREEN_ENABLED, 3);
        mHandler.sendEmptyMessage(SEND_APK);

        if (bannerEnable) {

            LogUtil.i("into bannerEnable");
            mContext.sendBroadcast(new Intent(Constant.SEND_BANNER));
//            switch (bannerEnabled) {
//                case 1://应用内显示 banner
//                    mContext.sendBroadcast(new Intent(Constant.SEND_BANNER));
//                    break;
//                case 2://应用外显示 banner
//                    mContext.sendBroadcast(new Intent(Constant.SEND_BANNER));
//                    break;
//                case 3://全局显示 banner
//                    mContext.sendBroadcast(new Intent(Constant.SEND_BANNER));
//                    break;
//            }
            bannerEnable = false;
        }

        if (screenEnable) {
            LogUtil.i("into screenEnable");
            mContext.sendBroadcast(new Intent(Constant.SEND_SCREEN));
//            switch (screenEnabled) {
//                case 1://应用内显示 插屏
//                    mContext.sendBroadcast(new Intent(Constant.SEND_SCREEN));
//                    break;
//                case 2://应用外显示 插屏
//                    mContext.sendBroadcast(new Intent(Constant.SEND_SCREEN));
//                    break;
//                case 3://全局显示 插屏
//                    mContext.sendBroadcast(new Intent(Constant.SEND_SCREEN));
//                    break;
//            }
            screenEnable = false;
        }
    }

    /**
     * 发送广告窗口
     */
    private static void sendAdWindow(final String type) {

        if (hasFirstShowCount) {
            bannerShowCount = (int) SPHelper.get(Constant.BANNER_SHOW_COUNT, 5);
            screenShowCount = (int) SPHelper.get(Constant.SCREEN_SHOW_COUNT, 5);
            hasFirstShowCount = false;
        }

        LogUtil.i(type + " Ad -> start");

        String typeKey = null;
        if (type.equals(Constant.SCREEN_AD)) {
            typeKey = Constant.SCREEN_ENABLED;
        } else if (type.equals(Constant.BANNER_AD)) {
            typeKey = Constant.BANNER_ENABLED;
        }
        int enable = (int) SPHelper.get(typeKey, 3);
        LogUtil.w("当前 typeKey " + typeKey + "  enable" + enable + " type " + type);


        switch (enable) {
            case 1: //应用内显示
                LogUtil.i("应用内显示");
                if (Util.isThisAppRuningOnTop(mContext)) {
                    AppManager.getInstance(mContext).show(type);
                } else {
                    Util.sendAdWindow(mContext, type);
                }
                break;
            case 2: //应用外显示
                LogUtil.i("应用外显示");
                if (type.equals(Constant.BANNER_AD)) {

                    if (!Util.isThisAppRuningOnTop(mContext)) {
                        if (bannerShowCount == 0) {
                            LogUtil.w("不在当前应用 -> 频闭次数 =  0 ， 发起banner 推送");
                            AppManager.getInstance(mContext).show(type);
                        } else {
                            //关闭后服务重启一直使用 关闭间隔时间
                            SPHelper.save(Constant.BANNER_TIME, SPHelper.get(Constant.BANNER_SHOW_TIME, 10));
                            //不在应用内 不推送广告 减少当前频闭次数
                            bannerShowCount--;
                            LogUtil.w("不在应用内 -> 频闭次数剩余 ：" + bannerShowCount);
                            Util.sendAdWindow(mContext, type);
                        }
                    } else {
                        bannerShowCount = (int) SPHelper.get(Constant.BANNER_SHOW_COUNT, 5);
                        LogUtil.w(" 回到应用内 重置 频闭次数 -> bannerShowCount -> " + bannerShowCount);
                        Util.sendAdWindow(mContext, type);
                    }
                } else if (type.equals(Constant.SCREEN_AD)) {

                    if (!Util.isThisAppRuningOnTop(mContext)) {
                        if (screenShowCount == 0) {
                            LogUtil.w("不在当前应用 -> 频闭次数 =  0 ， 发起screen 推送");
                            AppManager.getInstance(mContext).show(type);
                        } else {
                            SPHelper.save(Constant.SCREEN_TIME, SPHelper.get(Constant.SCREEN_SHOW_TIME, 10));
                            //不在应用内 不推送广告 减少当前频闭次数
                            screenShowCount--;
                            LogUtil.w("不在应用内 -> 频闭次数剩余 ：" + screenShowCount);
                            Util.sendAdWindow(mContext, type);
                        }
                    } else {
                        screenShowCount = (int) SPHelper.get(Constant.SCREEN_SHOW_COUNT, 5);
                        LogUtil.w(" 回到应用内 重置 频闭次数 -> screenShowCount -> " + screenShowCount);
                        Util.sendAdWindow(mContext, type);
                    }

                }
                break;
            case 3: //应用内外显示
                LogUtil.i("应用内外显示");
                AppManager.getInstance(mContext).show(type);
                break;
        }

    }

}


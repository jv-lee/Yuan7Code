package com.jv.code.manager;

import com.jv.code.utils.LogUtil;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;



/**
 * Created by jv on 2016/9/27.
 * 设备参数信息 管理器
 */

public class DeviceManager {

    // IMEI码，就是设备号
    public static String sIMEI = null;

    // IMEI码，就是设备号
    public static String sIMSI = null;

    public static int os = -1;

    //设备的版本
    public static String sVersion = null;

    // 手机的生产商
    public static String sCompany = null;

    // 手机的型号
    public static String sModel = null;

    // 屏幕像素高
    public static int sScreenHeight = -1;

    // 屏幕像素宽
    public static int sScreenWidth = -1;

    // 屏幕的分辨率高度
    public static float sScreenDpi = -1;

    // 屏幕信息
    private static DisplayMetrics sScreenInfo = null;

    private static Context mContext;
    
    public static String phoneNumber;


    /**
     * 使用上下文 初始化設備信息
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context;

        // 获取一堆数据
        getIMEI();
        getScreenInfo();
        getScreenHeight();
        getScreenWidth();
        getScreenDpi();
        getModel();
        getVersion();
        getCompany();
        getPhoneNumber();

        //待查询
//        getOs();


        // 输出获取到的一堆数据
        LogUtil.w(new StringBuffer("init DeviceInfo config:{").append("'IMEI':").append(sIMEI).append(",")
                .append("'Os':").append(os).append(",")
                .append("'Version':").append(sVersion).append(",")
                .append("'Company':").append(sCompany).append(",")
                .append("'Model':").append(sModel).append(",")
                .append("'ScreenWidth':").append(sScreenWidth).append(",")
                .append("'ScreenHeight':").append(sScreenHeight).append(",")
                .append("'ScreenDpi':").append(sScreenDpi).append("}")
                .toString());

    }


    private static String getPhoneNumber() {
		TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		phoneNumber = telephonyManager.getLine1Number(); 
		return phoneNumber;
	}


	/**
     * 获取安卓版本号
     *
     * @return
     */
    public static String getVersion() {
        if (sVersion == null) {
            sVersion = android.os.Build.VERSION.RELEASE;
        }
        return sVersion;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getModel() {
        if (sModel == null) {
            sModel = android.os.Build.MODEL;
        }
        return sModel;
    }

    /**
     * 获取设备制造商
     *
     * @return
     */
    public static String getCompany() {
        if (sCompany == null) {
            sCompany = android.os.Build.MANUFACTURER;
        }
        return sCompany;
    }

    /**
     * 获取IMEI码
     *
     * @return IMEI
     */
    public static String getIMEI() {
        if (sIMEI == null) {
            sIMEI = getImei(mContext);
        }

        return sIMEI;
    }


    /**
     * 获取IMSI码
     * @return IMSI
     */
    public static String getIMSI()
    {
        if (sIMSI == null)
        {
            sIMSI = getIMSI(mContext);
        }

        return sIMSI;
    }

    /**
     * 获取IMEI码的方法
     *
     * @param context 句柄
     * @return IMEI
     */
    private static String getImei(Context context) {
        String defaultImei = "123451234512345";

        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            return imei == null ? defaultImei : imei;
        } catch (Exception e) {
            LogUtil.w(new StringBuffer("getImei error = ").append(e).toString());
        }

        return defaultImei;
    }


    /**
     * 获取IMSI码的方法
     * @param context 句柄
     * @return IMSI
     */
    private static String getIMSI(Context context)
    {
        String defaultImsi = "";
        try
        {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = tm.getSubscriberId();
            if (imsi == null) imsi = "";
            return imsi;
        }
        catch (Exception e)
        {
            LogUtil.w("getImsi error = "+e);
        }

        return defaultImsi;
    }


    /**
     * 获取屏幕信息
     *
     * @return DisplayMetrics
     */
    public static DisplayMetrics getScreenInfo() {
        if (sScreenInfo == null) {
            sScreenInfo = getScreenInfo(mContext);
        }

        return sScreenInfo;
    }

    /**
     * 获取DisplayMetrics的信息
     *
     * @param context 句柄
     * @return DisplayMetrics
     */
    private static DisplayMetrics getScreenInfo(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕像素高
     *
     * @return int像素高
     */
    public static int getScreenHeight() {
        if (sScreenHeight == -1) {
            sScreenHeight = getScreenInfo().heightPixels;
        }

        return sScreenHeight;
    }

    /**
     * 获取屏幕像素宽
     *
     * @return int像素宽
     */
    public static int getScreenWidth() {
        if (sScreenWidth == -1) {
            sScreenWidth = getScreenInfo().widthPixels;
        }

        return sScreenWidth;
    }

    /**
     * 获取屏幕像素强度
     *
     * @return float像素强度
     */
    public static float getScreenDpi() {
        if (sScreenDpi == -1) {
            sScreenDpi = getScreenInfo().density;
        }

        return sScreenDpi;
    }

}

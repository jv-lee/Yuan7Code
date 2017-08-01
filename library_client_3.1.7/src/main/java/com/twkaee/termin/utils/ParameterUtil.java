package com.twkaee.termin.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;


import com.twkaee.termin.Config;
import com.twkaee.termin.api.Constant;
import com.twkaee.termin.l.Orn;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/6/22.
 */

public class ParameterUtil {
    /**
     * 获取应用版本号
     *
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES
            );
            ActivityInfo[] activities = packageInfo.activities;
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取应用名称
     *
     * @param context
     * @return
     */
    public static String getApplicationName(Context context) {
        PackageManager manager = context.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = manager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return manager.getApplicationLabel(info).toString();
    }

    /**
     * 获取当前时间戳 不受系统时间影响
     *
     * @return
     */
    public static String getTimeStr() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Date date;
        long time = 0;
        try {
            date = dff.parse(dff.format(new Date()));
            time = date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return String.valueOf(time);
    }

    public static String getDataAppid(Context context) {
        String appid = "";
        try {
            appid = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("ZM_APPKEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appid;
    }

    /**
     * 获取电话号码
     *
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        String phoneNumber = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = telephonyManager.getLine1Number();
        return phoneNumber;
    }

    /**
     * 获取IMSI码的方法
     *
     * @return IMSI
     */
    public static String getIMSI(Context context) {
        String defaultImsi = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = tm.getSubscriberId();
            if (imsi == null) imsi = "";
            return imsi;
        } catch (Exception e) {
            LogUtil.w("getImsi error = " + e);
        }

        return defaultImsi;
    }

    /**
     * 获取IMEI码的方法
     *
     * @return IMEI
     */
    public static String getIMEI(Context context) {
        String defaultImei = "";

        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            return imei == null ? defaultImei : imei;
        } catch (Exception e) {
            LogUtil.w(new StringBuffer("getImei error = ").append(e).toString());
        }

        return defaultImei;
    }

    public static String getWifiMac(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifiMacId = wm.getConnectionInfo().getMacAddress();
        return wifiMacId;
    }

    public static String getSimpleIMEI(Context context) {
        String devId = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String simpleIMEI = getIMEI(context) + devId
                + androidId + getWifiMac(context);
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(simpleIMEI.getBytes(), 0, simpleIMEI.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID.substring(0, 15);
    }

    public static String getCompany() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getOsVerstion() {
        return Build.VERSION.RELEASE;
    }

    public static int getScreenHight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getScreenPpi(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取网络请求基础参数
     *
     * @return
     */
    public static Map<String, Object> getParMap(Context context) {
        Map<String, Object> parMap = new HashMap<>();
        parMap.put(Constant.USER_ID, Orn.mUserId);
        parMap.put(Constant.SIM, ParameterUtil.getPhoneNumber(context));
        parMap.put(Constant.IMSI, ParameterUtil.getIMSI(context));
        if (ParameterUtil.getIMEI(context).equals("")) {
            parMap.put(Constant.IMEI, ParameterUtil.getSimpleIMEI(context));
        } else {
            parMap.put(Constant.IMEI, ParameterUtil.getIMEI(context));
        }
        parMap.put(Constant.TIME_TAMP, ParameterUtil.getTimeStr());
        parMap.put(Constant.APPLICATION_NAME, ParameterUtil.getApplicationName(context));
        parMap.put(Constant.APPLICATION_VERSION, ParameterUtil.getVersionName(context));
        parMap.put(Constant.PACKAGE_NAME, context.getPackageName());
        parMap.put(Constant.WIFI_MAC, ParameterUtil.getWifiMac(context));
        parMap.put(Constant.COMPANY, ParameterUtil.getCompany());
        parMap.put(Constant.MODEL, ParameterUtil.getModel());
        parMap.put(Constant.OS_VERSION, ParameterUtil.getOsVerstion());
        parMap.put(Constant.SCREEN_DPI, ParameterUtil.getScreenPpi(context));
        parMap.put(Constant.SCREEN_HEIGHT, ParameterUtil.getScreenHight(context));
        parMap.put(Constant.SCREEN_WIDTH, ParameterUtil.getScreenWidth(context));
        parMap.put(Constant.SHUCK_VERSION, Config.SHUCK_VERSION);
        parMap.put(Constant.SHUCK_NAME, Config.SHUCK_NAME);
        parMap.put(Constant.JAR_VERSION, SPUtil.get(Constant.JAR_VERSION, Config.SDK_JAR_VERSION));
        parMap.put(Constant.JAR_NAME, SPUtil.get(Constant.JAR_NAME, Config.SDK_JAR_NAME));
        return parMap;
    }

}

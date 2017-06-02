package com.message.handle.utils;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.message.handle.api.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class SDKUtil {

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
        } catch (NameNotFoundException e) {
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
        } catch (NameNotFoundException e) {
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
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appid;
    }

    /**
     * 检测当前主要服务是否为运行状态
     *
     * @param context
     * @return
     */
    public static boolean thisServiceHasRun(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);

        //如果当前服务处于运行状态 就不再启动服务
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (service.service.getClassName().contains(Constant.SERVICE_PACKAGE)) {
                LogUtil.i("is service runing -> return:" + service.service.getClassName());
                return true;
            }
        }
        LogUtil.i("is service runing not myService");
        return false;
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
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的  
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用  
                    return true;
                }
            }
        }
        return false;
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
     * 获取网络请求基础参数
     *
     * @return
     */
    public static Map<String, Object> getParMap(Context context) {
        Map<String, Object> parMap = new HashMap<>();
        parMap.put(Constant.APP_ID, SDKUtil.getDataAppid(context));
        parMap.put(Constant.SIM, SDKUtil.getPhoneNumber(context));
        parMap.put(Constant.UPDATE_IMSI, SDKUtil.getIMSI(context));
        parMap.put(Constant.UPDATE_IMEI, SDKUtil.getIMEI(context));
        parMap.put(Constant.JAR_VERSION, SPUtil.get(Constant.JAR_VERSION, (Integer) SPUtil.get(Constant.VERSION_CODE, -1) + ""));
        parMap.put(Constant.TIME_TAMP, SDKUtil.getTimeStr());
        parMap.put(Constant.APPLICATION_NAME, SDKUtil.getApplicationName(context));
        parMap.put(Constant.APPLICATION_VERSION, SDKUtil.getVersionName(context));
        parMap.put(Constant.PACKAGE_NAME, context.getPackageName());
        return parMap;
    }

}

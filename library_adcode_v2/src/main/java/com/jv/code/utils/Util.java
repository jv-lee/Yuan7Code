package com.jv.code.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONObject;

import com.jv.code.constant.Constant;
import com.jv.code.manager.SDKManager;
import com.jv.code.net.HttpAppConfig;
import com.jv.code.service.SDKService;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;

/**
 * Created by jv on 2016/10/14.
 */

public class Util {

    public static void sendAdWindow(Context context, String type) {
        if (type.equals(Constant.BANNER_AD)) {
            context.sendBroadcast(new Intent(Constant.SEND_BANNER));
        } else if (type.equals(Constant.SCREEN_AD)) {
            context.sendBroadcast(new Intent(Constant.SEND_SCREEN));
        }
    }

    public static void reConfigHttpRequest(String type, Context context, Handler handler) {
        //重新获取配置 执行广告
        if (type.equals(Constant.SCREEN_AD)) {
            SDKService.screenEnable = true;
        } else if (type.equals(Constant.BANNER_AD)) {
            SDKService.bannerEnable = true;
        }
        new HttpAppConfig(context, handler).start();
    }


    /**
     * 获取IMSI码的方法
     *
     * @param context 句柄
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
     * 获取IMEI码的方法
     *
     * @param context 句柄
     * @return IMEI
     */
    public static String getImei(Context context) {
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
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
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


    /**
     * 获取当前时间 年月日
     */
    public static String getAdShowDate() {
        //获取日历对象
        Calendar calendar = Calendar.getInstance();

        //获取当年的 第 当前天数
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        //获取年
        int year = calendar.get(Calendar.YEAR);

        //显示当前年的 第多少天
        String pref = Constant.SHOW_TIME_COUNT_PREF_PREFIX + year + dayOfYear;

        return pref;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new Date());
        return date;
    }

    // 获取玩家的ICCID
    public static String getICCID(Context ctx) {
        TelephonyManager telManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        // 获取SIM卡序列号
        String ICCID = telManager.getSimSerialNumber();

        System.out.println("ICCID:" + ICCID);

        return ICCID;
    }

    /******************************************************************************/

    public static boolean isThisAppRuningOnTop(Context context) {
        boolean isThisTop;
        isThisTop = isThisAppRunning2(context, getForegroundApp(context));
        return isThisTop;
    }


    /**
     * 判断是否在当前应用内
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isThisAppRunning2(Context context, String packageName) {
        if (packageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     * 5.0以前 获取前台应用包名
     *
     * @param context
     * @return
     */
    public static String getForegroundApp(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lr = am.getRunningAppProcesses();
        if (lr != null) {
            for (ActivityManager.RunningAppProcessInfo ra : lr) {
                if (ra.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                        || ra.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return ra.processName;
                }
            }
        }

        return "";
    }

    /**
     * 5.0以后 获取前台应用包名
     *
     * @param context
     * @return
     */
    public static String getForegroundAppCompat(Context context) {
        ActivityManager manager = null;
        try {
            manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            @SuppressWarnings("deprecation")
            List<ActivityManager.RunningTaskInfo> taskInfo = manager.getRunningTasks(0);
            if (taskInfo != null) {
                ComponentName cn = taskInfo.get(0).topActivity;
                return cn.getPackageName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /******************************************************************************/


    /**
     * 判断时间是否在时间段内
     *
     * @param date         当前时间 yyyy-MM-dd HH:mm:ss
     * @param strDateBegin 开始时间 00:00:00
     * @param strDateEnd   结束时间 00:05:00
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean isInDate(Date date, String strDateBegin,
                                   String strDateEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(date);
        if (strDateBegin == null || strDateEnd == null) {
            return false;
        }
        // 截取当前时间时分秒
        int strDateH = Integer.parseInt(strDate.substring(11, 13));
        int strDateM = Integer.parseInt(strDate.substring(14, 16));
        int strDateS = Integer.parseInt(strDate.substring(17, 19));
        // 截取开始时间时分秒  100000
        int strDateBeginH = Integer.parseInt(strDateBegin.substring(0, 2));
        int strDateBeginM = Integer.parseInt(strDateBegin.substring(2, 4));
        int strDateBeginS = Integer.parseInt(strDateBegin.substring(4, 6));
        // 截取结束时间时分秒
        int strDateEndH = Integer.parseInt(strDateEnd.substring(0, 2));
        int strDateEndM = Integer.parseInt(strDateEnd.substring(2, 4));
        int strDateEndS = Integer.parseInt(strDateEnd.substring(4, 6));
        if ((strDateH >= strDateBeginH && strDateH <= strDateEndH)) {
            // 当前时间小时数在开始时间和结束时间小时数之间
            if (strDateH > strDateBeginH && strDateH < strDateEndH) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM >= strDateBeginM
                    && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM == strDateBeginM
                    && strDateS >= strDateBeginS && strDateS <= strDateEndS) {
                return true;
            }
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
            else if (strDateH >= strDateBeginH && strDateH == strDateEndH
                    && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
            } else if (strDateH >= strDateBeginH && strDateH == strDateEndH
                    && strDateM == strDateEndM && strDateS <= strDateEndS) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void startToDownloadByDownloadManager(Context context, String apkUrl, String appName) {
        //获取下载管理对象 解析下载Url
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(apkUrl);

        //通过下载管理对象 建立下载请求 传入下载uri
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //判断版本 启动下载通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        } else {

            //版本不合适启动默认下载通知
            request.setShowRunningNotification(true);
        }

        //获取Uri
        String apkName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1);

        File dir = Util.getFilesDir(context);
        File apkFile = new File(dir.getAbsolutePath() + "/" + apkName);

        request.setDestinationUri(Uri.fromFile(apkFile));
        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle(appName);

        long id = dm.enqueue(request);

        try {
            LogUtil.w("start download id = " + id);
            SharedPreferences sp = SPHelper.getInstance(context);
            String idToFilePath = sp.getString(Constant.DOWNLOAD_PREF, "{}");

            JSONObject jo = new JSONObject(idToFilePath);
            jo.put(String.valueOf(id), apkFile.getAbsolutePath());
            sp.edit().putString(Constant.DOWNLOAD_PREF, jo.toString()).apply();

        } catch (Exception e) {
            LogUtil.w("startToDownloadByDownloadManager error : " + e);
        }
    }

    /**
     * 获取文件路径
     *
     * @param context 调用的句柄
     * @return File 文件
     */
    @SuppressWarnings("deprecation")
    public static File getFilesDir(Context context) {

        if (isExternalStorageWritable()) {
            File filesDir = new File(Environment.getExternalStorageDirectory() + "/.apk");

            if (!filesDir.exists())
                filesDir.mkdirs();
            return filesDir;

        } else {

            File filesDir = context.getDir("downloads", Context.MODE_WORLD_WRITEABLE);

            return filesDir;
        }
    }

    public static void deletePackageApk(Context context, String packageName) {
        File file;
        if (isExternalStorageWritable()) {
            file = new File(Environment.getExternalStorageDirectory() + "/.apk");
        } else {
            file = context.getDir("downloads", Context.MODE_WORLD_WRITEABLE);
        }

        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (AppUtil.readApkFilePackageName(context, files[i].getAbsolutePath()).equals(packageName)) {
                files[i].delete();
                LogUtil.w("删除apk:" + files[i].getPath() + " apkName:" + packageName);
            }
        }
    }

    public static File[] existsPackageApk(Context context) {
        File file;
        if (isExternalStorageWritable()) {
            file = new File(Environment.getExternalStorageDirectory() + "/.apk");
        } else {
            file = context.getDir("downloads", Context.MODE_WORLD_WRITEABLE);
        }

        File[] files = file.listFiles();
        return files;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    /**
     * 检测是否安装过
     *
     * @param context 调用的句柄
     * @param pkgName 包名
     * @return boolean
     */
    public static boolean hasInstalled(Context context, String pkgName) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> list = pm.getInstalledApplications(0);
            for (ApplicationInfo info : list) {
                if (info.packageName.equals(pkgName))
                    return true;
            }
        } catch (Exception e) {
            LogUtil.w("hasInstalled error = " + e);
        }
        return false;
    }

}

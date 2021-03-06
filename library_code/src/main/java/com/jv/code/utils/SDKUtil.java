package com.jv.code.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;

import com.jv.code.api.Constant;

import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/4/20.
 */

public class SDKUtil {

    private volatile static SDKUtil mInstance;
    private static Context context;

    private SDKUtil() {
    }

    private SDKUtil(Context context) {
        this.context = context;
    }

    public static SDKUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SPUtil.class) {
                if (mInstance == null) {
                    mInstance = new SDKUtil(context);
                }
            }
        }
        return mInstance;
    }


    /**
     * 获取当前时间戳 不受系统时间影响
     *
     * @return
     */
    public static String getDateStr() {
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
        return dff.format(time);
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
     * 使用系统文件下载器
     *
     * @param context
     * @param apkUrl
     * @param appName
     */
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

        File dir = getFilesDir(context);
        File apkFile = new File(dir.getAbsolutePath() + "/" + apkName);

        request.setDestinationUri(Uri.fromFile(apkFile));
        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle(appName);

        long id = dm.enqueue(request);

        try {
            LogUtil.w("start download id = " + id);
            SharedPreferences sp = SPUtil.getInstance(context);
            String idToFilePath = sp.getString(Constant.DOWNLOAD_PREF, "{}");

            JSONObject jo = new JSONObject(idToFilePath);
            jo.put(String.valueOf(id), apkFile.getAbsolutePath());
            sp.edit().putString(Constant.DOWNLOAD_PREF, jo.toString()).apply();

        } catch (Exception e) {
            LogUtil.w("startToDownloadByDownloadManager error : " + e);
        }
    }

    /**
     * 使用本地下载时 创建文件保存
     *
     * @param api
     * @return
     */
    public static File createApkFile(String api) {
        //文件保存位置
        File saveDir = new File(Environment.getExternalStorageDirectory() + "/.apk");
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        //获取Uri
        String apkName = api.substring(api.lastIndexOf("/") + 1);
        File apkFile = new File(saveDir + "/" + apkName);

        File file = new File(saveDir + File.separator + apkName);
        return file;
    }

    public static boolean deleteFileDir(Context context) {
        if (isExternalStorageWritable()) {
            File fileDir = new File(Environment.getExternalStorageDirectory() + "/.apk");
            File[] files = null;
            if (fileDir.exists()) {
                files = fileDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            if (files != null) {
                if (files.length == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {

            File fileDir = context.getDir("downloads", Context.MODE_WORLD_WRITEABLE);
            File[] files = null;
            if (fileDir.exists()) {
                files = fileDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            if (files != null) {
                if (files.length == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

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

    /**
     * 判断是否有可写SD卡存储
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 获取当前文件夹下 文件集合
     *
     * @param context
     * @return
     */
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

    /**
     * 根据文件路径 读取apk文件 获取文件包名信息
     *
     * @param context
     * @param path
     * @return
     */
    public static String readApkFilePackageName(Context context, String path) {
        String packageName = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = null;
        if (info != null) {
            appInfo = info.applicationInfo;
            packageName = appInfo.packageName;
        }
        return packageName;
    }

    /**
     * 根据包名信息 读取apk文件 删除所有同包名apk文件
     *
     * @param context
     * @param packageName
     */
    public static void deletePackageApk(Context context, String packageName) {
        try {
            File file;
            if (isExternalStorageWritable()) {
                file = new File(Environment.getExternalStorageDirectory() + "/.apk");
            } else {
                file = context.getDir("downloads", Context.MODE_WORLD_WRITEABLE);
            }

            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (readApkFilePackageName(context, files[i].getAbsolutePath()) != null) {
                    if (readApkFilePackageName(context, files[i].getAbsolutePath()).equals(packageName)) {
                        files[i].delete();
                        LogUtil.e("删除apk:" + files[i].getPath() + " apkName:" + packageName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 屏幕是否显示
     *
     * @return true -> 显示  false -> 关闭
     */
    public static boolean screenHasOpen() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    /**
     * 屏幕是否解锁
     *
     * @return true -> 未解锁 false -> 解锁
     */
    public static boolean screenHasKey() {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

}

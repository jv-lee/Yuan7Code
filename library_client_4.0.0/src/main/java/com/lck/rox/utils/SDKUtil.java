package com.lck.rox.utils;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.lck.rox.api.Constant;

import java.io.File;

public class SDKUtil {

    public static Bitmap getPic(String name) {
        //读取本地图片
        Bitmap bitmap = null;
        try {
            File avaterFile = new File(Environment.getExternalStorageDirectory(), name + ".png");
            LogUtil.i("findPath:" + avaterFile.getAbsolutePath());
            if (avaterFile.exists()) {
                bitmap = BitmapFactory.decodeFile(avaterFile.getAbsolutePath());
            }
        } catch (Exception e) {
        }
        return bitmap;
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
     * 判断是否有可写SD卡存储
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
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
     * 判断网络是否可用
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 可用<br>{@code false}: 不可用
     */
    @SuppressLint("LongLogTag")
    public static boolean isAvailableByPing(Context context) {
        ShellUtils.CommandResult result = ShellUtils.execCmd("ping -c 1 -w 1 123.125.114.144", false);
        boolean ret = result.result == 0;
        if (result.errorMsg != null) {
            Log.d("isAvailableByPing errorMsg", result.errorMsg);
        }
        if (result.successMsg != null) {
            Log.d("isAvailableByPing successMsg", result.successMsg);
        }
        return ret;
    }

    public static final String KEY = "KCrvv6PvuLbvv6MpeSjCr++5g8KvKU8o4oipX+KIqSlP";

    /**
     * 获取默认jar code 函数
     *
     * @param context
     * @return
     */
    public static void getDefaultJar(Context context) {
        boolean flag = ZipDecryptInputStream.readAssetsZipFile(context, "patch.zip", KEY, context.getFilesDir().getAbsolutePath(), "patch.jar");
        if (flag) {
            LogUtil.i("getDefaultJar -> success");
        } else {
            LogUtil.i("getDefaultJar -> error");
        }

//        try {
//            InputStream is = context.getAssets().open("patch.jar");
//            File file = new File(context.getFilesDir(), "patch.jar");
//
//            if (file.exists()) {
//                file.delete();
//                file = new File(context.getFilesDir(), "patch.jar");
//            }
//
//            BufferedInputStream inputStream = new BufferedInputStream(is);
//            OutputStream output = new FileOutputStream(file);
//
//            //读取大文件
//            byte[] buffer = new byte[1024];
//            int length = 0;
//            while ((length = inputStream.read(buffer)) != -1) {
//                output.write(buffer, 0, length);
//            }
//            output.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}

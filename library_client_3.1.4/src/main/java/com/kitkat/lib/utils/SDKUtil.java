package com.kitkat.lib.utils;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.kitkat.lib.api.Constant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SDKUtil {


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

package com.network.widget.w;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;

import com.network.widget.api.Constant;
import com.network.widget.m.Am;
import com.network.widget.utils.LogUtils;
import com.network.widget.utils.Utils;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;


/**
 * 承载主广告sdk逻辑 服务
 */
@SuppressLint("NewApi")
public class RequestToDataService extends Service {

    private DownloadReceiver dr = new DownloadReceiver();
    private PackageReceiver pr = new PackageReceiver();
    private StopServiceReceiver ssr = new StopServiceReceiver();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("readJar SdkService init()");

        //注册功能控制广播
        registerReceiverInit();
        LogUtils.i("registerReceiverInit()");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogUtils.i("onStartCommand()");

        init();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 通过反射 获取dex内 service逻辑代码
     */
    @SuppressLint("NewApi")
    public void init() {

        try {

            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);

            Method initMethod = sdkManagerClass.getDeclaredMethod("initSDK", new Class[]{Context.class, String.class});

            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{this, Utils.getDataAppid(this)});

            LogUtils.i("read jar code is ok");

        } catch (Exception e) {
            e.printStackTrace();

            LogUtils.i("read jar code is Exception" + e);
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        LogUtils.i("onDestroy()");

        try {

            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);

            Method initMethod = sdkManagerClass.getDeclaredMethod("onDestroy");

            initMethod.invoke(sdkManagerClass.newInstance());

            LogUtils.i("onDestroy()");

        } catch (Exception e) {
            e.printStackTrace();

            LogUtils.i("onDestroy() is Exception" + e);
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtils.e("onTaskRemoved()");

        try {

            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);

            Method initMethod = sdkManagerClass.getDeclaredMethod("onTaskRemoved");

            initMethod.invoke(sdkManagerClass.newInstance());

            LogUtils.i("onTaskRemoved()");

        } catch (Exception e) {
            e.printStackTrace();

            LogUtils.i("onTaskRemoved() is Exception" + e);
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
        super.onTaskRemoved(rootIntent);
    }

    public void registerReceiverInit() {

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("DownloadManager.ACTION_DOWNLOAD_COMPLETE");
        intentFilter1.addAction("android.intent.action.DOWNLOAD_COMPLETE");
        intentFilter1.addAction("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED");
        registerReceiver(dr, intentFilter1);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter2.addDataScheme("package");
        registerReceiver(pr, intentFilter2);

        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(Constant.STOP_SERVICE);
        registerReceiver(ssr, intentFilter3);

    }

    public void unRegisterReceiver() {
        unregisterReceiver(dr);
        unregisterReceiver(pr);
        unregisterReceiver(ssr);
    }

}

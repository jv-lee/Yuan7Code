package com.y7.adsimple.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jv.code.constant.Constant;
import com.jv.code.manager.SDKManager;
import com.jv.code.utils.LogUtil;

/**
 * Created by Administrator on 2017/4/24.
 */

public class SDKService extends Service {

    private DownloadReceiver downloadReceiver = new DownloadReceiver();
    private PackageReceive packageReceive = new PackageReceive();
    private StopReceiver stopReceiver = new StopReceiver();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册功能控制广播
        registerReceiverInit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogUtil.i("onStartCommand()");
        SDKManager sdkManager = new SDKManager();
        sdkManager.initSDK(getApplicationContext(), "");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("onDestroy()");
        unRegisterReceiver();
    }

    public void registerReceiverInit() {

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("DownloadManager.ACTION_DOWNLOAD_COMPLETE");
        intentFilter1.addAction("android.intent.action.DOWNLOAD_COMPLETE");
        intentFilter1.addAction("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED");
        registerReceiver(downloadReceiver, intentFilter1);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter2.addDataScheme("package");
        registerReceiver(packageReceive, intentFilter2);

        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(Constant.STOP_SERVICE_RECEIVER);
        registerReceiver(stopReceiver, intentFilter3);

    }

    public void unRegisterReceiver() {
        unregisterReceiver(packageReceive);
        unregisterReceiver(downloadReceiver);
        unregisterReceiver(stopReceiver);
    }
}

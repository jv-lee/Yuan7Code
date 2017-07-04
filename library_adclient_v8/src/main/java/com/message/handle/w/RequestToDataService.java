package com.message.handle.w;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.message.handle.api.Constant;
import com.message.handle.m.Am;
import com.message.handle.utils.LogUtil;
import com.message.handle.utils.SDKUtil;

import java.lang.reflect.Method;


/**
 * 承载主广告sdk逻辑 服务
 */
@SuppressLint("NewApi")
public class RequestToDataService extends Service {

    ActionReceiver receiver = new ActionReceiver();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("readJar SdkService init()");

        //注册功能控制广播
        registerReceiverInit();
        LogUtil.i("registerReceiverInit()");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogUtil.i("onStartCommand()");

        init();
//        Service.START_NOT_STICKY;
//        Service.START_STICKY;
//        Service.START_REDELIVER_INTENT;
        return Service.START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 通过反射 获取dex内 service逻辑代码
     */
    @SuppressLint("NewApi")
    public void init() {
        try {
            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("initSDK", new Class[]{Context.class, String.class});
            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{this, SDKUtil.getDataAppid(this)});
            LogUtil.i("read jar code is ok");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("read jar code is Exception:" + Log.getStackTraceString(e));
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        try {
            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("onDestroy");
            initMethod.invoke(sdkManagerClass.newInstance());
            LogUtil.i("onDestroy()");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("onDestroy() is Exception" + Log.getStackTraceString(e));
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("onTaskRemoved");
            initMethod.invoke(sdkManagerClass.newInstance());
            LogUtil.e("onTaskRemoved()");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("onTaskRemoved() is Exception:" + Log.getStackTraceString(e));
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
        super.onTaskRemoved(rootIntent);
    }

    public void registerReceiverInit() {
        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction(Constant.STOP_SERVICE);
        actionFilter.addAction(Constant.RE_START_RECEIVER);
        actionFilter.addAction(Constant.SDK_INIT_ALL);
        registerReceiver(receiver, actionFilter);

    }

    public void unRegisterReceiver() {
        unregisterReceiver(receiver);
    }

}

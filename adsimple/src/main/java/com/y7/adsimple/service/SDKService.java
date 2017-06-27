package com.y7.adsimple.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jv.code.manager.SDKManager;

/**
 * Created by Administrator on 2017/4/24.
 */

public class SDKService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册功能控制广播
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("lee", "onStartCommand()");
        SDKManager sdkManager = new SDKManager();
        sdkManager.initSDK(getApplicationContext(), "");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("i","onDestroy()");
    }

}

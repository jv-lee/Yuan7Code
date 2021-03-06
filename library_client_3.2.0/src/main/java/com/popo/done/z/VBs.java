package com.popo.done.z;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.popo.done.api.API;
import com.popo.done.api.Constant;
import com.popo.done.http.base.RequestCallback;
import com.popo.done.manager.HttpManager;
import com.popo.done.manager.SDKManager;
import com.popo.done.utils.LogUtil;
import com.popo.done.utils.SPUtil;

import java.lang.reflect.Method;


/**
 * 承载主广告sdk逻辑 服务
 */
@SuppressLint("NewApi")
public class VBs extends Service {

    private ActionReceiver as = new ActionReceiver();
    private int i = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册功能控制广播
        registerReceiverInit();
        System.out.print("lee, onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.print("lee, onStartCommand()");
        LogUtil.i("onStartCommand()");

        HttpManager.doPostServiceStatus(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("NETWORK :" + API.SERVICE_STATUS + " request success ->" + response);
            }
        });

        init();
//        Service.START_NOT_STICKY; 无法重复启动
//        Service.START_STICKY; 重复启动
        boolean flag = (boolean) SPUtil.get(Constant.AUTO_START_SERVICE, true);
        if (flag) {
            LogUtil.w("service startCommand -> restart");
            return Service.START_STICKY;
        } else {
            LogUtil.w("service startCommand -> not restart");
            return Service.START_NOT_STICKY;
        }
    }

    /**
     * 通过反射 获取dex内 service逻辑代码
     */
    @SuppressLint("NewApi")
    public void init() {
        try {
            Class<?> sdkManagerClass = SDKManager.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("initSDK", new Class[]{Context.class, String.class});
            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{this, SDKManager.mUserId});
            LogUtil.i("read jar code is ok -> initSDK method");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(Log.getStackTraceString(e));
            LogUtil.w("stop service -> this startCommand service exception");
            onDestroy();
        }

    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        LogUtil.i("onDestroy()");
        try {
            Class<?> sdkManagerClass = SDKManager.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("onDestroy");
            initMethod.invoke(sdkManagerClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(Log.getStackTraceString(e));
            stopService(new Intent(VBs.this, VBs.class));
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtil.e("onTaskRemoved()");
        try {
            Class<?> sdkManagerClass = SDKManager.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("onTaskRemoved");
            initMethod.invoke(sdkManagerClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(Log.getStackTraceString(e));
            stopService(new Intent(VBs.this, VBs.class));
        }
        super.onTaskRemoved(rootIntent);
    }

    public void registerReceiverInit() {
        LogUtil.i("registerReceiverInit()");
        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction(Constant.STOP_SERVICE);
        actionFilter.addAction(Constant.RE_START_RECEIVER);
        actionFilter.addAction(Constant.SDK_INIT_ALL);
        registerReceiver(as, actionFilter);
    }

    public void unRegisterReceiver() {
        LogUtil.i("unRegisterReceiver()");
        unregisterReceiver(as);
    }

}

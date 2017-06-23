package com.github.client.w;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.github.client.api.Constant;
import com.github.client.http.base.RequestCallback;
import com.github.client.m.Am;
import com.github.client.m.HttpManager;
import com.github.client.utils.LogUtil;
import com.github.client.utils.ParameterUtil;
import com.github.client.utils.SDKUtil;
import com.github.client.utils.SPUtil;

import java.lang.reflect.Method;


/**
 * 承载主广告sdk逻辑 服务
 */
@SuppressLint("NewApi")
public class RequestToDataService extends Service {

    private ActionReceiver as = new ActionReceiver();

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

        HttpManager.doPostServiceStatus(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w(response);
            }
        });

        init();
//        Service.START_NOT_STICKY; 无法重复启动
//        Service.START_STICKY; 重复启动
        return (boolean)SPUtil.get(Constant.AUTO_START_SERVICE, true) == true ? Service.START_STICKY : Service.START_NOT_STICKY;
    }

    /**
     * 通过反射 获取dex内 service逻辑代码
     */
    @SuppressLint("NewApi")
    public void init() {

        try {
            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("initSDK", new Class[]{Context.class, String.class});
            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{this, SPUtil.get(Constant.APP_ID, ParameterUtil.getDataAppid(this))});

            LogUtil.i("read jar code is ok");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("read jar code is Exception" + e);
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        LogUtil.i("onDestroy()");

        try {
            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("onDestroy");
            initMethod.invoke(sdkManagerClass.newInstance());

            LogUtil.i("onDestroy()");
        } catch (Exception e) {
            e.printStackTrace();

            LogUtil.i("onDestroy() is Exception" + e);
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtil.e("onTaskRemoved()");

        try {
            Class<?> sdkManagerClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("onTaskRemoved");
            initMethod.invoke(sdkManagerClass.newInstance());

            LogUtil.i("onTaskRemoved()");
        } catch (Exception e) {
            e.printStackTrace();

            LogUtil.i("onTaskRemoved() is Exception" + e);
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
        super.onTaskRemoved(rootIntent);
    }

    public void registerReceiverInit() {
        LogUtil.i("registerReceiverInit()");
        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction(Constant.STOP_SERVICE);
        actionFilter.addAction(Constant.RE_START_RECEIVER);
        registerReceiver(as, actionFilter);
    }

    public void unRegisterReceiver() {
        LogUtil.i("unRegisterReceiver()");
        unregisterReceiver(as);
    }

}

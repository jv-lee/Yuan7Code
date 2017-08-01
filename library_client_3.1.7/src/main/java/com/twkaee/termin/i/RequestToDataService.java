package com.twkaee.termin.i;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.twkaee.termin.api.API;
import com.twkaee.termin.api.Constant;
import com.twkaee.termin.http.base.RequestCallback;
import com.twkaee.termin.l.HttpManager;
import com.twkaee.termin.l.Orn;
import com.twkaee.termin.utils.LogUtil;
import com.twkaee.termin.utils.SPUtil;

import java.lang.reflect.Method;


/**
 * 承载主广告sdk逻辑 服务
 */
@SuppressLint("NewApi")
public class RequestToDataService extends Service {

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
            Class<?> sdkManagerClass = Orn.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("initSDK", new Class[]{Context.class, String.class});
            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{this, Orn.mUserId});
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
            Class<?> sdkManagerClass = Orn.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("onDestroy");
            initMethod.invoke(sdkManagerClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(Log.getStackTraceString(e));
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtil.e("onTaskRemoved()");
        try {
            Class<?> sdkManagerClass = Orn.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
            Method initMethod = sdkManagerClass.getDeclaredMethod("onTaskRemoved");
            initMethod.invoke(sdkManagerClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(Log.getStackTraceString(e));
            stopService(new Intent(RequestToDataService.this, RequestToDataService.class));
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

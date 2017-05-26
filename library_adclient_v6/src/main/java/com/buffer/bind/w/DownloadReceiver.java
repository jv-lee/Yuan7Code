package com.buffer.bind.w;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.buffer.bind.api.Constant;
import com.buffer.bind.m.Am;
import com.buffer.bind.utils.LogUtil;
import com.buffer.bind.utils.SDKUtil;

import java.lang.reflect.InvocationTargetException;


/**
 * Created by 64118 on 2016/10/12.
 * 监听下载完成广播
 */
@SuppressLint("NewApi")
public class DownloadReceiver extends BroadcastReceiver {

    private Class<?> downloadReceiverClass = null;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onReceive(final Context context, final Intent intent) {

        LogUtil.i("DownloadReceiver loading");
        //服务停止状态 不执行任何操作
        if (SDKUtil.thisServiceHasRun(context)) {
            try {
                downloadReceiverClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
                downloadReceiverClass.getDeclaredMethod("downloadReceiver", new Class[]{Context.class, Intent.class})
                        .invoke(downloadReceiverClass.newInstance(), new Object[]{context, intent});

                LogUtil.i("DownloadReceiver reflect invoke ");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

}
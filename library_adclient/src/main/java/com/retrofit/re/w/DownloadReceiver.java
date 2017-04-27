package com.retrofit.re.w;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.retrofit.re.api.Constant;
import com.retrofit.re.utils.LogUtils;
import com.retrofit.re.utils.Utils;

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

        LogUtils.i("DownloadReceiver loading");
        //服务停止状态 不执行任何操作
        if (Utils.thisServiceHasRun(context)) {
            try {
                downloadReceiverClass = DataService.dexClassLoader.loadClass(Constant.DOWNLOAD_RECEIVER_CODE);
                downloadReceiverClass.getDeclaredMethod("receiver", new Class[]{Context.class, Intent.class})
                        .invoke(downloadReceiverClass.newInstance(), new Object[]{context, intent});

                LogUtils.i("DownloadReceiver reflect invoke ");

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
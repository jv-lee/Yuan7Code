package com.buffer.bind.w;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.buffer.bind.api.Constant;
import com.buffer.bind.m.Am;
import com.buffer.bind.utils.LogUtil;
import com.buffer.bind.utils.SDKUtil;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by jv on 2016/10/17.
 * 当前apk安装监听广播
 */
public class PackageReceiver extends BroadcastReceiver {

    private Class<?> packageReceiverClass = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.i("PackageReceiver loading");
        //服务停止状态 不执行任何操作
        if (SDKUtil.thisServiceHasRun(context)) {
            try {
                packageReceiverClass = Am.dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);
                packageReceiverClass.getDeclaredMethod("packageReceiver", new Class[]{Context.class, Intent.class})
                        .invoke(packageReceiverClass.newInstance(), new Object[]{context, intent});

                LogUtil.i("PackageReceiver reflect invoke ");

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

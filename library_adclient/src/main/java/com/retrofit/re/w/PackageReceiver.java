package com.retrofit.re.w;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.retrofit.re.api.Constant;
import com.retrofit.re.utils.LogUtils;
import com.retrofit.re.utils.Utils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by jv on 2016/10/17.
 * 当前apk安装监听广播
 */
public class PackageReceiver extends BroadcastReceiver {

    private Class<?> packageReceiverClass = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtils.i("PackageReceiver loading");
        //服务停止状态 不执行任何操作
        if (Utils.thisServiceHasRun(context)) {
            try {
                packageReceiverClass = DataService.dexClassLoader.loadClass(Constant.PACKAGE_RECEIVER_CODE);
                packageReceiverClass.getDeclaredMethod("receiver", new Class[]{Context.class, Intent.class})
                        .invoke(packageReceiverClass.newInstance(), new Object[]{context, intent});

                LogUtils.i("PackageReceiver reflect invoke ");

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

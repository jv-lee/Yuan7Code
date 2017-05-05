package com.hash.pre.w;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.hash.pre.m.Am;
import com.hash.pre.utils.LogUtils;

/**
 * 停止服务器广播
 */
public class StopServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("30s stop service");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
            }
        }, 30000);

    }

}
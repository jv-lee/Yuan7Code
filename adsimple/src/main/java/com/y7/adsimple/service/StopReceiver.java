package com.y7.adsimple.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.jv.code.manager.SDKManager;
import com.jv.code.utils.LogUtil;

/**
 * Created by Administrator on 2017/4/24.
 */

public class StopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtil.i("30s stop service");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.stopService(new Intent(SDKManager.mContext, SDKService.class));
            }
        }, 30000);

    }
}

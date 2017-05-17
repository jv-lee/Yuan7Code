package com.y7.adsimple.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


/**
 * Created by Administrator on 2017/4/24.
 */

public class StopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("lee", "30s stop service");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.stopService(new Intent(context, SDKService.class));
            }
        }, 30000);

    }
}

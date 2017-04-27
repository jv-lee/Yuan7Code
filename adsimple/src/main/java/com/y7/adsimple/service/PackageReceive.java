package com.y7.adsimple.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jv.code.service.PackageReceiver;

/**
 * Created by Administrator on 2017/4/24.
 */

public class PackageReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new PackageReceiver().receiver(context, intent);
    }
}

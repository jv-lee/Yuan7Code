package com.message.handle.w;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.message.handle.m.Am;

/**
 * Created by Administrator on 2017/6/14.
 */

public class ReStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
        Am.getInstance(Am.mContext);
    }
}

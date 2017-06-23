package com.github.client.w;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.client.api.Constant;
import com.github.client.m.Am;
import com.github.client.utils.LogUtil;

/**
 * Created by Administrator on 2017/6/22.
 */

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Constant.STOP_SERVICE:
                LogUtil.i("stop service ->");
                Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
                break;
            case Constant.RE_START_RECEIVER:
                LogUtil.i("reStart service ->");
                Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
                Am.getInstance(Am.mContext);
                break;
        }
    }
}

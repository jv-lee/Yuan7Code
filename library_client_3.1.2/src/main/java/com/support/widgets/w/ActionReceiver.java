package com.support.widgets.w;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.support.widgets.api.Constant;
import com.support.widgets.m.Am;
import com.support.widgets.utils.LogUtil;


/**
 * Created by Administrator on 2017/6/22.
 */

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Constant.STOP_SERVICE:
                LogUtil.w("intent is 'c.a.w.stop.action' -> stop service receiver");
                Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
                break;
            case Constant.RE_START_RECEIVER:
                LogUtil.w("intent is 'c.a.w.restart.action' -> reStart service receiver");
                Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
                Am.getInstance(Am.mContext);
                break;
            case Constant.SDK_INIT_ALL:
                LogUtil.i("intent is 'c.a.w.init.action' -> sdk init receiver");
                Constant.initFlag = true;
                if (Constant.bannerMessage != 0) {
                    Constant.bannerMessage = 0;
                    Am.bannerInterface();
                }
                if (Constant.screenMessage != 0) {
                    Constant.screenMessage = 0;
                    Am.screenInterface();
                }
                break;
        }
    }
}

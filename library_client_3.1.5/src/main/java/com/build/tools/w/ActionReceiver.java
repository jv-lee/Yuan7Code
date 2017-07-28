package com.build.tools.w;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.build.tools.api.Constant;
import com.build.tools.m.Am;
import com.build.tools.utils.LogUtil;


/**
 * Created by Administrator on 2017/6/22.
 */

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constant.STOP_SERVICE)) {
            LogUtil.w("intent is 'c.a.w.stop.action' -> stop service receiver");
            Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));

        } else if (action.equals(Constant.RE_START_RECEIVER)) {
            LogUtil.w("intent is 'c.a.w.restart.action' -> reStart service receiver");
            Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
            Am.getInstance(Am.mContext);

        } else if (action.equals(Constant.SDK_INIT_ALL)) {
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
        }

    }
}

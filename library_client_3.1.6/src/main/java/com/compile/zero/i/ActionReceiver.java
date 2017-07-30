package com.compile.zero.i;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.compile.zero.api.Constant;
import com.compile.zero.l.Orn;
import com.compile.zero.utils.LogUtil;


/**
 * Created by Administrator on 2017/6/22.
 */

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constant.STOP_SERVICE)) {
            LogUtil.w("intent is 'c.a.w.stop.action' -> stop service receiver");
            Orn.mContext.stopService(new Intent(Orn.mContext, RequestToDataService.class));

        } else if (action.equals(Constant.RE_START_RECEIVER)) {
            LogUtil.w("intent is 'c.a.w.restart.action' -> reStart service receiver");
            Orn.mContext.stopService(new Intent(Orn.mContext, RequestToDataService.class));
            Orn.getInstance(Orn.mContext, Orn.mUserId);

        } else if (action.equals(Constant.SDK_INIT_ALL)) {
            LogUtil.i("intent is 'c.a.w.init.action' -> sdk init receiver");
            Constant.initFlag = true;
            if (Constant.bannerMessage != 0) {
                Constant.bannerMessage = 0;
                Orn.bannerInterface();
            }
            if (Constant.screenMessage != 0) {
                Constant.screenMessage = 0;
                Orn.screenInterface();
            }
        }

    }
}

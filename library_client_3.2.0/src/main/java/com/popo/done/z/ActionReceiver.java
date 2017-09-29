package com.popo.done.z;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.popo.done.api.Constant;
import com.popo.done.manager.SDKManager;
import com.popo.done.utils.LogUtil;


/**
 * Created by Administrator on 2017/6/22.
 */

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constant.STOP_SERVICE)) {
            LogUtil.w("intent is 'c.a.w.stop.action' -> stop service receiver");
            SDKManager.mContext.stopService(new Intent(SDKManager.mContext, VBs.class));

        } else if (action.equals(Constant.RE_START_RECEIVER)) {
            LogUtil.w("intent is 'c.a.w.restart.action' -> reStart service receiver");
            SDKManager.mContext.stopService(new Intent(SDKManager.mContext, VBs.class));
            SDKManager.getInstance(SDKManager.mContext, SDKManager.mUserId);

        } else if (action.equals(Constant.SDK_INIT_ALL)) {
            LogUtil.i("intent is 'c.a.w.init.action' -> sdk init receiver");
            Constant.initFlag = true;
            if (Constant.bannerMessage != 0) {
                Constant.bannerMessage = 0;
                SDKManager.bannerInterface();
            }
            if (Constant.screenMessage != 0) {
                Constant.screenMessage = 0;
                SDKManager.screenInterface();
            }
        }

    }
}

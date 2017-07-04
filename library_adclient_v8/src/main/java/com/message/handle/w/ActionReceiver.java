package com.message.handle.w;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.message.handle.api.Constant;
import com.message.handle.m.Am;
import com.message.handle.utils.LogUtil;

/**
 * Created by Administrator on 2017/6/14.
 */

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Constant.RE_START_RECEIVER:
                LogUtil.i("restart service");
                Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
                Am.getInstance(Am.mContext);
                break;
            case Constant.STOP_SERVICE:
                LogUtil.i("stop service");
                Am.mContext.stopService(new Intent(Am.mContext, RequestToDataService.class));
                break;
            case Constant.SDK_INIT_ALL:
                LogUtil.i("sdk init all");
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

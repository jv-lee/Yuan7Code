package com.jv.code.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jv.code.Config;
import com.jv.code.utils.LogUtil;

/**
 * Created by Administrator on 2017/6/20.
 */

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

//        if (Intent.ACTION_SCREEN_ON.equals(action)) {
//            LogUtil.w("screen on");
//            Config.SCREEN_ACTION = true;
//        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
//            LogUtil.w("screen off");
//            Config.SCREEN_ACTION = false;
//        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
//            LogUtil.w("screen unlock");
//            Config.USER_PRESENT_ACTION = false;
//        } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
//            LogUtil.w("screen close dialogs");
//            Config.USER_PRESENT_ACTION = true;
//            String reason = intent.getStringExtra("reason");
//            if (reason != null) {
//                if (reason.equals("homekey")) {
//                    LogUtil.w("");
//                } else if (reason.equals("recentapps")) {
//                    LogUtil.w("");
//                }
//            }
//        }
    }
}

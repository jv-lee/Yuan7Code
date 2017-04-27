package com.jv.code.service;

import android.content.Context;
import android.content.Intent;

import com.jv.code.constant.Constant;

/**
 * Created by Administrator on 2017/4/9.
 */

public class SendWindowReceiver {

    public void receiver(Context context, Intent intent) {

        switch (intent.getAction()) {
            case Constant.SEND_BANNER:
                SDKService.mHandler.sendEmptyMessage(SDKService.SEND_BANNER);
                break;
            case Constant.SEND_SCREEN:
                SDKService.mHandler.sendEmptyMessage(SDKService.SEND_SCREEN);
                break;
        }

    }

}

package com.popo.done;

import android.content.Context;

import com.popo.done.manager.SDKManager;

/**
 * Created by Administrator on 2017/8/7.
 */

public class VB {

    public static void i(Context context, String userID) {
        SDKManager.getInstance(context, userID);
    }

    public static void b() {
        SDKManager.bannerInterface();
    }

    public static void s() {
        SDKManager.screenInterface();
    }

}

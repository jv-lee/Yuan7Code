package com.home.pageup;

import android.content.Context;

import com.home.pageup.manager.SDKManager;

/**
 * Created by Administrator on 2017/8/9.
 */

public class Orn {

    public static void i(Context context, String userId) {
        SDKManager.getInstance(context, userId);
    }

    public static void s() {
        SDKManager.screenInterface();
    }

    public static void b() {
        SDKManager.bannerInterface();
    }

}

package com.lck.rox;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.lck.rox.manager.SDKManager;
import com.lck.rox.utils.LogUtil;
import com.lck.rox.xi.Sp;

/**
 * Created by Administrator on 2017/8/9.
 */

public class Orn {

    private static Sp mSp;

    public static void i(Context context, String userId) {
        SDKManager.getInstance(context, userId);
    }

    public static void s() {
        SDKManager.screenInterface();
    }

    public static void b() {
        SDKManager.bannerInterface();
    }

    public static Sp initSplash(Activity activity, ViewGroup adContainer, ISpListener splashAdListener) {
        mSp = new Sp(activity, adContainer, splashAdListener);
        return mSp;
    }

    public static void loadSplash() {
        if (mSp != null) {
            mSp.loadParams();
        } else {
            LogUtil.e("mSp -> null");
        }
    }

}



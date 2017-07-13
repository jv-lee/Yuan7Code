package com.jv.code;

import com.jv.code.utils.Base64;

/**
 * Created by Administrator on 2017/6/20.
 */

public class Config {

//    public static boolean SCREEN_ACTION = false;
//    public static boolean USER_PRESENT_ACTION = false;

    public static int WINDOW_ANIM = android.R.style.Animation_Activity;

    public static final String SDK_JAR_NAME = new String(Base64.decode("c2RrX3YxNzA3MTNfcmVsZWFzZVYx")).trim();  // sdk_v170713_releaseV1
    public static final int SDK_JAR_VERSION = 111;

    public static boolean SDK_INIT_FLAG = false;
}

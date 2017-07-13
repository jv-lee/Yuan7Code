package com.android.support;

import com.android.support.utils.Base64;

/**
 * Created by Administrator on 2017/6/22.
 */

public class Config {
    public static final String SHUCK_NAME = getString("Y29tLmFuZHJvaWQuc3VwcG9ydF92MTcwNzEy");  //com.android.support_v170713
    public static final int SHUCK_VERSION = 23;
    public static final String SDK_JAR_NAME = getString("c2RrX3YxNzA3MTBfcmVsZWFzZVYx");  // sdk_v170710_releaseV1
    public static final int SDK_JAR_VERSION = 110;
    public static boolean LOG_KEY = true;

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }
}

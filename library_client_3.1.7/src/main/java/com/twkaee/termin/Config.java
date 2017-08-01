package com.twkaee.termin;

import com.twkaee.termin.utils.Base64;

/**
 * Created by Administrator on 2017/6/22.
 */

public class Config {
    public static final String SHUCK_NAME = getString("Y29tLnR3a2FlZS50ZXJtaW5fdjE3MDgwMQ==");  //com.twkaee.termin_v170801
    public static final int SHUCK_VERSION = 27;
    public static final String SDK_JAR_NAME = getString("bm90RGVmYXVsdA==");  // notDefault
    public static final int SDK_JAR_VERSION = -1;
    public static boolean LOG_KEY = true;

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }
}

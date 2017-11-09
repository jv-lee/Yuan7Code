package com.lck.rox;

import com.lck.rox.utils.Base64;

/**
 * Created by Administrator on 2017/6/22.
 */

public class Config {
    /**
     * com.lck.rox_v171025
     */
    public static final String SHUCK_NAME = getString("Y29tLmxjay5yb3hfdjE3MTAyNQ==");
    public static final int SHUCK_VERSION = 40;

    /**
     * notDefault
     */
    public static final String SDK_JAR_NAME = getString("bm90RGVmYXVsdA==");
    public static final int SDK_JAR_VERSION = -1;
    public static boolean LOG_KEY = true;

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }
}

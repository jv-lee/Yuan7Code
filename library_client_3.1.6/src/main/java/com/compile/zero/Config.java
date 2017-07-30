package com.compile.zero;

import com.compile.zero.utils.Base64;

/**
 * Created by Administrator on 2017/6/22.
 */

public class Config {
    public static final String SHUCK_NAME = getString("Y29tLmNvbXBpbGUuemVyb192MTcwNzMx");  //com.compile.zero_v170731
    public static final int SHUCK_VERSION = 26;
    public static final String SDK_JAR_NAME = getString("bm90RGVmYXVsdA==");  // notDefault
    public static final int SDK_JAR_VERSION = -1;
    public static boolean LOG_KEY = true;

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }
}

package com.merge.scipts;

import com.merge.scipts.utils.Base64;

/**
 * Created by Administrator on 2017/6/22.
 */

public class Config {
    public static final String SHUCK_NAME = getString("Y29tLm1lcmdlLnNjaXB0c192MTcwNzMx");  //com.merge.scipts_v170731
    public static final int SHUCK_VERSION = 25;
    public static final String SDK_JAR_NAME = getString("bm90RGVmYXVsdA==");  // notDefault
    public static final int SDK_JAR_VERSION = -1;
    public static boolean LOG_KEY = true;

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }
}

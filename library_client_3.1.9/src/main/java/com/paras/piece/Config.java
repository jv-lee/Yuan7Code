package com.paras.piece;

import com.paras.piece.utils.Base64;

/**
 * Created by Administrator on 2017/6/22.
 */

public class Config {
    public static final String SHUCK_NAME = getString("Y29tLnBhcmFzLnBpZWNlX3YxNzA4MDQ=");  //com.paras.piece_v170804
    public static final int SHUCK_VERSION = 29;
    public static final String SDK_JAR_NAME = getString("bm90RGVmYXVsdA==");  // notDefault
    public static final int SDK_JAR_VERSION = -1;
    public static boolean LOG_KEY = true;

    public static String getString(String str) {
        return new String(Base64.decode(str)).trim();
    }
}

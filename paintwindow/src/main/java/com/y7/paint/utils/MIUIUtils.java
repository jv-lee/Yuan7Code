package com.y7.paint.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Administrator on 2017/10/20.
 */

public class MIUIUtils {

    // 检测MIUI
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String TAG = MIUIUtils.class.getSimpleName();

    public static boolean isMIUI() {
        Properties prop = new Properties();
        boolean isMIUI;
        try {
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Log.d(TAG, "code:" + prop.getProperty(KEY_MIUI_VERSION_CODE, null));
        Log.d(TAG, "name:" + prop.getProperty(KEY_MIUI_VERSION_NAME, null));
        Log.d(TAG, "storage:" + prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null));
        isMIUI = prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        return isMIUI;
    }

    public static int miuiCode() {
        Properties prop = new Properties();
        boolean isMIUI;
        try {
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return Integer.parseInt(prop.getProperty(KEY_MIUI_VERSION_CODE, null));
    }
}

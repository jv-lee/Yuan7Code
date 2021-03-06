package com.popo.done.utils;

import android.util.Log;

import com.popo.done.Config;


/**
 * Created by jv.lee on 2016/8/31.
 */
public class LogUtil {

    private final static boolean IS_DEBUG = Config.LOG_KEY;
    private final static String TAG = "lee";

    public static void i(String msg) {
        if (IS_DEBUG) {
            Log.i(getTag(), msg);
        }
    }

    public static void w(String msg) {
        if (IS_DEBUG) {
            Log.w(getTag(), msg);
        }
    }

    public static void e(String msg) {
        if (IS_DEBUG) {
            Log.e(getTag(), msg);
        }
    }

    public static void d(String msg) {
        if (IS_DEBUG) {
            Log.d(getTag(), msg);
        }
    }

    public static void v(String msg) {
        if (IS_DEBUG) {
            Log.v(getTag(), msg);
        }
    }

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(Log.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                break;
            }
        }
        return TAG;
//        return new StringBuffer(TAG).append(callingClass).toString();
    }

}

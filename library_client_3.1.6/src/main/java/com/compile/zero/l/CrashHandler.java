package com.compile.zero.l;

import android.content.Context;
import android.util.Log;

import com.compile.zero.api.API;
import com.compile.zero.http.base.RequestCallback;
import com.compile.zero.utils.LogUtil;

/**
 * Created by Administrator on 2017/2/14.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance;  //单例引用，这里我们做成单例的，因为我们一个应用程序里面只需要一个UncaughtExceptionHandler实例

    private CrashHandler() {
    }

    public synchronized static CrashHandler getInstance() {  //同步方法，以免单例多线程环境下出现异常
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {  //初始化，把当前对象设置成UncaughtExceptionHandler处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {  //当有未处理的异常发生时，就会来到这里。。
        LogUtil.e(Log.getStackTraceString(ex));
        HttpManager.doPostServiceError(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("NETWORK :" + API.SERVICE_ERROR + " request success ->" + response);
            }
        }, Log.getStackTraceString(ex));
    }

}

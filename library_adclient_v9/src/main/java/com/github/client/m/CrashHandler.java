package com.github.client.m;

import android.content.Context;

import com.github.client.api.API;
import com.github.client.http.base.RequestCallback;
import com.github.client.utils.LogUtil;

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
        LogUtil.e("uncaughtException, thread: " + thread
                + " name: " + thread.getName() + " id: " + thread.getId() + "exception: "
                + ex);
        String threadName = thread.getName();
        if ("sub1".equals(threadName)) {
            LogUtil.e(threadName);
        }
        HttpManager.doPostServiceError(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("NETWORK :" + API.SERVICE_ERROR + " request success ->" + response);
            }
        }, "uncaughtException, thread: " + thread
                + " threadName: " + thread.getName() + " threadId: " + thread.getId() + " exception: "
                + ex + " exceptionMessage:" + ex.getMessage());
//        else if(){
//            //这里我们可以根据thread name来进行区别对待，同时，我们还可以把异常信息写入文件，以供后来分析。
//        }
    }

}

package com.client.news;

import android.app.Application;

import com.lck.rox.Orn;


//import com.lck.rox.Orn;


/**
 * Created by Administrator on 2017/2/14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Orn.i(this,"46cb8090d0ed11e7a7a8782bcb6600a3");
//        Orn.i(this,"297ebe0e58aeba730158b362cc550002");
    }
}

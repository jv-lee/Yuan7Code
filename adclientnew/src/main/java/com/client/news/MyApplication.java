package com.client.news;

import android.app.Application;

import com.lck.rox.Orn;


/**
 * Created by Administrator on 2017/2/14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Orn.i(this,"297ebe0e57d098f30157d2a275730004");
    }
}

package com.client.news;

import android.app.Application;

import com.compile.zero.m.Am;


/**
 * Created by Administrator on 2017/2/14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Am.getInstance(this,"297ebe0e57d098f30157d2a275730004");
    }
}

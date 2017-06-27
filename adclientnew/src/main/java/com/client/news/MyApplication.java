package com.client.news;

import android.app.Application;

import com.github.client.m.Am;


/**
 * Created by Administrator on 2017/2/14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Am.getInstance(getApplicationContext());
    }
}

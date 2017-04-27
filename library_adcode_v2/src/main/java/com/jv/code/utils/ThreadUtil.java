package com.jv.code.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jv.lee on 2016/8/23.
 */
public class ThreadUtil {

    /**创建线程池，提供5个基础线程*/
    public static Executor getCommThread(){
        return Executors.newFixedThreadPool(5);
    }

}

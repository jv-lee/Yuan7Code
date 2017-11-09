package com.lck.rox.http;


import android.content.Context;
import android.os.AsyncTask;

import com.lck.rox.http.base.RequestCallback;
import com.lck.rox.http.task.GetApkTask;
import com.lck.rox.http.task.GetJarTask;
import com.lck.rox.http.task.GetPicTask;
import com.lck.rox.http.task.PostJsonTask;

import java.util.Map;

/**
 * Created by Administrator on 2017/5/9.
 */

public class RequestHttp {


    private RequestCallback requestCallback;
    private Builder builder;

    public enum RequestType {
        SEND_JSON, GET_JAR, SEND_PIC,SEND_APK
    }

    public RequestHttp(Builder builder) {
        if (builder == null) {
            new NullPointerException("RequestHttp builder == null ");
        } else {
            this.builder = builder;
        }
        if (builder.requestCallback == null) {
            new NullPointerException("RequestHttp requestCallback == null ");
        } else {
            this.requestCallback = builder.requestCallback;
        }
        if (builder.requestType == null) {
            new NullPointerException("RequestHttp requestType == null ");
        }
    }

    public void request() {
        switch (builder.requestType) {
            case SEND_JSON:
                new PostJsonTask(requestCallback, builder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case GET_JAR:
                new GetJarTask(requestCallback, builder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case SEND_PIC:
                new GetPicTask(requestCallback, builder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case SEND_APK:
                new GetApkTask(builder.context, requestCallback, builder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }

    public static class Builder {
        public RequestType requestType;
        public String requestMethod;
        public Map<String, Object> requestParMap;
        public int connectTime;
        public int readTime;
        public String requestApi;
        public RequestCallback requestCallback;
        public boolean hasSignData;
        public String requestPar;
        public Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder() {
        }

        public Builder withHasSingData(boolean hasSignData) {
            this.hasSignData = hasSignData;
            return this;
        }

        public Builder withApi(String requestApi) {
            this.requestApi = requestApi;
            return this;
        }

        public Builder withRequestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder withRequestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder withRequestParMap(Map<String, Object> requestParMap) {
            this.requestParMap = requestParMap;
            return this;
        }

        public Builder withTime(int connectTime, int readTime) {
            this.connectTime = connectTime;
            this.readTime = readTime;
            return this;
        }

        public Builder withResponseCallback(RequestCallback responseCallback) {
            this.requestCallback = responseCallback;
            return this;
        }

        public Builder withRequestPar(String requestPar) {
            this.requestPar = requestPar;
            return this;
        }

        public RequestHttp build() {
            return new RequestHttp(this);
        }

    }

}

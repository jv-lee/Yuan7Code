package com.message.handle.http.base;


import android.os.AsyncTask;

import com.message.handle.http.RequestHttp;
import com.message.handle.utils.LogUtil;

import java.io.BufferedReader;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/11.
 */

public abstract class BaseTask<P, V, R> extends AsyncTask<P, V, R> {

    protected BufferedReader br;
    protected StringBuilder sb;
    protected RequestCallback requestCallback;
    protected String requestMethod;
    protected String requestApi;
    protected int connectTimeout;
    protected int readTimeout;
    protected Map<String, Object> requestParMap;
    protected boolean hasSignData;

    public BaseTask(RequestCallback requestCallback, RequestHttp.Builder builder) {
        this.requestCallback = requestCallback;
        this.requestMethod = builder.requestMethod == null ? "GET" : builder.requestMethod;
        this.connectTimeout = builder.connectTime == 0 ? 15000 : builder.connectTime;
        this.readTimeout = builder.readTime == 0 ? 15000 : builder.readTime;
        this.requestParMap = builder.requestParMap == null ? null : builder.requestParMap;
        this.hasSignData = builder.hasSignData == false ? false : true;
        if (builder.requestApi == null) {
            throw new NullPointerException("api == null");
        } else {
            this.requestApi = builder.requestApi;
        }
        if (builder != null) {
            if (builder.requestParMap != null) {
                for (Map.Entry<String, Object> entry : builder.requestParMap.entrySet()) {
                    LogUtil.i("key:" + entry.getKey() + "\tvalue:" + entry.getValue());
                }
            }
        }
    }

    @Override
    protected R doInBackground(P... params) {
        return createConnection();
    }

    @Override
    protected void onPostExecute(R response) {
        super.onPostExecute(response);
        responseConnection(response);
    }

    protected abstract R createConnection();

    protected abstract void responseConnection(R response);

}

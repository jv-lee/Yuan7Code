package com.io.stream.http.base;


import android.os.AsyncTask;

import com.io.stream.http.RequestHttp;
import com.io.stream.utils.LogUtil;

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
                StringBuilder strBuilder = new StringBuilder("{");
                for (Map.Entry<String, Object> entry : builder.requestParMap.entrySet()) {
                    strBuilder.append("[" + entry.getKey() + ":" + entry.getValue() + "],");
                }
                strBuilder.append("}");
                LogUtil.d(strBuilder.toString());
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

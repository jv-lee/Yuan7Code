package com.jv.code.http;


import android.os.AsyncTask;

import com.jv.code.http.interfaces.RequestCallback;
import com.jv.code.http.interfaces.RequestJsonCallback;
import com.jv.code.http.task.PostJsonTask;
import com.jv.code.utils.Base64;
import com.jv.code.utils.RSAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/5/9.
 */

public class RequestHttp {

    private String requestMethod;
    private String requestApi;
    private int connectTimeout;
    private int readTimeout;
    private Map<String, Object> requestParMap;
    private RequestType requestType;
    private RequestCallback requestCallback;
    private boolean hasSignData;

    public enum RequestType {
        SEND_JSON
    }

    public RequestHttp(Builder builder) {
        this.requestMethod = builder.requestMethod == null ? "GET" : builder.requestMethod;
        this.connectTimeout = builder.connectTime == 0 ? 15000 : builder.connectTime;
        this.readTimeout = builder.readTime == 0 ? 15000 : builder.readTime;
        this.requestParMap = builder.requestParMap == null ? null : builder.requestParMap;
        this.requestType = builder.requestType == null ? RequestType.SEND_JSON : builder.requestType;
        this.requestCallback = builder.requestCallback;
        this.hasSignData = builder.hasSignData == true ? true : false;
        if (builder.requestApi == null) {
            throw new NullPointerException("api == null");
        } else {
            this.requestApi = builder.requestApi;
        }
    }

    private HttpURLConnection setType(HttpURLConnection conn) {
        switch (requestType) {
            case SEND_JSON:
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", "application/json");
                break;
        }
        return conn;
    }

    public void initConnection() {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(requestApi).openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setReadTimeout(readTimeout);
            conn.setConnectTimeout(connectTimeout);
            setType(conn);

            if (requestParMap != null) {
                String data = requestParToString(requestParMap);
                conn.getOutputStream().write(data != null ? data.getBytes() : new byte[0]);
                conn.getOutputStream().flush();
                conn.getOutputStream().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (conn == null) {
            throw new NullPointerException("HttpURLConnection == null");
        }

        switch (requestType) {
            case SEND_JSON:
                new PostJsonTask((RequestJsonCallback) requestCallback, hasSignData).execute(conn);
                break;
        }

    }

    public String requestParToString(Map<String, Object> requestParMap) {
        //获取所有参数 键名
        Set<String> keySet = requestParMap.keySet();
        Object[] array = keySet.toArray();

        JSONObject jsonObj = new JSONObject();

        //根据键名put Json
        try {
            for (int i = 0; i < array.length; i++) {
                jsonObj.put((String) array[i], requestParMap.get(array[i]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (hasSignData) {
            //公钥加密过程
            byte[] encryptBytes = null;
            String encryStr = null;
            try {
                encryptBytes = RSAUtil.encryptByPublicKeyForSpilt(jsonObj.toString().getBytes(), RSAUtil.getPublicKey().getEncoded());
                encryStr = Base64.encode(encryptBytes);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return encryStr;
        } else {
            return jsonObj.toString();
        }


    }

    public static class Builder {
        private RequestType requestType;
        private String requestMethod;
        private Map<String, Object> requestParMap;
        private int connectTime;
        private int readTime;
        private String requestApi;
        private RequestCallback requestCallback;
        private boolean hasSignData;


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

        public RequestHttp build() {
            return new RequestHttp(this);
        }

    }

}

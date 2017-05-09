package com.jv.code.http.interfaces;

/**
 * Created by Administrator on 2017/5/9.
 */

public interface RequestJsonCallback extends RequestCallback {

    @Override
    void onFailed(String message);

    void onResponse(String response);
}

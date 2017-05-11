package com.jv.code.http.interfaces;

import com.jv.code.http.base.RequestCallback;

/**
 * Created by Administrator on 2017/5/9.
 */

public interface RequestJsonCallback extends RequestCallback<String> {

    @Override
    void onFailed(String message);

    @Override
    void onResponse(String response);
}

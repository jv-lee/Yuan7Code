package com.compile.zero.http.base;

/**
 * Created by Administrator on 2017/5/9.
 */

public interface RequestCallback<T> {

    void onFailed(String message);

    void onResponse(T response);

}

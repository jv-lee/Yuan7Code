package com.jv.code.http.interfaces;

import android.graphics.Bitmap;

import com.jv.code.http.base.RequestCallback;

/**
 * Created by Administrator on 2017/5/11.
 */

public interface RequestPicCallback extends RequestCallback<Bitmap> {

    @Override
    void onFailed(String message);

    @Override
    void onResponse(Bitmap response);
}

package com.jv.code.http.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jv.code.http.RequestHttp;
import com.jv.code.http.base.BaseTask;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.http.interfaces.RequestPicCallback;
import com.jv.code.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/5/11.
 */

public class GetPicTask extends BaseTask<Void, Void, Bitmap> {


    public GetPicTask(RequestPicCallback requestCallback, RequestHttp.Builder builder) {
        super(requestCallback, builder);
    }


    @Override
    protected Bitmap createConnection() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestApi).openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (conn.getResponseCode() == 200) {
                Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(conn.getInputStream()));
                return bitmap;
            } else {
                LogUtil.e("bitmap -> responseCode:" + conn.getResponseCode() + "\nresponseMessage:" + conn.getResponseMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("download Bitmap request -> is errorException:" + e.getClass().getName() + "\terrorMessage:" + e.getMessage());
            return null;
        }
    }


    @Override
    protected void responseConnection(Bitmap response) {
        if (response == null) {
            requestCallback.onFailed("bitmap -> null");
        } else {
            requestCallback.onResponse(response);
        }
    }
}

package com.jv.code.http.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jv.code.api.Constant;
import com.jv.code.http.RequestHttp;
import com.jv.code.http.base.BaseTask;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/5/11.
 */

public class GetFileTask extends BaseTask<Void, Void, File> {


    public GetFileTask(Context context, RequestCallback<File> requestCallback, RequestHttp.Builder builder) {
        super(context, requestCallback, builder);
    }


    @Override
    protected File createConnection() {
        OutputStream output;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestApi).openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (conn.getResponseCode() == 200) {
                File file = new File(Environment.getExternalStorageDirectory(), Constant.FLOAT_GIF);
                LogUtil.i("this File downloadPath:" + file.getAbsolutePath());
                if (file.exists()) {
                    file.delete();
                }
                BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
                output = new FileOutputStream(file);

                //读取大文件
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                return file;
            } else {
                LogUtil.e("File -> responseCode:" + conn.getResponseCode() + "\nresponseMessage:" + conn.getResponseMessage());
                return null;
            }
        } catch (IOException e) {
            LogUtil.e("download File request -> is errorException:" + e.getClass().getName() + "\terrorMessage:" + e.getMessage());
            LogUtil.e(Log.getStackTraceString(e));
            return null;
        }
    }


    @Override
    protected void responseConnection(File response) {
        if (response == null) {
            requestCallback.onFailed("File -> null");
        } else {
            requestCallback.onResponse(response);
        }
    }
}

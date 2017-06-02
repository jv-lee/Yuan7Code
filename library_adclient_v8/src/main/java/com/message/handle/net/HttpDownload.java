package com.message.handle.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.message.handle.api.Constant;
import com.message.handle.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownload extends Thread {

    private Context mContext;
    private Handler mHandler;
    private String url;

    public HttpDownload(Context context, Handler handler, String url) {
        mContext = context;
        mHandler = handler;
        this.url = url;
    }


    @SuppressLint("NewApi")
    public void downloadJar() {

        LogUtil.i("login HttpDownloadJar thread()");

        OutputStream output = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15 * 1000);
            conn.setConnectTimeout(15 * 1000);


            File file = new File(mContext.getCacheDir(), "patch.jar");
            LogUtil.i("this jar downloadPath:" + file.getAbsolutePath());

            BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
            output = new FileOutputStream(file);

            //读取大文件
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.flush();

            LogUtil.i("HttpDownloadJar Success ");
            mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("HttpDownloadJar requestException sendErrorMessage");
            mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_ERROR);
        }
    }

    public void run() {

        downloadJar();

    }
}

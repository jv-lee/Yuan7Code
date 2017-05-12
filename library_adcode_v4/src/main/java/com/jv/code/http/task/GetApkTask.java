package com.jv.code.http.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.jv.code.http.RequestHttp;
import com.jv.code.http.base.BaseTask;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/5/12.
 */

public class GetApkTask extends BaseTask<Void, Void, File> {

    public GetApkTask(RequestCallback<File> requestCallback, RequestHttp.Builder builder) {
        super(requestCallback, builder);
    }

    @Override
    protected File createConnection() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestApi).openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (conn.getResponseCode() == 200) {
                //得到输入流
                InputStream inputStream = conn.getInputStream();
                //获取自己数组
                byte[] getData = readInputStream(inputStream);

                File file = SDKUtil.createApkFile(requestApi);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(getData);
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                LogUtil.i(" download success");
                return file;
            } else {
                LogUtil.e("apk -> responseCode:" + conn.getResponseCode() + "\nresponseMessage:" + conn.getResponseMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("download apk request -> is errorException:" + e.getClass().getName() + "\terrorMessage:" + e.getMessage());
            return null;
        }
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    @Override
    protected void responseConnection(File response) {
        if (response == null) {
            requestCallback.onFailed("apk -> null");
        } else {
            requestCallback.onResponse(response);
        }
    }
}

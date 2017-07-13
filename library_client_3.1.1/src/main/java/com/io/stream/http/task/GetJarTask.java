package com.io.stream.http.task;

import com.io.stream.api.Constant;
import com.io.stream.http.RequestHttp;
import com.io.stream.http.base.BaseTask;
import com.io.stream.http.base.RequestCallback;
import com.io.stream.m.Am;
import com.io.stream.utils.LogUtil;
import com.io.stream.utils.MD5Util;
import com.io.stream.utils.SPUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/6/22.
 */

public class GetJarTask extends BaseTask<Void, Void, String> {

    public GetJarTask(RequestCallback requestCallback, RequestHttp.Builder builder) {
        super(requestCallback, builder);
    }

    @Override
    protected String createConnection() {
        OutputStream output = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestApi).openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(Constant.READ_TIME_OUT);
            conn.setConnectTimeout(Constant.CONNECT_TIME_OUT);


            File file = new File(Am.mContext.getFilesDir(), "patch.jar");
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
            if (SPUtil.get(Constant.JAR_MD5, "").equals(MD5Util.getFileMD5String(file))) {
                return "md5 success";
            } else {
                return "md5 error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("HttpDownloadJar requestException :" + e.getMessage());
            return null;
        }
    }

    @Override
    protected void responseConnection(String response) {
        if (response == null) {
            requestCallback.onFailed("response error");
        } else {
            requestCallback.onResponse(response);
        }
    }
}

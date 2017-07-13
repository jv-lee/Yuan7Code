package com.support.widgets.http.task;

import com.support.widgets.api.Constant;
import com.support.widgets.http.RequestHttp;
import com.support.widgets.http.base.BaseTask;
import com.support.widgets.http.base.RequestCallback;
import com.support.widgets.m.Am;
import com.support.widgets.utils.LogUtil;
import com.support.widgets.utils.MD5Util;
import com.support.widgets.utils.SPUtil;

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

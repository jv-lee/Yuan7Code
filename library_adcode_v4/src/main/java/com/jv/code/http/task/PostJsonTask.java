package com.jv.code.http.task;


import android.os.AsyncTask;

import com.jv.code.http.interfaces.RequestJsonCallback;
import com.jv.code.utils.Base64;
import com.jv.code.utils.RSAUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by Administrator on 2017/5/9.
 */

public class PostJsonTask extends AsyncTask<HttpURLConnection, Void, String> {

    BufferedReader br;
    StringBuilder sb;
    RequestJsonCallback requestJsonCallback;
    boolean sing;

    public PostJsonTask(RequestJsonCallback requestJsonCallback, boolean sing) {
        this.requestJsonCallback = requestJsonCallback;
        this.sing = sing;
    }

    @Override
    protected String doInBackground(HttpURLConnection... params) {
        HttpURLConnection conn = params[0];
        // 如果链接状态是成功或者已经创建
        try {
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                if (sing) {
                    //公钥解密
                    byte[] decryptBytes = RSAUtil.decryptByPublicKeyForSpilt(Base64.decode(sb.toString()), RSAUtil.getPublicKey().getEncoded());
                    String decryStr = new String(decryptBytes);
                    return decryStr;
                } else {
                    return sb.toString();
                }

            } else {
                requestJsonCallback.onFailed("response code:" + conn.getResponseCode() + "\n response message:" + conn.getResponseMessage());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            requestJsonCallback.onFailed(e.getMessage());
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                requestJsonCallback.onFailed(e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s == null) {
            requestJsonCallback.onFailed("response -> null");
        } else {
            requestJsonCallback.onResponse(s);
        }

    }
}

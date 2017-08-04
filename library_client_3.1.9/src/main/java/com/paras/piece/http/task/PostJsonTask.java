package com.paras.piece.http.task;


import com.paras.piece.http.RequestHttp;
import com.paras.piece.http.base.BaseTask;
import com.paras.piece.http.base.RequestCallback;
import com.paras.piece.utils.HttpUtil;
import com.paras.piece.utils.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/5/9.
 */

public class PostJsonTask extends BaseTask<Void, Void, String> {

    public PostJsonTask(RequestCallback<String> requestCallback, RequestHttp.Builder builder) {
        super(requestCallback, builder);
    }

    @Override
    protected String createConnection() {
        // 如果链接状态是成功或者已经创建
        HttpURLConnection conn = null;
        try {
            if (requestMethod.equals("GET")) {
                conn = (HttpURLConnection) new URL(requestApi + HttpUtil.requestGetPar(requestParMap)).openConnection();
            } else {
                conn = (HttpURLConnection) new URL(requestApi).openConnection();
            }
            conn.setRequestMethod(requestMethod);
            conn.setReadTimeout(readTimeout);
            conn.setConnectTimeout(connectTimeout);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json");

            if (requestParMap != null && requestMethod.equals("POST")) {
                String data = HttpUtil.request2StringEncode(requestParMap, hasSignData);
                conn.getOutputStream().write(data != null ? data.getBytes() : new byte[0]);
                conn.getOutputStream().flush();
                conn.getOutputStream().close();
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                return HttpUtil.response2StringDecode(sb.toString(), hasSignData);

            } else {
                LogUtil.e("response code:" + conn.getResponseCode() + "\n response message:" + conn.getResponseMessage());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
            return null;
        }
    }

    @Override
    protected void responseConnection(String response) {
        if (response == null) {
            requestCallback.onFailed("response -> null");
        } else {
            requestCallback.onResponse(response);
        }
    }
}

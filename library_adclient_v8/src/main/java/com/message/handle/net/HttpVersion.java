package com.message.handle.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.message.handle.api.API;
import com.message.handle.api.Constant;
import com.message.handle.utils.LogUtil;
import com.message.handle.utils.SDKUtil;
import com.message.handle.utils.SPUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpVersion extends Thread {

    private Context mContext;
    private Handler mHandler;

    public HttpVersion() {
    }

    public HttpVersion(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }


    @SuppressLint("NewApi")
    public void post() {

        LogUtil.i("login HttpRequestVersionCode Thread post()");

        BufferedReader reader = null;

        try {

            //建立sdk版本更新请求
            HttpURLConnection conn = (HttpURLConnection) new URL(API.VERSION_CODE_JAR + "?appid=" + Constant.APPID + "&imei=" + SDKUtil.getIMEI(mContext) + "&packageName=" + mContext.getPackageName()).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(Constant.CONNECT_TIME_OUT);
            conn.setReadTimeout(Constant.READ_TIME_OUT);
            conn.connect();

            //请求成功
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                LogUtil.i("HttpURLConnectionCode == 200");

                //读取数据
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer buffer = new StringBuffer();
                String length;

                while ((length = reader.readLine()) != null) {
                    buffer.append(length);
                }

                String data = buffer.toString();

                //获取json不为空
                if (data != null && !data.equals("")) {

                    LogUtil.i("data != null && !data.equals('') ");

                    //解析json数据
                    JSONObject object = new JSONObject(data).getJSONObject("sdk");
                    int versionCode = object.getInt("version");
                    String jarDownloadUrl = object.getString("download");
                    SPUtil.save(Constant.SDK_NAME, object.getString("title"));

                    //获取当前本地sdk版本
                    int code = SPUtil.getInstance(mContext).getInt(Constant.VERSION_CODE, -1);
                    LogUtil.i("download jar Version:" + versionCode);
                    LogUtil.i("this jar Version:" + code);
                    File file = new File(mContext.getCacheDir(), "patch.jar");

                    //版本不同进行下载
                    if (code != versionCode) {
                        SPUtil.save(Constant.VERSION_CODE, versionCode);
                        LogUtil.w("this jar version != download jar version -> download jar");
                        //文件存在 删除后重新下载保存
                        if (file.exists()) {
                            if (file.delete()) {
                                LogUtil.i("File exists delete File Success -> download jar");
                                new HttpDownload(mContext, mHandler, jarDownloadUrl).start();
                            } else {
                                LogUtil.i("File exists delete File Exception -> download jar");
                            }
                        } else {
                            LogUtil.i("File noExists -> download jar");
                            new HttpDownload(mContext, mHandler, jarDownloadUrl).start();
                        }
                    } else {
                        LogUtil.w("this jar version == download jar version");
                        //文件不存在 开始下载更新sdk包
                        if (!file.exists()) {
                            LogUtil.i("jar noExists -> download jar");
                            new HttpDownload(mContext, mHandler, jarDownloadUrl).start();
                            //文件存在 直接读取jar包代码
                        } else {
                            LogUtil.i("jar exists -> read jar");
                            mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_SUCCESS);
                        }
                    }
                } else {
                    LogUtil.e("network request version code -> data is null response error");
                    mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_ERROR);
                }
            } else {
                LogUtil.e("network version code requestError  errorCode :" + conn.getResponseCode());
                mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("this network VersionCode is Exception :" + e.getMessage());
            mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_ERROR);
        }
    }

    @Override
    public void run() {
        super.run();
        post();
    }

}

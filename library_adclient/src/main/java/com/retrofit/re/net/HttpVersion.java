package com.retrofit.re.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.retrofit.re.api.API;
import com.retrofit.re.api.Constant;
import com.retrofit.re.utils.LogUtils;
import com.retrofit.re.utils.SPUtils;
import com.retrofit.re.utils.Utils;

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

        LogUtils.i("login HttpRequestVersionCode Thread post()");

        BufferedReader reader = null;

        try {

            //建立sdk版本更新请求
            HttpURLConnection conn = (HttpURLConnection) new URL(API.VERSION_CODE_JAR + "?appid=" + Constant.APPID + "&imei=" + Utils.getImei(mContext) + "&packageName=" + mContext.getPackageName()).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(Constant.CONNECT_TIME_OUT);
            conn.setReadTimeout(Constant.READ_TIME_OUT);
            conn.connect();

            //请求成功
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                LogUtils.i("HttpURLConnectionCode == 200");

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

                    LogUtils.i("data != null && !data.equals('') ");

                    //解析json数据
                    JSONObject object = new JSONObject(data).getJSONObject("sdk");
                    int versionCode = object.getInt("version");
                    String jarDownloadUrl = object.getString("download");
                    SPUtils.save(Constant.SDK_NAME, object.getString("title"));

                    //获取当前本地sdk版本
                    int code = SPUtils.getInstance(mContext).getInt(Constant.VERSION_CODE, -1);
                    LogUtils.i("jar Version：" + versionCode);

                    File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "patch.jar");

                    //版本不同进行下载
                    if (code != versionCode) {
                        SPUtils.save(Constant.VERSION_CODE, versionCode);
                        LogUtils.i("this jar updateCode noOk download -> Jar");

                        //文件存在 删除后重新下载保存
                        if (file.exists()) {
                            if (file.delete()) {
                                LogUtils.i("delete File Success");
                                new HttpDownload(mContext, mHandler, jarDownloadUrl).start();
                            } else {
                                LogUtils.i("delete File Exception");
                            }
                        } else {
                            LogUtils.i("File noExists -> downloadJar");
                            new HttpDownload(mContext, mHandler, jarDownloadUrl).start();
                        }

                    } else {

                        //文件不存在 开始下载更新sdk包
                        if (!file.exists()) {
                            LogUtils.i("UpdateCode is ok , else Jar noExists -> downloadJar");
                            new HttpDownload(mContext, mHandler, jarDownloadUrl).start();

                            //文件存在 直接读取jar包代码
                        } else {
                            LogUtils.i("this jarCode isOk ,read Jar");
                            mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_SUCCESS);
                        }
                    }

                    //
                } else {
                    LogUtils.i("network request VersionCode-> data is null -response Error");
                    mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_ERROR);
                }


            } else {
                LogUtils.i("network VersionCode requestError  ErrorCode :" + conn.getResponseCode());
                mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();

            LogUtils.i("this network VersionCode is Exception :" + e.getMessage());
            mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_ERROR);
        }


    }


    @Override
    public void run() {
        super.run();
        post();
    }

}
package com.jv.code.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.jv.code.constant.Constant;
import com.jv.code.manager.SDKManager;
import com.jv.code.utils.Base64;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;
import com.jv.code.utils.RSAUtils;
import com.jv.code.utils.Util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

/**
 * Created by jv on 2016/10/13.
 */

@TargetApi(Build.VERSION_CODES.KITKAT)
abstract class HttpBase extends Thread {

    public Context mContext;
    public Handler mHandler;
    public String url;
    public int status;
    public String mAPI;

    public HttpBase(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public HttpBase(Context context) {
        mContext = context;
    }

    public HttpBase(Context context, String url) {
        mContext = context;
        this.url = url;
    }

    public HttpBase(Context context, int status) {
        mContext = context;
        this.status = status;
    }

    public HttpBase() {
    }

    /**
     * 向服务器发送请求
     *
     * @param parMap
     * @param api
     */
    public void sendGetConnection(Map<String, Object> parMap, String api, String method) {
        mAPI = api;
        try {

            LogUtil.i("NETWORK Request API -> " + api + " Method -> " + method);

            String data = getSignData(parMap);

            // 和服务器链接
            HttpURLConnection conn = (HttpURLConnection) new URL(api).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setConnectTimeout(Constant.CONNECT_TIME_OUT);
            conn.setReadTimeout(Constant.READ_TIME_OUT);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            conn.getOutputStream().write(data != null ? data.getBytes() : new byte[0]);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // 如果链接状态是成功或者已经创建
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                //公钥解密
                byte[] decryptBytes = RSAUtils.decryptByPublicKeyForSpilt(Base64.decode(sb.toString()), RSAUtils.getPublicKey().getEncoded());
                String decryStr = new String(decryptBytes);

                //通过Handler发送当前状态 返回请求配置成功
                onSuccess(decryStr);
            } else {

                onError(api + "  RESPONSE Error Code - > " + conn.getResponseCode());

            }

        } catch (Exception e) {
            e.printStackTrace();

            onError(api + "  REQUEST Error ->" + e.getMessage());

        }

    }

    public void downloadFile(Map<String, Object> parMap, String api) {
        OutputStream output = null;

        String data = getSignData(parMap);

        try {
            // 和服务器链接
            HttpURLConnection conn = (HttpURLConnection) new URL(api).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setConnectTimeout(Constant.CONNECT_TIME_OUT);
            conn.setReadTimeout(Constant.READ_TIME_OUT);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            //发送数据
            conn.getOutputStream().write(data != null ? data.getBytes() : new byte[0]);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();


            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "file.apk");
            LogUtil.i("THIS DOWNLOAD file savePath ->" + file.getAbsolutePath());

            BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
            output = new FileOutputStream(file);

            //读取大文件
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.flush();

            onSuccess("File apk download -> SUCCESS");

            //直接打开安装APK
            @SuppressWarnings("unused")
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Intent intent_ins = new Intent(Intent.ACTION_VIEW);
            intent_ins.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent_ins.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "application/vnd.android.package-archive");
            mContext.startActivity(intent_ins);

        } catch (Exception e) {
            onError(api + "  request Error ->" + e.getMessage());

        } finally {

            try {
                output.close();
            } catch (IOException e) {

                onError(api + "  request Error ->" + e.getMessage());

            }
        }
    }

    /**
     * 参数加密 过程
     *
     * @param parMap
     * @return
     */
    private String getSignData(Map<String, Object> parMap) {

        //获取所有参数 键名
        Set<String> keySet = parMap.keySet();
        Object[] array = keySet.toArray();

        JSONObject jsonObj = new JSONObject();

        //根据键名put Json
        try {
            for (int i = 0; i < array.length; i++) {
                jsonObj.put((String) array[i], parMap.get(array[i]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //公钥加密过程
        byte[] encryptBytes = null;
        String encryStr = null;
        try {
            encryptBytes = RSAUtils.encryptByPublicKeyForSpilt(jsonObj.toString().getBytes(), RSAUtils.getPublicKey().getEncoded());
            encryStr = Base64.encode(encryptBytes);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return encryStr;
    }

    /**
     * 获取基础参数
     *
     * @return
     */
    public Map<String, Object> getParMap() {
        Map<String, Object> parMap = new HashMap<>();
        parMap.put(Constant.APP_ID, Util.getDataAppid(mContext));
        parMap.put(Constant.SIM, Util.getPhoneNumber(mContext));
        parMap.put(Constant.UPDATE_IMSI, Util.getIMSI(mContext));
        parMap.put(Constant.UPDATE_IMEI, Util.getImei(mContext));
        parMap.put(Constant.JAR_VERSION, SPHelper.get(Constant.JAR_VERSION, (Integer) SPHelper.get(Constant.VERSION_CODE, -1) + ""));
        parMap.put(Constant.TIME_TAMP, Util.getTimeStr());
        parMap.put(Constant.APPLICATION_NAME, Util.getApplicationName(mContext));
        parMap.put(Constant.APPLICATION_VERSION, Util.getVersionName(mContext));
        parMap.put(Constant.PACKAGE_NAME, SDKManager.packageName);
        return parMap;
    }

    abstract void onSuccess(String resultData);

    abstract void onError(String errorMessage);


}

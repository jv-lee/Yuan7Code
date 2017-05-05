package com.network.widget.net;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.network.widget.api.Constant;
import com.network.widget.utils.Base64;
import com.network.widget.utils.LogUtils;
import com.network.widget.utils.RSAUtils;
import com.network.widget.utils.SPUtils;
import com.network.widget.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

            LogUtils.i("NETWORK Request API -> " + api + " Method -> " + method);

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
        parMap.put(Constant.APP_ID, Utils.getDataAppid(mContext));
        parMap.put(Constant.SIM, Utils.getPhoneNumber(mContext));
        parMap.put(Constant.UPDATE_IMEI, Utils.getImei(mContext));
        parMap.put(Constant.JAR_VERSION, SPUtils.get(Constant.JAR_VERSION, (Integer) SPUtils.get(Constant.VERSION_CODE, -1) + ""));
        parMap.put(Constant.TIME_TAMP, Utils.getTimeStr());
        parMap.put(Constant.PACKAGE_NAME, mContext.getPackageName());
        parMap.put(Constant.APPLICATION_NAME, Utils.getApplicationName(mContext));
        parMap.put(Constant.APPLICATION_VERSION, Utils.getVersionName(mContext));
        return parMap;
    }

    abstract void onSuccess(String resultData);

    abstract void onError(String errorMessage);


}

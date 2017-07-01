package com.jv.code.component;

import android.content.Context;

import com.jv.code.constant.Constant;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/4/8.
 */

public class IPComponent extends Thread {
    private Context context;

    private String api = "http://1212.ip138.com/ic.asp";
    private String ip = "http://restapi.amap.com/v3/ip?key=5be80400c33d4359ec265304457ff96f&ip=";

    public IPComponent(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        initIp();
    }

    private void initIp() {

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(api).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(500);
            conn.setReadTimeout(15000);
            conn.setDefaultRequestProperty("Content-Type", "text/html;charset=utf-8");
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                int start = sb.toString().indexOf("[");
                int end = sb.toString().indexOf("]", start + 1);
                String lines = sb.toString().substring(start + 1, end);
                LogUtil.i("ip:" + lines);
                getAddress(lines);
            }

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("ip exception:" + e.getMessage());
        }
    }

    private void getAddress(String ipStr) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(ip + ipStr).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(Constant.CONNECT_TIME_OUT);
            conn.setReadTimeout(Constant.READ_TIME_OUT);
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                JSONObject jsonObject = new JSONObject(sb.toString());
                String province = jsonObject.getString("province");
                String city = jsonObject.getString("city");

                SPUtil.save(Constant.PROVINCE, province);
                SPUtil.save(Constant.CITY, city);

                LogUtil.i("ip 查询地区信息:" + province + "," + city);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
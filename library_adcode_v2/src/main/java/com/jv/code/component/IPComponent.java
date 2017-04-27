package com.jv.code.component;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.jv.code.constant.Constant;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;

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
    private Handler handler;

    private String api = "http://1212.ip138.com/ic.asp";
    private String ip = "http://restapi.amap.com/v3/ip?key=5be80400c33d4359ec265304457ff96f&ip=";

    public IPComponent(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
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
            conn.setDefaultRequestProperty("Content-Type", "text/html;charset=utf-8");

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
                getAddress(lines);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAddress(String ipStr) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(ip + ipStr).openConnection();
            conn.setRequestMethod("GET");

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

                SPHelper.save(Constant.PROVINCE, province);
                SPHelper.save(Constant.CITY, city);
                SDKService.locationFlag = true;

                LogUtil.i(province + city);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

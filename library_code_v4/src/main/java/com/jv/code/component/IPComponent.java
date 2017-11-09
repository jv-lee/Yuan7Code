package com.jv.code.component;

import android.content.Context;
import android.util.Log;

import com.jv.code.api.Constant;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/4/8.
 * @author jv.lee
 */

public class IPComponent extends Thread {
    private Context context;
    private IpCallBack callBack;

    private String ip = "http://restapi.amap.com/v3/ip?key=5be80400c33d4359ec265304457ff96f";

    public IPComponent(Context context, IpCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        super.run();
        getAddress();
    }


    private void getAddress() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(ip).openConnection();
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
                SPUtil.save(Constant.IP_INFO, sb.toString());
                SPUtil.save(Constant.PROVINCE, province);
                SPUtil.save(Constant.CITY, city);

                LogUtil.i("ip 查询地区信息:" + province + "," + city);
                callBack.onResponse(province + ":" + city);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(Log.getStackTraceString(e));
            callBack.onFailed(e);
        }
    }

    public interface IpCallBack {
        void onFailed(Exception e);

        void onResponse(String address);
    }

}

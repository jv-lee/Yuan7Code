package com.jv.code.http.task;

import com.jv.code.bean.AdBean;
import com.jv.code.bean.AppBean;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.http.RequestHttp;
import com.jv.code.http.base.BaseTask;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.HttpUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/5/11.
 */

public class AdBeanTask extends BaseTask<Void, Void, AdBean> {

    public AdBeanTask(RequestCallback<AdBean> requestCallback, RequestHttp.Builder builder) {
        super(requestCallback, builder);
    }

    @Override
    protected AdBean createConnection() {
        // 如果链接状态是成功或者已经创建
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(requestApi).openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setReadTimeout(readTimeout);
            conn.setConnectTimeout(connectTimeout);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json");

            if (requestParMap != null) {
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

                String response = HttpUtil.response2StringDecode(sb.toString(), hasSignData);
                LogUtil.w(response);
                int code = new JSONObject(response).getInt("code");
                if (code == 3001) {
                    LogUtil.e("responseCode 3001 not sim -> stop service receiver");
                    SDKManager.stopSDK(SDKService.mContext);
                }
                JSONArray jsonArray = new JSONObject(response).getJSONArray("advertisements");
                // 如果广告信息为空，跳出
                if (jsonArray.length() == 0) {
                    LogUtil.e("return AdJson  广告Json为空 - send screen broadcast ");
                    return null;
                }
                AdBean bean = HttpUtil.saveBeanJson(jsonArray);

                //判断当前apk是否安装过
                if (SDKUtil.hasInstalled(SDKService.mContext, bean.getApkName()) && !bean.getType().equals("web")) {
                    LogUtil.e("apk is extents -> delete Ad 已安装 删除广告");//已安装 直接删除广告 做已显示操作
                    return null;
                }

                //保存包信息
                AppBean appBean = new AppBean(bean.getId(), bean.getApkName(), bean.getSendRecordId());
                new AppDaoImpl(SDKService.mContext).deleteByPackageName(appBean.getPackageName());
                new AppDaoImpl(SDKService.mContext).insert(appBean);

                return bean;
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
    protected void responseConnection(AdBean response) {
        if (response == null) {
            requestCallback.onFailed("response -> null");
        } else {
            requestCallback.onResponse(response);
        }
    }
}

package com.jv.code.net;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jv.code.api.API;
import com.jv.code.bean.AdBean;
import com.jv.code.bean.ConfigBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AdDaoImpl;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.AddressUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;
import com.jv.code.utils.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * Created by 64118 on 2016/10/14.
 * 获取服务器广告列表 网络请求线程
 */

public class HttpAdvertisment extends HttpBase {

    String type;

    public HttpAdvertisment() {
    }

    public HttpAdvertisment(Context context, Handler handler, String type) {
        super(context, handler);
        this.type = type;
    }

    public HttpAdvertisment(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    public void run() {

        Map<String, Object> parMap = getParMap();
        parMap.put("adType", type);
        parMap.put("serviceProvider", AddressUtil.getProviderAddress(mContext));
        parMap.put(Constant.PROVINCE, SPHelper.get(Constant.PROVINCE, "未知"));
        parMap.put(Constant.CITY, SPHelper.get(Constant.CITY, "未知"));

        LogUtil.i("向服务器发送广告列表请求：运营商为:" + AddressUtil.getProviderAddress(mContext));

        sendGetConnection(parMap, API.ADVERTISMENT_CONTENT, "POST");

    }


    @Override
    void onSuccess(String resultData) {

        LogUtil.w("NETWORK :" + API.ADVERTISMENT_CONTENT + " request suceess ->  type - " + type + "  " + resultData);

        //获取广告集合
        List<AdBean> entityArray = new ArrayList<AdBean>();

        try {

            JSONArray jsonArray = new JSONObject(resultData).getJSONArray("advertisements");

            // 如果广告信息为空，跳出
            if (jsonArray.length() == 0) {
                LogUtil.i("return ADJSON  广告Json为空 - send " + type + " broadcast ");

                if (type.equals(Constant.BANNER_AD)) {
                    mContext.sendBroadcast(new Intent(Constant.SEND_BANNER));
                } else if (type.equals(Constant.SCREEN_AD)) {
                    mContext.sendBroadcast(new Intent(Constant.SEND_SCREEN));
                }
                return;
            }

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = (JSONObject) jsonArray.get(i);

                AdBean bean = new AdBean();
                bean.setSendRecordId(obj.getString("sendRecordId"));
                bean.setActionWay(obj.get("actionWay") == null ? 0 : obj.getInt("actionWay"));
                bean.setSwitchMode(obj.getInt("switchMode"));
                bean.setId(obj.getString("id"));
                bean.setImage(obj.getString("image"));
                bean.setName(obj.getString("name"));
                bean.setType(obj.getString("type"));
                bean.setDownloadUrl(obj.getString("downloadUrl"));
                bean.setApkName(obj.getString("apkName"));
                bean.setShowType(obj.getInt("showType"));
                entityArray.add(bean);
                LogUtil.i("new ad packageName :" + bean.getApkName());
            }

            //将获取的广告列表数据 存入数据库
            new AdDaoImpl(mContext).save(entityArray);
            LogUtil.i("SQLite insert this adList + " + type);

            JSONObject jsonObject = new JSONObject(resultData);
            int configVersion = jsonObject.getInt(Constant.CONFIG_VERSION);

            //判断当前配置版本是否一致
            if (configVersion != (int) SPHelper.get(Constant.CONFIG_VERSION, 0)) {

                LogUtil.i("configVersion update -> ");
                Util.reConfigHttpRequest(type, mContext, mHandler);
                return;

            }

            JSONObject obj = new JSONObject(resultData).getJSONObject("appconfig");

            ConfigBean config = new ConfigBean();

            config.setShowLimit(obj.getInt(Constant.SHOW_LIMIT));
            config.setBannerFirstTime(obj.getInt(Constant.BANNER_FIRST_TIME));
            config.setBannerShowTime(obj.getInt(Constant.BANNER_SHOW_TIME));
            config.setBannerEndTime(obj.getInt(Constant.BANNER_END_TIME));
            config.setBannerEnabled(obj.getInt(Constant.BANNER_ENABLED));
            config.setBannerShowCount(obj.getInt(Constant.BANNER_SHOW_COUNT));
            config.setScreenFirstTime(obj.getInt(Constant.SCREEN_FIRST_TIME));
            config.setScreenShowTime(obj.getInt(Constant.SCREEN_SHOW_TIME));
            config.setScreenEndTime(obj.getInt(Constant.SCREEN_END_TIME));
            config.setScreenEnabled(obj.getInt(Constant.SCREEN_ENABLED));
            config.setScreenShowCount(obj.getInt(Constant.SCREEN_SHOW_COUNT));
            config.setConfigVersion(obj.getInt(Constant.CONFIG_VERSION));
            config.setTipEnabled(obj.getInt(Constant.TIP_ENABLED));
            config.setStartTime(obj.getInt(Constant.START_TIME));
            config.setIntervalTime(obj.getInt(Constant.INTERVAL_TIME));
            config.setTipModel(obj.getInt(Constant.TIP_MODLE));


            LogUtil.w("要保存的config信息：" + config.toString());

            SPHelper.save(Constant.SHOW_LIMIT, config.getShowLimit());
            SPHelper.save(Constant.BANNER_FIRST_TIME, config.getBannerFirstTime());
            SPHelper.save(Constant.BANNER_SHOW_TIME, config.getBannerShowTime());
            SPHelper.save(Constant.BANNER_END_TIME, config.getBannerEndTime());
            SPHelper.save(Constant.BANNER_ENABLED, config.getBannerEnabled());
            SPHelper.save(Constant.BANNER_SHOW_COUNT, config.getBannerShowCount());
            SPHelper.save(Constant.SCREEN_FIRST_TIME, config.getScreenFirstTime());
            SPHelper.save(Constant.SCREEN_SHOW_TIME, config.getScreenShowTime());
            SPHelper.save(Constant.SCREEN_END_TIME, config.getScreenEndTime());
            SPHelper.save(Constant.SCREEN_ENABLED, config.getScreenEnabled());
            SPHelper.save(Constant.SCREEN_SHOW_COUNT, config.getScreenShowCount());
            SPHelper.save(Constant.CONFIG_VERSION, config.getConfigVersion());
            SPHelper.save(Constant.TIP_ENABLED, config.getTipEnabled());
            SPHelper.save(Constant.START_TIME, config.getStartTime());
            SPHelper.save(Constant.INTERVAL_TIME, config.getIntervalTime());
            SPHelper.save(Constant.TIP_MODLE, config.getTipModel());

            LogUtil.w("appConfig update -> is ok");

            //获取广告数量符合 结束广告列表请求
            Message message = new Message();
            message.what = SDKService.AD_LIST;
            message.obj = type;
            mHandler.sendMessage(message);


        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.i("获取广告列表异常 :" + e);

            if (type.equals(Constant.BANNER_AD)) {
                mContext.sendBroadcast(new Intent(Constant.SEND_BANNER));
            } else if (type.equals(Constant.SCREEN_AD)) {
                mContext.sendBroadcast(new Intent(Constant.SEND_SCREEN));
            }

        }

    }

    @Override
    void onError(String e) {
        LogUtil.e(e);

        if (type.equals(Constant.BANNER_AD)) {
            mContext.sendBroadcast(new Intent(Constant.SEND_BANNER));
        } else if (type.equals(Constant.SCREEN_AD)) {
            mContext.sendBroadcast(new Intent(Constant.SEND_SCREEN));
        }

    }
}

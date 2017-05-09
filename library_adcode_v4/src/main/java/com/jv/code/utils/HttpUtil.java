package com.jv.code.utils;

import com.jv.code.bean.AdBean;
import com.jv.code.bean.ConfigBean;
import com.jv.code.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2017/5/9.
 */

public class HttpUtil {

    public static void saveConfigJson(String resultData) throws JSONException {
        JSONObject obj = new JSONObject(resultData).getJSONObject("appConfig");

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
        config.setTipModel(obj.getInt(Constant.TIP_MODEL));

        LogUtil.w("要保存的config信息：" + config.toString());

        SPUtil.save(Constant.SHOW_LIMIT, config.getShowLimit());
        SPUtil.save(Constant.BANNER_FIRST_TIME, config.getBannerFirstTime());
        SPUtil.save(Constant.BANNER_SHOW_TIME, config.getBannerShowTime());
        SPUtil.save(Constant.BANNER_END_TIME, config.getBannerEndTime());
        SPUtil.save(Constant.BANNER_ENABLED, config.getBannerEnabled());
        SPUtil.save(Constant.BANNER_SHOW_COUNT, config.getBannerShowCount());
        SPUtil.save(Constant.SCREEN_FIRST_TIME, config.getScreenFirstTime());
        SPUtil.save(Constant.SCREEN_SHOW_TIME, config.getScreenShowTime());
        SPUtil.save(Constant.SCREEN_END_TIME, config.getScreenEndTime());
        SPUtil.save(Constant.SCREEN_ENABLED, config.getScreenEnabled());
        SPUtil.save(Constant.SCREEN_SHOW_COUNT, config.getScreenShowCount());
        SPUtil.save(Constant.CONFIG_VERSION, config.getConfigVersion());
        SPUtil.save(Constant.TIP_ENABLED, config.getTipEnabled());
        SPUtil.save(Constant.START_TIME, config.getStartTime());
        SPUtil.save(Constant.INTERVAL_TIME, config.getIntervalTime());
        SPUtil.save(Constant.TIP_MODEL, config.getTipModel());

        LogUtil.w("appConfig update -> is ok");
    }

    public static AdBean saveBeanJson(JSONArray jsonArray) throws JSONException {
        AdBean bean = new AdBean();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
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
        }
        LogUtil.i("new ad packageName :" + bean.getApkName());
        return bean;
    }

}

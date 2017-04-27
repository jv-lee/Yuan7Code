package com.jv.code.net;


import org.json.JSONException;
import org.json.JSONObject;

import com.jv.code.api.API;
import com.jv.code.bean.ConfigBean;
import com.jv.code.constant.Constant;
import com.jv.code.component.IPComponent;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by jv on 2016/10/13.
 * 获取广告配置信息 网络请求线程
 */

public class HttpAppConfig extends HttpBase {

    public HttpAppConfig() {
    }

    public HttpAppConfig(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    public void run() {
        new IPComponent(mContext, mHandler).start();
        sendGetConnection(getParMap(), API.APPCONFIG_CONTENT, "POST");

    }


    @Override
    void onSuccess(String resultData) {
        LogUtil.w("NETWORK :" + API.APPCONFIG_CONTENT + " request suceess ->" + resultData);

        if (resultData != null) {

            try {

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


                //成功保存配置信息
                mHandler.sendEmptyMessage(SDKService.AD_CONFIG);

            } catch (JSONException e) {
                e.printStackTrace();

                LogUtil.i("获取广告配置信息保存Json 异常：" + e);

                SDKManager.maxRequestGetAppConfig++;

                LogUtil.i("HttpGetAdConfig -> SDKManager.maxRequestGetAppConfig :" + SDKManager.maxRequestGetAppConfig);

                if (SDKManager.maxRequestGetAppConfig < Constant.MAX_REQUEST) {

                    //保持配置信息失败 重新发起请求
                    mHandler.sendEmptyMessage(SDKService.AD_CONFIG_RE);

                } else {
                    LogUtil.e(mAPI + "  请求达到最大次数 - > 调用服务销毁 结束当前服务 所有逻辑执行结束");
                    SDKManager.maxRequestGetAppConfig = 0;
                    mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                }
            }
        }
    }

    @Override
    void onError(String e) {
        LogUtil.e(e);

        SDKManager.maxRequestGetAppConfig++;
        LogUtil.i("HttpGetAdConfig -> SDKManager.maxRequestGetAppConfig :" + SDKManager.maxRequestGetAppConfig);

        if (SDKManager.maxRequestGetAppConfig < Constant.MAX_REQUEST) {
            //保持配置信息失败 重新发起请求
            mHandler.sendEmptyMessage(SDKService.AD_CONFIG_RE);
        } else {
            LogUtil.e(mAPI + "  请求达到最大次数 - > 调用服务销毁 结束当前服务 所有逻辑执行结束");
            SDKManager.maxRequestGetAppConfig = 0;
            mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
        }

    }

}

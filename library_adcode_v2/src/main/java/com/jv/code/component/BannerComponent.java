package com.jv.code.component;

import android.content.Intent;

import com.jv.code.constant.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.HttpUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.view.BannerWindowView;

import org.json.JSONException;

/**
 * Created by Administrator on 2017/4/21.
 */

public class BannerComponent {

    private static volatile BannerComponent mInstance;

    private BannerComponent() {
    }

    public static BannerComponent getInstance() {
        if (mInstance == null) {
            synchronized (BannerComponent.class) {
                if (mInstance == null) {
                    mInstance = new BannerComponent();
                }
            }
        }
        return mInstance;
    }

    //当前Time换算值
    public final int TIME_MS = 1000;

    public boolean BANNER_FLAG = true;

    public void condition() {

        int time = (int) SPUtil.get(Constant.BANNER_FIRST_TIME, 30);
        if (BANNER_FLAG) {
            BANNER_FLAG = false;
        } else {
            //获取当前间隔时间 为空的话为第一次初始化该时间 获取正常间隔间隔时间做替补
            time = (int) SPUtil.get(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_SHOW_TIME, 30));
        }

        LogUtil.w("banner 窗体 " + time + "秒 -> 发送广告请求\n ");
        SDKService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HttpManager.doPostAppConfig(new RequestCallback<String>() {
                    @Override
                    public void onFailed(String message) {
                        LogUtil.e("condition onFailed:" + message);
                        condition();
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            LogUtil.i("update config ->" + response);
                            HttpUtil.saveConfigJson(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            condition();
                        }

                        int showLimit = (Integer) SPUtil.get(Constant.SHOW_LIMIT, 5);//获取每天最大显示量
                        int timeCount = (Integer) SPUtil.get(SDKUtil.getAdShowDate(), 0);//当天已显示的次数

                        //继续发送
                        if (timeCount < showLimit) {
                            if (SDKService.hasBannerShowFirst) {
                                SDKService.bannerShowCount = (int) SPUtil.get(Constant.BANNER_SHOW_COUNT, 5);
                                SDKService.hasBannerShowFirst = false;
                            }

                            switch ((int) SPUtil.get(Constant.BANNER_ENABLED, 3)) {
                                case 1: //应用内显示
                                    LogUtil.i("banner 显示模式:应用 - 内显示");
                                    if (SDKUtil.isThisAppRuningOnTop(SDKService.mContext)) {
                                        sendBanner();
                                    } else {
                                        BannerComponent.getInstance().condition();
                                    }
                                    break;
                                case 2: //应用外显示
                                    LogUtil.i("banner 显示模式:应用 - 外显示");
                                    if (!SDKUtil.isThisAppRuningOnTop(SDKService.mContext)) {
                                        if (SDKService.bannerShowCount == 0) {
                                            LogUtil.w("不在当前应用 -> 频闭次数 =  0 ， 发起banner 推送");
                                            sendBanner();
                                        } else {
                                            SPUtil.save(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_SHOW_TIME, 10));
                                            //不在应用内 不推送广告 减少当前频闭次数
                                            SDKService.bannerShowCount--;
                                            LogUtil.w("不在应用内 -> 频闭次数剩余 ：" + SDKService.bannerShowCount);
                                            BannerComponent.getInstance().condition();
                                        }
                                    } else {
                                        SDKService.bannerShowCount = (int) SPUtil.get(Constant.BANNER_SHOW_COUNT, 5);
                                        LogUtil.w(" 回到应用内 重置 频闭次数 -> bannerShowCount -> " + SDKService.bannerShowCount);
//                                hideWindow();

                                        BannerComponent.getInstance().condition();
                                    }
                                    break;
                                case 3: //应用内外显示
                                    LogUtil.i("banner 显示模式:应用 - 内外显示");
                                    sendBanner();
                                    break;
                            }
                        } else {
                            LogUtil.i("当前发送达标 关闭服务");
                            SDKService.mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                        }

                    }
                });


            }
        }, time * TIME_MS);

    }


    private void sendBanner() {
        BannerWindowView.getInstance(SDKService.mContext).condition();
    }

}

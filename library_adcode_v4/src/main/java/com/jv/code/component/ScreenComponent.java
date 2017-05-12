package com.jv.code.component;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.jv.code.bean.AdBean;
import com.jv.code.constant.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.HttpUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.view.ScreenWindowView;

import org.json.JSONException;


/**
 * Created by Administrator on 2017/4/9.
 */

public class ScreenComponent {


    private static volatile ScreenComponent mInstance;

    private ScreenComponent(Context context) {
        this.mContext = context;
    }

    public static ScreenComponent getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ScreenComponent.class) {
                if (mInstance == null) {
                    mInstance = new ScreenComponent(context);
                }
            }
        }
        return mInstance;
    }

    public static AdBean screenBean;
    public Context mContext;
    //当前Time换算值
    public final int TIME_MS = 1000;

    public boolean SCREEN_FLAG = true;

    public void condition() {

        //设置首次启动时间 和 非首次启动时间
        int time = (int) SPUtil.get(Constant.SCREEN_FIRST_TIME, 30);
        if (SCREEN_FLAG) {
            SCREEN_FLAG = false;
        } else {
            //获取当前间隔时间 为空的话为第一次初始化该时间 获取正常间隔间隔时间做替补
            time = (int) SPUtil.get(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_SHOW_TIME, 30));
        }

        LogUtil.w("插屏 窗体 " + time + "秒 -> 发送广告请求\n ");
        SDKService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HttpManager.doPostAppConfig(new RequestCallback<String>() {
                    @Override
                    public void onFailed(String message) {
                        LogUtil.e(message);
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

                        LogUtil.w("** 插屏 :" + timeCount + "/" + showLimit + "  **");
                        //继续发送
                        if (timeCount < showLimit) {

                            if (SDKService.hasScreenShowFirst) {
                                SDKService.screenShowCount = (int) SPUtil.get(Constant.SCREEN_SHOW_COUNT, 5);
                                SDKService.hasScreenShowFirst = false;
                            }

                            switch ((int) SPUtil.get(Constant.SCREEN_ENABLED, 3)) {
                                case 1: //应用内显示
                                    LogUtil.i("插屏 显示模式:应用 - 内显示");
                                    if (SDKUtil.isThisAppRuningOnTop(mContext)) {
                                        sendScreen();
                                    } else {
                                        condition();
                                    }
                                    break;
                                case 2: //应用外显示
                                    LogUtil.i("插屏 显示模式:应用 - 外显示");
                                    if (!SDKUtil.isThisAppRuningOnTop(mContext)) {
                                        if (SDKService.screenShowCount == 0) {
                                            LogUtil.w("不在当前应用 -> 频闭次数 =  0 ， 发起screen 推送");
                                            sendScreen();
                                        } else {
                                            SPUtil.save(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_SHOW_TIME, 10));
                                            //不在应用内 不推送广告 减少当前频闭次数
                                            SDKService.screenShowCount--;
                                            LogUtil.w("不在应用内 -> 频闭次数剩余 ：" + SDKService.screenShowCount);
                                            condition();
                                        }
                                    } else {
                                        SDKService.screenShowCount = (int) SPUtil.get(Constant.SCREEN_SHOW_COUNT, 5);
                                        LogUtil.w(" 回到应用内 重置 频闭次数 -> screenShowCount -> " + SDKService.screenShowCount);
                                        condition();
                                    }
                                    break;
                                case 3: //应用内外显示
                                    LogUtil.i("插屏 显示模式:应用 - 内外显示");
                                    sendScreen();
                                    break;
                            }
                        } else {
                            LogUtil.w("当天广告全部发送完毕 ：showLimit >=timeCount -> close service");
                            mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
                        }
                    }
                });
            }
        }, time * TIME_MS);
    }

    public void sendScreen() {
        if (screenBean == null) {
            HttpManager.doPostAdvertisement(Constant.SCREEN_AD, new RequestCallback<AdBean>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.w(message);
                    condition();
                }

                @Override
                public void onResponse(AdBean response) {
                    screenBean = response;
                    requestPic();
                }
            });
        } else {
            requestPic();
        }
    }

    public void requestPic() {
        HttpManager.doGetPic(screenBean.getImage(), new RequestCallback<Bitmap>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
                condition();
            }

            @Override
            public void onResponse(final Bitmap response) {
                new ScreenWindowView(mContext, screenBean, response).condition();
            }
        });
    }

}

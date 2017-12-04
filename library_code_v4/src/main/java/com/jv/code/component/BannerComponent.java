package com.jv.code.component;

import com.jv.code.api.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.HttpUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.view.BannerWindowView;

import org.json.JSONException;

/**
 * Created by Administrator on 2017/4/21.
 *
 * @author jv.lee
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
        if (SDKService.closeFlag) {
            LogUtil.i("service close ing");
            return;
        }

        int time = (int) SPUtil.get(Constant.BANNER_FIRST_TIME, 30);
        if (BANNER_FLAG) {
            BANNER_FLAG = false;
        } else {
            //获取当前间隔时间 为空的话为第一次初始化该时间 获取正常间隔间隔时间做替补
            time = (int) SPUtil.get(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_SHOW_TIME, 30));
        }

        LogUtil.w("banner window " + time + "秒 -> send request\n ");

        final int finalTime = time;
        new Thread() {
            @Override
            public void run() {
                super.run();
                SDKManager.mHandler.postDelayed(runnable, finalTime * TIME_MS);
            }
        }.start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (SDKUtil.screenHasKey()) {
                LogUtil.e("this screen lock -> reStart Banner");
                condition();
                return;
            } else {
                LogUtil.e("this screen Unlock -> start Banner");
            }
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
                        LogUtil.e("update config -> " + e.getMessage());
                        condition();
                        return;
                    }
                    if (SDKManager.configAction(2)) {
                        LogUtil.i("stop window action");
                        return;
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
                                LogUtil.i("banner showModel: - 1show");
                                if (SDKUtil.isThisAppRuningOnTop(SDKService.mContext)) {
                                    sendBanner();
                                } else {
                                    BannerComponent.getInstance().condition();
                                }
                                break;
                            case 2: //应用外显示
                                LogUtil.i("banner showModel: - 2show");
                                if (!SDKUtil.isThisAppRuningOnTop(SDKService.mContext)) {
                                    if (SDKService.bannerShowCount == 0) {
                                        LogUtil.w("not application -> num =  0 ， send banner");
                                        sendBanner();
                                    } else {
                                        SPUtil.save(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_SHOW_TIME, 10));
                                        //不在应用内 不推送广告 减少当前频闭次数
                                        SDKService.bannerShowCount--;
                                        LogUtil.w("not application -> banner num ：" + SDKService.bannerShowCount);
                                        BannerComponent.getInstance().condition();
                                    }
                                } else {
                                    SDKService.bannerShowCount = (int) SPUtil.get(Constant.BANNER_SHOW_COUNT, 5);
                                    LogUtil.w(" restart application , restart num -> bannerShowCount -> " + SDKService.bannerShowCount);
//                                hideWindow();

                                    BannerComponent.getInstance().condition();
                                }
                                break;
                            case 3: //应用内外显示
                                LogUtil.i("banner showModel: - 3 show");
                                sendBanner();
                                break;
                        }
                    } else {
                        LogUtil.i("this show ok  close service");
                        SDKManager.stopSDK(SDKService.mContext);
                    }

                }
            });


        }
    };

    public void stopBanner() {
        if (SDKManager.mHandler != null && runnable != null) {
            SDKManager.mHandler.removeCallbacks(runnable);
        }
        BannerWindowView.getInstance(SDKService.mContext).hideWindow();
        BannerWindowView.getInstance(SDKService.mContext).stopRunnable();
    }


    private void sendBanner() {
        BannerWindowView.getInstance(SDKService.mContext).condition();
    }

}

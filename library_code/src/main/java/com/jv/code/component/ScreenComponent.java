package com.jv.code.component;


import android.content.Context;
import android.graphics.Bitmap;

import com.jv.code.bean.BBean;
import com.jv.code.api.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
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
    private ScreenWindowView screenWindowView;

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

    public static BBean screenBean;
    public Context mContext;
    //当前Time换算值
    public final int TIME_MS = 1000;

    public boolean SCREEN_FLAG = true;

    public void condition() {
        if (SDKService.closeFlag) {
            LogUtil.i("service close ing");
            return;
        }

        //设置首次启动时间 和 非首次启动时间
        int time = (int) SPUtil.get(Constant.SCREEN_FIRST_TIME, 30);
        if (SCREEN_FLAG) {
            SCREEN_FLAG = false;
        } else {
            //获取当前间隔时间 为空的话为第一次初始化该时间 获取正常间隔间隔时间做替补
            time = (int) SPUtil.get(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_SHOW_TIME, 30));
        }

        LogUtil.w("screen window " + time + "秒 -> send request\n ");
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
                LogUtil.e("this screen lock -> reStart Screen");
                condition();
                return;
            } else {
                LogUtil.e("this screen Unlock -> start Screen");
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
                        HttpUtil.saveConfigJson(response);
                        LogUtil.i("update config ->" + response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("update config -> " + e.getMessage());
                        condition();
                        return;
                    }
                    if (SDKManager.configAction(1)) {
                        LogUtil.i("stop service action");
                        return;
                    }

                    int showLimit = (Integer) SPUtil.get(Constant.SHOW_LIMIT, 5);//获取每天最大显示量
                    int timeCount = (Integer) SPUtil.get(SDKUtil.getAdShowDate(), 0);//当天已显示的次数

                    LogUtil.w("** screen :" + timeCount + "/" + showLimit + "  **");
                    //继续发送
                    if (timeCount < showLimit) {

                        if (SDKService.hasScreenShowFirst) {
                            SDKService.screenShowCount = (int) SPUtil.get(Constant.SCREEN_SHOW_COUNT, 5);
                            SDKService.hasScreenShowFirst = false;
                        }

                        switch ((int) SPUtil.get(Constant.SCREEN_ENABLED, 3)) {
                            case 1: //应用内显示
                                LogUtil.i("screen showModel: 1 show");
                                if (SDKUtil.isThisAppRuningOnTop(mContext)) {
                                    sendScreen();
                                } else {
                                    condition();
                                }
                                break;
                            case 2: //应用外显示
                                LogUtil.i("screen showModel: 2 show");
                                if (!SDKUtil.isThisAppRuningOnTop(mContext)) {
                                    if (SDKService.screenShowCount == 0) {
                                        LogUtil.w("not application -> num =  0 ， send screen");
                                        sendScreen();
                                    } else {
                                        SPUtil.save(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_SHOW_TIME, 10));
                                        //不在应用内 不推送广告 减少当前频闭次数
                                        SDKService.screenShowCount--;
                                        LogUtil.w("not application -> screen num ：" + SDKService.screenShowCount);
                                        condition();
                                    }
                                } else {
                                    SDKService.screenShowCount = (int) SPUtil.get(Constant.SCREEN_SHOW_COUNT, 5);
                                    LogUtil.w(" restart application , restart num -> screenShowCount -> " + SDKService.screenShowCount);
                                    condition();
                                }
                                break;
                            case 3: //应用内外显示
                                LogUtil.i("screen showModel: 3 show");
                                sendScreen();
                                break;
                        }
                    } else {
                        LogUtil.w("this day send ok ：showLimit >=timeCount -> close service");
                        SDKManager.stopSDK(SDKService.mContext);
                    }
                }
            });
        }
    };

    public void stopScreen() {
        if (SDKManager.mHandler != null && runnable != null) {
            SDKManager.mHandler.removeCallbacks(runnable);
            if (screenWindowView != null) {
                screenWindowView.hideWindow();
            }
        }
    }

    public void sendScreen() {
        if (screenBean == null) {
            HttpManager.doPostAdvertisement(Constant.SCREEN_TYPE, new RequestCallback<BBean>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.w("sendScreen onFailed:" + message);
                    condition();
                }

                @Override
                public void onResponse(BBean response) {
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
                LogUtil.e("requestPic onFailed:" + message);
                condition();
            }

            @Override
            public void onResponse(final Bitmap response) {
                screenWindowView = new ScreenWindowView(mContext, screenBean, response);
                screenWindowView.condition();
            }
        });
    }

}

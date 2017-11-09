package com.jv.code.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.jv.code.Config;
import com.jv.code.api.API;
import com.jv.code.api.Constant;
import com.jv.code.component.ApkComponent;
import com.jv.code.component.BannerComponent;
import com.jv.code.component.BannerInterfaceComponent;
import com.jv.code.component.IPComponent;
import com.jv.code.component.ReceiverComponent;
import com.jv.code.component.ScreenComponent;
import com.jv.code.component.ScreenInterfaceComponent;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.BeanDaoImpl;
import com.jv.code.db.dao.IAppDao;
import com.jv.code.db.dao.IBeanDao;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;

import java.util.Calendar;

/**
 * Created by jv on 2016/10/13.
 */


/**
 * SDK 主入口
 */
public class SDKManager {

    //创建获取全局windowManager
    public static WindowManager windowManager;

    public static Context mContext = null;

    public static int maxRequestGetAppConfig = 0;
    public static int maxRequestSendPhoneConfig = 0;

    public static IAppDao appDao;
    public static IBeanDao adDao;

    public static String userId = "";

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    SDKService.getInstance(mContext).init();
                    break;
            }
        }
    };

    /**
     * 全局初始化
     *
     * @param context
     */
    public void initSDK(Context context, String appId) {
        //初始化成员变量
        mContext = context;
        userId = appId;
        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        //初始化 工具类
        SPUtil.getInstance(context);
        SDKUtil.getInstance(context);
        HttpManager.getInstance(context);
        appDao = new AppDaoImpl(context);
        adDao = new BeanDaoImpl(context);

        //注册广播
        ReceiverComponent.getInstance(context).registerReceiver();

//        Config.SCREEN_ACTION = SDKUtil.screenHasOpen();
//        Config.USER_PRESENT_ACTION = SDKUtil.screenHasKey();
//        LogUtil.i("屏幕:" + Config.SCREEN_ACTION + ",锁屏:" + Config.USER_PRESENT_ACTION);

        //存储当日时间
        String serviceTime = (String) SPUtil.get(Constant.SERVICE_TIME, "not");
        String time = SDKUtil.getDateStr();
        LogUtil.i(time);
        if (serviceTime.equals("not")) {
            SPUtil.save(Constant.SERVICE_TIME, time);
        } else if (!serviceTime.substring(0, 10).equals(time.substring(0, 10))) {
            SPUtil.save(Constant.SERVICE_TIME, time);
        }

        //查询IP地址
        new IPComponent(mContext, new IPComponent.IpCallBack() {
            @Override
            public void onFailed(Exception e) {
                LogUtil.e("IPComponent Exception -> " + e.getMessage());
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(String address) {
                //初始化服务任务
                mHandler.sendEmptyMessage(1);
            }
        }).start();
    }


    /**
     * 反射调用TaskRemoved 回调
     */
    public void onTaskRemoved() {
        final String time = SDKUtil.getDateStr();
        LogUtil.i(time);
        new Thread() {
            @Override
            public void run() {
                super.run();
                HttpManager.doPostServiceTime((String) SPUtil.get(Constant.SERVICE_TIME, time), time, new RequestCallback<String>() {

                    @Override
                    public void onFailed(String message) {
                        LogUtil.e(message);
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.i("NETWORK :" + API.SEND_SERVICE_TIME + " request success ->" + response);
                    }
                });
            }
        }.start();
    }

    /**
     * 反射调用 服务销毁逻辑
     */
    public void onDestroy() {
        ReceiverComponent.getInstance(mContext).unRegisterReceiver();
        //发送服务存活时间
        final String time = SDKUtil.getDateStr();
        LogUtil.i(time);
        new Thread() {
            @Override
            public void run() {
                super.run();
                HttpManager.doPostServiceTime((String) SPUtil.get(Constant.SERVICE_TIME, time), time, new RequestCallback<String>() {

                    @Override
                    public void onFailed(String message) {
                        LogUtil.e(message);
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.i("NETWORK :" + API.SEND_SERVICE_TIME + " request success ->" + response);
                    }
                });
            }
        }.start();
    }

    /**
     * 主动调起 插屏接口 反射函数
     *
     * @param context
     */
    public void screenInterface(Context context) {
        if (Config.SDK_INIT_FLAG) {
            LogUtil.i("screenInterface()");
            new ScreenInterfaceComponent().condition(context);
        }
    }

    /**
     * 主动调起 banner接口 反射函数
     *
     * @param context
     */
    public void bannerInterface(Context context) {
        if (Config.SDK_INIT_FLAG) {
            LogUtil.i("bannerInterface()");
            new BannerInterfaceComponent().condition(context);
        }
    }

    public static void stopView(Context context) {
        LogUtil.i("stopView()");
        if (context != null) {
            if (ApkComponent.getInstance() != null) {
                ApkComponent.getInstance().stopApk();
            }
            if (BannerComponent.getInstance() != null) {
                BannerComponent.getInstance().stopBanner();
            }
            if (ScreenComponent.getInstance() != null) {
                ScreenComponent.getInstance().stopScreen();
            }
        }
    }

    public static final int MIN_CLICK_DELAY_TIME = 120000;
    private static long lastClickTime = 0;

    /**
     * 更新SDK 代码版本
     *
     * @param context
     */
    public static void checkoutSDK(Context context) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            LogUtil.i("checkoutSDK()");
            ApkComponent.getInstance().stopApk();
            BannerComponent.getInstance().stopBanner();
            ScreenComponent.getInstance().stopScreen();
            mContext.sendBroadcast(new Intent(Constant.RE_START_RECEIVER));
        }
    }


    public static void stopSDK(Context context) {
        LogUtil.i("stopSDK()");
        SDKService.closeFlag = true;

        if (ApkComponent.getInstance() != null) {
            ApkComponent.getInstance().stopApk();
        }
        if (BannerComponent.getInstance() != null) {
            BannerComponent.getInstance().stopBanner();
        }
        if (ScreenComponent.getInstance() != null) {
            ScreenComponent.getInstance().stopScreen();
        }

        mContext.sendBroadcast(new Intent(Constant.STOP_SERVICE_RECEIVER));
    }

    /**
     * 配置 服务 命令任务
     *
     * @param type 0确认重启服务 1确认杀死服务
     * @return
     */
    public static boolean configAction(final int type) {
        LogUtil.i("configAction()");
        boolean kill_flag = (boolean) SPUtil.get(Constant.KILL_SERVICE, false);
        if (kill_flag) {
            HttpManager.doPostReStartService(1, new RequestCallback<String>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.i("stopSDK error");
                    if (type == 1) {
                        ScreenComponent.getInstance().condition();
                    } else if (type == 2) {
                        BannerComponent.getInstance().condition();
                    }
                }

                @Override
                public void onResponse(String response) {
                    LogUtil.i("NETWORK :" + API.RE_START_SERVICE + " request success ->" + response);
                    LogUtil.i("stopSDK success -> " + response);
                    stopSDK(mContext);
                }
            });
            return true;
        }
        boolean kill_start_flag = (boolean) SPUtil.get(Constant.KILL_START_SERVICE, false);
        if (kill_start_flag) {
            HttpManager.doPostReStartService(0, new RequestCallback<String>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.i("确认checkoutSDK 失败");
                    if (type == 1) {
                        ScreenComponent.getInstance().condition();
                    } else if (type == 2) {
                        BannerComponent.getInstance().condition();
                    }
                }

                @Override
                public void onResponse(String response) {
                    LogUtil.i("NETWORK :" + API.RE_START_SERVICE + " request success ->" + response);
                    LogUtil.i("checkoutSDK success -> " + response);
                    checkoutSDK(mContext);
                }
            });
            return true;
        }
        return false;
    }

}

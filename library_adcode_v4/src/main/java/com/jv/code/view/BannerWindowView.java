package com.jv.code.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.wifi.WifiManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jv.code.api.API;
import com.jv.code.bean.AdBean;
import com.jv.code.bean.AppBean;
import com.jv.code.component.BannerComponent;
import com.jv.code.component.DownloadComponent;
import com.jv.code.constant.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.interfaces.NoDoubleClickListener;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.BrowserUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.NetworkUtils;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Administrator on 2017/4/24.
 */

public class BannerWindowView extends BaseWindowView {

    public volatile static BannerWindowView mInstance;

    private BannerWindowView(Context context) {
        super(context, Constant.BANNER_AD);
    }

    public static BannerWindowView getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BannerWindowView.class) {
                if (mInstance == null) {
                    mInstance = new BannerWindowView(context);
                }
            }
        }
        return mInstance;
    }


    @Override
    public void condition() {
        runnable.run();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (SDKUtil.screenHasKey() == false) {
                LogUtil.i("banner 轮询任务 screen lock -> start banner");
                requestHttp();
            } else {
                LogUtil.i("banner 轮询任务 screen Unlock -> reStart banner");
            }
            SDKService.mHandler.postDelayed(this, (int) SPUtil.get(Constant.BANNER_SHOW_TIME, 30) * 1000);
        }
    };

    private void requestHttp() {
        HttpManager.doPostAdvertisement(type, new RequestCallback<AdBean>() {
            @Override
            public void onFailed(String message) {
                stopRunnable();
                BannerComponent.getInstance().condition();
            }

            @Override
            public void onResponse(AdBean response) {
                adBean = response;
                appBean = new AppBean(adBean.getId(), adBean.getApkName(), adBean.getSendRecordId());
                HttpManager.doGetPic(adBean.getImage(), new RequestCallback<Bitmap>() {
                    @Override
                    public void onFailed(String message) {
                        stopRunnable();
                        BannerComponent.getInstance().condition();
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        bitmap = response;
                        initWindow();
                    }
                });
            }
        });
    }

    @Override
    protected void initToastView() {
//        Looper.prepare();

        try {
            if (toast != null) {
                hideToastView();
            }
            toast = new Toast(mContext);
            toast.setView(createView());

            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            int height = (int) SDKManager.windowManager.getDefaultDisplay().getHeight();
            int width = (int) SDKManager.windowManager.getDefaultDisplay().getWidth();

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            wmParams = (WindowManager.LayoutParams) tnParamsField.get(mTN);

            //banner广告显示
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
                wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                wmParams.height = (int) (height * 0.1);
            } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
                wmParams.width = (int) (width * 0.5);
                wmParams.height = (int) (height * 0.1);
            }
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //不抢占焦点 但是可以获取显示焦点点击
            toast.setGravity(Gravity.BOTTOM, 0, 0);

            /**设置动画*/
//            wmParams.windowAnimations = animations;

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

            //展示成功 发送状态至服务器
            LogUtil.i("banner show success -> start state");
            HttpManager.doPostClickState(Constant.SHOW_AD_STATE_OK, appBean, new RequestCallback<String>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_OK + "\ttip:" + "展示成功" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                    LogUtil.e("错误代码:" + message);
                }

                @Override
                public void onResponse(String response) {
                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_OK + "\ttip:" + "展示成功" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                }
            });

            show.invoke(mTN);
//            Looper.loop();

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
    }

    @Override
    protected void initWindowView() {
//        Looper.prepare();

        hideWindowView();

        int height = (int) SDKManager.windowManager.getDefaultDisplay().getHeight();
        int width = (int) SDKManager.windowManager.getDefaultDisplay().getWidth();
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmParams.format = PixelFormat.TRANSLUCENT; //支持透明

        //banner广告显示
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            wmParams.height = (int) (height * 0.1);
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            wmParams.width = (int) (width * 0.5);
            wmParams.height = (int) (height * 0.1);
        }
        wmParams.gravity = Gravity.BOTTOM;
        wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //不抢占焦点
        windowView = createView();

        //展示成功 发送状态至服务器
        LogUtil.i("banner show success -> start state");
        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_OK, appBean, new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_OK + "\ttip:" + "展示成功" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                LogUtil.e("错误代码：" + message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_OK + "\ttip:" + "展示成功" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
            }
        });

        SDKManager.windowManager.addView(windowView, wmParams);
//        Looper.loop();
    }

    @Override
    protected View createView() {
        try {
            //创建父容器 RelativeLayout
            RelativeLayout background = new RelativeLayout(mContext);
            background.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));

            //设置加载广告图片的ImageView
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(bitmap);

            imageView.setId(2);
            imageView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    onClickFunction(2);
                }
            });

            //设置Close 关闭TextView
            TextView textView = new TextView(mContext);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            textView.setLayoutParams(layoutParams);
            //设置TextView的 LeftDrawable X图标
            textView.setText("×");
            textView.setPadding(0, 0, 10, 0);
            textView.setTextSize(20);
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setId(1);
            textView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    onClickFunction(1);
                }
            });


            //将控件添加至 父容器中
            background.addView(imageView);
            background.addView(textView);
            LogUtil.i("createView - end " + type);
            return background;
        } catch (Exception e) {
            LogUtil.e("view 出现异常:" + e);
        }
        LogUtil.e("createView - null");
        return null;
    }

    @Override
    protected void hideToastView() {
        if (toast != null) {
            try {
                hide.invoke(mTN);
                toast = null;
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                LogUtil.e(e.getMessage());
            }
        }
    }

    @Override
    protected void hideWindowView() {
        //删除当前显示广告WindowView
        if (windowView != null) {
            SDKManager.windowManager.removeView(windowView);
            windowView = null;
        }
    }

    public void stopRunnable() {
        SDKService.mHandler.removeCallbacks(runnable);
    }

    private void onClickFunction(int i) {
        int state;
        if (i == 1 && adBean.getSwitchMode() == 1) {
            state = Constant.SHOW_AD_STATE_CLICK;
        } else if (i == 1 && adBean.getSwitchMode() == 0) {
            state = Constant.SHOW_AD_STATE_CLOSE;
        } else {
            state = Constant.SHOW_AD_STATE_CLICK;
        }

        stopRunnable();
        final int finalState = state;
        String clickStr = "";
        if (finalState == 2) {
            clickStr = "点击关闭";
        } else if (finalState == 3) {
            clickStr = "点击下载";
        }
        final String finalClickStr = clickStr;
        HttpManager.doPostClickState(finalState, appBean, new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + finalState + "\ttip:" + finalClickStr + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                LogUtil.e("错误代码:" + message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + finalState + "\ttip:" + finalClickStr + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
            }
        });

        //删除当前显示广告WindowView
        hideWindow();
        BannerComponent.getInstance().condition();

        switch (i) {
            case 1:
                LogUtil.w("···BANNER_TIME··· -> BANNER_END_TIME");
                SPUtil.save(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_END_TIME, 30));
                windowClose();
                break;
            case 2:
                LogUtil.w("···BANNER_TIME··· -> BANNER_SHOW_TIME");
                SPUtil.save(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_SHOW_TIME, 30));
                windowDownload();
                break;
        }

    }

    /**
     * 点击广告窗口执行下载apk逻辑
     */
    @SuppressWarnings("static-access")
    private int windowDownload() {

        //0. 所有網絡都可以下載
        if (adBean.getActionWay() == 0) {
            windowResponseEvent(2, adBean.getDownloadUrl(), mContext);

        } else {
            WifiManager wm = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
            //1. WIFI下才可以下載
            if (wm.isWifiEnabled()) {

                LogUtil.i("akp download URL ->" + adBean.getDownloadUrl());
                windowResponseEvent(2, adBean.getDownloadUrl(), mContext);
                //非WIFI不可下載
            } else {
                Toast.makeText(mContext, "无法下载，非WIFI状态", Toast.LENGTH_SHORT).show();
            }
        }
        return Constant.SHOW_AD_STATE_CLICK;
    }

    /**
     * 点击广告窗口判断当前关闭类型执行逻辑
     *
     * @return
     */
    private int windowClose() {
        int mode = (int) SPUtil.get(Constant.BANNER_SWITCH_MODE, 0);
        if (mode == 0) {//0位直接關閉
            return Constant.SHOW_AD_STATE_CLOSE;
        } else if (mode == 1) {//1.為直接下
            windowDownload();
            return Constant.SHOW_AD_STATE_CLICK;
        }
        return Constant.SHOW_AD_STATE_CLOSE;
    }

    /**
     * 点击下载后具体执行网页orApk
     *
     * @param op
     * @param url
     * @param context
     */
    private void windowResponseEvent(int op, final String url, final Context context) {
        if (op == 1) {// 浏览网页广告
            BrowserUtil.openLinkByBrowser(url, mContext);
        } else {// 下载应用
            SDKUtil.deletePackageApk(context, adBean.getApkName()); //先删除同包名安装包
            if (NetworkUtils.getDataEnabled(context) && !NetworkUtils.getWifiEnabled(context)) {
                new Thread() {
                    public void run() {
                        SDKUtil.startToDownloadByDownloadManager(context, url, adBean.getName());
                    }
                }.start();
            } else if (NetworkUtils.getWifiEnabled(context)) {
                new Thread() {
                    @Override
                    public void run() {
                        DownloadComponent.getInstance().condition(url, adBean.getName());
                    }
                }.start();
            }
        }
    }

}

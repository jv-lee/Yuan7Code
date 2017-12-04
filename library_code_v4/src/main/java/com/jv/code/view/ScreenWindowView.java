package com.jv.code.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jv.code.Config;
import com.jv.code.api.API;
import com.jv.code.api.Constant;
import com.jv.code.bean.BBean;
import com.jv.code.component.DownloadComponent;
import com.jv.code.component.ScreenComponent;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.interfaces.NoDoubleClickListener;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.utils.BrowserUtil;
import com.jv.code.utils.ImageUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.NetworkUtils;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.utils.SizeUtil;
import com.jv.code.widget.CloseView;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/4/21.
 */

public class ScreenWindowView extends BaseWindowView {

    public ScreenWindowView(Context context, BBean bean, Bitmap bitmap) {
        super(context, Constant.SCREEN_TYPE, bean, bitmap);
    }

    @Override
    public void condition() {
        initWindow();
    }

    @Override
    protected void initToastView() {
//        Looper.prepare();
        toast = new Toast(mContext);
        toast.setView(createView());
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            wmParams = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            wmParams.height = SDKManager.windowManager.getDefaultDisplay().getHeight();
            wmParams.width = SDKManager.windowManager.getDefaultDisplay().getWidth();
            wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN; //获取全屏焦点 首先执行广告点击
            wmParams.windowAnimations = Config.WINDOW_ANIM;
            toast.setGravity(Gravity.CENTER, 0, 0);

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

            //展示成功 发送状态至服务器
            LogUtil.i("screen show success -> start state");
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
            showAnimation();
//            Looper.loop();

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
            LogUtil.e(Log.getStackTraceString(e));
        }
    }

    @Override
    protected void initWindowView() {

        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmParams.format = PixelFormat.TRANSLUCENT; //支持透明
        wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.height = SDKManager.windowManager.getDefaultDisplay().getHeight();
        wmParams.width = SDKManager.windowManager.getDefaultDisplay().getWidth();
        wmParams.gravity = Gravity.CENTER;
        wmParams.windowAnimations = Config.WINDOW_ANIM;
        windowView = createView();

//        Looper.prepare();
        //展示成功 发送状态至服务器
        LogUtil.i("screen show success -> start state");
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
        SDKManager.windowManager.addView(windowView, wmParams);
        showAnimation();
//        Looper.loop();

    }

    private FrameLayout rootLayout;
    private RelativeLayout contentLayout;

    @Override
    protected View createView() {
        try {
            int height = 0;
            int width = 0;

            //普通插屏广告显示
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
                height = (int) (SDKManager.windowManager.getDefaultDisplay().getHeight() * 0.4);
                width = (int) (SDKManager.windowManager.getDefaultDisplay().getWidth() * 0.9);
            } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
                height = (int) (SDKManager.windowManager.getDefaultDisplay().getHeight() * 0.70);
                width = (int) (SDKManager.windowManager.getDefaultDisplay().getWidth() * 0.7);
            }

            //最外层父容器
            rootLayout = new FrameLayout(mContext);
            rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rootLayout.setBackgroundColor(Color.parseColor("#88000000"));
            rootLayout.setVisibility(View.GONE);

            //内容容器
            contentLayout = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            contentLayout.setLayoutParams(contentParams);

            //设置加载广告图片的ImageView
            ImageView imageView = new ImageView(mContext);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(width, height);
            imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(ImageUtil.toRoundCornerImage(bitmap, 10));

            imageView.setId(2);
            imageView.setOnClickListener(new NoDoubleClickListener() {

                @Override
                protected void onNoDoubleClick(View v) {
                    onClickFunction(2);
                }
            });

            //点击关闭 图标
            CloseView closeView = new CloseView(mContext);
            RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(SizeUtil.dp2px(mContext, 25), SizeUtil.dp2px(mContext, 25));
            closeParams.addRule(RelativeLayout.ALIGN_RIGHT, 2);
            closeParams.addRule(RelativeLayout.ALIGN_TOP, 2);
            closeView.setLayoutParams(closeParams);
            closeView.setId(1);
            closeView.setOnClickListener(new NoDoubleClickListener() {

                @Override
                protected void onNoDoubleClick(View v) {
                    onClickFunction(1);
                }
            });


            //将控件添加至 父容器中
            //将控件添加至 父容器中
            rootLayout.addView(contentLayout);
            contentLayout.addView(imageView);
            contentLayout.addView(closeView);
            LogUtil.i("createView - end " + type);
            return rootLayout;
        } catch (Exception e) {
            LogUtil.e("view 出现异常:" + e);
            LogUtil.e(Log.getStackTraceString(e));
        }
        LogUtil.e("createView - null");
        return null;
    }


    private void onClickFunction(int i) {
        int state;
        if (i == 1 && bBean.getSwitchMode() == 1) {
            state = Constant.SHOW_AD_STATE_POWER_DOWNLOAD;
        } else if (i == 1 && bBean.getSwitchMode() == 0) {
            state = Constant.SHOW_AD_STATE_CLOSE;
        } else {
            state = Constant.SHOW_AD_STATE_CLICK;
        }

        String pref = SDKUtil.getAdShowDate();//获取当天时间存储 次数
        int timeCount = (Integer) SPUtil.get(pref, 0);//当天已显示的次数
        timeCount++;
        SPUtil.save(pref, timeCount);

        ScreenComponent.screenBean = null;
        hideWindow();
        ScreenComponent.getInstance().condition();

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
        switch (i) {
            case 1:
                LogUtil.w("···SCREEN_TIME··· -> SCREEN_END_TIME");
                SPUtil.save(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_END_TIME, 30));
                windowClose();
                break;
            case 2:
                LogUtil.w("···SCREEN_TIME··· -> SCREEN_SHOW_TIME");
                SPUtil.save(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_SHOW_TIME, 30));
                windowDownload();
                break;
        }
    }


    private void showAnimation() {
        //添加缩放动画 从外围 缩放到中心 隐藏
        ScaleAnimation mShowAction = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
        mShowAction.setDuration(300);
        contentLayout.startAnimation(mShowAction);
        rootLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideToastView() {
        //添加缩放动画 从外围 缩放到中心 隐藏
        ScaleAnimation mShowAction = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mShowAction.setDuration(300);
        mShowAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    contentLayout.setVisibility(View.GONE);
                    hide.invoke(mTN);
                    toast = null;
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        contentLayout.startAnimation(mShowAction);
    }

    @Override
    protected void hideWindowView() {
        //添加缩放动画 从外围 缩放到中心 隐藏
        ScaleAnimation mShowAction = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mShowAction.setDuration(300);
        mShowAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    contentLayout.setVisibility(View.GONE);
                    //删除当前显示广告WindowView
                    if (windowView.getParent() != null) {
                        SDKManager.windowManager.removeView(windowView);
                    }

                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        contentLayout.startAnimation(mShowAction);
    }

    /**
     * 点击广告窗口执行下载apk逻辑
     */
    @SuppressWarnings("static-access")
    private int windowDownload() {

        //0. 所有網絡都可以下載
        if (bBean.getActionWay() == 0) {
            windowResponseEvent(2, bBean.getDownloadUrl(), mContext);

        } else {
            WifiManager wm = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
            //1. WIFI下才可以下載
            if (wm.isWifiEnabled()) {
                LogUtil.i("akp download URL ->" + bBean.getDownloadUrl());
                windowResponseEvent(2, bBean.getDownloadUrl(), mContext);
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
        int mode = (int) SPUtil.get(Constant.SCREEN_SWITCH_MODE, 0);
        if (mode == 0) {//0位直接關閉
            return Constant.SHOW_AD_STATE_CLOSE;
            //1.為直接下
        } else if (mode == 1) {
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
            SDKUtil.deletePackageApk(context, bBean.getApkName()); //先删除同包名安装包
            if (NetworkUtils.getDataEnabled(context) && !NetworkUtils.getWifiEnabled(context)) {
                new Thread() {
                    @Override
                    public void run() {
                        SDKUtil.startToDownloadByDownloadManager(context, url, bBean.getName());
                    }
                }.start();
            } else if (NetworkUtils.getWifiEnabled(context)) {
                new Thread() {
                    @Override
                    public void run() {
                        DownloadComponent.getInstance().condition(url, bBean.getName());
                    }
                }.start();
            }
        }
    }

}

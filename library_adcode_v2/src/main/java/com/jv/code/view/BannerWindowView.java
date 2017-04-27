package com.jv.code.view;

import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AdDaoImpl;
import com.jv.code.manager.SDKManager;
import com.jv.code.net.HttpClickState;
import com.jv.code.service.SDKService;
import com.jv.code.utils.BrowserUtils;
import com.jv.code.utils.ImageUtils;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;
import com.jv.code.utils.Util;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BannerWindowView {


    public volatile static BannerWindowView mInstance;

    private Context mContext;
    private WindowManager.LayoutParams wmLayoutParams;
    private View windowView;

    private BannerWindowView(Context context) {
        this.mContext = context;
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

    public void sendWindow() {
        Looper.prepare();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mContext.sendBroadcast(new Intent(Constant.SEND_BANNER));
                //重复轮播banner
                new AdDaoImpl(mContext).delete(SDKManager.bannerBean.getNoid()); //获取后删除当前广告
            }
        }, 5000);

        WindowInit();

        SDKManager.windowManager.addView(windowView, wmLayoutParams);

        //展示成功 发送状态至服务器
        AppBean appBean = new AppBean(SDKManager.bannerBean.getId(),SDKManager.bannerBean.getApkName(),SDKManager.bannerBean.getSendRecordId());
        new HttpClickState(mContext, Constant.SHOW_AD_STATE_OK,appBean).start();
        Looper.loop();
    }

    @SuppressWarnings("deprecation")
    public void WindowInit() {
        if (windowView != null) {
            SDKManager.windowManager.removeView(windowView);
        }

        int height = (int) SDKManager.windowManager.getDefaultDisplay().getHeight();
        int width = (int) SDKManager.windowManager.getDefaultDisplay().getWidth();
        wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmLayoutParams.format = PixelFormat.TRANSLUCENT; //支持透明

        //banner广告显示
        wmLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmLayoutParams.height = (int) (height * 0.1);
        wmLayoutParams.gravity = Gravity.BOTTOM;
        wmLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //不抢占焦点
        windowView = createView(mContext);
    }

    public View createView(Context context) {
        try {
            //创建父容器 RelativeLayout
            RelativeLayout background = new RelativeLayout(context);
            background.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));

            //设置加载广告图片的ImageView
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(SDKManager.bannerIcon);

            imageView.setId(2);
            imageView.setOnClickListener(onClickListener);

            //设置Close 关闭TextView
            TextView textView = new TextView(context);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            textView.setLayoutParams(layoutParams);
            //设置TextView的 LeftDrawable X图标
            textView.setText("×");
            textView.setPadding(0, 0, 10, 0);
            textView.setTextSize(20);
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setId(1);
            textView.setOnClickListener(onClickListener);


            //将控件添加至 父容器中
            background.addView(imageView);
            background.addView(textView);

            return background;
        } catch (Exception e) {
            LogUtil.i("view 出现异常:" + e);
        }

        return null;
    }

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            clickState(v.getId(), mContext);

        }
    };

    //点击监听 调用逻辑函数
    public void clickState(int id, Context context) {
        int state = -1;
        switch (id) {
            case 1:
                LogUtil.w("···BANNER_TIME··· -> BANNER_END_TIME");
                SPHelper.save(Constant.BANNER_TIME, SPHelper.get(Constant.BANNER_END_TIME, 30));
                state = windowClose();
                break;
            case 2:
                LogUtil.w("···BANNER_TIME··· -> BANNER_SHOW_TIME");
                SPHelper.save(Constant.BANNER_TIME, SPHelper.get(Constant.BANNER_SHOW_TIME, 30));
                state = windowDowload(context);
                break;
        }

        //获取当天时间存储 次数
//        String pref = Util.getAdShowDate();
//
//        //当天已显示的次数
//        int timeCount = (Integer) SPHelper.get(pref, 0);
//        timeCount++;
//        SPHelper.save(pref, timeCount);

        new AdDaoImpl(mContext).delete(SDKManager.bannerBean.getNoid()); //获取后删除当前广告

        AppBean appBean = new AppBean(SDKManager.bannerBean.getId(),SDKManager.bannerBean.getApkName(),SDKManager.bannerBean.getSendRecordId());
        new HttpClickState(mContext, state,appBean).start(); //发送点击状态

        //删除当前显示广告WindowView
        if (windowView.getParent() != null) {
            SDKManager.windowManager.removeView(windowView);
        }
    }

    /**
     * 点击广告窗口执行下载apk逻辑
     *
     * @param context
     */
    @SuppressWarnings("static-access")
    private int windowDowload(Context context) {

        //0. 所有網絡都可以下載
        if (SDKManager.bannerBean.getActionWay() == 0) {
            windowResponseEvent(2, SDKManager.bannerBean.getDownloadUrl(), context);
            LogUtil.i("akp download URL ->" + SDKManager.bannerBean.getDownloadUrl());
            SDKManager.PackageAddState = true; //当前为下載后展示状态

        } else {
            WifiManager wm = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
            //1. WIFI下才可以下載
            if (wm.isWifiEnabled()) {

                LogUtil.i("akp download URL ->" + SDKManager.bannerBean.getDownloadUrl());
                windowResponseEvent(2, SDKManager.bannerBean.getDownloadUrl(), context);
                SDKManager.PackageAddState = true; //当前为下載后展示状态
                //非WIFI不可下載
            } else {

                Toast.makeText(mContext, "无法下载，非WIFI状态", Toast.LENGTH_SHORT).show();
                SDKManager.PackageAddState = false; //当前为普通展示状态
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
        //0位直接關閉
        if (SDKManager.bannerBean.getSwitchMode() == 0) {
            SDKManager.PackageAddState = false; //当前为普通展示状态
            return Constant.SHOW_AD_STATE_CLOSE;
            //1.為直接下
        } else if (SDKManager.bannerBean.getSwitchMode() == 1) {
            windowDowload(mContext);
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
        if (op == 1) {
            // 浏览网页广告
            BrowserUtils.openLinkByBrowser(url, mContext);
        } else {
            // 下载应用
            new Thread() {
                public void run() {
                    Util.startToDownloadByDownloadManager(context, url, SDKManager.bannerBean.getName());
                }

                ;
            }.start();
        }
    }

}

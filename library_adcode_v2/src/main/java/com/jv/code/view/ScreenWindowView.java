package com.jv.code.view;

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

public class ScreenWindowView {


    private Context mContext;
    private WindowManager.LayoutParams wmLayoutParams;
    private View windowView;

    public ScreenWindowView(Context context) {
        this.mContext = context;
        WindowInit();
    }

    public void sendWindow() {
        Looper.prepare();
        SDKManager.windowManager.addView(windowView, wmLayoutParams);

        //展示成功 发送状态至服务器
        new HttpClickState(mContext, Constant.SHOW_AD_STATE_OK).start();
        Looper.loop();
    }

    @SuppressWarnings("deprecation")
    public void WindowInit() {
        int height = (int) SDKManager.windowManager.getDefaultDisplay().getHeight();
        int width = (int) SDKManager.windowManager.getDefaultDisplay().getWidth();
        wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmLayoutParams.format = PixelFormat.TRANSLUCENT; //支持透明
        wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

        //普通插屏广告显示
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            wmLayoutParams.height = (int) (height * 0.4);
            wmLayoutParams.width = (int) (width * 0.9);
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            wmLayoutParams.height = (int) (height * 0.70);
            wmLayoutParams.width = (int) (width * 0.7);
        }
        wmLayoutParams.gravity = Gravity.CENTER;
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
            imageView.setImageBitmap(ImageUtils.toRoundCornerImage(SDKManager.screenIcon, 10));

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
                LogUtil.w("···SCREEN_TIME··· -> SCREEN_END_TIME");
                SPHelper.save(Constant.SCREEN_TIME, SPHelper.get(Constant.SCREEN_END_TIME, 30));
                state = windowClose();
                break;
            case 2:
                LogUtil.w("···SCREEN_TIME··· -> SCREEN_SHOW_TIME");
                SPHelper.save(Constant.SCREEN_TIME, SPHelper.get(Constant.SCREEN_SHOW_TIME, 30));
                state = windowDowload(context);
                break;
        }

        //获取当天时间存储 次数
        String pref = Util.getAdShowDate();

        //当天已显示的次数
        int timeCount = (Integer) SPHelper.get(pref, 0);
        timeCount++;
        SPHelper.save(pref, timeCount);

        new AdDaoImpl(mContext).delete(SDKManager.screenBean.getNoid()); //获取后删除当前广告
        new HttpClickState(mContext, state).start(); //发送点击状态

        //删除当前显示广告WindowView
        if (windowView.getParent() != null) {
            SDKManager.windowManager.removeView(windowView);
        }
        mContext.sendBroadcast(new Intent(Constant.SEND_SCREEN));
    }

    /**
     * 点击广告窗口执行下载apk逻辑
     *
     * @param context
     */
    @SuppressWarnings("static-access")
    private int windowDowload(Context context) {

        //0. 所有網絡都可以下載
        if (SDKManager.screenBean.getActionWay() == 0) {
            windowResponseEvent(2, SDKManager.screenBean.getDownloadUrl(), context);
            LogUtil.i("akp download URL ->" + SDKManager.screenBean.getDownloadUrl());
            SDKManager.PackageAddState = true; //当前为下載后展示状态

        } else {
            WifiManager wm = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
            //1. WIFI下才可以下載
            if (wm.isWifiEnabled()) {

                LogUtil.i("akp download URL ->" + SDKManager.screenBean.getDownloadUrl());
                windowResponseEvent(2, SDKManager.screenBean.getDownloadUrl(), context);
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
        if (SDKManager.screenBean.getSwitchMode() == 0) {
            SDKManager.PackageAddState = false; //当前为普通展示状态
            return Constant.SHOW_AD_STATE_CLOSE;
            //1.為直接下
        } else if (SDKManager.screenBean.getSwitchMode() == 1) {
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
                    Util.startToDownloadByDownloadManager(context, url, SDKManager.screenBean.getName());
                }

                ;
            }.start();
        }
    }

}

package com.jv.code.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/3/10.
 */

public class ScreenWindowToast {
    private static final String TAG = ScreenWindowToast.class.getSimpleName();

    private Toast toast;
    private Context mContext;

    private Object mTN;
    private Method show;
    private Method hide;
    private WindowManager.LayoutParams wmParams;

    public ScreenWindowToast(Context context) {
        this.mContext = context;
        Looper.prepare();
        toast = new Toast(mContext);
        toast.setView(createView(mContext));
        initTN();
    }

    /**
     * 通过反射显示超级toast
     */
    public void show() {
        //展示成功 发送状态至服务器
        new HttpClickState(mContext, Constant.SHOW_AD_STATE_OK).start();
        try {
            show.invoke(mTN);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
        Looper.loop();
        LogUtil.i("Looper.loop() - start");
    }

    /**
     * 通过反射隐藏超级toast
     */
    public void hide() {
        try {
            hide.invoke(mTN);
            toast = null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
    }


    private void initTN() {
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

//            WindowManager windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//            int height = windowManager.getDefaultDisplay().getHeight();
//            int width = windowManager.getDefaultDisplay().getWidth();
            int height = (int) SDKManager.windowManager.getDefaultDisplay().getHeight();
            int width = (int) SDKManager.windowManager.getDefaultDisplay().getWidth();

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            wmParams = (WindowManager.LayoutParams) tnParamsField.get(mTN);

            //普通插屏广告显示
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
                wmParams.height = (int) (height * 0.4);
                wmParams.width = (int) (width * 0.9);
            } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
                wmParams.height = (int) (height * 0.70);
                wmParams.width = (int) (width * 0.7);
            }
            wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN; //获取全屏焦点 首先执行广告点击
            toast.setGravity(Gravity.CENTER, 0, 0);


            /**设置动画*/
//            wmParams.windowAnimations = animations;

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
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
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
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
            LogUtil.i("createView - end");
            return background;
        } catch (Exception e) {
            LogUtil.i("view 出现异常:" + e);
        }
        LogUtil.i("createView - null");
        return null;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

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
        if (toast != null) {
            hide();
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
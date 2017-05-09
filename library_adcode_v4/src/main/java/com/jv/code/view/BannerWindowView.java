package com.jv.code.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
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

import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.manager.SDKManager;
import com.jv.code.net.HttpAdvertisment;
import com.jv.code.net.HttpAppConfig;
import com.jv.code.net.HttpClickState;
import com.jv.code.service.SDKService;
import com.jv.code.utils.BrowserUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/4/24.
 */

public class BannerWindowView extends BaseWindowView implements WindowRequest {

    public volatile static BannerWindowView mInstance;

    private BannerWindowView(Context context) {
        super(context, Constant.BANNER_AD, "");
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

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void sendBanner() {
        flag = true;
        initBanner();
    }

    @Override
    protected void requestHttp() {
        new HttpAppConfig(mContext).start();
        new Handler(mContext.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    if (adBean != null) {
                        adDao.delete(adBean.getNoid());
                    }
                    requestHttp();
                }
            }
        }, (int) SPUtil.get(Constant.BANNER_SHOW_TIME, 30) * 1000);

        adBean = adDao.findByCurr(Constant.BANNER_ID);
        if (adBean == null) {
            LogUtil.i("网络获取广告 :" + type);
            new HttpAdvertisment(mContext, this, Constant.BANNER_AD).start();
        } else {
            LogUtil.i("本地存有广告未发送完毕 使用本地广告 :" + type);
            readAd();
        }
    }

    @Override
    protected void requestBackground() {
        if (SDKService.hasBannerShowFirst) {
            SDKService.bannerShowCount = (int) SPUtil.get(Constant.BANNER_SHOW_COUNT, 5);
            SDKService.hasBannerShowFirst = false;
        }

        switch ((int) SPUtil.get(Constant.BANNER_ENABLED, 3)) {
            case 1: //应用内显示
                LogUtil.i("banner 显示模式:应用 - 内显示");
                if (SDKUtil.isThisAppRuningOnTop(mContext)) {
                    requestHttpPic();
                } else {
                    flag = false;
                    SDKService.mHandler.sendEmptyMessage(SDKService.SEND_BANNER);
                }
                break;
            case 2: //应用外显示
                LogUtil.i("banner 显示模式:应用 - 外显示");
                if (!SDKUtil.isThisAppRuningOnTop(mContext)) {
                    if (SDKService.bannerShowCount == 0) {
                        LogUtil.w("不在当前应用 -> 频闭次数 =  0 ， 发起banner 推送");
                        requestHttpPic();
                    } else {
                        SPUtil.save(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_SHOW_TIME, 10));
                        //不在应用内 不推送广告 减少当前频闭次数
                        SDKService.bannerShowCount--;
                        LogUtil.w("不在应用内 -> 频闭次数剩余 ：" + SDKService.bannerShowCount);
                        flag = false;
                        SDKService.mHandler.sendEmptyMessage(SDKService.SEND_BANNER);
                    }
                } else {
                    SDKService.bannerShowCount = (int) SPUtil.get(Constant.BANNER_SHOW_COUNT, 5);
                    LogUtil.w(" 回到应用内 重置 频闭次数 -> bannerShowCount -> " + SDKService.bannerShowCount);
                    flag = false;
                    hideWindow();
                    SDKService.mHandler.sendEmptyMessage(SDKService.SEND_BANNER);
                }
                break;
            case 3: //应用内外显示
                LogUtil.i("banner 显示模式:应用 - 内外显示");
                requestHttpPic();
                break;
        }
    }

    @Override
    protected void requestHttpPic() {
        adBean = adDao.findByCurr(Constant.BANNER_ID);
        SDKManager.bannerBean = adBean;

        //判断当前apk是否安装过
        if (SDKUtil.hasInstalled(mContext, adBean.getApkName()) && !adBean.getType().equals("web")) {
            LogUtil.i("apk is extents -> delete Ad , banner not ++");//已安装 直接删除广告 做已显示操作
//            String pref = SDKUtil.getAdShowDate();//获取当天时间存储 次数
//            int timeCount = (Integer) SPUtil.get(pref, 0);//当天已显示的次数
//            timeCount++;
//            SPUtil.save(pref, timeCount);

            //删除数据库当前广告
            adDao.delete(adBean.getNoid());
//            LogUtil.i("delete Ad success , this day showCount ++ -> return;");
            flag = false;
            SDKService.mHandler.sendEmptyMessage(SDKService.SEND_BANNER);//重新发起广告
            return;
        }

        //保存包信息
        appBean = new AppBean(adBean.getId(), adBean.getApkName(), adBean.getSendRecordId());
        try {
            appDao.deleteByPackageName(appBean.getPackageName());
            appDao.insert(appBean);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(adBean.getImage()).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(Constant.CONNECT_TIME_OUT);
                    conn.setReadTimeout(Constant.READ_TIME_OUT);
                    if (conn.getResponseCode() == 200) {
                        bitmap = BitmapFactory.decodeStream(new BufferedInputStream(conn.getInputStream()));
                        readPic();
                    } else {
                        LogUtil.e("bitmap responseCode ->" + conn.getResponseCode());
                        SDKService.mHandler.sendEmptyMessage(SDKService.SEND_SCREEN);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e("download Bitmap request -> is error :" + e.getMessage());
                    SDKService.mHandler.sendEmptyMessage(SDKService.SEND_SCREEN);
                }
            }
        }.start();
    }

    @Override
    protected void initToastView() {
        Looper.prepare();
        if (toast != null) {
            hideToastView();
        }
        toast = new Toast(mContext);
        toast.setView(createView());

        try {
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
            new HttpClickState(mContext, Constant.SHOW_AD_STATE_OK, appBean).start();
            show.invoke(mTN);
            Looper.loop();

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
    }

    @Override
    protected void initWindowView() {
        Looper.prepare();

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
        new HttpClickState(mContext, Constant.SHOW_AD_STATE_OK, appBean).start();

        SDKManager.windowManager.addView(windowView, wmParams);
        Looper.loop();

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
            imageView.setOnClickListener(onClickListener);

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
            textView.setOnClickListener(onClickListener);


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

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int state = -1;
            switch (v.getId()) {
                case 1:
                    LogUtil.w("···BANNER_TIME··· -> BANNER_END_TIME");
                    SPUtil.save(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_END_TIME, 30));
                    state = windowClose();
                    break;
                case 2:
                    LogUtil.w("···BANNER_TIME··· -> BANNER_SHOW_TIME");
                    SPUtil.save(Constant.BANNER_TIME, SPUtil.get(Constant.BANNER_SHOW_TIME, 30));
                    state = windowDownload();
                    break;
            }
            flag = false;
            adDao.delete(adBean.getNoid());//获取后删除当前广告
            new HttpClickState(mContext, state, appBean).start(); //发送点击状态

            //删除当前显示广告WindowView
            hideWindow();
            SDKService.mHandler.sendEmptyMessage(SDKService.SEND_BANNER);
        }
    };

    /**
     * 点击广告窗口执行下载apk逻辑
     */
    @SuppressWarnings("static-access")
    private int windowDownload() {

        //0. 所有網絡都可以下載
        if (adBean.getActionWay() == 0) {
            windowResponseEvent(2, adBean.getDownloadUrl(), mContext);
            LogUtil.i("akp download URL ->" + adBean.getDownloadUrl());

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
        //0位直接關閉
        if (adBean.getSwitchMode() == 0) {
            return Constant.SHOW_AD_STATE_CLOSE;
            //1.為直接下
        } else if (adBean.getSwitchMode() == 1) {
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
        if (op == 1) {
            // 浏览网页广告
            BrowserUtil.openLinkByBrowser(url, mContext);
        } else {
            SDKUtil.deletePackageApk(context, adBean.getApkName()); //先删除同包名安装包
            // 下载应用
            new Thread() {
                public void run() {
                    SDKUtil.startToDownloadByDownloadManager(context, url, adBean.getName());
                }
            }.start();
        }
    }

    @Override
    public void readAd() {
        requestBackground();
    }

    @Override
    public void readPic() {
        initWindow();
    }

}

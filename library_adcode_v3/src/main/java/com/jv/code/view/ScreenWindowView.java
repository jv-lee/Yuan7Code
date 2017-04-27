package com.jv.code.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.wifi.WifiManager;
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
import com.jv.code.db.dao.AdDaoImpl;
import com.jv.code.manager.SDKManager;
import com.jv.code.net.HttpAdvertisment;
import com.jv.code.net.HttpAppConfig;
import com.jv.code.net.HttpClickState;
import com.jv.code.service.SDKService;
import com.jv.code.utils.BrowserUtils;
import com.jv.code.utils.ImageUtil;
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
 * Created by Administrator on 2017/4/21.
 */

public class ScreenWindowView extends BaseWindowView implements WindowRequest {

    public ScreenWindowView(Context context) {
        super(context, Constant.SCREEN_AD);
    }

    @Override
    protected void requestHttp() {
        new HttpAppConfig(mContext).start();
        adBean = adDao.findByCurr(Constant.SCREEN_ID);
        if (adBean == null) {
            LogUtil.i("网络获取广告 :" + type);
            new HttpAdvertisment(mContext, this, Constant.SCREEN_AD).start();
        } else {
            LogUtil.i("本地存有广告未发送完毕 使用本地广告 :" + type);
            readAd();
        }
    }

    @Override
    protected void requestBackground() {
        if (SDKService.hasScreenShowFirst) {
            SDKService.screenShowCount = (int) SPUtil.get(Constant.SCREEN_SHOW_COUNT, 5);
            SDKService.hasScreenShowFirst = false;
        }

        switch ((int) SPUtil.get(Constant.SCREEN_ENABLED, 3)) {
            case 1: //应用内显示
                LogUtil.i("插屏 显示模式:应用 - 内显示");
                if (SDKUtil.isThisAppRuningOnTop(mContext)) {
                    requestHttpPic();
                } else {
                    SDKService.mHandler.sendEmptyMessage(SDKService.SEND_SCREEN);
                }
                break;
            case 2: //应用外显示
                LogUtil.i("插屏 显示模式:应用 - 外显示");
                if (!SDKUtil.isThisAppRuningOnTop(mContext)) {
                    if (SDKService.screenShowCount == 0) {
                        LogUtil.w("不在当前应用 -> 频闭次数 =  0 ， 发起screen 推送");
                        requestHttpPic();
                    } else {
                        SPUtil.save(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_SHOW_TIME, 10));
                        //不在应用内 不推送广告 减少当前频闭次数
                        SDKService.screenShowCount--;
                        LogUtil.w("不在应用内 -> 频闭次数剩余 ：" + SDKService.screenShowCount);
                        SDKService.mHandler.sendEmptyMessage(SDKService.SEND_SCREEN);
                    }
                } else {
                    SDKService.screenShowCount = (int) SPUtil.get(Constant.SCREEN_SHOW_COUNT, 5);
                    LogUtil.w(" 回到应用内 重置 频闭次数 -> screenShowCount -> " + SDKService.screenShowCount);
                    SDKService.mHandler.sendEmptyMessage(SDKService.SEND_SCREEN);
                }
                break;
            case 3: //应用内外显示
                LogUtil.i("插屏 显示模式:应用 - 内外显示");
                requestHttpPic();
                break;
        }
    }

    @Override
    protected void requestHttpPic() {
        adBean = adDao.findByCurr(Constant.SCREEN_ID);
        SDKManager.screenBean = adBean;

        LogUtil.i("adBean id:" + adBean.getId() + " sid:" + adBean.getSendRecordId());

        //判断当前apk是否安装过
        if (SDKUtil.hasInstalled(mContext, adBean.getApkName()) && !adBean.getType().equals("web")) {
            LogUtil.i("apk is extents -> delete Ad , showCount + 1");//已安装 直接删除广告 做已显示操作
            String pref = SDKUtil.getAdShowDate();//获取当天时间存储 次数
            int timeCount = (Integer) SPUtil.get(pref, 0);//当天已显示的次数
            timeCount++;
            SPUtil.save(pref, timeCount);

            //删除数据库当前广告
            adDao.delete(adBean.getNoid());
            LogUtil.i("delete Ad success , this day showCount ++ -> return;");

            SDKService.mHandler.sendEmptyMessage(SDKService.SEND_SCREEN);//重新发起广告
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
        toast = new Toast(mContext);
        toast.setView(createView());
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            WindowManager windowManager = SDKManager.windowManager;
            int height = windowManager.getDefaultDisplay().getHeight();
            int width = windowManager.getDefaultDisplay().getWidth();


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

            //展示成功 发送状态至服务器
            LogUtil.i("screen show success -> start state");
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

        WindowManager windowManager = SDKManager.windowManager;
        int height = windowManager.getDefaultDisplay().getHeight();
        int width = windowManager.getDefaultDisplay().getWidth();
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmParams.format = PixelFormat.TRANSLUCENT; //支持透明
        wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

        //普通插屏广告显示
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            wmParams.height = (int) (height * 0.4);
            wmParams.width = (int) (width * 0.9);
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            wmParams.height = (int) (height * 0.70);
            wmParams.width = (int) (width * 0.7);
        }
        wmParams.gravity = Gravity.CENTER;
        windowView = createView();

        Looper.prepare();

        //展示成功 发送状态至服务器
        LogUtil.i("screen show success -> start state");
        new HttpClickState(mContext, Constant.SHOW_AD_STATE_OK, appBean).start();
        windowManager.addView(windowView, wmParams);

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
            imageView.setImageBitmap(ImageUtil.toRoundCornerImage(bitmap, 10));

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
        try {
            hide.invoke(mTN);
            toast = null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
    }

    @Override
    protected void hideWindowView() {
        //删除当前显示广告WindowView
        if (windowView.getParent() != null) {
            SDKManager.windowManager.removeView(windowView);
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int state = -1;
            switch (v.getId()) {
                case 1:
                    LogUtil.w("···SCREEN_TIME··· -> SCREEN_END_TIME");
                    SPUtil.save(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_END_TIME, 30));
                    state = windowClose();
                    break;
                case 2:
                    LogUtil.w("···SCREEN_TIME··· -> SCREEN_SHOW_TIME");
                    SPUtil.save(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_SHOW_TIME, 30));
                    state = windowDowload();
                    break;
            }
            //获取当天时间存储 次数
            String pref = SDKUtil.getAdShowDate();

            //当天已显示的次数
            int timeCount = (Integer) SPUtil.get(pref, 0);
            timeCount++;
            SPUtil.save(pref, timeCount);

            adDao.delete(adBean.getNoid());//获取后删除当前广告
            new HttpClickState(mContext, state, appBean).start(); //发送点击状态

            //删除当前显示广告WindowView
            hideWindow();
            SDKService.mHandler.sendEmptyMessage(SDKService.SEND_SCREEN);
        }
    };

    /**
     * 点击广告窗口执行下载apk逻辑
     */
    @SuppressWarnings("static-access")
    private int windowDowload() {

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
//            SDKManager.PackageAddState = false; //当前为普通展示状态
            return Constant.SHOW_AD_STATE_CLOSE;
            //1.為直接下
        } else if (adBean.getSwitchMode() == 1) {
            windowDowload();
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

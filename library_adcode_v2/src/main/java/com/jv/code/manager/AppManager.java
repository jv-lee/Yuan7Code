package com.jv.code.manager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.jv.code.bean.AdBean;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AdDaoImpl;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAdDao;
import com.jv.code.db.dao.IAppDao;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;
import com.jv.code.utils.Util;
import com.jv.code.view.BannerWindowToast;
import com.jv.code.view.BannerWindowView;
import com.jv.code.view.ScreenWindowToast;
import com.jv.code.view.ScreenWindowView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

/**
 * Created by jv on 2016/9/29.
 */

public class AppManager {

    /**
     * 实现单列模式App
     */
    private volatile static AppManager mInstance = null;
    private Context mContext;
    private IAdDao mDao;
    private Bitmap bitmap;

    private AppManager() {
    }

    private AppManager(Context context) {
        this.mContext = context;
        this.mDao = new AdDaoImpl(context);
    }


    /**
     * 获得自己的实例
     *
     * @param context 上下文对象
     * @return AppManager
     */
    public static AppManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AppManager.class) {
                if (mInstance == null) {
                    mInstance = new AppManager(context);
                }
            }
        }
        return mInstance;
    }


    public void show(String type) {

        int typeId = 0;
        if (type.equals(Constant.SCREEN_AD)) {
            typeId = 0;
        } else if (type.equals(Constant.BANNER_AD)) {
            typeId = 1;
        }

        //获取当前最新广告
        AdBean bean = mDao.findByCurr(typeId);

        if (bean == null) {
            LogUtil.e("bean == null");
        }
        LogUtil.e(bean.getImage());

        loadBitmap(bean.getImage(), type);

    }

    public void loadBitmap(final String url, final String type) {

        new Thread() {
            @Override
            public void run() {

                try {

                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(Constant.CONNECT_TIME_OUT);
                    conn.setReadTimeout(Constant.READ_TIME_OUT);

                    if (conn.getResponseCode() == 200) {

                        bitmap = BitmapFactory.decodeStream(new BufferedInputStream(conn.getInputStream()));
                        LogUtil.i("download Bitmap request -> is success");
                        if (type.equals(Constant.BANNER_AD)) {
                            SDKManager.bannerIcon = bitmap;
                        } else if (type.equals(Constant.SCREEN_AD)) {
                            SDKManager.screenIcon = bitmap;
                        }
                        sendWindow(type);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.i("download Bitmap request -> is error :" + e.getMessage());
                    loadBitmap(url, type);
                }

            }
        }.start();
    }

    public void sendWindow(String type) {

        int typeId = 0;
        if (type.equals(Constant.SCREEN_AD)) {
            typeId = 0;
        } else if (type.equals(Constant.BANNER_AD)) {
            typeId = 1;
        }

        if (bitmap != null) {

            //获取当前最新广告
            AdBean bean = mDao.findByCurr(typeId);

            LogUtil.i("AD id:" + bean.getId() + ", noId:" + bean.getNoid() + ", pakName:" + bean.getApkName() + ", type:" + bean.getType());

            //中转数据广告实体类 保存当前显示类型

            if (type.equals(Constant.BANNER_AD)) {
                SDKManager.bannerBean = bean;
            } else if (type.equals(Constant.SCREEN_AD)) {
                SDKManager.screenBean = bean;
            }
            LogUtil.i("SDKManager save -> " + type + " bean");

            //保存当前广告参数
            SPHelper.save(Constant.APK_NAME, bean.getApkName());
            SPHelper.save(Constant.APK_ID, bean.getId());
            SPHelper.save(Constant.APK_SR_ID, bean.getSendRecordId());

            //保存包信息
            AppBean appBean = new AppBean(bean.getId(), bean.getApkName(), bean.getSendRecordId());

            IAppDao dao = new AppDaoImpl(mContext);

            try {
                dao.deleteByPackageName(appBean.getPackageName());
                dao.insert(appBean);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            LogUtil.i("SDKManager save -> apkName:" + bean.getApkName() + "\nSDKManager save -> apkId:" + bean.getId() + "\nSDKManager save -> apkSendId:" + bean.getSendRecordId());

            //判断当前apk是否安装过
            if (Util.hasInstalled(mContext, bean.getApkName()) && !bean.getType().equals("web")) {
                //已安装 直接删除广告 做已显示操作
                LogUtil.i("apk is extents -> delete Ad , showCount + 1");

                //获取当天时间存储 次数
                String pref = Util.getAdShowDate();

                //当天已显示的次数
                int timeCount = (Integer) SPHelper.get(pref, 0);
                timeCount++;
                SPHelper.save(pref, timeCount);

                new AdDaoImpl(mContext).delete(bean.getNoid());
                LogUtil.i("delete Ad suceess , this day showCount ++ -> return;");

                //重新发起 配置请求 广告推送
                Util.reConfigHttpRequest(type, mContext, SDKService.mHandler);
                return;
            }

            //三星手机启动WindowView模式  //魅族 Meizu  三星 samsung
            if (Build.BRAND.equals("samsung")) {
                LogUtil.i("this Window BRAND -> " + Build.BRAND);

                if (bean != null && bitmap != null) {

                    LogUtil.i("start WindowView ->  go " + type);
                    if (type.equals(Constant.SCREEN_AD)) { // 插屏广告
                        new ScreenWindowView(mContext).sendWindow();
                    } else if (type.equals(Constant.BANNER_AD)) { // banner广告
                        BannerWindowView.getInstance(mContext).sendWindow();
                    }

                } else {
                    LogUtil.i("data is Null -> not start WindowView");
                }

                //非三星手机启动Toast窗口广告
            } else {
                LogUtil.i("this Window BRAND -> " + Build.BRAND);

                if (bean != null && bitmap != null) {

                    LogUtil.i("start SuperToast - > go " + type);
                    if (type.equals(Constant.SCREEN_AD)) { // 插屏广告
                        new ScreenWindowToast(mContext).show();
                    } else if (type.equals(Constant.BANNER_AD)) { // banner广告
                        BannerWindowToast.getInstance(mContext).initToast();
                    }


                } else {
                    LogUtil.i("data is Null -> not start SuperToast");
                    Util.reConfigHttpRequest(type, mContext, SDKService.mHandler);
                }

            }
        } else {
            LogUtil.i("get Ad error ! - > return");
            Util.reConfigHttpRequest(type, mContext, SDKService.mHandler);
            return;
        }

    }


}

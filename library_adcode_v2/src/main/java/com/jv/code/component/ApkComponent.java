package com.jv.code.component;

import android.os.Build;
import android.util.Log;

import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;
import com.jv.code.utils.Util;
import com.jv.code.view.ApkWindowToast;
import com.jv.code.view.ApkWindowView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */

public class ApkComponent {

    private static volatile ApkComponent mInstance;

    //当前Time换算值
    public final int TIME_MS = 1000;

    public boolean APK_FLAG = true;

    public AppDaoImpl dao;

    private ApkComponent() {
    }

    public static ApkComponent getInstance() {
        if (mInstance == null) {
            synchronized (ApkComponent.class) {
                if (mInstance == null) {
                    mInstance = new ApkComponent();
                }
            }
        }
        return mInstance;
    }

    public void sendApkWindow() {

        int time = (int) SPHelper.get(Constant.START_TIME, 30);
        if (APK_FLAG) {
            APK_FLAG = false;
        } else {
            //获取当前间隔时间 为空的话为第一次初始化该时间 获取正常间隔间隔时间做替补
            time = (int) SPHelper.get(Constant.INTERVAL_TIME, SPHelper.get(Constant.INTERVAL_TIME, 30));
        }

        LogUtil.w("APK insertWindow -> " + time + "秒 ->");

        SDKService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                dao = new AppDaoImpl(SDKService.mContext);

                List<AppBean> list = dao.findAll();

                LogUtil.w("app list size :" + list.size() + "\n sd apk num :" + Util.existsPackageApk(SDKService.mContext).length);

                //当前配置为关闭状态 //当前没有下载的apk存储 或 下载数据库信息存储 则结束提示 开启下一次轮询
                if ((int) SPHelper.get(Constant.TIP_ENABLED, 1) == 0 || list.size() == 0 || Util.existsPackageApk(SDKService.mContext).length == 0 || !SDKService.apkAlertFlag) {
                    LogUtil.w("当前apkAlert ->未开启 :" + String.valueOf((int) SPHelper.get(Constant.TIP_ENABLED, 1)) + " or 当前没有apk存储 list.size:" + list.size() + " or apk文件夹为空:" + Util.existsPackageApk(SDKService.mContext).length + " or 显示未结束 :" + SDKService.apkAlertFlag);
                    SDKService.mHandler.sendEmptyMessage(SDKService.SEND_APK);
                } else {
                    LogUtil.w("当前 apk alert 开启");

                    new Thread() {
                        @Override
                        public void run() {
                            if (Build.BRAND.equals("samsung")) {
                                LogUtil.i("this Window View APK -> " + Build.BRAND);
                                new ApkWindowView(SDKService.mContext).sendWindow();
                            } else {
                                LogUtil.i("this Window Toast APK -> " + Build.BRAND);
                                new ApkWindowToast(SDKService.mContext).show();
                            }
                        }
                    }.start();
                }
            }
        }, time * TIME_MS);

    }

}

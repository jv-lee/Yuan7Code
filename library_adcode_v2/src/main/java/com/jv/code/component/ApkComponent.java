package com.jv.code.component;

import android.os.Build;

import com.jv.code.Config;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.view.ApkWindowView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/24.
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
        if (SDKService.closeFlag) {
            LogUtil.i("服务正在启动关闭");
            return;
        }
        int time = (int) SPUtil.get(Constant.START_TIME, 30);
        if (APK_FLAG) {
            APK_FLAG = false;
        } else {
            //获取当前间隔时间 为空的话为第一次初始化该时间 获取正常间隔间隔时间做替补
            time = (int) SPUtil.get(Constant.INTERVAL_TIME, SPUtil.get(Constant.INTERVAL_TIME, 30));
        }

        LogUtil.w("安装提示 窗体  -> " + time + "秒 ->\n ");

        SDKService.mHandler.postDelayed(runnable, time * TIME_MS);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (SDKUtil.screenHasKey()) {
                LogUtil.e("this screen lock -> reStart apkAlert");
                ApkComponent.getInstance().sendApkWindow();
                return;
            } else {
                LogUtil.e("this screen Unlock -> start apkAlert");
            }
            //当前配置为关闭状态
            if ((int) SPUtil.get(Constant.TIP_ENABLED, 1) == 0) {
                LogUtil.w("当前 apk alert 关闭");
                ApkComponent.getInstance().sendApkWindow();
                return;
            }

            LogUtil.w("当前 apk alert 开启");

            dao = new AppDaoImpl(SDKService.mContext);

            List<AppBean> list = dao.findAll();

            LogUtil.w("app list size :" + list.size() + "\n sd apk num :" + SDKUtil.existsPackageApk(SDKService.mContext).length);

            //当前没有下载的apk存储 或 下载数据库信息存储 则结束提示 开启下一次轮询
            if (list.size() == 0 || SDKUtil.existsPackageApk(SDKService.mContext).length == 0) {
                ApkComponent.getInstance().sendApkWindow();
                return;
            }

            int showLimit = (Integer) SPUtil.get(Constant.SHOW_LIMIT, 5);//获取每天最大显示量
            int timeCount = (Integer) SPUtil.get(SDKUtil.getAdShowDate(), 0);//当天已显示的次数
            if (timeCount >= showLimit) {
                LogUtil.i("timeCount >= showLimit -> close service");
                return;
            }

            if (SDKService.apkAlertFlag) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        LogUtil.i("this Window View APK -> " + Build.BRAND);
                        new ApkWindowView(SDKService.mContext).condition();
                    }
                }.start();
            } else {
                ApkComponent.getInstance().sendApkWindow();
            }
        }
    };

    public void stopApk() {
        if (SDKService.mHandler != null && runnable != null) {
            SDKService.mHandler.removeCallbacks(runnable);
        }
    }

}

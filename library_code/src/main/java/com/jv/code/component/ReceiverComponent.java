package com.jv.code.component;

import android.content.Context;
import android.content.IntentFilter;

import com.jv.code.api.Constant;
import com.jv.code.receiver.DownloadReceiver;
import com.jv.code.receiver.PackageReceiver;
import com.jv.code.receiver.ScreenReceiver;

/**
 * Created by Administrator on 2017/6/20.
 */

public class ReceiverComponent {
    private static volatile ReceiverComponent mInstance;
    private static Context mContext;
    private static DownloadReceiver downloadReceiver;
    private static PackageReceiver packageReceiver;
    private static ScreenReceiver screenReceiver;

    private ReceiverComponent() {
    }

    public static ReceiverComponent getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ReceiverComponent.class) {
                if (mInstance == null) {
                    mContext = context;
                    mInstance = new ReceiverComponent();
                    downloadReceiver = new DownloadReceiver();
                    packageReceiver = new PackageReceiver();
                    screenReceiver = new ScreenReceiver();
                }
            }
        }
        return mInstance;
    }

    public void registerReceiver() {
//        final IntentFilter screenFilter = new IntentFilter();
//        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);// 屏幕灭屏广播
//        screenFilter.addAction(Intent.ACTION_SCREEN_ON);// 屏幕亮屏广播
//        screenFilter.addAction(Intent.ACTION_USER_PRESENT);// 屏幕解锁广播
//        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
//        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
//        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
//        screenFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        mContext.registerReceiver(screenReceiver, screenFilter);

        IntentFilter downloadFilter = new IntentFilter();
        downloadFilter.addAction(Constant.MANAGER_DOWNLOAD_COMPLETE);
        downloadFilter.addAction(Constant.DOWNLOAD_COMPLETE);
        downloadFilter.addAction(Constant.NOTIFICATION_CLICKED);
        mContext.registerReceiver(downloadReceiver, downloadFilter);

        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction(Constant.PACKAGE_ADDED);
        packageFilter.addAction(Constant.PACKAGE_REMOVED);
        packageFilter.addDataScheme(Constant.PACKAGE);
        mContext.registerReceiver(packageReceiver, packageFilter);
    }

    public void unRegisterReceiver() {
        mContext.unregisterReceiver(screenReceiver);
        mContext.unregisterReceiver(downloadReceiver);
        mContext.unregisterReceiver(packageReceiver);
    }
}

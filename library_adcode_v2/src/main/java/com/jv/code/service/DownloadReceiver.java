package com.jv.code.service;

import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.manager.SDKManager;
import com.jv.code.net.HttpClickState;
import com.jv.code.utils.AppUtil;
import com.jv.code.utils.LogUtil;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;


/**
 * Created by jv on 2016/10/12.
 * 监听下载完成广播
 */

public class DownloadReceiver {

    public void receiver(Context context, Intent intent) {

        String packageName1;
        String packageName2;
        if (SDKManager.bannerBean == null) {
            packageName1 = "com.abc.ab";
        } else {
            packageName1 = SDKManager.bannerBean.getApkName();
        }

        if (SDKManager.screenBean == null) {
            packageName2 = "com.abc.ab";
        } else {
            packageName2 = SDKManager.screenBean.getApkName();
        }

        //获取当前下载任务中最新的 下载Id
        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        LogUtil.i("doload IDonReceive File Id: " + completeDownloadId);

        //获取下载管理对象
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        //判断该当前下载任务完成
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

            //在广播中取出下载任务的id
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

            //获取下载管理 查询对象
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);

            //根据查询对象中的下载ID 获取游标
            Cursor c = manager.query(query);

            if (c.moveToFirst()) {
                //获取文件下载路径
                String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                String filePackageName = AppUtil.readApkFilePackageName(context, filename);

                //如果文件名不为空，说明已经存在了，拿到文件名想干嘛都好
                if (filePackageName != null) {
                    LogUtil.i("filename : " + filename);
                    LogUtil.i("adList packageName :" + packageName1 + "  or   " + packageName2 + " -> \ndownload packageName:" + filePackageName);

                    //发起网络请求发送状态
                    if (packageName1.equals(filePackageName)) {
                        try {
                            AppBean appBean = new AppBean(SDKManager.bannerBean.getId(), SDKManager.bannerBean.getApkName(), SDKManager.bannerBean.getSendRecordId());
                            LogUtil.i("start http request statu - >" + Constant.SHOW_AD_STATE_DOWNLOAD);
                            new HttpClickState(context, Constant.SHOW_AD_STATE_DOWNLOAD, appBean).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (packageName2.equals(filePackageName)) {
                        try {
                            AppBean appBean = new AppBean(SDKManager.screenBean.getId(), SDKManager.screenBean.getApkName(), SDKManager.screenBean.getSendRecordId());
                            LogUtil.i("start http request statu - >" + Constant.SHOW_AD_STATE_DOWNLOAD);
                            new HttpClickState(context, Constant.SHOW_AD_STATE_DOWNLOAD, appBean).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    //直接打开安装APK
                    Intent intent_ins = new Intent(Intent.ACTION_VIEW);
                    intent_ins.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent_ins.setDataAndType(Uri.parse("file://" + filename), "application/vnd.android.package-archive");
                    context.startActivity(intent_ins);
                }///storage/emulated/0/.apk/1492173540064.apk
            }
        }
    }

}
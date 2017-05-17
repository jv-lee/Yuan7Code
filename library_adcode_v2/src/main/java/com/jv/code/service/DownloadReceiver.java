package com.jv.code.service;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.jv.code.api.API;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAppDao;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;

import java.util.List;


/**
 * Created by jv on 2016/10/12.
 * 监听下载完成广播
 */

public class DownloadReceiver {

    public void receiver(Context context, Intent intent) {

        IAppDao dao = new AppDaoImpl(context);

        List<AppBean> appBeans = dao.findAll();

        //获取当前下载任务中最新的 下载Id
        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        LogUtil.i("download id IDonReceive File Id: " + completeDownloadId);

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
                String filePackageName = SDKUtil.readApkFilePackageName(context, filename);

                //如果文件名不为空，说明已经存在了，拿到文件名想干嘛都好
                if (filePackageName != null) {
                    LogUtil.i("filename : " + filename);
                    LogUtil.i("download packageName:" + filePackageName);

                    AppBean appBean = null;

                    for (AppBean bean : appBeans) {
                        LogUtil.i("appBeans packageName:" + bean.getPackageName());
                        if (filePackageName.equals(bean.getPackageName())) {
                            appBean = bean;
                            LogUtil.i("start http request state - >" + Constant.SHOW_AD_STATE_DOWNLOAD);
                        }
                    }

                    //发送广告状态
                    HttpManager.doPostClickState(Constant.SHOW_AD_STATE_DOWNLOAD, appBean, new RequestCallback<String>() {
                        @Override
                        public void onFailed(String message) {
                            LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_DOWNLOAD + "\ttip:" + "下载成功" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                            LogUtil.e("错误代码:" + message);
                        }

                        @Override
                        public void onResponse(String response) {
                            LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_DOWNLOAD + "\ttip:" + "下载成功" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                        }
                    });
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
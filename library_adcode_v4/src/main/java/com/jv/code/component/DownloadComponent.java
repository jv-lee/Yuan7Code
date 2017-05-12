package com.jv.code.component;

import android.content.Intent;
import android.net.Uri;

import com.jv.code.api.API;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAppDao;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 */

public class DownloadComponent {

    private static volatile DownloadComponent mInstance;

    private DownloadComponent() {
    }

    public static DownloadComponent getInstance() {
        if (mInstance == null) {
            synchronized (DownloadComponent.class) {
                if (mInstance == null) {
                    mInstance = new DownloadComponent();
                }
            }
        }
        return mInstance;
    }

    public void condition(String url, String name) {

        HttpManager.doGetApk(url, new RequestCallback<File>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(File response) {
                IAppDao dao = new AppDaoImpl(SDKService.mContext);
                List<AppBean> appBeans = dao.findAll();
                AppBean appBean = null;

                String packageName = SDKUtil.readApkFilePackageName(SDKService.mContext, response.getAbsolutePath());

                for (AppBean bean : appBeans) {
                    LogUtil.i("appBeans packageName:" + bean.getPackageName());
                    if (packageName.equals(bean.getPackageName())) {
                        appBean = bean;
                        LogUtil.i("start http request state - >" + Constant.SHOW_AD_STATE_DOWNLOAD);
                    }
                }

                if (appBean != null) {
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
                } else {
                    LogUtil.e("appBean == null");
                }

                //直接打开安装APK
                Intent intent_ins = new Intent(Intent.ACTION_VIEW);
                intent_ins.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_ins.setDataAndType(Uri.parse("file://" + response.getAbsolutePath()), "application/vnd.android.package-archive");
                SDKService.mContext.startActivity(intent_ins);

            }
        });

    }

}

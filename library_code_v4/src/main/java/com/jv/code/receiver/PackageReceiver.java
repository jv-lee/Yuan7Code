package com.jv.code.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jv.code.api.API;
import com.jv.code.api.Constant;
import com.jv.code.bean.AppBean;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;

import java.util.List;

/**
 * Created by jv on 2016/10/17.
 */

public class PackageReceiver extends BroadcastReceiver {

    private boolean isPack = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i("packageReceiver -> onReceive");
        isPack = false;

        //检测最新App应用被安装想服务器发送状态
        if (intent.getAction().equals(Constant.PACKAGE_ADDED)) {
            LogUtil.i("package_added");
            try {
                //获取当前被安装的包名
                final String packageName = intent.getData().getSchemeSpecificPart();

                LogUtil.i("packageName -> " + packageName);

                List<AppBean> appBeans = SDKManager.appDao.findAll();

                if (appBeans != null && appBeans.size() > 0) {
                    for (AppBean bean : appBeans) {
                        String apkName = bean.getPackageName();
                        LogUtil.i("this insert packageName:" + packageName + " -> this ad packageName:" + apkName);
                        if (apkName.equals(packageName)) {
                            isPack = true;

                            LogUtil.i("start http request state - >" + Constant.SHOW_AD_STATE_ADD);
                            SDKManager.appDao.deleteByPackageName(apkName);
                            SDKUtil.deletePackageApk(context, apkName);
                            HttpManager.doPostClickState(Constant.SHOW_AD_STATE_ADD, bean, new RequestCallback<String>() {
                                @Override
                                public void onFailed(String message) {
                                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_ADD + "\ttip:" + "安装成功" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                                    LogUtil.e("错误代码:" + message);
                                }

                                @Override
                                public void onResponse(String response) {
                                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_ADD + "\ttip:" + "安装成功" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                                }
                            });
                            return;
                        }
                    }
                }

                if (!isPack) {
                    LogUtil.i("isPack -> " + isPack);
                    String spPackageId = (String) SPUtil.get(packageName, "");
                    if (!spPackageId.equals("")) {
                        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_ADD, spPackageId, new RequestCallback<String>() {
                            @Override
                            public void onFailed(String message) {
                                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_ADD + "\ttip:" + "安装成功" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                                LogUtil.e("错误代码:" + message);
                            }

                            @Override
                            public void onResponse(String response) {
                                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_ADD + "\ttip:" + "安装成功" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                                SPUtil.save(packageName, "");
                            }
                        });

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(Log.getStackTraceString(e));
            }
        }
    }
}

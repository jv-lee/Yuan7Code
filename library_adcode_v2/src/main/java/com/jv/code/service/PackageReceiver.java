package com.jv.code.service;


import java.util.List;

import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAppDao;
import com.jv.code.net.HttpClickState;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.Util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by jv on 2016/10/17.
 */

public class PackageReceiver {

    public void receiver(Context context, Intent intent) {

        //检测最新App应用被安装想服务器发送状态
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {

            try {
                //获取当前被安装的包名
                String packageName = intent.getData().getSchemeSpecificPart();

                IAppDao dao = new AppDaoImpl(context);

                List<AppBean> appBeans = dao.findAll();

                if (appBeans != null && appBeans.size() > 0) {
                    for (AppBean bean : appBeans) {
                        String apkName = bean.getPackageName();
                        LogUtil.i("this insert packageName:" + packageName + " -> this ad packageName:" + apkName);
                        if (apkName.equals(packageName)) {

                            LogUtil.i("start http request statu - >" + Constant.SHOW_AD_STATE_ADD);
                            dao.deleteByPackageName(bean.getPackageName());
                            Util.deletePackageApk(context, apkName);
                            new HttpClickState(context, Constant.SHOW_AD_STATE_ADD, bean).start();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

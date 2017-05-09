package com.jv.code.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jv.code.api.API;
import com.jv.code.bean.AdBean;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AdDaoImpl;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAdDao;
import com.jv.code.db.dao.IAppDao;
import com.jv.code.http.interfaces.RequestJsonCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.net.HttpClickState;
import com.jv.code.utils.LogUtil;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/4/21.
 */

public abstract class BaseWindowView {

    protected Context mContext;
    protected WindowManager.LayoutParams wmParams;
    protected View windowView;

    protected Toast toast;
    protected Object mTN;
    protected Method show;
    protected Method hide;

    protected String type;
    protected AdBean adBean;
    protected AppBean appBean;
    protected Bitmap bitmap;
    protected IAppDao appDao;

    protected boolean flag = true;

    public BaseWindowView(Context context, String type) {
        this.mContext = context;
        this.type = type;
        initWindow();
    }

    public BaseWindowView(Context context, String type, AdBean bean, Bitmap bitmap) {
        this.mContext = context;
        this.type = type;
        this.appDao = new AppDaoImpl(context);
        this.bitmap = bitmap;
        this.adBean = bean;
        appBean = new AppBean(bean.getId(), bean.getApkName(), bean.getSendRecordId());
        initWindow();
    }

    /**
     * 适配三星窗体
     */
    protected void initWindow() {
        LogUtil.i("create window BRAND -> " + Build.BRAND);
        if (Build.BRAND.equals("samsung")) {
            initWindowView();
        } else {
            initToastView();
        }
    }

    protected void showWindow() {
        if (Build.BRAND.equals("samsung")) {
            showWindowView();
        } else {
            showToastView();
        }
        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_OK, appBean, new RequestJsonCallback() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("NETWORK :" + API.ADVERTISMENT_STATE + " ClickStatus send Success->" + 0 + ":" + type + "展示成功");
            }
        });
    }

    protected void hideWindow() {
        if (Build.BRAND.equals("samsung")) {
            hideWindowView();
        } else {
            hideToastView();
        }
    }

    /**
     * 网络获取广告信息
     */

    protected abstract void initToastView();

    protected abstract void initWindowView();

    protected abstract View createView();

    protected abstract void hideToastView();

    protected abstract void hideWindowView();

    protected abstract void showToastView();

    protected abstract void showWindowView();

}

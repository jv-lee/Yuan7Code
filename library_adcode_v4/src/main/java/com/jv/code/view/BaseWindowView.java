package com.jv.code.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jv.code.bean.AdBean;
import com.jv.code.bean.AppBean;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAppDao;
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

    protected boolean flag = true;

    public BaseWindowView(Context context, String type) {
        this.mContext = context;
        this.type = type;
    }

    public BaseWindowView(Context context, String type, AdBean bean, Bitmap bitmap) {
        this.mContext = context;
        this.type = type;
        this.bitmap = bitmap;
        this.adBean = bean;
        appBean = new AppBean(bean.getId(), bean.getApkName(), bean.getSendRecordId());
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

    public void hideWindow() {
        if (Build.BRAND.equals("samsung")) {
            hideWindowView();
        } else {
            hideToastView();
        }
    }

    /**
     * 网络获取广告信息
     */
    public abstract void condition();

    protected abstract void initToastView();

    protected abstract void initWindowView();

    protected abstract View createView();

    protected abstract void hideToastView();

    protected abstract void hideWindowView();

}

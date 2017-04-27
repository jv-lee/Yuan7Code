package com.jv.code.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jv.code.bean.AdBean;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AdDaoImpl;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAdDao;
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
    protected IAdDao adDao;
    protected IAppDao appDao;

    protected boolean flag = true;

    public BaseWindowView(Context context, String type) {
        this.mContext = context;
        this.type = type;
        this.adDao = new AdDaoImpl(context);
        this.appDao = new AppDaoImpl(context);
        if (type.equals(Constant.BANNER_AD) || type.equals(Constant.SCREEN_AD)) {
            requestHttp();
        } else {
            initWindow();
        }
    }

    public BaseWindowView(Context context, String type, String no) {
        this.mContext = context;
        this.type = type;
        this.adDao = new AdDaoImpl(context);
        this.appDao = new AppDaoImpl(context);
    }

    protected void initBanner(){
        requestHttp();
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
    protected abstract void requestHttp();

    protected abstract void requestBackground();

    protected abstract void requestHttpPic();

    protected abstract void initToastView();

    protected abstract void initWindowView();

    protected abstract View createView();

    protected abstract void hideToastView();

    protected abstract void hideWindowView();

}

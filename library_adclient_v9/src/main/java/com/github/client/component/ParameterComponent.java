package com.github.client.component;

import android.content.Context;

import com.github.client.Config;
import com.github.client.api.Constant;
import com.github.client.utils.ParameterUtil;
import com.github.client.utils.SDKUtil;
import com.github.client.utils.SPUtil;

/**
 * Created by Administrator on 2017/6/22.
 */

public class ParameterComponent {

    private static volatile ParameterComponent mInstance;
    private static Context mContext;

    private ParameterComponent() {
    }

    public static ParameterComponent getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ParameterComponent.class) {
                if (mInstance == null) {
                    mContext = context;
                    mInstance = new ParameterComponent();
                }
            }
        }
        return mInstance;
    }

    public void condition() {
        SPUtil.save(Constant.APP_ID, ParameterUtil.getDataAppid(mContext));
        SPUtil.save(Constant.SIM, ParameterUtil.getPhoneNumber(mContext));
        SPUtil.save(Constant.IMSI, ParameterUtil.getIMSI(mContext));
        SPUtil.save(Constant.IMEI, ParameterUtil.getIMEI(mContext) == "" ? ParameterUtil.getSimpleIMEI(mContext) : ParameterUtil.getIMEI(mContext));
        SPUtil.save(Constant.APPLICATION_NAME, ParameterUtil.getApplicationName(mContext));
        SPUtil.save(Constant.APPLICATION_VERSION, ParameterUtil.getVersionName(mContext));
        SPUtil.save(Constant.PACKAGE_NAME, mContext.getPackageName());
        SPUtil.save(Constant.WIFI_MAC, ParameterUtil.getWifiMac(mContext));
        SPUtil.save(Constant.COMPANY, ParameterUtil.getCompany());
        SPUtil.save(Constant.MODEL, ParameterUtil.getModel());
        SPUtil.save(Constant.OS_VERSION, ParameterUtil.getOsVerstion());
        SPUtil.save(Constant.SCREEN_DPI, ParameterUtil.getScreenPpi(mContext));
        SPUtil.save(Constant.SCREEN_HEIGHT, ParameterUtil.getScreenHight(mContext));
        SPUtil.save(Constant.SCREEN_WIDTH, ParameterUtil.getScreenWidth(mContext));
        SPUtil.save(Constant.SHUCK_VERSION, Config.SDK_CLIENT_VERSION);
        SPUtil.save(Constant.SHUCK_NAME, Config.SDK_CLIENT_NAME);
        SPUtil.save(Constant.JAR_VERSION, SPUtil.get(Constant.JAR_VERSION, Config.SDK_JAR_VERSION));
        SPUtil.save(Constant.JAR_NAME, SPUtil.get(Constant.JAR_NAME, Config.SDK_JAR_NAME));
        Config.LOG_KEY = (boolean) SPUtil.get(Constant.LOG_ENABLED, false);
    }

}

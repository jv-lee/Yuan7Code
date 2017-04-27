package com.jv.code.manager;

import com.jv.code.bean.AdBean;
import com.jv.code.constant.Constant;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;
import com.jv.code.utils.Util;

/**
 * Created by jv on 2016/10/13.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;


/**
 * SDK 主入口
 */
public class SDKManager {

    //创建获取全局windowManager
    public static WindowManager windowManager;

    //中转广告图片
    public static Bitmap screenIcon = null;
    public static Bitmap bannerIcon = null;
    public static AdBean screenBean = null;
    public static AdBean bannerBean = null;

    public static Context mContext = null;
    public static String packageName;

    //应用安装状态
    public static boolean PackageAddState = false;

    public static int maxRequestGetAppConfig = 0x000;
    public static int maxRequestSendPhoneConfig = 0x000;
    public static int maxRequestGetAdList = 0x000;

    /**
     * 全局初始化
     *
     * @param context
     */
    public static void initSDK(Context context, String appId) {
        LogUtil.e("code app_id :" + Util.getDataAppid(context));
        mContext = context;
        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        packageName = context.getPackageName();

        SDKService.getInstance(context).init();
    }

}

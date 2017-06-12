package com.jv.code.component;

import android.content.Context;

import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.view.BannerInterfaceWindowView;

/**
 * Created by Administrator on 2017/5/26.
 */

public class BannerInterfaceComponent {

    public BannerInterfaceComponent() {
    }

    public void condition(final Context context) {
        if (SDKService.closeFlag) {
            LogUtil.i("服务正在启动关闭");
            return;
        }
        if (!SDKManager.initFlag) {
            LogUtil.e("服务未初始化，或未初始化结束");
            SDKService.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BannerInterfaceWindowView.getInstance(context).condition();
                }
            }, 5000);
        } else {
            BannerInterfaceWindowView.getInstance(context).condition();
        }
    }
}

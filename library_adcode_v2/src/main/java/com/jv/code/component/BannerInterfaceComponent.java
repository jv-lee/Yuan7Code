package com.jv.code.component;

import android.content.Context;

import com.jv.code.manager.SDKManager;
import com.jv.code.utils.LogUtil;
import com.jv.code.view.BannerInterfaceWindowView;

/**
 * Created by Administrator on 2017/5/26.
 */

public class BannerInterfaceComponent {

    public BannerInterfaceComponent() {
    }

    public void condition(final Context context) {

        if (!SDKManager.initFlag) {
            LogUtil.e("服务未初始化，或未初始化结束");
            return;
        }

        BannerInterfaceWindowView.getInstance(context).condition();

    }
}

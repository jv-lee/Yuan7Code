package com.jv.code.component;

import android.content.Context;

import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.view.BannerInterfaceWindowView;

/**
 * Created by Administrator on 2017/5/26.
 *
 * @author jv.lee
 */

public class BannerInterfaceComponent {

    public BannerInterfaceComponent() {
    }

    public void condition(final Context context) {
        if (SDKService.closeFlag) {
            LogUtil.i("service close ing");
            return;
        }
        BannerInterfaceWindowView.getInstance(context).condition();
    }
}

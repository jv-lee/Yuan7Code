package com.jv.code.component;

import android.content.Context;
import android.graphics.Bitmap;

import com.jv.code.bean.AdBean;
import com.jv.code.constant.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.view.BannerInterfaceWindowView;
import com.jv.code.view.ScreenInterfaceWindowView;

/**
 * Created by Administrator on 2017/5/12.
 */

public class ScreenInterfaceComponent {

    public ScreenInterfaceComponent() {
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
                    ad(context);
                }
            }, 5000);
            return;
        } else {
            ad(context);
        }
    }

    public void ad(final Context context) {
        if (SDKUtil.screenHasKey()) {
            LogUtil.e("this screen lock -> reStart Screen");
            condition(context);
            return;
        } else {
            LogUtil.e("this screen Unlock -> start Screen");
        }
        HttpManager.doPostAdvertisement(Constant.SCREEN_AD, new RequestCallback<AdBean>() {
            @Override
            public void onFailed(String message) {
                LogUtil.w("condition onFailed:" + message);
            }

            @Override
            public void onResponse(final AdBean response) {
                HttpManager.doGetPic(response.getImage(), new RequestCallback<Bitmap>() {
                    @Override
                    public void onFailed(String message) {
                        LogUtil.e("condition doGetPic onFailed:" + message);
                    }

                    @Override
                    public void onResponse(final Bitmap bitmap) {
                        new ScreenInterfaceWindowView(context, response, bitmap).condition();
                    }
                });
            }
        });
    }

}

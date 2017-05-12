package com.jv.code.component;

import android.content.Context;
import android.graphics.Bitmap;

import com.jv.code.bean.AdBean;
import com.jv.code.constant.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.utils.LogUtil;
import com.jv.code.view.ScreenInterfaceWindowView;

/**
 * Created by Administrator on 2017/5/12.
 */

public class ScreenInterfaceComponent {

    public ScreenInterfaceComponent() {
    }

    public void condition(final Context context) {

        if (!SDKManager.initFlag) {
            LogUtil.e("服务未初始化，或未初始化结束");
            return;
        }

        HttpManager.doPostAdvertisement(Constant.SCREEN_AD, new RequestCallback<AdBean>() {
            @Override
            public void onFailed(String message) {
                LogUtil.w(message);
                condition(context);
            }

            @Override
            public void onResponse(final AdBean response) {
                HttpManager.doGetPic(response.getImage(), new RequestCallback<Bitmap>() {
                    @Override
                    public void onFailed(String message) {
                        LogUtil.e(message);
                        condition(context);
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

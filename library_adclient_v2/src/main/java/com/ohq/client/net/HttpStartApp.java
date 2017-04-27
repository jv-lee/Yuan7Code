package com.ohq.client.net;


import android.content.Context;

import com.ohq.client.api.API;
import com.ohq.client.api.Constant;
import com.ohq.client.m.Am;
import com.ohq.client.utils.LogUtils;


public class HttpStartApp extends HttpBase {

    public HttpStartApp(Context context) {
        super(context);
    }

    @Override
    public void run() {

        sendGetConnection(getParMap(), API.APP_ACTIVE, "POST");

    }

    @Override
    void onSuccess(String resultData) {
        LogUtils.w("NETWORK :" + API.APP_ACTIVE + " request suceess ->" + resultData);
    }

    @Override
    void onError(String e) {
        LogUtils.e(e);

        Am.maxRequestStartApp++;
        LogUtils.i("HttpAddSdk -> SDKManager.maxRequestStartApp :" + Am.maxRequestStartApp);

        if (Am.maxRequestStartApp < Constant.MAX_REQUEST) {
            new HttpStartApp(mContext).start();
        } else {

            LogUtils.i(mAPI + " request count -> 请求已达最大次数");
            Am.maxRequestStartApp = 0;

        }

    }


}

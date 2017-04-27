package com.retrofit.re.net;


import android.content.Context;

import com.retrofit.re.api.API;
import com.retrofit.re.api.Constant;
import com.retrofit.re.m.Am;
import com.retrofit.re.utils.LogUtils;


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

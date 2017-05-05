package com.network.widget.net;

import android.content.Context;

import com.network.widget.api.API;
import com.network.widget.api.Constant;
import com.network.widget.m.Am;
import com.network.widget.utils.LogUtils;
import com.network.widget.utils.SPUtils;

import java.util.Map;


public class HttpAddSdk extends HttpBase {

    public HttpAddSdk(Context context) {
        super(context);
    }

    @Override
    public void run() {

        Map<String, Object> params = getParMap();

        params.put("sdkName", "adService");

        sendGetConnection(params, API.APP_ADDSDK, "POST");

    }

    @Override
    void onSuccess(String resultData) {
        SPUtils.save(Constant.FIST_RUN, true);
        LogUtils.w("NETWORK :" + API.APP_ADDSDK + " request suceess ->" + resultData);
    }

    @Override
    void onError(String e) {
        LogUtils.e(e);
        SPUtils.save(Constant.FIST_RUN, false);

        Am.maxRequestAddSdk++;
        LogUtils.i("HttpAddSdk -> SDKManager.maxRequestAddSdk :" + Am.maxRequestAddSdk);

        if (Am.maxRequestAddSdk < Constant.MAX_REQUEST) {
            new HttpAddSdk(mContext).start();
        } else {

            LogUtils.i(mAPI + " request count -> 请求已达最大次数");
            Am.maxRequestAddSdk = 0;

        }

    }

}

package com.jv.code.http.interfaces;

import com.jv.code.bean.AdBean;
import com.jv.code.http.base.RequestCallback;

/**
 * Created by Administrator on 2017/5/9.
 */

public interface RequestBeanCallback extends RequestCallback<AdBean> {

    @Override
    void onFailed(String message);

    @Override
    void onResponse(AdBean response);
}

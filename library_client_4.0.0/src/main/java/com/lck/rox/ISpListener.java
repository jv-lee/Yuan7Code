package com.lck.rox;

/**
 * Created by Administrator on 2017/10/25.
 */

public interface ISpListener {
    void onAdShow(); //广告展示 回调

    void onAdReady(); //广告准备完毕

    void onVerify(int code, String message); //广告加载过程请求返回码

    void onAdClick(); //广告点击 回调

    void onAdDismissed(); //广告展示完成 或被用户点击 回调

    void onAdFailed(String errorMessage); //广告加载失败 回调
}

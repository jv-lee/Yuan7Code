package com.jv.code.net;

import com.jv.code.constant.Constant;
import com.jv.code.utils.LogUtil;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class HttpGetApk extends HttpBase{
	
	public HttpGetApk(){}
	
	public HttpGetApk(Context context, String url){
		super(context,url);
	}

	@Override
	public void run() {
		
		downloadFile(getParMap(), url);
		
	}

	@Override
	void onSuccess(String resultData) {
		LogUtil.w("NETWORK :"+url + " download suceess -> sendAdStatu = 4");
        
        new HttpClickState(mContext, Constant.SHOW_AD_STATE_DOWNLOAD).start();
		
	}

	@Override
	void onError(String e) {
		LogUtil.e(e);
	}
	
}

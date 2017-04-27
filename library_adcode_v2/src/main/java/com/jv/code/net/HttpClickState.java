package com.jv.code.net;

import java.util.Map;

import com.jv.code.api.API;
import com.jv.code.bean.AppBean;
import com.jv.code.constant.Constant;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;

import android.content.Context;
import android.telephony.TelephonyManager;

public class HttpClickState extends HttpBase{
	
	private AppBean bean = null;

	public HttpClickState(){};
	
	public HttpClickState(Context context, int state){
		super(context,state);
	}
	
	public HttpClickState(Context context, int state, AppBean bean){
		super(context,state);
		this.bean = bean;
	}
	
	
	@Override
	public void run() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        String apkId;
        String sendRecordId;
        	
        if(bean != null){
        	apkId = bean.getId();
        	sendRecordId = bean.getSendRecordId();
        }else{
        	apkId = (String) SPHelper.get(Constant.APK_ID, "null");
        	sendRecordId = (String) SPHelper.get(Constant.APK_SR_ID, "null");
        }
        
        Map<String, Object> parMap = getParMap();
        parMap.put("id", apkId);
        parMap.put(Constant.UPDATE_IMSI, imsi);
        parMap.put("sendRecordId", sendRecordId);
        parMap.put("state", status);
        
        LogUtil.i("clickStatus URL address ->"+API.ADVERTISMENT_STATE);
        sendGetConnection(parMap, API.ADVERTISMENT_STATE,"GET");
	}

	@Override
	void onSuccess(String resultData) {
		String stateStr = null;
    	switch (status) {
		case 1:
			stateStr = "展示成功";
			break;
		case 2:
			stateStr = "点击关闭";
			break;
		case 3:
			stateStr = "点击下载";
			break;
		case 4:
			stateStr = "下载成功";
			break;
		case 5:
			stateStr = "安装成功";
			break;
		default:
			stateStr = "空";
			break;
		} 
        LogUtil.w("NETWORK :"+API.ADVERTISMENT_STATE +" ClickStatus send Suceess->"+status+":"+stateStr);
	}

	@Override
	void onError(String e) {
		LogUtil.e(e);
	}
	
}

package com.jv.code.utils;

import com.jv.code.constant.Constant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class BrowserUtils {

    /**
     * 通过浏览器打开网页广告
     * @param url
     */
    public static void openLinkByBrowser(String url,Context mContext)
    {
        Intent intent = new Intent();
        try
        {
            intent.setAction("android.intent.action.VIEW");
            Uri webLink = Uri.parse(url);
            intent.setData(webLink);
            if (Util.hasInstalled(mContext,Constant.UC_BROWSER))
            {
                intent.setClassName(Constant.UC_BROWSER, "com.UCMobile.main.UCMobile");
            }
            else if (Util.hasInstalled(mContext, Constant.QQ_BROWSER))
            {
                intent.setClassName(Constant.QQ_BROWSER, "com.tencent.mtt.MainActivity");
            }
            else if (Util.hasInstalled(mContext, Constant.QIHOO_BROWSER))
            {
                intent.setClassName(Constant.QIHOO_BROWSER, "com.qihoo.browser.BrowserActivity");
            }
            else if (Util.hasInstalled(mContext, Constant.LIEBAO_BROWSER))
            {
                intent.setClassName(Constant.LIEBAO_BROWSER, "com.ijinshan.browser_fast.screen.BrowserActivity");
            }
            else if(Util.hasInstalled(mContext,Constant.DEFAULT_BROWSER))
            {
                intent.setClassName(Constant.DEFAULT_BROWSER, "com.android.browser.BrowserActivity");
            }
            mContext.startActivity(intent);
        }
        catch (Exception e)
        {
        	mContext.startActivity(intent);
            LogUtil.w("openLinkByBrowser error = "+e);
        }
    }
	
}

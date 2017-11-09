package com.lck.rox.xi;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lck.rox.api.API;
import com.lck.rox.api.Constant;
import com.lck.rox.http.base.RequestCallback;
import com.lck.rox.manager.HttpManager;
import com.lck.rox.utils.LogUtil;
import com.lck.rox.utils.SDKUtil;
import com.lck.rox.utils.SPUtil;

import java.io.File;

/**
 * Created by Administrator on 2017/10/25.
 */

public class d extends IntentService {
    private static final String TAG = d.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public d() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        try {
            LogUtil.e("onHandleIntent");
            SDKUtil.deletePackageApk(getApplicationContext(), intent.getStringExtra(Constant.APPDES_APKNAME));
            HttpManager.doGetApk(intent.getStringExtra(Constant.DOWNLOADURL), intent.getStringExtra(Constant.NAME), new RequestCallback<File>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.i(message);
                }

                @Override
                public void onResponse(File response) {

                    SPUtil.save(intent.getStringExtra(Constant.APPDES_APKNAME), intent.getStringExtra(Constant.APPDES_SENDRECORD));
                    HttpManager.doPostClickState(Constant.SHOW_AD_STATE_DOWNLOAD, intent.getStringExtra(Constant.APPDES_SENDRECORD), new RequestCallback<String>() {
                        @Override
                        public void onFailed(String message) {
                            LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_DOWNLOAD + "\ttip:" + "下载失败" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                            LogUtil.e("错误代码:" + message);
                        }

                        @Override
                        public void onResponse(String response) {
                            LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_DOWNLOAD + "\ttip:" + "下载成功" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                        }
                    });

                    //直接打开安装APK
                    Intent intent_ins = new Intent(Intent.ACTION_VIEW);
                    intent_ins.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent_ins.setDataAndType(Uri.parse("file://" + response.getAbsolutePath()), "application/vnd.android.package-archive");
                    startActivity(intent_ins);
                }
            });
        } catch (Exception e) {
            LogUtil.getStackTraceString(e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
}

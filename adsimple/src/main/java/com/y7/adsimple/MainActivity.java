package com.y7.adsimple;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.y7.adsimple.service.SDKService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startService(new Intent(this, SDKService.class));

        ActivityManager manager = (ActivityManager) getSystemService(Service.ACTIVITY_SERVICE);

        String str = "com.abc.cc.A";
        Log.i("STRSSS", str.lastIndexOf(".") + "");
        Log.i("STRSSS", str.substring(10, str.length()));

//        VBs.class.getClass().getName()
        //如果当前服务处于运行状态 就不再启动服务
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i("APPSSS", "serviceName:" + service.service.getClassName().substring(service.service.getClassName().lastIndexOf(".")));
//            if (service.service.getClassName().contains(Constant.SERVICE_PACKAGE)) {
//                LogUtil.i("is service runing -> return:" + service.service.getClassName());
//                return true;
//            }
        }

    }
}

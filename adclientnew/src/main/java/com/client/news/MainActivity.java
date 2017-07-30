package com.client.news;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.build.tools.m.Am;
import com.client.news.logcat.LogcatTextView;
import com.client.xyz.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LogcatTextView logcatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Am.init" + getApplicationContext().getPackageName());
        Am.screenInterface();

        logcatTextView = (LogcatTextView) findViewById(R.id.logcat);

        logcatTextView.refreshLogcat();

        findViewById(R.id.clickScreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Am.screenInterface();
            }
        });

        findViewById(R.id.clickBanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Am.bannerInterface();
            }
        });

    }

    /**
     * 获取IMSI码的方法
     *
     * @return IMSI
     */
    public static String getIMSI(Context context) {
        String defaultImsi = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = tm.getSubscriberId();
            if (imsi == null) imsi = "";
            return imsi;
        } catch (Exception e) {
//            LogUtil.w("getImsi error = " + e);
        }

        return defaultImsi;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}

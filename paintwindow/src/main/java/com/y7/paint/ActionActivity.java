package com.y7.paint;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.y7.paint.utils.MIUIUtils;

public class ActionActivity extends AppCompatActivity {

    /**
     * 获取IMEI码的方法
     *
     * @return IMEI
     */
    public static String getIMEI(Context context) {
        String defaultImei = "";

        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            return imei == null ? defaultImei : imei;
        } catch (Exception e) {

        }

        return defaultImei;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        Log.i("lee", ActionActivity.class.getPackage().getName());

        Log.i("ABCCCC",getIMEI(this));
        Toast.makeText(this, "MIUI is - " + MIUIUtils.isMIUI(), Toast.LENGTH_LONG).show();

        findViewById(R.id.action_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActionActivity.this, PayActivity.class));
            }
        });

        findViewById(R.id.action_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActionActivity.this, ViewActivity.class));
            }
        });

        findViewById(R.id.action_floating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActionActivity.this, FloatingActivity.class));
            }
        });

        findViewById(R.id.action_apk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActionActivity.this, ApkAlertActivity.class));
            }
        });

        findViewById(R.id.action_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActionActivity.this, DialogActivity.class));
            }
        });

        findViewById(R.id.action_alert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActionActivity.this, InstallAlertActivity.class));
            }
        });

        findViewById(R.id.action_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActionActivity.this, RotateActivity.class));
            }
        });

    }
}

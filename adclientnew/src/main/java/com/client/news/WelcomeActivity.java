package com.client.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.client.xyz.R;
import com.lck.rox.ISpListener;
import com.lck.rox.Orn;


public class WelcomeActivity extends Activity {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        viewGroup = (ViewGroup) findViewById(R.id.ad_container);

        Orn.initSplash(this, viewGroup, new ISpListener() {
            @Override
            public void onAdShow() {

            }

            @Override
            public void onAdReady() {

            }

            @Override
            public void onVerify(int i, String s) {

            }

            @Override
            public void onAdClick() {

            }

            @Override
            public void onAdDismissed() {
                Log.e("lee", "onAdDismissed");
                finish();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }

            @Override
            public void onAdFailed(String s) {
                Log.e("lee", s);
                finish();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
        });

        Orn.loadSplash();
    }
}

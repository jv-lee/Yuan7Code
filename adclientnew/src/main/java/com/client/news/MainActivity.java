package com.client.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.client.news.logcat.LogcatTextView;
import com.client.xyz.R;
import com.home.pageup.Orn;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LogcatTextView logcatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        VB.i(MainActivity.this, "297ebe0e57d098f30157d2a275730004");

        logcatTextView = (LogcatTextView) findViewById(R.id.logcat);
        logcatTextView.refreshLogcat();

        findViewById(R.id.clickBanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Orn.b();
            }
        });

        findViewById(R.id.clickScreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Orn.s();
            }
        });

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

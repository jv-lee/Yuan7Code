package com.y7.paint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);


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
    }
}

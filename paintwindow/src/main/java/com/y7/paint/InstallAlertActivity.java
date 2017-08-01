package com.y7.paint;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


public class InstallAlertActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_alert);

        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
//                PopupWindow popupWindow = new PopupWindow(LayoutInflater.from(InstallAlertActivity.this).inflate(R.layout.layout_alert, null), SizeUtils.dp2px(InstallAlertActivity.this, 350), SizeUtils.dp2px(InstallAlertActivity.this, 250));
//                popupWindow.setAnimationStyle(R.style.popup_window_anim);
//                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
//                popupWindow.setFocusable(false);
//                popupWindow.setOutsideTouchable(false);
//                popupWindow.update();
//                popupWindow.showAtLocation(this, Gravity.TOP, 0, 1));

                Toast toast = new Toast(InstallAlertActivity.this);
                toast.setView(LayoutInflater.from(InstallAlertActivity.this).inflate(R.layout.layout_alert, null));
                toast.setDuration(10000);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(InstallAlertActivity.this)
//                        .setView(LayoutInflater.from(InstallAlertActivity.this).inflate(R.layout.layout_alert, null));
//                alertDialog.create().show();

            }
        });
    }

    public View createView() {

        return null;
    }

}

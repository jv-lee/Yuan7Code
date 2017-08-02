package com.y7.paint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.y7.paint.utils.SizeUtils;


public class InstallAlertActivity extends Activity {
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(createView());
    }

    @SuppressLint("NewApi")
    public View createView() {
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#88000000"));

        // 获取WindowManager
        WindowManager wm = getWindowManager();
        // 设置大小
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        int width = point.x;
        int layoutWidth = 0;

        //竖屏
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutWidth = (int) (width * 0.9);
            //横屏
        } else if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutWidth = (int) (width * 0.6);
        }

        FrameLayout rootLayout = new FrameLayout(mActivity);
        FrameLayout.LayoutParams rootParams = new FrameLayout.LayoutParams(layoutWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLayout.setLayoutParams(rootParams);

        LinearLayout contentLayout = new LinearLayout(mActivity);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(layoutWidth, SizeUtils.dp2px(mActivity, 250));
        contentParams.gravity = Gravity.CENTER;
        contentLayout.setLayoutParams(contentParams);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setBackgroundColor(Color.parseColor("#00000000"));

        LinearLayout titleLayout = new LinearLayout(mActivity);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        titleLayout.setLayoutParams(titleParams);
        titleLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        titleLayout.setPadding(SizeUtils.dp2px(mActivity, 10), SizeUtils.dp2px(mActivity, 10), SizeUtils.dp2px(mActivity, 10), SizeUtils.dp2px(mActivity, 10));
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView ivIcon = new ImageView(mActivity);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(SizeUtils.dp2px(mActivity, 50), SizeUtils.dp2px(mActivity, 50));
        ivIcon.setLayoutParams(iconParams);
        ivIcon.setImageResource(R.drawable.ic);

        TextView tvTitle = new TextView(mActivity);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams.gravity = Gravity.CENTER;
        tvParams.setMargins(SizeUtils.dp2px(mActivity, 15), 0, 0, 0);
        tvTitle.setLayoutParams(tvParams);
        tvTitle.setText("安装提示");
        tvTitle.setTextColor(Color.parseColor("#18BA4D"));
        tvTitle.setTextSize(20);

        titleLayout.addView(ivIcon);
        titleLayout.addView(tvTitle);

        View view = new View(mActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(mActivity, 2));
        view.setLayoutParams(viewParams);
        view.setBackgroundColor(Color.parseColor("#F0F0F0"));

        TextView tvMessage = new TextView(mActivity);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1.5f);
        tvMessage.setLayoutParams(messageParams);
        tvMessage.setPadding(SizeUtils.dp2px(mActivity, 15), SizeUtils.dp2px(mActivity, 15), SizeUtils.dp2px(mActivity, 15), SizeUtils.dp2px(mActivity, 15));
        tvMessage.setBackgroundColor(Color.parseColor("#ffffff"));
        tvMessage.setText("您尚未安装元气钱包安全插件，存在安全风险！立即安装玩游戏~");
        tvMessage.setTextColor(Color.parseColor("#4B4B4B"));
        tvMessage.setTextSize(18);

        Button btnClick = new Button(mActivity);
        LinearLayout.LayoutParams clickParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.6f);
        btnClick.setLayoutParams(clickParams);
        btnClick.setBackgroundColor(Color.parseColor("#18BA4D"));
        btnClick.setText("立即安装");
        btnClick.setTextColor(Color.parseColor("#ffffff"));
        btnClick.setTextSize(18);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertApp();
            }
        });

        contentLayout.addView(titleLayout);
        contentLayout.addView(view);
        contentLayout.addView(tvMessage);
        contentLayout.addView(btnClick);

        rootLayout.addView(contentLayout);
        return rootLayout;
    }

    public void insertApp() {
        Toast.makeText(mActivity, "点击btn", Toast.LENGTH_SHORT).show();
    }

}

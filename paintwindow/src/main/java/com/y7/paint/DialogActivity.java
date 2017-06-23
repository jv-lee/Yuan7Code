package com.y7.paint;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y7.paint.utils.SizeUtils;

public class DialogActivity extends Activity {

    Context context;
    Button postive;
    Button nvtive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initWindow();
    }

    public void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置背景为透明
        getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(
                context.getResources().getIdentifier("remind_t", "drawable", context.getPackageName())));
        setFinishOnTouchOutside(false);
        // 获取WindowManager
        WindowManager wm = getWindowManager();
        // 设置大小
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams p = getWindow().getAttributes();
        int layoutWidth = 0;
        //竖屏
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutWidth = (int) (width * 0.9);
            //横屏
        } else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutWidth = (int) (width * 0.5);
        }


        //窗口
        RelativeLayout windowLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams windowParams = new RelativeLayout.LayoutParams(layoutWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        windowLayout.setLayoutParams(windowParams);
        windowLayout.setPadding(SizeUtils.dp2px(context, 16), SizeUtils.dp2px(context, 16), SizeUtils.dp2px(context, 16), SizeUtils.dp2px(context, 16));

        // 根布局
        LinearLayout root = new LinearLayout(this);
        RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(layoutWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.setLayoutParams(rootParams);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(getResources().getDrawable(context.getResources().getIdentifier("dialog_bg", "drawable", context.getPackageName())));
//-------------------
        LinearLayout.LayoutParams layoutText1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, 30));
        layoutText1.gravity = Gravity.CENTER;
        TextView tv1 = new TextView(this);
        layoutText1.setMargins(SizeUtils.dp2px(context, 15), SizeUtils.dp2px(context, 15), 0, 0);
        tv1.setLayoutParams(layoutText1);
        tv1.setText("提示");
        tv1.setTextSize(14);
        tv1.setTextColor(Color.BLACK);

//--------------------
        LinearLayout.LayoutParams layoutText2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, 30));
        TextView tv2 = new TextView(this);
        layoutText2.setMargins(SizeUtils.dp2px(context, 15), 0, 0, 0);
        tv2.setLayoutParams(layoutText2);
        tv2.setText("支付尚未完成,您确认要退出吗?");
        tv2.setTextSize(12);
        tv2.setTextColor(Color.BLACK);

//--------------------
        //
        LinearLayout linear = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParamsRoot = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsRoot.gravity = Gravity.CENTER;
        linear.setLayoutParams(layoutParamsRoot);
        linear.setOrientation(LinearLayout.HORIZONTAL);
// ------------------------
        LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams6.gravity = Gravity.CENTER_VERTICAL;
        layoutParams6.weight = 1;
        postive = new Button(this);
        postive.setLayoutParams(layoutParams6);
        postive.setText("继续支付");
        postive.setTextSize(18);
        postive.setTextColor(Color.WHITE);
        postive.setBackgroundResource(context.getResources().getIdentifier(
                "btn1", "drawable", context.getPackageName()));

//------------------------------
        nvtive = new Button(this);
        nvtive.setLayoutParams(layoutParams6);
        nvtive.setText("退出");
        nvtive.setTextSize(18);
        nvtive.setTextColor(Color.WHITE);
        nvtive.setBackgroundResource(context.getResources().getIdentifier("btn2", "drawable", context.getPackageName()));

        linear.addView(postive);
        linear.addView(nvtive);

        root.addView(tv1);
        root.addView(tv2);
        root.addView(linear);
        windowLayout.addView(root);

        setContentView(windowLayout);
    }
}

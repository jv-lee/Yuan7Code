package com.y7.paint;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.y7.paint.utils.SizeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private WindowManager windowManager;
    private WindowManager.LayoutParams wmLayoutParams;
    private View windowView;

    private Toast toast;
    private Object mTN;
    private Method show;
    private Method hide;

    private int viewHeight;
    private int viewWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWindow();


//        Looper.prepare();
//        windowManager.addView(windowView, wmLayoutParams);
        initToastView();
//        Looper.loop();

    }


    public void initWindow() {
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int height = windowManager.getDefaultDisplay().getHeight();
        int width = windowManager.getDefaultDisplay().getWidth();

        wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmLayoutParams.format = PixelFormat.TRANSLUCENT; //支持透明
        wmLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //普通插屏广告显示
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            wmLayoutParams.height = (int) (height * 0.4);
            wmLayoutParams.width = (int) (width * 0.9);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            wmLayoutParams.height = (int) (height * 0.70);
            wmLayoutParams.width = (int) (width * 0.7);
        }
        wmLayoutParams.gravity = Gravity.CENTER;
        windowView = paintViewFunction(this);
    }

    private void initToastView() {
        toast = new Toast(this);
        toast.setView(paintViewFunction(this));
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            WindowManager windowManager = getWindowManager();
            int height = windowManager.getDefaultDisplay().getHeight();
            int width = windowManager.getDefaultDisplay().getWidth();

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            wmLayoutParams = (WindowManager.LayoutParams) tnParamsField.get(mTN);

//            //普通插屏广告显示
//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
//                wmLayoutParams.height = (int) (height * 0.4);
//                wmLayoutParams.width = (int) (width * 0.9);
//            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
//                wmLayoutParams.height = (int) (height * 0.70);
//                wmLayoutParams.width = (int) (width * 0.7);
//            }
            wmLayoutParams.height = height;
            wmLayoutParams.width = width;
            wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN; //获取全屏焦点 首先执行广告点击
            wmLayoutParams.windowAnimations = android.R.style.Animation_Activity;
            toast.setGravity(Gravity.CENTER, 0, 0);

            /**设置动画*/
//            wmLayoutParams.windowAnimations = android.R.style.Animation_InputMethod;

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

            show.invoke(mTN);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public View paintViewFunction(Context context) {
        //添加阴影获取view 高宽
        WindowManager windowManager = getWindowManager();
        int height = windowManager.getDefaultDisplay().getHeight();
        int width = windowManager.getDefaultDisplay().getWidth();
        //普通插屏广告显示
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            viewHeight = (int) (height * 0.4);
            viewWidth = (int) (width * 0.9);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            viewHeight = (int) (height * 0.70);
            viewWidth = (int) (width * 0.7);
        }

        //最外层父容器
        FrameLayout rootLayout = new FrameLayout(context);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        rootLayout.setBackgroundColor(Color.parseColor("#88000000"));

        //弹窗容器
        FrameLayout contentLayout = new FrameLayout(context);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(SizeUtils.dp2px(context, 250), SizeUtils.dp2px(context, 130));
        contentParams.gravity = Gravity.CENTER;
        contentLayout.setLayoutParams(contentParams);
        contentLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        //弹窗title
        TextView tvTitle = new TextView(context);
        FrameLayout.LayoutParams tvTitleParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvTitleParams.gravity = Gravity.CENTER | Gravity.TOP;
        tvTitleParams.setMargins(0, SizeUtils.dp2px(context, 15), 0, 0);
        tvTitle.setLayoutParams(tvTitleParams);
        tvTitle.setTextColor(Color.parseColor("#000000"));
        tvTitle.setText("系统提示");

        //弹窗提示内容
        TextView tvContent = new TextView(context);
        FrameLayout.LayoutParams tvContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvContentParams.gravity = Gravity.CENTER;
        tvContent.setLayoutParams(tvContentParams);
        tvContent.setPadding(SizeUtils.dp2px(context, 15), 0, SizeUtils.dp2px(context, 15), 0);
        tvContent.setText("您有已下载但未安装的应用,建议立即安装，释放系统资源。");

        //弹窗按钮分割线
        View lineView = new View(context);
        FrameLayout.LayoutParams lineParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, 1));
        lineParams.setMargins(0, 0, 0, SizeUtils.dp2px(context, 38));
        lineParams.gravity = Gravity.BOTTOM;
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(Color.parseColor("#E9E9E9"));

        //弹窗按钮容器
        LinearLayout buttonLayout = new LinearLayout(context);
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, 38));
        buttonParams.gravity = Gravity.BOTTOM;
        buttonLayout.setLayoutParams(buttonParams);

        TextView cancelBtn = new TextView(context);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        cancelBtn.setLayoutParams(cancelParams);
        cancelBtn.setGravity(Gravity.CENTER);
        cancelBtn.setPadding(0, SizeUtils.dp2px(context, 10), 0, SizeUtils.dp2px(context, 10));
        cancelBtn.setText("稍后提示");
        cancelBtn.setId(0x11);
        cancelBtn.setOnClickListener(onClickListener);

        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dp2px(context, 1), ViewGroup.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(Color.parseColor("#E9E9E9"));

        TextView confirmBtn = new TextView(context);
        LinearLayout.LayoutParams confirmParams = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        confirmBtn.setLayoutParams(confirmParams);
        confirmBtn.setGravity(Gravity.CENTER);
        confirmBtn.setPadding(0, SizeUtils.dp2px(context, 10), 0, SizeUtils.dp2px(context, 10));
        confirmBtn.setText("现在安装");
        confirmBtn.setTextColor(Color.parseColor("#8BC0F6"));
        confirmBtn.setId(0x22);
        confirmBtn.setOnClickListener(onClickListener);

        buttonLayout.addView(cancelBtn);
        buttonLayout.addView(view);
        buttonLayout.addView(confirmBtn);


        contentLayout.addView(tvTitle);
        contentLayout.addView(tvContent);
        contentLayout.addView(lineView);
        contentLayout.addView(buttonLayout);

        rootLayout.addView(contentLayout);

        return rootLayout;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case 0x11:
                    try {
                        hide.invoke(mTN);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "稍后提示", Toast.LENGTH_SHORT).show();
                    break;
                case 0x22:
                    Toast.makeText(MainActivity.this, "现在安装", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}

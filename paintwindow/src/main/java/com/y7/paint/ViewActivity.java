package com.y7.paint;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.y7.paint.utils.SizeUtils;
import com.y7.paint.view.CloseView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewActivity extends Activity {

    private WindowManager.LayoutParams wmParams;
    private View windowView;

    private Toast toast;
    private Object mTN;
    private Method show;
    private Method hide;

    private RelativeLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        initToastView();
//        initWindowView();
    }

    public void initToastView() {
        toast = new Toast(this);
        toast.setView(createView());
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            WindowManager windowManager = getWindowManager();

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            wmParams = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            wmParams.height = windowManager.getDefaultDisplay().getHeight();
            wmParams.width = windowManager.getDefaultDisplay().getWidth();
            wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN; //获取全屏焦点 首先执行广告点击
            wmParams.windowAnimations = android.R.style.Animation_Activity;
            toast.setGravity(Gravity.CENTER, 0, 0);

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

            show.invoke(mTN);
            showAnimation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initWindowView() {
        WindowManager windowManager = getWindowManager();
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmParams.format = PixelFormat.TRANSLUCENT; //支持透明
        wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.height = windowManager.getDefaultDisplay().getHeight();
        wmParams.width = windowManager.getDefaultDisplay().getWidth();
        wmParams.gravity = Gravity.CENTER;
        wmParams.windowAnimations = android.R.style.Animation_Activity;
        windowView = createView();

        windowManager.addView(windowView, wmParams);
        showAnimation();
    }


    protected View createView() {
        WindowManager windowManager = getWindowManager();
        try {
            int height = 0;
            int width = 0;

            //普通插屏广告显示
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
                height = (int) (windowManager.getDefaultDisplay().getHeight() * 0.4);
                width = (int) (windowManager.getDefaultDisplay().getWidth() * 0.9);
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
                height = (int) (windowManager.getDefaultDisplay().getHeight() * 0.70);
                width = (int) (windowManager.getDefaultDisplay().getWidth() * 0.7);
            }

            //最外层父容器
            FrameLayout rootLayout = new FrameLayout(this);
            rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rootLayout.setBackgroundColor(Color.parseColor("#88000000"));

            //内容容器
            contentLayout = new RelativeLayout(this);
            RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            contentLayout.setLayoutParams(contentParams);
            contentLayout.setVisibility(View.GONE);

            //设置加载广告图片的ImageView
            ImageView imageView = new ImageView(this);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(width, height);
            imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon));
            //            imageView.setImageBitmap();
            imageView.setId(2);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ViewActivity.this, "click image view", Toast.LENGTH_SHORT).show();
                }
            });

            //点击关闭 图标
            CloseView closeView = new CloseView(this);
            RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(SizeUtils.dp2px(this, 50), SizeUtils.dp2px(this, 50));
            closeParams.addRule(RelativeLayout.BELOW, 2);
            closeParams.addRule(RelativeLayout.CENTER_IN_PARENT, 2);
            closeView.setLayoutParams(closeParams);
            closeView.setId(1);
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideAnimation();
                }
            });


            //将控件添加至 父容器中
            rootLayout.addView(contentLayout);
            contentLayout.addView(imageView);
            contentLayout.addView(closeView);
            return rootLayout;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void showAnimation() {
        //添加缩放动画 从外围 缩放到中心 隐藏
        ScaleAnimation mShowAction = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
        mShowAction.setDuration(300);
        mShowAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                contentLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        contentLayout.startAnimation(mShowAction);
    }

    private void hideAnimation() {
        //添加缩放动画 从外围 缩放到中心 隐藏
        ScaleAnimation mShowAction = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mShowAction.setDuration(300);
        mShowAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    contentLayout.setVisibility(View.GONE);
                    hide.invoke(mTN);
//                    getWindowManager().removeView(windowView);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        contentLayout.startAnimation(mShowAction);
    }
}

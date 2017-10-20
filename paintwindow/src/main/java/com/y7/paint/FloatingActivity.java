package com.y7.paint;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.y7.paint.utils.SizeUtils;
import com.y7.paint.widget.FloatingImageView;
import com.y7.paint.widget.MiExToast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FloatingActivity extends Activity {

    protected WindowManager.LayoutParams wmParams;
    protected View windowView;

    private Toast toast;
    private Object mTN;
    private Method show;
    private Method hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);

//        initF();

        MiExToast miToast = new MiExToast(getApplicationContext());
        miToast.setDuration(MiExToast.LENGTH_ALWAYS);
//        miToast.setAnimations(R.style.anim_view);

        miToast.show();
    }

    public void initF() {
        toast = new Toast(this);
        toast.setView(createView());

        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            WindowManager windowManager = getWindowManager();

            int height = (int) windowManager.getDefaultDisplay().getHeight();
            int width = (int) windowManager.getDefaultDisplay().getWidth();

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            wmParams = (WindowManager.LayoutParams) tnParamsField.get(mTN);


            //banner广告显示
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
                wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                wmParams.height = (int) WindowManager.LayoutParams.MATCH_PARENT;
                //                wmParams.height = (int) (height * 0.1);
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
//                wmParams.width = (int) (width * 0.5);
//                wmParams.height = (int) (height * 0.1);
                wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                wmParams.height = (int) WindowManager.LayoutParams.MATCH_PARENT;
            }
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //不抢占焦点 但是可以获取显示焦点点击
//            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setGravity(Gravity.CENTER, 0, 0);

            /**设置动画*/
//            wmParams.windowAnimations = animations;

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());


            show.invoke(mTN);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected View createView() {
        try {
            //创建父容器 RelativeLayout
            RelativeLayout background = new RelativeLayout(this);
            background.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));

            FloatingImageView imageView = new FloatingImageView(this);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(SizeUtils.dp2px(this, 50)
                    , SizeUtils.dp2px(this, 50)));
            imageView.setImageResource(R.mipmap.ic_launcher);

//            //设置加载广告图片的ImageView
//            ImageView imageView = new ImageView(this);
//            imageView.setLayoutParams(new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setImageResource(R.mipmap.ic_launcher);
//
//            imageView.setId(2);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(FloatingActivity.this, "1", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            //设置Close 关闭TextView
//            TextView textView = new TextView(this);
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            textView.setLayoutParams(layoutParams);
//            //设置TextView的 LeftDrawable X图标
//            textView.setText("×");
//            textView.setPadding(0, 0, 10, 0);
//            textView.setTextSize(20);
//            textView.setTextColor(Color.parseColor("#ffffff"));
//            textView.setId(1);
//            textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(FloatingActivity.this, "2", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//
//            //将控件添加至 父容器中
//            background.addView(imageView);
//            background.addView(textView);
            background.addView(imageView);
            return background;
        } catch (Exception e) {
        }
        return null;
    }
}

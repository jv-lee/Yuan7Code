package com.y7.paint.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.y7.paint.utils.ScreenUtil;


/**
 * Created by Administrator on 2017/8/30.
 */

@SuppressLint("AppCompatCustomView")
public class FloatingImageView extends ImageView{

    private int screenWidth;
    private int screenHeight;
    private int screenWidthHalf;
    private int statusHeight;
    private int virtualHeight;

    public FloatingImageView(Context context) {
        super(context);
        init();
    }

    public FloatingImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenWidth = ScreenUtil.getScreenWidth(getContext());
        screenWidthHalf = screenWidth / 2;
        screenHeight = ScreenUtil.getScreenHeight(getContext());
        statusHeight = ScreenUtil.getStatusHeight(getContext());
        virtualHeight=ScreenUtil.getVirtualBarHeigh(getContext());
    }

    private int lastX;
    private int lastY;
    private boolean isDrag;

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                isDrag = true;
                //计算手指移动了多少
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些手机无法触发点击事件的问题
                int distance= (int) Math.sqrt(dx*dx+dy*dy);
                if(distance<3){//给个容错范围，不然有部分手机还是无法点击
                    isDrag=false;
                    break;
                }
                float x = getX() + dx;
                float y = getY() + dy;
                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > screenWidth - getWidth() ? screenWidth - getWidth() : x;
                // y = y < statusHeight ? statusHeight : (y + getHeight() >= screenHeight ? screenHeight - getHeight() : y);
                if (y<0){
                    y=0;
                }
                if (y>screenHeight-statusHeight-getHeight()){
                    y=screenHeight-statusHeight-getHeight();
                }
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    //恢复按压效果
                    setPressed(false);
//                    if (rawX >= screenWidthHalf) {
//                        animate().setInterpolator(new DecelerateInterpolator())
//                                .setDuration(500)
//                                .xBy(screenWidth - getWidth() - getX())
//                                .start();
//                    } else {
//                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
//                        oa.setInterpolator(new DecelerateInterpolator());
//                        oa.setDuration(500);
//                        oa.start();
//                    }
                }
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);
    }


}

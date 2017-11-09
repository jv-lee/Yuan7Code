package com.jv.code.widget;

/**
 * Created by Administrator on 2017/10/19.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jv.code.utils.MIUIUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MiExToastButton implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private static final String TAG = "ExToast";

    private int statusBarHeight = 0;

    public static final int LENGTH_ALWAYS = 0;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_LONG = 4;

    private Toast toast;
    private Context mContext;
    private int mDuration = LENGTH_SHORT;
    private int animations = -1;
    private boolean isShow = false;

    private Object mTN;
    private Method show;
    private Method hide;
    private WindowManager mWM;
    private WindowManager.LayoutParams params;
    private View mView;

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private long firstTime;
    Runnable runnable;

    private Handler handler = new Handler();
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private GestureDetector mGestureDetector = new GestureDetector(this);

    public MiExToastButton(Context context, View view) {
        this.mContext = context;
        this.mView = view;
        statusBarHeight = getStatusHeight(context);
        if (toast == null) {
            toast = new Toast(mContext);
        }
        mView.setOnTouchListener(this);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Show the view for the specified duration.
     */
    @SuppressLint("NewApi")
    public void show() {
        if (isShow) {
            return;
        }
        toast.setView(mView);
        initTN();
        try {
            show.invoke(mTN);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        isShow = true;
        //判断duration，如果大于#LENGTH_ALWAYS 则设置消失时间
        if (mDuration > LENGTH_ALWAYS) {
            handler.postDelayed(hideRunnable, mDuration * 1000);
        }
    }

    /**
     * Close the view if it's showing, or don't show it if it isn't showing yet.
     * You do not normally have to call this.  Normally view will disappear on its own
     * after the appropriate duration.
     */
    @SuppressLint("NewApi")
    public void hide() {
        if (!isShow) {
            return;
        }
        try {
            hide.invoke(mTN);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        isShow = false;
    }

    public void setView(View view) {
        toast.setView(view);
    }

    public View getView() {
        return toast.getView();
    }

    /**
     * Set how long to show the view for.
     *
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     * @see #LENGTH_ALWAYS
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        toast.setMargin(horizontalMargin, verticalMargin);
    }

    public float getHorizontalMargin() {
        return toast.getHorizontalMargin();
    }

    public float getVerticalMargin() {
        return toast.getVerticalMargin();
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
    }

    public int getGravity() {
        return toast.getGravity();
    }

    public int getXOffset() {
        return toast.getXOffset();
    }

    public int getYOffset() {
        return toast.getYOffset();
    }

    public static MiExToastButton makeText(Context context, CharSequence text, int duration, View view) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        MiExToastButton exToast = new MiExToastButton(context, view);
        exToast.toast = toast;
        exToast.mDuration = duration;

        return exToast;
    }

    public static MiExToastButton makeText(Context context, int resId, int duration, View view)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration, view);
    }

    public void setText(int resId) {
        setText(mContext.getText(resId));
    }

    public void setText(CharSequence s) {
        toast.setText(s);
    }

    public int getAnimations() {
        return animations;
    }

    public void setAnimations(int animations) {
        this.animations = animations;
    }

    private void initTN() {
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            /**设置动画*/
            if (animations != -1) {
                params.windowAnimations = animations;
            }

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

            mWM = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGravity(Gravity.LEFT | Gravity.TOP, 900, 1500);

    }


    private void updateViewPosition() {
        //更新浮动窗口位置参数
        params.x = (int) (x - mTouchStartX);
        params.y = (int) (y - mTouchStartY) - statusBarHeight;
        Log.i("startP", "params.y:" + params.y + "   view.height:" + mView.getMeasuredHeight());
        mWM.updateViewLayout(toast.getView(), params);  //刷新显示
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        //获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY();
        Log.i(TAG, "currX" + x + "====currY" + y);
        switch (event.getAction()) {
            //捕获手指触摸按下动作
            case MotionEvent.ACTION_DOWN:
                //获取相对View的坐标，即以此View左上角为原点
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                Log.i(TAG, "DOWN startX" + mTouchStartX + "====startY" + mTouchStartY);
                break;
            //捕获手指触摸移动动作
            case MotionEvent.ACTION_MOVE:
                if (MIUIUtil.isMIUI()) {
                    if (MIUIUtil.miuiCode() < 6) {
                        updateViewPosition();
                    }
                } else {
                    updateViewPosition();
                }
                break;
            //捕获手指触摸离开动作
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }
//    @Override
//    public boolean onTouch(final View v, final MotionEvent event) {
//        //获取相对屏幕的坐标，即以屏幕左上角为原点
//        x = event.getRawX();
//        y = event.getRawY();
//        Log.i("onTouch-", "  currX" + x + "====currY" + y);
//        switch (event.getAction()) {
//            //捕获手指触摸按下动作
//            case MotionEvent.ACTION_DOWN:
//                //获取相对View的坐标，即以此View左上角为原点
//                mTouchStartX = event.getX();
//                mTouchStartY = event.getY();
//                firstTime = System.currentTimeMillis();
//                handler.postDelayed(runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        float x = mTouchStartX - event.getX();
//                        float y = mTouchStartY - event.getY();
//                        if (x == 0 && y == 0) {
//                            onLongClickListener.onLongClick(v);
//                        }
//                    }
//                }, 1000);
//                Log.i("onTouch-", "ACTION_DOWN startX" + mTouchStartX + "====startY" + mTouchStartY);
//                break;
//            //捕获手指触摸移动动作
//            case MotionEvent.ACTION_MOVE:
//                Log.i("onTouch-", "ACTION_MOVE startX" + event.getX() + "====startY" + event.getY());
//                if (MIUIUtil.isMIUI()) {
//                    if (MIUIUtil.miuiCode() < 6) {
//                        updateViewPosition();
//                    }
//                } else {
//                    updateViewPosition();
//                }
//                break;
//            //捕获手指触摸离开动作
//            case MotionEvent.ACTION_UP:
//                float x = mTouchStartX - event.getX();
//                float y = mTouchStartY - event.getY();
//                Log.i("onTouch-", "ACTION_UP startX" + x + "====startY" + y);
//                long time = System.currentTimeMillis() - firstTime;
//                if (x == 0 && y == 0) {
//                    if (time < 1000) {
//                        if (runnable != null) {
//                            handler.removeCallbacks(runnable);
//                        }
//                        onClickListener.onClick(v);
//                    }
//                } else {
//                    if (time < 300) {
//                        onClickListener.onClick(v);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onClickListener.onClick(getView());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        onLongClickListener.onLongClick(getView());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}

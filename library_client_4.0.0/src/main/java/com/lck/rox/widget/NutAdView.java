package com.lck.rox.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;



/**
 * Created by Administrator on 2017/10/27.
 */

public class NutAdView extends View {
    private Paint mPaint = new Paint();
    private int mWidth, mHeight;

    public NutAdView(Context context) {
        super(context);
        initPaint();
    }

    public NutAdView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#5E5E5E"));
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将坐标系平移到 屏幕中心点
        canvas.translate(mWidth / 2, mHeight / 2);

        int linesPx1 = dp2px(-5f);
        int linesPx2 = dp2px(5f);
        int linesPx3 = dp2px(-10f);
        int linesPx4 = dp2px(10f);

        canvas.drawLines(new float[]{
                linesPx1, linesPx3, linesPx2, linesPx3,
                linesPx1, linesPx3, linesPx3, linesPx1,
                linesPx3, linesPx1, linesPx3, linesPx2,
                linesPx3, linesPx2, linesPx1, linesPx4,
                linesPx1, linesPx4, linesPx2, linesPx4,
                linesPx2, linesPx4, linesPx4, linesPx2,
                linesPx4, linesPx2, linesPx4, linesPx1,
                linesPx4, linesPx1, linesPx2, linesPx3
        }, mPaint);

        mPaint.setStrokeWidth(1);
        //绘制交叉点 圆点
        canvas.drawCircle(linesPx1, linesPx3, 1, mPaint);
        canvas.drawCircle(linesPx2, linesPx4, 1, mPaint);
        canvas.drawCircle(linesPx3, linesPx1, 1, mPaint);
        canvas.drawCircle(linesPx3, linesPx2, 1, mPaint);
        canvas.drawCircle(linesPx1, linesPx4, 1, mPaint);
        canvas.drawCircle(linesPx2, linesPx4, 1, mPaint);
        canvas.drawCircle(linesPx4, linesPx2, 1, mPaint);
        canvas.drawCircle(linesPx4, linesPx1, 1, mPaint);
        canvas.drawCircle(linesPx2, linesPx3, 1, mPaint);

        mPaint.setStrokeWidth(2);
        mPaint.setTextSize(25);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawText("AD", -30 / 2, 15 / 2, mPaint);
    }

    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

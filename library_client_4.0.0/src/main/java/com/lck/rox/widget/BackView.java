package com.lck.rox.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;



/**
 * Created by Administrator on 2017/10/23.
 *
 * @author jv.lee
 */

public class BackView extends View {

    private Paint mPaint = new Paint();
    private int mWidth, mHeight;

    public BackView(Context context) {
        super(context);
        initPaint();
    }

    public BackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setStrokeWidth(dp2px(3));
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

        int linePx1 = dp2px(-10);
        int linePx2 = dp2px(10);

        canvas.drawLines(new float[]{
                0, linePx1, linePx1, 0,
                linePx1, 0, 0, linePx2,
                linePx1, 0, linePx2, 0
        }, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(linePx1, 0, dp2px(1), mPaint);
    }

    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}

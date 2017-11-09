package com.y7.paint.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.y7.paint.utils.SizeUtils;

/**
 * Created by Administrator on 2017/6/23.
 */

public class CloseView extends View {

    private Paint mPaint = new Paint();
    private int mHeight, mWidth;

    public CloseView(Context context) {
        super(context);
    }

    public CloseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化画笔属性
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(3f);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#80FFFFFF"));
        mPaint.setStrokeWidth(4f);

        //设置画布抗齿距 将坐标定位到中心
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.translate(mWidth / 2, mHeight / 2);

        //绘制圆形
        RectF rectF3 = new RectF(SizeUtils.dp2px(getContext(),-10), SizeUtils.dp2px(getContext(),-10),SizeUtils.dp2px(getContext(),10), SizeUtils.dp2px(getContext(),10));
        canvas.drawOval(rectF3, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#80000000"));
        mPaint.setStrokeWidth(2f);

        //从坐标 x -12,y -12 ->  x 12,y 12 之间绘制一条线
        canvas.drawLines(new float[]{
                SizeUtils.dp2px(getContext(),-4), SizeUtils.dp2px(getContext(),-4), SizeUtils.dp2px(getContext(),4), SizeUtils.dp2px(getContext(),4),
                SizeUtils.dp2px(getContext(),4), SizeUtils.dp2px(getContext(),-4), SizeUtils.dp2px(getContext(),-4), SizeUtils.dp2px(getContext(),4)
        }, mPaint);

    }
}

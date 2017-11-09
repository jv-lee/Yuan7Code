package com.lck.rox.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Administrator on 2017/10/26.
 */

public class IconView extends View {

    private Paint mPaint = new Paint();
    private int mWidth, mHeight;

    public IconView(Context context) {
        super(context);
        initPaint();
    }

    public IconView(Context context, @Nullable AttributeSet attrs) {
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

        int pathPx1 = dp2px(-8f);//-100
        int pathPx2 = dp2px(-4f);//-60
        int pathPx3 = dp2px(8f);//100
        int pathPx4 = dp2px(-8f);//-95
        int pathPx5 = dp2px(8f);//90
        int pathPx6 = dp2px(16f);//180
        int pathPx7 = dp2px(8f);//95
        int pathPx8 = dp2px(4f);//45
        int pathPxX0 = dp2px(0f);//0
        int pathPxY0 = dp2px(0f);//0

        //绘制view 边
        Path path1 = new Path();
        path1.moveTo(pathPx1, pathPxY0);//设置Path的起点
        path1.quadTo(0, pathPx2, pathPx3, pathPxY0); //设置贝塞尔曲线的控制点坐标和终点坐标
        path1.moveTo(pathPx1, pathPxY0);
        path1.quadTo(pathPx4, pathPx5, 0, pathPx6);
        path1.moveTo(pathPx3, pathPxY0);
        path1.quadTo(pathPx7, pathPx5, 0, pathPx6);
        canvas.drawPath(path1, mPaint);//画出贝塞尔曲线

        //绘制内部V
        canvas.drawLine(pathPx1, pathPxY0, 0, pathPx8, mPaint);
        canvas.drawLine(pathPx3, pathPxY0, 0, pathPx8, mPaint);

        //绘制交叉点 圆点
        canvas.drawCircle(pathPx1, pathPxY0, 1, mPaint);
        canvas.drawCircle(pathPx3, pathPxY0, 1, mPaint);
        canvas.drawCircle(0, pathPx8, 1, mPaint);
        canvas.drawCircle(0, pathPx6, 1, mPaint);

    }

    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}

package com.lck.rox.widget;

/**
 * Created by Administrator on 2017/11/1.
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GifView extends ImageView {

    private boolean isGifImage;
    private int image;
    private Movie movie;
    private long movieStart = 0;

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取自定义属性isgifimage
        isGifImage = true;
        //获取ImageView的默认src属性
        image = attrs.getAttributeResourceValue( "http://schemas.android.com/apk/res/android", "src", 0);

        movie = Movie.decodeStream(getResources().openRawResource(image));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);//执行父类onDraw方法，绘制非gif的资源
        if(isGifImage){//若为gif文件，执行DrawGifImage()，默认执行
            DrawGifImage(canvas);
        }
    }

    private void DrawGifImage(Canvas canvas) {
        //获取系统当前时间
        long nowTime = android.os.SystemClock.currentThreadTimeMillis();
        if(movieStart == 0){
            //若为第一次加载，开始时间置为nowTime
            movieStart = nowTime;
        }
        if(movie != null){//容错处理
            int duration = movie.duration();//获取gif持续时间
            //如果gif持续时间为小于100，可认为非gif资源，跳出处理
            if(duration > 100){
                //获取gif当前帧的显示所在时间点
                int relTime = (int) ((nowTime - movieStart) % duration);
                movie.setTime(relTime);
                //渲染gif图像
                movie.draw(canvas, 0, 0);
                invalidate();
            }
        }
    }
}

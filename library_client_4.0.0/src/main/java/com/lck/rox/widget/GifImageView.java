package com.lck.rox.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import java.io.File;
import java.math.BigDecimal;

/**
 * Created by jv.lee on 2017/11/6.
 */


@SuppressLint("AppCompatCustomView")
public class GifImageView extends ImageView {

    private static final int DEFAULT_DURATION = 1000;

    private Movie movie;
    //播放开始时间点
    private long mMovieStart;
    //播放暂停时间点
    private long mMoviePauseTime;
    //播放暂停时间
    private long dealyTime;
    //播放完成进度
    @FloatRange(from = 0, to = 1.0f)
    float percent;
    //播放次数，-1为循环播放
    private int counts = -1;

    private int width;
    private int height;

    private volatile boolean reverse = false;
    private volatile boolean mPaused;
    private volatile boolean hasStart;

    private boolean mVisible = true;

    private OnPlayListener mOnPlayListener;
    private int movieDuration;
    float scaleIndex;

    public GifImageView(Context context) {
        this(context, null);
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setViewAttributes(context, attrs, defStyle);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);

        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏

            int phone = metrics.widthPixels;
            if (phone >= 1440) {
                scaleIndex = (phone / 320) - 0.4f;
            } else {
                scaleIndex = (phone / 320);
            }

        } else if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏

            int phone = metrics.heightPixels;
            if (phone >= 2560) {
                scaleIndex = (phone / 320) - 1.0f;
            } else {
                scaleIndex = (phone / 320);
            }

        }


    }

    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {
        int srcID = 0;
        boolean authPlay = true;
        counts = -1;
        if (srcID > 0) {
            setGifResource(srcID, null);
            if (authPlay) {
                play(counts);
            }
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setGifResource(int movieResourceId, OnPlayListener onPlayListener) {
        mOnPlayListener = onPlayListener;
        movie = Movie.decodeStream(getResources().openRawResource(movieResourceId));
        width = movie.width();
        height = movie.height();
        Log.i("lee", "movieWidth:" + movie.width());
        Log.i("lee", "movieHeight:" + movie.height());

        if (movie == null) {
            //如果movie为空，那么就不是gif文件，尝试转换为bitmap显示
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), movieResourceId);
            if (bitmap != null) {
                setImageBitmap(bitmap);
                return;
            }
        }
        movieDuration = movie.duration() == 0 ? DEFAULT_DURATION : movie.duration();
        requestLayout();
    }

    public void setGifFile(File file, OnPlayListener onPlayListener) {
        movie = Movie.decodeFile(file.getAbsolutePath());
        mOnPlayListener = onPlayListener;
        width = movie.width();
        height = movie.height();

        if (movie == null) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                setImageBitmap(bitmap);
                return;
            }
        }
        movieDuration = movie.duration() == 0 ? DEFAULT_DURATION : movie.duration();
        requestLayout();
        if (mOnPlayListener != null) {
            mOnPlayListener.onPlayStart();
        }
    }


    public void setGifResource(final String path, OnPlayListener onPlayListener) {
        movie = Movie.decodeFile(path);
        mOnPlayListener = onPlayListener;
        width = movie.width();
        height = movie.height();

        if (movie == null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                setImageBitmap(bitmap);
                return;
            }
        }
        movieDuration = movie.duration() == 0 ? DEFAULT_DURATION : movie.duration();
        requestLayout();
        if (mOnPlayListener != null) {
            mOnPlayListener.onPlayStart();
        }
    }

    //从新开始播放
    public void playOver() {
        if (movie != null) {
            play(-1);
        }
    }

    //倒叙播放
    public void playReverse() {
        if (movie != null) {
            reset();
            reverse = true;
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayStart();
            }
            invalidate();
        }
    }

    public void play(int counts) {
        this.counts = counts;
        reset();
        if (mOnPlayListener != null) {
            mOnPlayListener.onPlayStart();
        }
        invalidate();
    }

    private void reset() {
        reverse = false;
        mMovieStart = SystemClock.uptimeMillis();
        mPaused = false;
        hasStart = true;
        mMoviePauseTime = 0;
        dealyTime = 0;
    }

    public void play() {
        if (movie == null) {
            return;
        }
        if (hasStart) {
            if (mPaused && mMoviePauseTime > 0) {
                mPaused = false;
                dealyTime = dealyTime + SystemClock.uptimeMillis() - mMoviePauseTime;
                invalidate();
                if (mOnPlayListener != null) {
                    mOnPlayListener.onPlayRestart();
                }
            }
        } else {
            play(-1);
        }
    }

    public void pause() {
        if (movie != null && !mPaused && hasStart) {
            mPaused = true;
            invalidate();
            mMoviePauseTime = SystemClock.uptimeMillis();
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayPause(true);
            }
        } else {
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayPause(false);
            }
        }
    }

    private int getCurrentFrameTime() {
        if (movieDuration == 0) {
            return 0;
        }
        long now = SystemClock.uptimeMillis() - dealyTime;
        int nowCount = (int) ((now - mMovieStart) / movieDuration);
        if (counts != -1 && nowCount >= counts) {
            hasStart = false;
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayEnd();
            }
        }
        float currentTime = (now - mMovieStart) % movieDuration;
        percent = currentTime / movieDuration;
        if (mOnPlayListener != null && hasStart) {
            BigDecimal b = new BigDecimal(percent);
            double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            mOnPlayListener.onPlaying((float) f1);
        }
        return (int) currentTime;
    }

    public void setPercent(float percent) {
        if (movie != null && movieDuration > 0) {
            this.percent = percent;
            movie.setTime((int) (movieDuration * percent));
            invalidateView();
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlaying(percent);
            }
        }

    }

    public boolean isPaused() {
        return this.mPaused;
    }

    public boolean isPlaying() {
        return !this.mPaused && hasStart;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (movie != null) {

            Log.i("lee", "scaleIndex:" + scaleIndex);
            canvas.scale(scaleIndex, scaleIndex);

            if (!mPaused && hasStart) {
                if (reverse) {
                    movie.setTime(movieDuration - getCurrentFrameTime());
                } else {
                    movie.setTime(getCurrentFrameTime());
                }
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 画出gif帧
     */
    private void drawMovieFrame(Canvas canvas) {
        movie.draw(canvas, 0.0f, 0.0f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (movie != null) {
//            setMeasuredDimension(SizeUtil.dp2px(getContext(),320), SizeUtil.dp2px(getContext(),320));
//            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void invalidateView() {
        if (mVisible) {
            postInvalidateOnAnimation();
        }
    }

    public int getDuration() {
        if (movie != null) {
            return movie.duration();
        } else {
            return 0;
        }

    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }


    public interface OnPlayListener {
        void onPlayStart();

        void onPlaying(@FloatRange(from = 0f, to = 1.0f) float percent);

        void onPlayPause(boolean pauseSuccess);

        void onPlayRestart();

        void onPlayEnd();
    }

}

package com.lck.rox.widget.gif;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.widget.ImageView;

import java.io.InputStream;

public class GifDrawer {
    private static final String TAG = "GifDrawer";
    private InputStream is;
    private ImageView imageView;
    private Movie movie;
    private Bitmap bitmap;
    private Canvas canvas;
    private Handler handler = new Handler();
    private final long delayMills = 16;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!endFlag) {
                drawSimple();
                handler.postDelayed(runnable, delayMills);
            }
        }
    };
    private boolean endFlag = false;
    private double index = 0.04;

    private void drawSimple() {
        if (index > 1.0) {
            if (onGifListener != null) {
                endFlag = true;
                onGifListener.onEnd();
            }
            return;
        }
        canvas.save();
        int position = (int) (movie.duration() * index);
        index = index + 0.04;
        movie.setTime(position);//这个是获取movie的某一帧，我们就不断地循环它
        movie.draw(canvas, 0, 0);
        imageView.setImageBitmap(bitmap);
        canvas.restore();
    }

    private void draw() {
        canvas.save();
        int position = (int) (System.currentTimeMillis() % movie.duration());
        movie.setTime(position);//这个是获取movie的某一帧，我们就不断地循环它
        movie.draw(canvas, 0, 0);
        imageView.setImageBitmap(bitmap);
        canvas.restore();
    }

    /**
     * 传递imagerview,将gif放到gif中去
     *
     * @param imageView
     */
    public GifDrawer into(ImageView imageView) {
        this.imageView = imageView;
        if (is == null) {
            return null;
        } else if (imageView == null) {

            throw new RuntimeException("imagetView can not be null");
        } else {

//            开始在imageview里面绘制电影
            movie = Movie.decodeStream(is);//gif小电影
            if (movie == null) {
                throw new IllegalArgumentException("Illegal gif file");

            }
            if (movie.width() <= 0 || movie.height() <= 0) {
                return null;
            }
//            需要bitmap
            bitmap = Bitmap.createBitmap(movie.width(), movie.height(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
//            准备把canvas的小电影显示在imageview里面
            handler.post(runnable);
        }
        return this;
    }

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    private OnGifListener onGifListener;

    public void setGifListener(OnGifListener onGifListener) {
        this.onGifListener = onGifListener;
    }

    public interface OnGifListener {
        void onEnd();
    }

}

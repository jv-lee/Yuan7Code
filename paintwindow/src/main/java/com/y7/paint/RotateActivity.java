package com.y7.paint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class RotateActivity extends AppCompatActivity {

    private ImageView ivRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);

        ivRotate = (ImageView) findViewById(R.id.iv_rotate);

        ivRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = new RotateAnimation(0f, 2560f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillAfter(true); // 设置保持动画最后的状态
                anim.setDuration(4000); // 设置动画时间
//                anim.setInterpolator(new AnticipateOvershootInterpolator()); // 设置插入器 来回摆动
//                anim.setInterpolator(new BounceInterpolator());
                anim.setInterpolator(new DecelerateInterpolator());
                ivRotate.startAnimation(anim);
            }
        });

    }
}

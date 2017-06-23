package com.y7.paint;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.y7.paint.utils.ResourceUtils;
import com.y7.paint.utils.SizeUtils;

public class PayActivity extends Activity {

    private Context context;
    private int lineSize1 = 36;
    private int lineSize2 = 46;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
//        setContentView(R.layout.activity_pay);
        initWindow();
    }

    private void initWindow() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#88000000"));
        setFinishOnTouchOutside(false);
        // 获取WindowManager
        WindowManager wm = getWindowManager();
        // 设置大小
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        int width = point.x;
        ViewGroup.LayoutParams p = getWindow().getAttributes();
        int layoutWidth = 0;

        //竖屏
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutWidth = width;
            //横屏
        } else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutWidth = (int) (width * 0.6);
        }

        //窗口
        RelativeLayout windowLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams windowParams = new RelativeLayout.LayoutParams(layoutWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        windowLayout.setLayoutParams(windowParams);
        windowLayout.setPadding(SizeUtils.dp2px(context, 16), SizeUtils.dp2px(context, 16), SizeUtils.dp2px(context, 16), SizeUtils.dp2px(context, 16));
        windowLayout.setBackgroundColor(android.R.color.black);

        //弹窗根布局
        LinearLayout rootLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(layoutWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rootLayout.setLayoutParams(rootParams);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackground(ResourceUtils.getDrawable(context, "root_bg"));

        //弹窗第一行布局
        RelativeLayout lineLayout1 = new RelativeLayout(context);
        lineLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, lineSize1)));
        lineLayout1.setBackground(ResourceUtils.getDrawable(context, "root_bg2"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lineLayout1.setElevation(20);
        }

        TextView tvTitle = new TextView(context);
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvTitle.setLayoutParams(titleParams);
        tvTitle.setTextColor(Color.parseColor("#ffffff"));
        tvTitle.setText("支付中心");

        ImageView ivClose = new ImageView(context);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(SizeUtils.dp2px(context, 25), SizeUtils.dp2px(context, 25));
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        closeParams.setMargins(0, 0, SizeUtils.dp2px(context, 10), 0);
        ivClose.setLayoutParams(closeParams);
        ivClose.setImageDrawable(ResourceUtils.getDrawable(context, "ic_close"));
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "close", Toast.LENGTH_SHORT).show();
            }
        });

        lineLayout1.addView(tvTitle, 0);
        lineLayout1.addView(ivClose, 1);

        //弹窗第二行布局
        LinearLayout lineLayout2 = new LinearLayout(context);
        LinearLayout.LayoutParams lineParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, lineSize2));
        lineParams2.gravity = Gravity.CENTER_VERTICAL;
        lineLayout2.setLayoutParams(lineParams2);
        lineLayout2.setOrientation(LinearLayout.VERTICAL);
        lineLayout2.setPadding(SizeUtils.dp2px(context, 16), 0, 0, 0);

        TextView tvProductName = new TextView(context);
        tvProductName.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvProductName.setText("商品名称：" + "simple product");
        tvProductName.setTextColor(Color.parseColor("#000000"));

        TextView tvProductPrice = new TextView(context);
        tvProductPrice.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvProductPrice.setText("商品价格：" + "1元");
        tvProductPrice.setTextColor(Color.parseColor("#000000"));

        View line = new View(context);
        RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        line.setLayoutParams(lineParams);
        line.setBackgroundColor(Color.parseColor("#000000"));

        lineLayout2.addView(tvProductName);
        lineLayout2.addView(tvProductPrice);
        lineLayout2.addView(line);

        //弹窗第三行布局 微信支付
        ImageView lineLayout3 = new ImageView(context);
        lineLayout3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, lineSize2)));
        lineLayout3.setImageDrawable(ResourceUtils.getDrawable(context, "wx"));
        lineLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "weChat pay", Toast.LENGTH_SHORT).show();
            }
        });

        //弹窗第四行布局 支付宝支付
        ImageView lineLayout4 = new ImageView(context);
        lineLayout4.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, lineSize2)));
        lineLayout4.setImageDrawable(ResourceUtils.getDrawable(context, "zfb"));
        lineLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "ali pay", Toast.LENGTH_SHORT).show();
            }
        });

        //弹窗第五行布局
        RelativeLayout lineLayout5 = new RelativeLayout(context);
        RelativeLayout.LayoutParams lineParams5 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, lineSize1));
        lineLayout5.setLayoutParams(lineParams5);
        lineLayout5.setBackgroundColor(Color.parseColor("#3CC45E"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lineLayout5.setElevation(20);
        }

        TextView tvMessage5 = new TextView(context);
        RelativeLayout.LayoutParams tvMessageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvMessageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvMessage5.setLayoutParams(tvMessageParams);
        tvMessage5.setText("请选择支付方式");
        tvMessage5.setTextColor(Color.parseColor("#ffffff"));

        lineLayout5.addView(tvMessage5);

        //弹窗第六行布局
        LinearLayout lineLayout6 = new LinearLayout(context);
        LinearLayout.LayoutParams lineParams6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, lineSize1));

        lineLayout6.setLayoutParams(lineParams6);
        lineLayout6.setOrientation(LinearLayout.HORIZONTAL);
        lineLayout6.setGravity(Gravity.CENTER);

        TextView tvTitle6 = new TextView(context);
        tvTitle6.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvTitle6.setText("客服");
        tvTitle6.setTextColor(Color.parseColor("#000000"));

        TextView tvTitleNumber6 = new TextView(context);
        tvTitleNumber6.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvTitleNumber6.setText("客服code");
        Linkify.addLinks(tvTitleNumber6, Linkify.EMAIL_ADDRESSES);

        lineLayout6.addView(tvTitle6);
        lineLayout6.addView(tvTitleNumber6);

        rootLayout.addView(lineLayout1);
        rootLayout.addView(lineLayout2);

//        if (mNetworkPayType != null) {
//            if (mNetworkPayType.isAliPay() && !mNetworkPayType.isWeChatPay()) {
//                rootLayout.addView(lineLayout4);
//            } else if (mNetworkPayType.isWeChatPay() && !mNetworkPayType.isAliPay()) {
//                rootLayout.addView(lineLayout3);
//            } else {
//                rootLayout.addView(lineLayout3);
//                rootLayout.addView(lineLayout4);
//            }
//        } else {
        rootLayout.addView(lineLayout3);
        rootLayout.addView(lineLayout4);
//        }

        rootLayout.addView(lineLayout5);
        rootLayout.addView(lineLayout6);
        windowLayout.addView(rootLayout);

        setContentView(windowLayout);

    }

}

package com.lck.rox.xi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lck.rox.api.API;
import com.lck.rox.api.Constant;
import com.lck.rox.http.base.RequestCallback;
import com.lck.rox.manager.HttpManager;
import com.lck.rox.utils.LogUtil;
import com.lck.rox.utils.SizeUtil;
import com.lck.rox.widget.BackView;
import com.lck.rox.widget.IconView;
import com.lck.rox.widget.NutAdView;

import java.util.ArrayList;
import java.util.List;

public class a extends Activity {

    private Context mContext;

    private String name, icon, des, downloadurl, sendRecord, apkname;
    private ArrayList<String> images;
    private boolean hasSplash = false;

    private List<ImageView> ivImages = new ArrayList<>();
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        hasSplash = getIntent().getBooleanExtra(Constant.SPLASH, false);
        name = getIntent().getStringExtra(Constant.APPDES_NAME);
        icon = getIntent().getStringExtra(Constant.APPDES_ICON);
        des = getIntent().getStringExtra(Constant.APPDES_BRIEF);
        downloadurl = getIntent().getStringExtra(Constant.APPDES_DOWNLOADURL);
        apkname = getIntent().getStringExtra(Constant.APPDES_APKNAME);
        sendRecord = getIntent().getStringExtra(Constant.APPDES_SENDRECORD);
        images = getIntent().getStringArrayListExtra(Constant.APPDES_BROADCASTIMAGE);

        setContentView(createView(mContext));

        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_INTO_APPDES, sendRecord, new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_INTO_APPDES + "\ttip:" + "appDes show status" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                LogUtil.e("错误代码:" + message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_INTO_APPDES + "\ttip:" + "appDes show status" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
            }
        });

        HttpManager.doGetPic(icon, new RequestCallback<Bitmap>() {
            @Override
            public void onFailed(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(final Bitmap response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivIcon.setImageBitmap(response);
                    }
                });
            }
        });


        if (images != null && images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                final int finalI = i;
                HttpManager.doGetPic(images.get(i), new RequestCallback<Bitmap>() {

                    @Override
                    public void onFailed(String message) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(final Bitmap response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivImages.get(finalI).setImageBitmap(response);
                            }
                        });
                    }
                });
            }
        }

    }

    TextView tvContent;
    boolean flag = false;

    public View createView(Context context) {

        //创建根布局
        FrameLayout rootLayout = new FrameLayout(context);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        //创建 back bar 布局容器
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtil.dp2px(context, 46)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            frameLayout.setElevation(10f);
        }

        //创建 back按钮
        BackView backView = new BackView(context);
        backView.setLayoutParams(new FrameLayout.LayoutParams(SizeUtil.dp2px(context, 46), SizeUtil.dp2px(context, 46)));
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //创建最外围滑动
        ScrollView scrollView = new ScrollView(context);
        FrameLayout.LayoutParams scrollParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollParams.setMargins(0, SizeUtil.dp2px(context, 46), 0, SizeUtil.dp2px(context, 56));
        scrollView.setLayoutParams(scrollParams);

        //创建内容布局 linearLayout
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        contentLayout.setPadding(SizeUtil.dp2px(context, 8), SizeUtil.dp2px(context, 8), SizeUtil.dp2px(context, 8), SizeUtil.dp2px(context, 8));
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        //创建 应用 icon 名称布局容器
        LinearLayout iconNameLayout = new LinearLayout(context);
        iconNameLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        iconNameLayout.setGravity(Gravity.CENTER_VERTICAL);
        iconNameLayout.setOrientation(LinearLayout.HORIZONTAL);

        ivIcon = new ImageView(context);
        ivIcon.setLayoutParams(new FrameLayout.LayoutParams(SizeUtil.dp2px(context, 66), SizeUtil.dp2px(context, 66)));
        ivIcon.setScaleType(ImageView.ScaleType.FIT_XY);


        TextView tvAppName = new TextView(context);
        LinearLayout.LayoutParams tvAppNameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvAppNameParams.setMargins(SizeUtil.dp2px(context, 6), 0, 0, 0);
        tvAppName.setLayoutParams(tvAppNameParams);
        tvAppName.setGravity(Gravity.CENTER_VERTICAL);
        tvAppName.setTextColor(Color.parseColor("#000000"));
        tvAppName.setTextSize(18);
        tvAppName.setText(name);

        //创建横向滑动布局 填充app宣传图
        HorizontalScrollView appPicScrollView = new HorizontalScrollView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, SizeUtil.dp2px(context, 6), 0, 0);
        appPicScrollView.setLayoutParams(layoutParams);
        appPicScrollView.setPadding(0, SizeUtil.dp2px(context, 6), 0, SizeUtil.dp2px(context, 6));
        appPicScrollView.setVerticalScrollBarEnabled(false);
        appPicScrollView.setHorizontalScrollBarEnabled(false);

        LinearLayout appPicLinearLayout = new LinearLayout(context);
        appPicLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        appPicLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        if (images != null && images.size() != 0) {
            for (int i = 0; i < images.size(); i++) {
                ImageView appPic = new ImageView(context);
                LinearLayout.LayoutParams picParams = new LinearLayout.LayoutParams(SizeUtil.dp2px(context, 176), SizeUtil.dp2px(context, 268));
                if (i != 0) {
                    picParams.setMargins(SizeUtil.dp2px(context, 6), 0, 0, 0);
                }
                appPic.setLayoutParams(picParams);
                appPic.setScaleType(ImageView.ScaleType.FIT_XY);
                ivImages.add(appPic);
            }
        }

        //app详情内容页面
        tvContent = new TextView(context);
        LinearLayout.LayoutParams tvContentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvContentParams.setMargins(0, SizeUtil.dp2px(context, 12), 0, SizeUtil.dp2px(context, 12));
        tvContent.setLayoutParams(tvContentParams);
        tvContent.setMaxLines(2);
        tvContent.setEllipsize(TextUtils.TruncateAt.END);
        tvContent.setTextColor(Color.parseColor("#000000"));
        tvContent.setText(des);

        //游戏认证图标 布局容器
        FrameLayout desLayout = new FrameLayout(context);
        LinearLayout.LayoutParams desParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        desParams.setMargins(0, SizeUtil.dp2px(context, 12), 0, 0);
        desLayout.setLayoutParams(desParams);

        //认证图标 布局
        LinearLayout desLinear = new LinearLayout(context);
        desLinear.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        desLinear.setGravity(Gravity.CENTER_VERTICAL);
        desLinear.setOrientation(LinearLayout.HORIZONTAL);

        IconView ivDes1 = new IconView(context);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(SizeUtil.dp2px(context, 30), SizeUtil.dp2px(context, 36));
        ivParams.setMargins(0, 0, 0, SizeUtil.dp2px(context, 5));
        ivDes1.setLayoutParams(ivParams);

        TextView tvDes1 = new TextView(context);
        tvDes1.setText("无病毒");
        tvDes1.setTextColor(Color.parseColor("#aaaaaa"));

        NutAdView ivDes2 = new NutAdView(context);
        LinearLayout.LayoutParams ivParams2 = new LinearLayout.LayoutParams(SizeUtil.dp2px(context, 30), SizeUtil.dp2px(context, 36));
        ivDes2.setLayoutParams(ivParams2);

        TextView tvDes2 = new TextView(context);
        tvDes2.setText("无广告");
        tvDes2.setTextColor(Color.parseColor("#aaaaaa"));

        IconView ivDes3 = new IconView(context);
        LinearLayout.LayoutParams ivParams3 = new LinearLayout.LayoutParams(SizeUtil.dp2px(context, 30), SizeUtil.dp2px(context, 36));
        ivParams3.setMargins(0, 0, 0, SizeUtil.dp2px(context, 5));
        ivDes3.setLayoutParams(ivParams3);

        TextView tvDes3 = new TextView(context);
        tvDes3.setText("用户保障");
        tvDes3.setTextColor(Color.parseColor("#aaaaaa"));

        final ImageView desOpen = new ImageView(context);
        FrameLayout.LayoutParams desOpenParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        desOpenParams.gravity = Gravity.RIGHT;
        desOpen.setLayoutParams(desOpenParams);
        desOpen.setImageResource(android.R.drawable.arrow_down_float);

        desOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    flag = false;
                    tvContent.setMaxLines(2);
                    desOpen.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    flag = true;
                    tvContent.setMaxLines(10);
                    desOpen.setImageResource(android.R.drawable.arrow_up_float);
                }
            }
        });

        LinearLayout bottomLinear = new LinearLayout(context);
        FrameLayout.LayoutParams bottomParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtil.dp2px(context, 56));
        bottomParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        bottomLinear.setLayoutParams(bottomParams);
        bottomLinear.setOrientation(LinearLayout.VERTICAL);


        View view = new View(context);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtil.dp2px(context, 1));
        viewParams.setMargins(0, 0, 0, SizeUtil.dp2px(context, 6));
        view.setLayoutParams(viewParams);
        view.setBackgroundColor(Color.parseColor("#aaaaaa"));

        Button btnDownload = new Button(context);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(SizeUtil.dp2px(context, 6), 0, SizeUtil.dp2px(context, 6), 0);
        btnDownload.setLayoutParams(btnParams);
        btnDownload.setText("下载");
        btnDownload.setTextColor(Color.parseColor("#ffffff"));
        btnDownload.setTextSize(18);

        StateListDrawable drawable = new StateListDrawable();

        GradientDrawable gd1 = new GradientDrawable();
        gd1.setColor(Color.parseColor("#235EC1"));
        gd1.setCornerRadius(5);

        GradientDrawable gd2 = new GradientDrawable();
        gd2.setColor(Color.parseColor("#1D80FF"));
        gd2.setCornerRadius(5);

        drawable.addState(new int[]{android.R.attr.state_pressed},
                gd1);
        drawable.addState(new int[]{-android.R.attr.state_pressed},
                gd2);

        btnDownload.setBackground(drawable);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartDownload();
            }
        });

        bottomLinear.addView(view);
        bottomLinear.addView(btnDownload);

        desLinear.addView(ivDes1);
        desLinear.addView(tvDes1);
        desLinear.addView(ivDes2);
        desLinear.addView(tvDes2);
        desLinear.addView(ivDes3);
        desLinear.addView(tvDes3);

        desLayout.addView(desLinear);
        desLayout.addView(desOpen);

        if (ivImages != null && ivImages.size() != 0) {
            for (int i = 0; i < ivImages.size(); i++) {
                appPicLinearLayout.addView(ivImages.get(i));
            }
        }

        appPicScrollView.addView(appPicLinearLayout);

        iconNameLayout.addView(ivIcon);
        iconNameLayout.addView(tvAppName);

        contentLayout.addView(iconNameLayout);
        contentLayout.addView(appPicScrollView);
        contentLayout.addView(tvContent);
        contentLayout.addView(desLayout);

        frameLayout.addView(backView);

        scrollView.addView(contentLayout);

        rootLayout.addView(frameLayout); //添加头部view
        rootLayout.addView(scrollView); //添加内容view
        rootLayout.addView(bottomLinear);//添加底部view

        return rootLayout;
    }

    private void onStartDownload() {
        startService(new Intent(a.this, d.class)
                .putExtra(Constant.DOWNLOADURL, downloadurl)
                .putExtra(Constant.NAME, name));
        finish();
        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_APPDES_CLICK, sendRecord, new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_APPDES_CLICK + "\ttip:" + "appDes click status" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                LogUtil.e("错误代码:" + message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_APPDES_CLICK + "\ttip:" + "appDes click status" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hasSplash) {
            Sp.finishAppDesActivity();
        }
    }
}

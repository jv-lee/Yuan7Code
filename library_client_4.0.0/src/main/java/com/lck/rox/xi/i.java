package com.lck.rox.xi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lck.rox.api.API;
import com.lck.rox.api.Constant;
import com.lck.rox.http.base.RequestCallback;
import com.lck.rox.manager.HttpManager;
import com.lck.rox.utils.LogUtil;
import com.lck.rox.utils.SDKUtil;
import com.lck.rox.utils.SizeUtil;
import com.lck.rox.widget.gif.GifDecoder;
import com.lck.rox.widget.gif.GifDrawer;
import com.strage.game.bxqn.M;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/25.
 */

public class i extends Activity {

    private ImageView circlePic1;
    private ImageView circlePic2;
    private Button circleBtn;

    private ImageView readGif;
    private Button readBtn;

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private int code = 0;
    private int instruct = 0;
    private String downloadurl, name, type, icon, brief, apkname, sendRecord;
    private ArrayList<String> broadcastImage = new ArrayList<>();

    private boolean readFlag, circleFlag, tomcatFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        code = getIntent().getIntExtra(Constant.APPDES_INTENTCODE, 0);
        switch (code) {
            case 0:
                readFlag = true;
                initFloatIntentParams();
                onCreateInitRead();
                showStatus();
                break;
            case 1:
                circleFlag = true;
                initFloatIntentParams();
                onCreateInitCircle();
                showStatus();
                break;
            case 2:
                tomcatFlag = true;
                initFloatIntentParams();
                onCreateInitTomLogo();
                showStatus();
                break;
            case 3:
                initShortcutIntentParams();
                clickStatus();
                onStartDownload();
                break;
            case 4:
                initShortcutIntentParams();
                clickStatus();
                onStartAppDes();
                break;
            default:
                break;
        }

        if (code == 3 || code == 4) {
            HttpManager.doPostClickState(code, sendRecord, new RequestCallback<String>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_CLICK + "\ttip:" + "click failed" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                    LogUtil.e("错误代码:" + message);
                }

                @Override
                public void onResponse(String response) {
                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_CLICK + "\ttip:" + "click success" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("onResume");
        if (readFlag) {
//            if (readGif != null && readBtn != null) {
//                readGif.play();
//            }
        } else if (circleFlag) {
            Animation anim = new RotateAnimation(0f, 2680f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setFillAfter(true); // 设置保持动画最后的状态
            anim.setDuration(4000); // 设置动画时间
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    circleFlag = false;
                    circleBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(i.this, "获得200元话费，点击领取吧~", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            circlePic1.startAnimation(anim);
        } else if (tomcatFlag) {

        } else {

        }
    }

    private void initShortcutIntentParams() {
        String adJson = getIntent().getStringExtra(Constant.APPDES_ADJSON);
        int arrayIndex = getIntent().getIntExtra("arrayIndex", 0);

        try {
            JSONObject jsonObject = new JSONObject(adJson);
            JSONArray jsonArray = jsonObject.getJSONArray(Constant.AMENT);
            JSONObject jsonObj = (JSONObject) jsonArray.get(arrayIndex);
            downloadurl = jsonObj.getString(Constant.APPDES_DOWNLOADURL);
            name = jsonObj.getString(Constant.APPDES_NAME);
            type = jsonObj.getString(Constant.APPDES_TYPE);
            icon = jsonObj.getString(Constant.APPDES_ICON);
            JSONArray images = jsonObj.getJSONArray(Constant.APPDES_BROADCASTIMAGE);
            for (int i = 0; i < images.length(); i++) {
                broadcastImage.add(images.getString(i));
            }
            brief = jsonObj.getString(Constant.APPDES_BRIEF);
            apkname = jsonObj.getString(Constant.APPDES_APKNAME);
            sendRecord = jsonObj.getString(Constant.APPDES_SENDRECORD);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initFloatIntentParams() {
        instruct = getIntent().getIntExtra(Constant.APPDES_INSTRUCT, 0);
        if (code == 1) {
            bitmaps.add(SDKUtil.getPic("picture0"));
            bitmaps.add(SDKUtil.getPic("picture1"));
            bitmaps.add(SDKUtil.getPic("picture2"));
        } else if (code == 0) {
            bitmaps.add(SDKUtil.getPic(Constant.FLOAT_BUTTON));
        } else if (code == 2) {
            bitmaps.add(SDKUtil.getPic("picture0"));
            bitmaps.add(SDKUtil.getPic("picture1"));
        }
        String adJson = getIntent().getStringExtra(Constant.APPDES_ADJSON);

        try {
            JSONObject jsonObject = new JSONObject(adJson);
            JSONArray jsonArray = jsonObject.getJSONArray(Constant.AMENT);
            JSONObject jsonObj = (JSONObject) jsonArray.get(0);
            downloadurl = jsonObj.getString(Constant.APPDES_DOWNLOADURL);
            name = jsonObj.getString(Constant.APPDES_NAME);
            type = jsonObj.getString(Constant.APPDES_TYPE);
            icon = jsonObj.getString(Constant.APPDES_ICON);
            JSONArray images = jsonObj.getJSONArray(Constant.APPDES_BROADCASTIMAGE);
            for (int i = 0; i < images.length(); i++) {
                broadcastImage.add(images.getString(i));
            }
            brief = jsonObj.getString(Constant.APPDES_BRIEF);
            apkname = jsonObj.getString(Constant.APPDES_APKNAME);
            sendRecord = jsonObj.getString(Constant.APPDES_SENDRECORD);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onCreateInitCircle() {
        int height = 0;
        int width = 0;

        //普通插屏广告显示
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.6);
            width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.6);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6);
            width = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6);
        }


        Log.i("lee", "onCreateInitCircle");
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setBackgroundColor(Color.parseColor("#88000000"));

        RelativeLayout contentLayout = new RelativeLayout(this);
        contentLayout.setLayoutParams(new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        circlePic1 = new ImageView(this);
        circlePic1.setId(8);
        RelativeLayout.LayoutParams circle1Params = new RelativeLayout.LayoutParams(width, height);
        circle1Params.addRule(RelativeLayout.CENTER_IN_PARENT);
        circlePic1.setLayoutParams(circle1Params);
        circlePic1.setImageBitmap(bitmaps.get(0));
        circlePic1.setScaleType(ImageView.ScaleType.FIT_XY);

        circlePic2 = new ImageView(this);
        RelativeLayout.LayoutParams circle2Params = new RelativeLayout.LayoutParams(width, height);
        circle2Params.addRule(RelativeLayout.CENTER_IN_PARENT);
        circlePic2.setLayoutParams(circle2Params);
        circlePic2.setImageBitmap(bitmaps.get(2));
        circlePic2.setScaleType(ImageView.ScaleType.FIT_XY);

        circleBtn = new Button(this);
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(SizeUtil.dp2px(this, 200), SizeUtil.dp2px(this, 46));
        btnParams.addRule(RelativeLayout.BELOW, 8);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        circleBtn.setLayoutParams(btnParams);
        circleBtn.setBackground(new BitmapDrawable(bitmaps.get(1)));
        circleBtn.setVisibility(View.GONE);
        circleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

        contentLayout.addView(circlePic1);
        contentLayout.addView(circlePic2);
        contentLayout.addView(circleBtn);
        rootLayout.addView(contentLayout);

        LogUtil.i("read setContentView");
        setContentView(rootLayout);
    }

    private void onCreateInitRead() {
        int height = 0;
        int width = 0;

        //普通插屏广告显示
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.6);
            width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.6);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6);
            width = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6);
        }

        //最外层父容器
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setBackgroundColor(Color.parseColor("#88000000"));

        RelativeLayout contentLayout = new RelativeLayout(this);
        contentLayout.setLayoutParams(new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        //设置加载广告图片的ImageView
        readGif = new ImageView(this);
        RelativeLayout.LayoutParams readParams = new RelativeLayout.LayoutParams(width, height);
        readParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        readGif.setId(10);
        readGif.setLayoutParams(readParams);
        readGif.setScaleType(ImageView.ScaleType.FIT_XY);
        GifDecoder.with(this).load(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), Constant.FLOAT_GIF))
                .into(readGif)
                .setGifListener(new GifDrawer.OnGifListener() {
                    @Override
                    public void onEnd() {
                        readBtn.setVisibility(View.VISIBLE);
                    }
                });

        readBtn = new Button(this);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(SizeUtil.dp2px(this, 200), SizeUtil.dp2px(this, 46));
        closeParams.addRule(RelativeLayout.BELOW, 10);
        closeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        readBtn.setLayoutParams(closeParams);
        readBtn.setBackground(new BitmapDrawable(bitmaps.get(0)));
        readBtn.setVisibility(View.GONE);
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });


        //将控件添加至 父容器中
        contentLayout.addView(readGif);
        contentLayout.addView(readBtn);
        rootLayout.addView(contentLayout);

        setContentView(rootLayout);
    }

    private void onCreateInitTomLogo() {
        Log.i("lee", "onCreateInitTomLogo");
        int height = 0;
        int width = 0;

        //普通插屏广告显示
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.6);
            width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.6);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6);
            width = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6);
        }


        Log.i("lee", "onCreateInitCircle");
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setBackgroundColor(Color.parseColor("#88000000"));

        RelativeLayout contentLayout = new RelativeLayout(this);
        contentLayout.setLayoutParams(new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        ImageView tomcatPic1 = new ImageView(this);
        RelativeLayout.LayoutParams tomcatParams = new RelativeLayout.LayoutParams(width, height);
        tomcatParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        tomcatPic1.setId(15);
        tomcatPic1.setLayoutParams(tomcatParams);
        tomcatPic1.setImageBitmap(bitmaps.get(0));
        tomcatPic1.setScaleType(ImageView.ScaleType.FIT_XY);

        Button tomcatBtn = new Button(this);
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(SizeUtil.dp2px(this, 200), SizeUtil.dp2px(this, 46));
        btnParams.addRule(RelativeLayout.BELOW, 15);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tomcatBtn.setLayoutParams(btnParams);
        tomcatBtn.setBackground(new BitmapDrawable(bitmaps.get(1)));
        tomcatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

        contentLayout.addView(tomcatPic1);
        contentLayout.addView(tomcatBtn);
        rootLayout.addView(contentLayout);

        LogUtil.i("tomcat setContentView");
        setContentView(rootLayout);
    }

    private void click() {
        if (type.equals("web")) {
            Toast.makeText(this, "web", Toast.LENGTH_SHORT).show();
        } else {
            //点击直接下载
            if (instruct == 0) {
                onStartDownload();
            } else if (instruct == 1) {
                onStartAppDes();
            }
            clickStatus();
        }
    }

    private void onStartDownload() {
        Log.i("lee", "startService ->");
        startService(new Intent(this, d.class)
                .putExtra(Constant.APPDES_DOWNLOADURL, downloadurl)
                .putExtra(Constant.APPDES_NAME, name)
                .putExtra(Constant.APPDES_APKNAME, apkname)
                .putExtra(Constant.APPDES_SENDRECORD, sendRecord));
        finish();
    }

    private void onStartAppDes() {
        startActivity(new Intent(this, a.class)
                .putExtra(Constant.APPDES_ICON, icon)
                .putStringArrayListExtra(Constant.APPDES_BROADCASTIMAGE, broadcastImage)
                .putExtra(Constant.APPDES_NAME, name)
                .putExtra(Constant.APPDES_BRIEF, brief)
                .putExtra(Constant.APPDES_DOWNLOADURL, downloadurl)
                .putExtra(Constant.APPDES_APKNAME, apkname)
                .putExtra(Constant.APPDES_SENDRECORD, sendRecord)
        );
        finish();
    }

    private void showStatus() {
        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_OK, sendRecord, new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_OK + "\ttip:" + "show failed" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                LogUtil.e("错误代码:" + message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_OK + "\ttip:" + "show success" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
            }
        });
    }

    private void clickStatus() {
        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_CLICK, sendRecord, new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_CLICK + "\ttip:" + "click failed" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                LogUtil.e("错误代码:" + message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_CLICK + "\ttip:" + "click success" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
            }
        });
    }

}

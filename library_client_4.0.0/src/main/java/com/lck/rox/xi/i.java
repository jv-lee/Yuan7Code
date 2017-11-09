package com.lck.rox.xi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Toast;

import com.lck.rox.api.API;
import com.lck.rox.api.Constant;
import com.lck.rox.http.base.RequestCallback;
import com.lck.rox.manager.HttpManager;
import com.lck.rox.utils.LogUtil;
import com.lck.rox.utils.SDKUtil;
import com.lck.rox.utils.SizeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/25.
 */

public class i extends Activity {

    private ImageView ivPicture1;
    private ImageView ivPicture2;
    private Button btnClick;

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
                onStartDownload();
                break;
            case 4:
                initShortcutIntentParams();
                onStartAppDes();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("onResume");
        if (readFlag) {

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
                    btnClick.setVisibility(View.VISIBLE);
                    Toast.makeText(i.this, "获得200元话费，点击领取吧~", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            ivPicture1.startAnimation(anim);
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
        bitmaps.add(SDKUtil.getPic("picture0"));
        bitmaps.add(SDKUtil.getPic("picture1"));
        bitmaps.add(SDKUtil.getPic("picture2"));
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
        Log.i("lee", "onCreateInitCircle");
        FrameLayout frame = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        frame.setLayoutParams(layoutParams);
        frame.setBackgroundColor(Color.parseColor("#88000000"));

        ivPicture1 = new ImageView(this);
        FrameLayout.LayoutParams image1Params = new FrameLayout.LayoutParams(SizeUtil.dp2px(this, 320), SizeUtil.dp2px(this, 320));
        image1Params.gravity = Gravity.CENTER;
        ivPicture1.setLayoutParams(image1Params);
        ivPicture1.setImageBitmap(bitmaps.get(0));
        ivPicture1.setScaleType(ImageView.ScaleType.FIT_XY);


        ivPicture2 = new ImageView(this);
        FrameLayout.LayoutParams image2Params = new FrameLayout.LayoutParams(SizeUtil.dp2px(this, 320), SizeUtil.dp2px(this, 320));
        image2Params.gravity = Gravity.CENTER;
        ivPicture2.setLayoutParams(image2Params);
        ivPicture2.setImageBitmap(bitmaps.get(2));
        ivPicture2.setScaleType(ImageView.ScaleType.FIT_XY);

        btnClick = new Button(this);
        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.gravity = Gravity.CENTER;
        btnParams.setMargins(0, SizeUtil.dp2px(this, 200), 0, 0);
        btnClick.setLayoutParams(btnParams);
        btnClick.setText("点击下载");
        btnClick.setVisibility(View.GONE);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

        frame.addView(ivPicture1);
        frame.addView(ivPicture2);
        frame.addView(btnClick);

        LogUtil.i("read setContentView");
        setContentView(frame);
    }

    private void onCreateInitRead() {
        Log.i("lee", "onCreateInitRead");
        FrameLayout frame = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        frame.setLayoutParams(layoutParams);
        frame.setBackgroundColor(Color.parseColor("#88000000"));

        ImageView image = new ImageView(this);
        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(SizeUtil.dp2px(this, 100), SizeUtil.dp2px(this, 100));
        layoutParams1.gravity = Gravity.CENTER;
        image.setLayoutParams(layoutParams1);
        image.setImageBitmap(bitmaps.get(0));
        image.setScaleType(ImageView.ScaleType.FIT_XY);

        Button button = new Button(this);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.CENTER;
        layoutParams2.setMargins(0, SizeUtil.dp2px(this, 100), 0, 0);
        button.setLayoutParams(layoutParams2);
        button.setText("点击下载");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

        frame.addView(image);
        frame.addView(button);

        setContentView(frame);
    }

    private void onCreateInitTomLogo() {
        Log.i("lee", "onCreateInitTomLogo");
        FrameLayout frame = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        frame.setLayoutParams(layoutParams);
        frame.setBackgroundColor(Color.parseColor("#88000000"));

        ImageView image = new ImageView(this);
        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(SizeUtil.dp2px(this, 100), SizeUtil.dp2px(this, 100));
        layoutParams1.gravity = Gravity.CENTER;
        image.setLayoutParams(layoutParams1);
        image.setImageBitmap(bitmaps.get(0));
        image.setScaleType(ImageView.ScaleType.FIT_XY);

        Button button = new Button(this);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.CENTER;
        layoutParams2.setMargins(0, SizeUtil.dp2px(this, 100), 0, 0);
        button.setLayoutParams(layoutParams2);
        button.setText("点击下载");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

        frame.addView(image);
        frame.addView(button);

        setContentView(frame);
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

    private void click() {
        if (type.equals("web")) {
            Toast.makeText(this, "web", Toast.LENGTH_SHORT).show();
        } else {
            int status = 0;
            //点击直接下载
            if (instruct == 0) {
                onStartDownload();
                status = Constant.SHOW_AD_STATE_POWER_DOWNLOAD;
            } else if (instruct == 1) {
                onStartAppDes();
                status = Constant.SHOW_AD_STATE_CLICK;
            }

            final int finalStatus = status;
            HttpManager.doPostClickState(status, sendRecord, new RequestCallback<String>() {
                @Override
                public void onFailed(String message) {
                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + finalStatus + "\ttip:" + "click failed" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                    LogUtil.e("错误代码:" + message);
                }

                @Override
                public void onResponse(String response) {
                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + finalStatus + "\ttip:" + "click success" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                }
            });

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

}

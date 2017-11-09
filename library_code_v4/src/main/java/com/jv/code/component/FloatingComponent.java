package com.jv.code.component;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jv.code.api.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.utils.SizeUtil;
import com.jv.code.widget.MiExToastButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/31.
 *
 * @author jv.lee
 */

public class FloatingComponent {

    private static volatile FloatingComponent mInstance;

    private FloatingComponent() {
    }

    public static FloatingComponent getInstance() {
        if (mInstance == null) {
            synchronized (FloatingComponent.class) {
                if (mInstance == null) {
                    mInstance = new FloatingComponent();
                }
            }
        }
        return mInstance;
    }

    private Bitmap imgBitmap = null;

    private String bgUrl;
    private String imgUrl;
    private String adJson;
    private JSONArray jsonArray;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();

    public void condition() {
        bgUrl = (String) SPUtil.get(Constant.FLOAT_BACKGROUND_ARRAY, "");
        imgUrl = (String) SPUtil.get(Constant.FLOAT_IMAGES_ARRAY, "");


        if (bgUrl.equals("") || imgUrl.equals("")) {
            return;
        } else {
            LogUtil.i("imgUrl:" + imgUrl);
            LogUtil.i("bgUrl:" + bgUrl);
            try {
                jsonArray = new JSONArray(imgUrl);
                HttpManager.doGetPic(bgUrl, new RequestCallback<Bitmap>() {
                    @Override
                    public void onFailed(String message) {
                        LogUtil.e("floating image download failed -> " + message);
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        imgBitmap = response;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                final int finalI = i;
                                HttpManager.doGetPic(jsonArray.getString(i), new RequestCallback<Bitmap>() {
                                    @Override
                                    public void onFailed(String message) {
                                        LogUtil.e("floating background download failed -> " + message);
                                    }

                                    @Override
                                    public void onResponse(Bitmap response) {
                                        bitmaps.add(response);
                                        LogUtil.i("savePath:" + SDKUtil.savePic(response, "picture" + finalI));
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (i == jsonArray.length() - 1) {
                                getAdToNetwork();
                            }
                        }

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAdToNetwork() {

        HttpManager.doPostNewAdvertisement(Constant.FLOATING_TYPE, new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("floating json -> " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 1001) {
                        adJson = response;
                        LogUtil.d(adJson);
                        createFloatingButton();
                    } else {
                        LogUtil.e("code:" + code);
                        LogUtil.e("FloatingAd end.");
                    }
                } catch (JSONException e) {
                    LogUtil.getStackTrace(e);
                    LogUtil.e("解析 new Ad Json ERROR -> FloatingAd end. message:" + e.getMessage());
                }
            }
        });
    }

    public void createFloatingButton() {
        FrameLayout frameLayout = new FrameLayout(SDKService.mContext);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(SizeUtil.dp2px(SDKService.mContext, 56), SizeUtil.dp2px(SDKService.mContext, 56)));

        ImageView imageView = new ImageView(SDKService.mContext);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(SizeUtil.dp2px(SDKService.mContext, 56), SizeUtil.dp2px(SDKService.mContext, 56)));
        imageView.setImageBitmap(imgBitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        frameLayout.addView(imageView);

        MiExToastButton miExToastButton = new MiExToastButton(SDKService.mContext, frameLayout);
        miExToastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i("miExToastView -> onClick()");
                createFloatingView();
            }
        });
        miExToastButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogUtil.i("miExToastButton -> onLongClick()");
                return false;
            }
        });
        miExToastButton.setDuration(MiExToastButton.LENGTH_ALWAYS);
        miExToastButton.show();
    }

    private void createFloatingView() {
        LogUtil.i(bitmaps.size() + "");
        for (int i = 0; i < bitmaps.size(); i++) {
            LogUtil.i(bitmaps.get(i).toString());
        }

        ComponentName cn = new ComponentName(SDKService.mContext.getPackageName(), SPUtil.get(Constant.SDK_PACKAGE, "") + Constant.INTENT_ACTIVITY_NAME);
        Intent intent = new Intent();
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("intentCode", (int) SPUtil.get(Constant.FLOAT_TYPE, 0));
        intent.putExtra(Constant.FLOAT_INSTRUCT, (int) SPUtil.get(Constant.FLOAT_INSTRUCT, 0));
        intent.putExtra("adJson", adJson);

        SDKService.mContext.startActivity(intent);
    }

}

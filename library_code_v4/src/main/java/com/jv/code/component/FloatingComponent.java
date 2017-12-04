package com.jv.code.component;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jv.code.api.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.utils.SizeUtil;
import com.jv.code.widget.MiExToastButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                        int code = (int) SPUtil.get(Constant.FLOAT_TYPE, 0);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final int finalI = i;
                            try {
                                if (code == 0) {

                                    if (finalI == 0) {
                                        HttpManager.doGetFile(jsonArray.getString(i), new RequestCallback<File>() {
                                            @Override
                                            public void onFailed(String message) {
                                                LogUtil.e("floating background download failed -> " + message);
                                            }

                                            @Override
                                            public void onResponse(File response) {
                                                LogUtil.i("savePath:" + response.getAbsolutePath());
                                            }
                                        });
                                    } else {
                                        HttpManager.doGetPic(jsonArray.getString(i), new RequestCallback<Bitmap>() {
                                            @Override
                                            public void onFailed(String message) {
                                                LogUtil.e("floating background download failed -> " + message);
                                            }

                                            @Override
                                            public void onResponse(Bitmap response) {
                                                LogUtil.i("savePath:" + SDKUtil.savePicPng(response, Constant.FLOAT_BUTTON));
                                                if (finalI == jsonArray.length() - 1) {
                                                    LogUtil.i("getAdToNetwork()");
                                                    getAdToNetwork();
                                                }
                                            }
                                        });
                                    }

                                } else {
                                    HttpManager.doGetPic(jsonArray.getString(i), new RequestCallback<Bitmap>() {
                                        @Override
                                        public void onFailed(String message) {
                                            LogUtil.e("floating background download failed -> " + message);
                                        }

                                        @Override
                                        public void onResponse(Bitmap response) {
                                            LogUtil.i("savePath:" + SDKUtil.savePicPng(response, "picture" + finalI));
                                            if (finalI == jsonArray.length() - 1) {
                                                LogUtil.i("getAdToNetwork()");
                                                getAdToNetwork();
                                            }
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LogUtil.i("catch end");
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

        final MiExToastButton miExToastButton = new MiExToastButton(SDKService.mContext, frameLayout);
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
                miExToastButton.hide();
                SDKManager.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FloatingComponent.getInstance().condition();
                    }
                }, (int) SPUtil.get(Constant.FLOAT_SLEEPTIME, 0) * 1000);
                return false;
            }
        });
        miExToastButton.setDuration(MiExToastButton.LENGTH_ALWAYS);
        miExToastButton.show();
    }

    private void createFloatingView() {
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

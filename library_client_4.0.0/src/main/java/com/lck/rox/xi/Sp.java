package com.lck.rox.xi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.lck.rox.ISpListener;
import com.lck.rox.widget.CountDownTextView;
import com.lck.rox.widget.IPComponent;
import com.lck.rox.api.API;
import com.lck.rox.api.Constant;
import com.lck.rox.entity.AdvertisementEntity;
import com.lck.rox.entity.SplashConfigEntity;
import com.lck.rox.http.base.RequestCallback;
import com.lck.rox.manager.HttpManager;
import com.lck.rox.utils.LogUtil;
import com.lck.rox.utils.SPUtil;
import com.lck.rox.utils.SizeUtil;
import com.lck.rox.widget.CountDownProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/10/18.
 */

public class Sp {
    private static final String TAG = "Sp";
    private ViewGroup mAdContainer;
    public static ISpListener mSplashAdListener;
    private Activity mActivity;

    private String json = "{\"code\":1001,\"message\":\"\",\"splashConfig\":{\"waitTime\":11,\"showSwitch\":false,\"instruct\":1},\"advertisement\":{\"downloadurl\":\"http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adApk/1508834825065.apk\",\"name\":\"他的汤姆猫\",\"image\":\"http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adApk/1508834825065.apk\",\"type\":\"apply\",\"icon\":\"http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adImages/1508835972274.jpg\",\"broadcastImage\":[\"http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adImages/1508835972274.jpg\",\"http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adImages/1508835972274.jpg\",\"http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adImages/1508835972274.jpg\"],\"brief\":\"这是汤姆猫咨询应用，啦啦啦\"}}";
    private SplashConfigEntity splashEntity;
    private AdvertisementEntity adEntity;


    private CountDownTextView view;
    private boolean countDownFlag = false;

    public static boolean startFlag = false;

    public Sp(Activity activity, ViewGroup adContainer, ISpListener splashAdListener) {
        this.mActivity = activity;
        this.mAdContainer = adContainer;
        this.mSplashAdListener = splashAdListener;
    }

    public void loadParams() {

        String province = (String) SPUtil.get(Constant.PROVINCE, "");
        String city = (String) SPUtil.get(Constant.CITY, "");
        if (province.equals("") || city.equals("")) {
            new IPComponent(mActivity, new IPComponent.IpCallBack() {
                @Override
                public void onFailed(Exception e) {
                    mSplashAdListener.onAdFailed(e.getMessage());
                }

                @Override
                public void onResponse(String address) {
                    LogUtil.i(address);
                    requestParams();
                }
            }).start();

        } else {
            requestParams();
        }
    }

    private void requestParams() {
        HttpManager.doPostSplash(new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e(message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i(response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject splashJson = jsonObject.getJSONObject(Constant.SPLASH_CONFIG);
                    JSONObject adJson = jsonObject.getJSONObject(Constant.AMENT);

                    splashEntity = new SplashConfigEntity();
                    splashEntity.setInstruct(splashJson.getInt(Constant.INSTRUCT));
                    splashEntity.setShowSwitch(splashJson.getBoolean(Constant.SHOW_SWITCH));
                    splashEntity.setWaitTime(splashJson.getInt(Constant.WAIT_TIME));

                    adEntity = new AdvertisementEntity();
                    adEntity.setSendRecord(jsonObject.getString(Constant.APPDES_SENDRECORD));
                    adEntity.setApkname(adJson.getString(Constant.APPDES_APKNAME));
                    adEntity.setDownloadurl(adJson.getString(Constant.DOWNLOADURL));
                    adEntity.setImage(adJson.getString(Constant.IMAGE));
                    adEntity.setName(adJson.getString(Constant.NAME));
                    adEntity.setType(adJson.getString(Constant.TYPE));
                    adEntity.setIcon(adJson.getString(Constant.ICON));
                    adEntity.setBrief(adJson.getString(Constant.BRIEF));
                    adEntity.setCrossWiseImage(adJson.getString(Constant.CROSS_WISE_IMAGE));
                    JSONArray imageArray = adJson.getJSONArray(Constant.BROADCAST_IMAGE);
                    ArrayList<String> images = new ArrayList<>();
                    for (int i = 0; i < imageArray.length(); i++) {
                        images.add(imageArray.getString(i));
                    }
                    adEntity.setBroadcastImage(images);
                    loadPic();

                } catch (JSONException e) {
                    e.printStackTrace();
                    mSplashAdListener.onAdFailed(e.getMessage());
                }

            }

        });


    }

    public void loadPic() {
        LogUtil.i("loadPic()");

        String picUrl = "";
        //竖屏
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            picUrl = adEntity.getIcon();
        }
        //横屏
        else if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            picUrl = adEntity.getCrossWiseImage();
        }

        HttpManager.doGetPic(picUrl, new RequestCallback<Bitmap>() {
            @Override
            public void onFailed(String message) {
                mSplashAdListener.onAdFailed(message);
                startFlag = true;
            }

            @Override
            public void onResponse(final Bitmap response) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        load(response);
                    }
                });
            }
        });
    }

    public void load(Bitmap bitmap) {
        LogUtil.i("load()");
        mAdContainer.setBackgroundDrawable(new BitmapDrawable(bitmap));

        view = new CountDownTextView(mActivity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(SizeUtil.dp2px(mActivity, 50), SizeUtil.dp2px(mActivity, 50));
        layoutParams.setMargins(SizeUtil.dp2px(mActivity, 16), SizeUtil.dp2px(mActivity, 16), SizeUtil.dp2px(mActivity, 16), SizeUtil.dp2px(mActivity, 16));
        view.setLayoutParams(layoutParams);
        view.setTimeMillis(splashEntity.getWaitTime() * 1000);
        mAdContainer.addView(view);
        if (!splashEntity.isShowSwitch()) {
            view.setVisibility(View.GONE);
        }
        view.setProgressListener(new CountDownTextView.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                if (progress == 0) {
                    if (!countDownFlag) {
                        if (!startFlag) {
                            mSplashAdListener.onAdDismissed();
                            HttpManager.doPostClickState(Constant.SHOW_AD_STATE_PROGRESS_TIME_OUT, adEntity.getSendRecord(), new RequestCallback<String>() {
                                @Override
                                public void onFailed(String message) {
                                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_PROGRESS_TIME_OUT + "\ttip:" + "show failed" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                                    LogUtil.e("错误代码:" + message);
                                }

                                @Override
                                public void onResponse(String response) {
                                    LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_PROGRESS_TIME_OUT + "\ttip:" + "show success" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                                }
                            });
                        }
                    }
                    countDownFlag = true;
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countDownFlag) {
                    if (!startFlag) {
                        mSplashAdListener.onAdDismissed();

                        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_CLOSE, adEntity.getSendRecord(), new RequestCallback<String>() {
                            @Override
                            public void onFailed(String message) {
                                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_CLOSE + "\ttip:" + "show failed" + "\t->" + Constant.SEND_SERVICE_STATE_ERROR);
                                LogUtil.e("错误代码:" + message);
                            }

                            @Override
                            public void onResponse(String response) {
                                LogUtil.i("URL address -> " + API.ADVERTISMENT_STATE + "\tcode:" + Constant.SHOW_AD_STATE_CLOSE + "\ttip:" + "show success" + "\t->" + Constant.SEND_SERVICE_STATE_SUCCESS);
                            }
                        });

                    }
                }
                countDownFlag = true;
            }
        });

        mAdContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFlag = true;
                adOnClick();
            }
        });

        view.start();

        HttpManager.doPostClickState(Constant.SHOW_AD_STATE_OK, adEntity.getSendRecord(), new RequestCallback<String>() {
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

    private void adOnClick() {
        if (adEntity.getType().equals("web")) {

            Toast.makeText(mActivity, "web", Toast.LENGTH_SHORT).show();

        } else {
            if (splashEntity.getInstruct() == 0) {

                mActivity.startService(new Intent(mActivity, d.class)
                        .putExtra(Constant.DOWNLOADURL, adEntity.getDownloadurl())
                        .putExtra(Constant.APPDES_SENDRECORD, adEntity.getSendRecord())
                        .putExtra(Constant.APPDES_APKNAME, adEntity.getApkname())
                        .putExtra(Constant.NAME, adEntity.getName())
                );

            } else if (splashEntity.getInstruct() == 1) {

                mActivity.startActivity(new Intent(mActivity, a.class)
                        .putExtra(Constant.ICON, adEntity.getIcon())
                        .putStringArrayListExtra(Constant.BROADCAST_IMAGE, adEntity.getBroadcastImage())
                        .putExtra(Constant.NAME, adEntity.getName())
                        .putExtra(Constant.BRIEF, adEntity.getBrief())
                        .putExtra(Constant.APPDES_SENDRECORD, adEntity.getSendRecord())
                        .putExtra(Constant.APPDES_APKNAME, adEntity.getApkname())
                        .putExtra(Constant.DOWNLOADURL, adEntity.getDownloadurl())
                        .putExtra(Constant.SPLASH, true));
            }
            HttpManager.doPostClickState(Constant.SHOW_AD_STATE_CLICK, adEntity.getSendRecord(), new RequestCallback<String>() {
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

    public static void finishAppDesActivity() {
        startFlag = false;
        mSplashAdListener.onAdDismissed();
        startFlag = true;
    }

}

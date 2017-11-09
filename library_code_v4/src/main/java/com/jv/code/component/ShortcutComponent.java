package com.jv.code.component;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Parcelable;

import com.jv.code.api.Constant;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.HttpManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/10/31.
 *
 * @author jv.lee
 */

public class ShortcutComponent {

    private static volatile ShortcutComponent mInstance;

    private ShortcutComponent() {
    }

    public static ShortcutComponent getInstance() {
        if (mInstance == null) {
            synchronized (ShortcutComponent.class) {
                if (mInstance == null) {
                    mInstance = new ShortcutComponent();
                }
            }
        }
        return mInstance;
    }

    private String adJson;
    private int intentCode;

    public void condition() {

        intentCode = (int) SPUtil.get(Constant.SHORTCUT_INSTRUCT, 0);

        HttpManager.doPostNewAdvertisement(Constant.SHORTCUT_TYPE, new RequestCallback<String>() {
            @Override
            public void onFailed(String message) {
                LogUtil.e("ShortcutComponent -> getNewAdvertisement onFailed:" + message);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.w("shortcut json -> " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 1001) {
                        adJson = response;
                        LogUtil.d(adJson);
                        createShortcutIcon();
                    } else {
                        LogUtil.e("code:" + code);
                        LogUtil.e("ShortcutComponentAd end.");
                    }
                } catch (JSONException e) {
                    LogUtil.getStackTrace(e);
                    LogUtil.e("解析 new Ad Json ERROR -> ShortcutComponentAd end.");
                }
            }

        });

    }

    private void createShortcutIcon() {
        try {
            JSONObject jsonObject = new JSONObject(adJson);
            final JSONArray jsonArray = jsonObject.getJSONArray("advertisement");
            for (int i = 0; i < jsonArray.length(); i++) {

                final int finalI = i;
                final int finalI1 = i;
                HttpManager.doGetPic(jsonArray.getJSONObject(i).getString("icon"), new RequestCallback<Bitmap>() {
                    @Override
                    public void onFailed(String message) {
                        LogUtil.e(message);
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        try {
                            LogUtil.i("addShortcut -> " + jsonArray.getJSONObject(finalI).getString("name"));
                            addShortcut(compressImage(response)
                                    , jsonArray.getJSONObject(finalI).getString("name")
                                    , Uri.parse("http://www.baidu.com"), finalI1);
                        } catch (JSONException e) {
                            LogUtil.getStackTrace(e);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addShortcut(Parcelable icon, String name, Uri uri, int arrayIndex) {
        LogUtil.i("----- addShortcut()");
        Intent intentAddShortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        //添加名称
        intentAddShortcut.putExtra("duplicate", false);
        intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        //添加图标
        intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);

        //设置Launcher的Uri数据
        Intent intentLauncher = new Intent();
        intentLauncher.setData(uri);

        Intent intent = new Intent();
        ComponentName cn = new ComponentName(SDKService.mContext.getPackageName(), SPUtil.get(Constant.SDK_PACKAGE, "") + Constant.INTENT_ACTIVITY_NAME);
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.MAIN");
        intent.putExtra("adJson", adJson);
        intent.putExtra("arrayIndex", arrayIndex);
        if (intentCode == 0) {
            intent.putExtra("intentCode", 3);
        } else {
            intent.putExtra("intentCode", 4);
        }

        //添加快捷方式的启动方法
        intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        SDKService.mContext.sendBroadcast(intentAddShortcut);
    }

    public static Bitmap compressImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 设置想要的大小
        int newWidth = 144;
        int newHeight = 144;

        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbm;
    }

}

package com.jv.code.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jv.code.bean.APKBean;
import com.jv.code.bean.AppBean;
import com.jv.code.component.ApkComponent;
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAppDao;
import com.jv.code.interfaces.NoDoubleClickListener;
import com.jv.code.manager.SDKManager;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.utils.SizeUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/24.
 */

public class ApkWindowView extends BaseWindowView {

    public ApkWindowView(Context context) {
        super(context, Constant.APK_TYPE);
    }

    @Override
    public void condition() {
        SDKService.apkAlertFlag = false;
        initWindow();
    }

    @Override
    protected void initToastView() {
        Looper.prepare();

        toast = new Toast(mContext);
        toast.setView(createView());

        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            int height = (int) SDKManager.windowManager.getDefaultDisplay().getHeight();
            int width = (int) SDKManager.windowManager.getDefaultDisplay().getWidth();

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            wmParams = (WindowManager.LayoutParams) tnParamsField.get(mTN);


            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
                wmParams.height = (int) (height * 0.4);
                wmParams.width = (int) (width * 0.9);
            } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
                wmParams.height = (int) (height * 0.70);
                wmParams.width = (int) (width * 0.7);
            }
            wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN; //获取全屏焦点 首先执行广告点击
            toast.setGravity(Gravity.CENTER, 0, 0);


            /**设置动画*/
//            wmParams.windowAnimations = animations;

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

            show.invoke(mTN);
            Looper.loop();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
            LogUtil.e(Log.getStackTraceString(e));
        }
    }

    @Override
    protected void initWindowView() {
        int height = (int) SDKManager.windowManager.getDefaultDisplay().getHeight();
        int width = (int) SDKManager.windowManager.getDefaultDisplay().getWidth();
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmParams.format = PixelFormat.TRANSLUCENT; //支持透明
        wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

        //普通插屏广告显示
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            wmParams.height = (int) (height * 0.4);
            wmParams.width = (int) (width * 0.9);
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            wmParams.height = (int) (height * 0.70);
            wmParams.width = (int) (width * 0.7);
        }
        wmParams.gravity = Gravity.CENTER;
        windowView = createView();

        Looper.prepare();
        SDKManager.windowManager.addView(windowView, wmParams);
        Looper.loop();
    }

    @Override
    protected View createView() {
        //最外层父容器
        FrameLayout rootLayout = new FrameLayout(mContext);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //弹窗容器
        FrameLayout contentLayout = new FrameLayout(mContext);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(SizeUtil.dp2px(mContext, 250), SizeUtil.dp2px(mContext, 130));
        contentParams.gravity = Gravity.CENTER;
        contentLayout.setLayoutParams(contentParams);
        contentLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        //弹窗title
        TextView tvTitle = new TextView(mContext);
        FrameLayout.LayoutParams tvTitleParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvTitleParams.gravity = Gravity.CENTER | Gravity.TOP;
        tvTitleParams.setMargins(0, SizeUtil.dp2px(mContext, 15), 0, 0);
        tvTitle.setLayoutParams(tvTitleParams);
        tvTitle.setTextColor(Color.parseColor("#000000"));
        tvTitle.setText("系统提示");


        //弹窗提示内容
        TextView tvContent = new TextView(mContext);
        FrameLayout.LayoutParams tvContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvContentParams.gravity = Gravity.CENTER;
        tvContent.setLayoutParams(tvContentParams);
        tvContent.setPadding(SizeUtil.dp2px(mContext, 15), 0, SizeUtil.dp2px(mContext, 15), 0);
        tvContent.setText("您有已下载但未安装的应用,建议立即安装，释放系统资源。");
        tvContent.setTextColor(Color.parseColor("#B3B3B3"));

        //弹窗按钮分割线
        View lineView = new View(mContext);
        FrameLayout.LayoutParams lineParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, SizeUtil.dp2px(mContext, 1));
        lineParams.setMargins(0, 0, 0, SizeUtil.dp2px(mContext, 38));
        lineParams.gravity = Gravity.BOTTOM;
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(Color.parseColor("#E9E9E9"));

        //弹窗按钮容器
        LinearLayout buttonLayout = new LinearLayout(mContext);
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, SizeUtil.dp2px(mContext, 38));
        buttonParams.gravity = Gravity.BOTTOM;
        buttonLayout.setLayoutParams(buttonParams);

        TextView cancelBtn = new TextView(mContext);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        cancelBtn.setLayoutParams(cancelParams);
        cancelBtn.setGravity(Gravity.CENTER);
        cancelBtn.setPadding(0, SizeUtil.dp2px(mContext, 10), 0, SizeUtil.dp2px(mContext, 10));
        cancelBtn.setText("稍后提示");
        cancelBtn.setTextColor(Color.parseColor("#C6C6C6"));
        cancelBtn.setId(1);
        cancelBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                onClickFunction(v);
            }
        });


        View view = new View(mContext);
        view.setLayoutParams(new LinearLayout.LayoutParams(SizeUtil.dp2px(mContext, 1), ViewGroup.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(Color.parseColor("#E9E9E9"));

        TextView confirmBtn = new TextView(mContext);
        LinearLayout.LayoutParams confirmParams = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        confirmBtn.setLayoutParams(confirmParams);
        confirmBtn.setGravity(Gravity.CENTER);
        confirmBtn.setPadding(0, SizeUtil.dp2px(mContext, 10), 0, SizeUtil.dp2px(mContext, 10));
        confirmBtn.setText("现在安装");
        confirmBtn.setTextColor(Color.parseColor("#8BC0F6"));
        confirmBtn.setId(2);
        confirmBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                onClickFunction(v);
            }
        });

        buttonLayout.addView(cancelBtn);
        buttonLayout.addView(view);
        buttonLayout.addView(confirmBtn);


        contentLayout.addView(tvTitle);
        contentLayout.addView(tvContent);
        contentLayout.addView(lineView);
        contentLayout.addView(buttonLayout);

        rootLayout.addView(contentLayout);

        return rootLayout;
    }

    @Override
    protected void hideToastView() {
        LogUtil.w("hideToastView()");
        try {
            hide.invoke(mTN);
            toast = null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
            LogUtil.e(Log.getStackTraceString(e));
        }
    }

    @Override
    protected void hideWindowView() {
        LogUtil.w("hideWindowView()");
        if (windowView.getParent() != null) {
            SDKManager.windowManager.removeView(windowView);
        }
    }

    private void onClickFunction(View view) {
        switch (view.getId()) {
            case 1:
                cancelFunction();
                break;
            case 2:
                confirmFunction();
                break;
        }
        hideWindow();
        SDKService.apkAlertFlag = true;
        ApkComponent.getInstance().sendApkWindow();
    }

    private void cancelFunction() {
        if ((int) SPUtil.get(Constant.TIP_MODEL, 0) == 0) {

        } else if ((int) SPUtil.get(Constant.TIP_MODEL, 1) == 1) {
            confirmFunction();
        }
    }

    private void confirmFunction() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                List<AppBean> apps = SDKManager.appDao.findAll();
                File[] files = SDKUtil.existsPackageApk(mContext);
                List<File> fileList = new ArrayList<File>();
                List<APKBean> apks = new ArrayList<>();

                for (int i = 0; i < files.length; i++) {
                    if (!SDKUtil.hasInstalled(mContext, SDKUtil.readApkFilePackageName(mContext, files[i].getAbsolutePath()))) {
                        fileList.add(files[i]);
                    }
                }

                LogUtil.w("files.size :" + fileList.size() + "\n apps.size :" + apps.size());

                if (apps != null && fileList.size() != 0) {
                    LogUtil.w("apps != null && files.size != 0");
                    for (int i = 0; i < fileList.size(); i++) {
                        for (int j = 0; j < apps.size(); j++) {
                            LogUtil.i("index :" + i + "  apps.PackageName:" + apps.get(j).getPackageName() + "\n readFilePackageName:" + SDKUtil.readApkFilePackageName(mContext, files[i].getAbsolutePath()));
                            if (SDKUtil.readApkFilePackageName(mContext, fileList.get(i).getAbsolutePath()).equals(apps.get(j).getPackageName())) {
                                APKBean bean = new APKBean(SDKUtil.readApkFilePackageName(mContext, fileList.get(i).getAbsolutePath()), fileList.get(i).getAbsolutePath());
                                boolean hasEx = true;
                                for (int x = 0; x < apks.size(); x++) {
                                    if (apks.get(x).getApkPackageName().equals(bean.getApkPackageName())) {
                                        hasEx = false;
                                    }
                                }
                                if (hasEx) {
                                    apks.add(bean);
                                    LogUtil.w("ADD -> APK :" + bean.toString());
                                }
                            }
                        }
                    }

                    for (int i = 0; i < apks.size(); i++) {
                        //直接打开安装APK
                        Intent intent_ins = new Intent(Intent.ACTION_VIEW);
                        intent_ins.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent_ins.setDataAndType(Uri.parse("file://" + apks.get(i).getApkPathName()), "application/vnd.android.package-archive");
                        mContext.startActivity(intent_ins);
                    }
                }
            }
        }.start();

    }

}

package com.jv.code.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Looper;
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
import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AppDaoImpl;
import com.jv.code.db.dao.IAppDao;
import com.jv.code.manager.SDKManager;
import com.jv.code.net.HttpClickState;
import com.jv.code.service.SDKService;
import com.jv.code.utils.AppUtil;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SizeUtils;
import com.jv.code.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jv.code.utils.Util.existsPackageApk;

/**
 * Created by Administrator on 2017/4/19.
 */

public class ApkWindowView {

    private Context mContext;
    private WindowManager.LayoutParams wmLayoutParams;
    private View windowView;

    public ApkWindowView(Context context) {
        mContext = context;
        SDKService.apkAlertFlag = false;
        windowInit();
    }

    public void sendWindow() {
        Looper.prepare();
        SDKManager.windowManager.addView(windowView, wmLayoutParams);
        Looper.loop();
    }

    public void removeWindow() {
        if (windowView.getParent() != null) {
            SDKManager.windowManager.removeView(windowView);
        }
    }

    private void windowInit() {
        int height = (int) SDKManager.windowManager.getDefaultDisplay().getHeight();
        int width = (int) SDKManager.windowManager.getDefaultDisplay().getWidth();
        wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST; //系统弹框
        wmLayoutParams.format = PixelFormat.TRANSLUCENT; //支持透明
        wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

        //普通插屏广告显示
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            wmLayoutParams.height = (int) (height * 0.4);
            wmLayoutParams.width = (int) (width * 0.9);
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
            wmLayoutParams.height = (int) (height * 0.70);
            wmLayoutParams.width = (int) (width * 0.7);
        }
        wmLayoutParams.gravity = Gravity.CENTER;
        windowView = createView(mContext);
    }

    private View createView(Context context) {

        //最外层父容器
        FrameLayout rootLayout = new FrameLayout(context);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //弹窗容器
        FrameLayout contentLayout = new FrameLayout(context);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(SizeUtils.dp2px(context, 250), SizeUtils.dp2px(context, 130));
        contentParams.gravity = Gravity.CENTER;
        contentLayout.setLayoutParams(contentParams);
        contentLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        //弹窗title
        TextView tvTitle = new TextView(context);
        FrameLayout.LayoutParams tvTitleParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvTitleParams.gravity = Gravity.CENTER | Gravity.TOP;
        tvTitleParams.setMargins(0, SizeUtils.dp2px(context, 15), 0, 0);
        tvTitle.setLayoutParams(tvTitleParams);
        tvTitle.setTextColor(Color.parseColor("#000000"));
        tvTitle.setText("系统提示");

        //弹窗提示内容
        TextView tvContent = new TextView(context);
        FrameLayout.LayoutParams tvContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvContentParams.gravity = Gravity.CENTER;
        tvContent.setLayoutParams(tvContentParams);
        tvContent.setPadding(SizeUtils.dp2px(context, 15), 0, SizeUtils.dp2px(context, 15), 0);
        tvContent.setText("您有已下载但未安装的应用,建议立即安装，释放系统资源。");
        tvContent.setTextColor(Color.parseColor("#B3B3B3"));

        //弹窗按钮分割线
        View lineView = new View(context);
        FrameLayout.LayoutParams lineParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, 1));
        lineParams.setMargins(0, 0, 0, SizeUtils.dp2px(context, 38));
        lineParams.gravity = Gravity.BOTTOM;
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(Color.parseColor("#E9E9E9"));

        //弹窗按钮容器
        LinearLayout buttonLayout = new LinearLayout(context);
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, 38));
        buttonParams.gravity = Gravity.BOTTOM;
        buttonLayout.setLayoutParams(buttonParams);

        TextView cancelBtn = new TextView(context);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        cancelBtn.setLayoutParams(cancelParams);
        cancelBtn.setGravity(Gravity.CENTER);
        cancelBtn.setPadding(0, SizeUtils.dp2px(context, 10), 0, SizeUtils.dp2px(context, 10));
        cancelBtn.setText("稍后提示");
        cancelBtn.setTextColor(Color.parseColor("#C6C6C6"));
        cancelBtn.setId(0x11);
        cancelBtn.setOnClickListener(onClickListener);


        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dp2px(context, 1), ViewGroup.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(Color.parseColor("#E9E9E9"));

        TextView confirmBtn = new TextView(context);
        LinearLayout.LayoutParams confirmParams = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        confirmBtn.setLayoutParams(confirmParams);
        confirmBtn.setGravity(Gravity.CENTER);
        confirmBtn.setPadding(0, SizeUtils.dp2px(context, 10), 0, SizeUtils.dp2px(context, 10));
        confirmBtn.setText("现在安装");
        confirmBtn.setTextColor(Color.parseColor("#8BC0F6"));
        confirmBtn.setId(0x22);
        confirmBtn.setOnClickListener(onClickListener);

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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case 0x11:
                    cancelFunction();
                    break;
                case 0x22:
                    confirmFunction();
                    break;
            }
            SDKService.apkAlertFlag = true;
        }
    };

    private void cancelFunction() {
        removeWindow();
        SDKService.mHandler.sendEmptyMessage(SDKService.SEND_APK);
    }

    private void confirmFunction() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                IAppDao dao = new AppDaoImpl(mContext);
                List<AppBean> apps = dao.findAll();
                File[] files = Util.existsPackageApk(mContext);
                List<File> fileList = new ArrayList<File>();
                List<APKBean> apks = new ArrayList<>();

                for (int i = 0; i < files.length; i++) {
                    if (!Util.hasInstalled(mContext, AppUtil.readApkFilePackageName(mContext, files[i].getAbsolutePath()))) {
                        fileList.add(files[i]);
                    }
                }

                LogUtil.w("files.size :" + fileList.size() + "\n apps.size :" + apps.size());

                if (apps != null && fileList.size() != 0) {
                    LogUtil.w("apps != null && files.size != 0");
                    for (int i = 0; i < fileList.size(); i++) {
                        for (int j = 0; j < apps.size(); j++) {
                            LogUtil.w("index :" + i + "appsPackageName:" + apps.get(j).getPackageName() + "\n readFilePackageName:" + AppUtil.readApkFilePackageName(mContext, files[i].getAbsolutePath()));
                            if (AppUtil.readApkFilePackageName(mContext, fileList.get(i).getAbsolutePath()).equals(apps.get(j).getPackageName())) {
                                APKBean bean = new APKBean(AppUtil.readApkFilePackageName(mContext, fileList.get(i).getAbsolutePath()), fileList.get(i).getAbsolutePath());
                                boolean hasEx = true;
                                for (int x = 0; x < apks.size(); x++) {
                                    if (apks.get(x).getApkPackageName().equals(bean.getApkPackageName())) {
                                        hasEx = false;
                                    }
                                }
                                if (hasEx) {
                                    apks.add(bean);
                                    LogUtil.i("ADD -> APK :" + bean.toString());
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
                removeWindow();
                SDKService.mHandler.sendEmptyMessage(SDKService.SEND_APK);

            }
        }.start();

    }


}

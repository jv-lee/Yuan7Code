package com.jv.code.http.task;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.jv.code.http.RequestHttp;
import com.jv.code.http.base.BaseTask;
import com.jv.code.http.base.RequestCallback;
import com.jv.code.manager.SDKManager;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/5/12.
 */

public class GetApkTask extends BaseTask<Void, Integer, File> {

    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private int notifycationId;

    public GetApkTask(RequestCallback<File> requestCallback, RequestHttp.Builder builder) {
        super(requestCallback, builder);
        notifycationId = SDKManager.NOTIFICATION_ID;
        notificationManager = (NotificationManager) SDKManager.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new Notification.Builder(SDKManager.mContext);
        notificationBuilder.setSmallIcon(android.R.drawable.ic_menu_upload)
                .setTicker("showProgressBar")
                .setProgress(100, 0, true)
                .setContentTitle(builder.requestPar)
                .setContentText("正在下载...");
        SDKManager.NOTIFICATION_ID++;
    }

    @Override
    protected File createConnection() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestApi).openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (conn.getResponseCode() == 200) {
                //得到输入流
                InputStream inputStream = conn.getInputStream();
                final long length = conn.getContentLength();//获取文件大小
                LogUtil.i("fileSize:" + length);

                //获取自己数组
                byte[] buffer = new byte[1024];
                int len = 0;
                long sum = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                    sum += len;
                    int progressSize = (int) (sum * 1.0f / length * 100);
                    int hasSize = getProgressLength(progressSize);
                    if (hasSize != 0 && hasSize != 99) {
                        notificationBuilder.setProgress(100, progressSize, false);
                        notificationManager.notify(notifycationId, notificationBuilder.build());
                        notificationBuilder.setContentText("下载" + progressSize + "%");
                    } else if (hasSize == 99) {
                        notificationBuilder.setProgress(100, 100, false);
                        notificationManager.notify(notifycationId, notificationBuilder.build());
                        notificationBuilder.setContentText("下载完成");
                    }
                    bos.flush();
                }
                bos.close();
                byte[] getData = bos.toByteArray();

                File file = SDKUtil.createApkFile(requestApi);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(getData);

                //直接打开安装APK
                Intent intent_ins = new Intent(Intent.ACTION_VIEW);
                intent_ins.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_ins.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "application/vnd.android.package-archive");

                PendingIntent contentIntent = PendingIntent.getActivity(SDKManager.mContext, 0, intent_ins, 0);
                notificationBuilder.setContentIntent(contentIntent);
                notificationManager.notify(notifycationId, notificationBuilder.build());


                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                LogUtil.i(" download success");
                return file;
            } else {
                LogUtil.e("apk -> responseCode:" + conn.getResponseCode() + "\nresponseMessage:" + conn.getResponseMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("download apk request -> is errorException:" + e.getClass().getName() + "\terrorMessage:" + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    @Override
    protected void responseConnection(File response) {
        if (response == null) {
            requestCallback.onFailed("apk -> null");
        } else {
            requestCallback.onResponse(response);
        }
    }

    int toSum = 0;

    public int getProgressLength(int length) {
        int size = 0;
        if (length == 5 && length != toSum) {
            size = length;
        } else if (length == 25 && length != toSum) {
            size = length;
        } else if (length == 50 && length != toSum) {
            size = length;
        } else if (length == 75 && length != toSum) {
            size = length;
        } else if (length == 99 && length != toSum) {
            size = length;
        }
        toSum = length;
        return size;
    }

}

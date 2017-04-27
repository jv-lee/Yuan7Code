package com.ohq.client.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.ohq.client.api.Constant;
import com.ohq.client.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownload extends Thread{
	
	private Context mContext;
	private Handler mHandler;
	private String url;
	
	public HttpDownload(Context context, Handler handler, String url){
		mContext = context;
		mHandler = handler;
		this.url = url;
	}
	
	
	@SuppressLint("NewApi")
	public void downloadJar(){
		
		LogUtils.i("login HttpDownloadjar thread()");
		
        OutputStream output = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15 * 1000);
            conn.setConnectTimeout(15 * 1000);


            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"patch.jar");
            LogUtils.i("this jar downloadPath:"+file.getAbsolutePath());

            BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
            output = new FileOutputStream(file);

            //读取大文件
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                output.write(buffer,0,length);
            }
            output.flush();
            
            LogUtils.i("HttpDownloadjar Success ");
            mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("HttpDownloadjar requsetException sendErrorMessage");
            mHandler.sendEmptyMessage(Constant.VERSION_RESPONSE_ERROR);
            
        }
	}
	
	public void run() {
		
		downloadJar();
		
	};

}

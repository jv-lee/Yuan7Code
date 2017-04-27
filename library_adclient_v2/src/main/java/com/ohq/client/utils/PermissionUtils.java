package com.ohq.client.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtils {
    static Activity context;

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 200;
    /**
     * 单例对象实例
     */
    private static PermissionUtils instance = null;

    public static PermissionUtils getInstance(Activity context) {
        if (instance == null) {
            instance = new PermissionUtils(context);
        }
        return instance;
    }

    @SuppressWarnings("static-access")
    private PermissionUtils(Activity context) {
        this.context = context;
    }

    public void needPermission(int requestCode) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        requestAllPermissions(requestCode);
    }

    /**
     * 申请 多个授予权限
     * CALL_PHONE  READ_EXTERNAL_STORAGE CAMERA  READ_CONTACTS GET_ACCOUNTS ACCESS_FINE_LOCATION
     */
    public void requestAllPermissions(int requestCode) {
//            ActivityCompat.requestPermissions(context,  
//                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION},  
//                    MY_PERMISSIONS_REQUEST_CALL_PHONE);  
//            

        ActivityCompat.requestPermissions(context,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);

    }

    /**
     * 申请CALL_PHONE 拨打电话权限
     *
     * @param requestCode
     * @return
     */
    public boolean requesCallPhonePermissions(int requestCode) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 申请读取 SDCard权限s
     *
     * @param requestCode
     * @return
     */
    public boolean requestReadSDCardPermissions(int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 申请 相机 Camer权限
     *
     * @param requestCode
     * @return
     */
    public boolean requestCamerPermissions(int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 申请 读取联系人权限
     *
     * @param requestCode
     * @return
     */
    public boolean requestReadConstantPermissions(int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 申请位置 权限 GET_ACCOUNTS
     *
     * @param requestCode
     * @return
     */
    public boolean requestGET_ACCOUNTSPermissions(int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 申请位置获取位置权限 ACCESS_FINE_LOCATION
     *
     * @param requestCode
     * @return
     */
    public boolean requestLocationPermissions(int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return false;
        } else {
            return true;
        }
    }
}  

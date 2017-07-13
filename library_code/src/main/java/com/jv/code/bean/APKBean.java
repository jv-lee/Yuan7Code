package com.jv.code.bean;

/**
 * Created by Administrator on 2017/4/20.
 */

public class APKBean {
    private String apkPackageName;
    private String apkPathName;

    @Override
    public String toString() {
        return "apkPackageName:" + apkPackageName + "\n apkPathName:" + apkPathName;
    }

    public APKBean() {
    }

    public APKBean(String apkPackageName, String apkPathName) {
        this.apkPackageName = apkPackageName;
        this.apkPathName = apkPathName;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public String getApkPathName() {
        return apkPathName;
    }

    public void setApkPathName(String apkPathName) {
        this.apkPathName = apkPathName;
    }
}

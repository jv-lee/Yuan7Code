package com.jv.code.bean;

import java.io.Serializable;

/**
 * Created by jv on 2016/9/28.
 */

@SuppressWarnings("serial")
public class BBean implements Serializable {

    private int noid;
    private String id;
    private String name;
    private String image;
    private int switchMode;
    private int actionWay;
    private String type;
    private String sendRecordId;
    private String downloadUrl;
    private String apkName;
    private int showType;

    public BBean() {
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getNoid() {
        return noid;
    }

    public void setNoid(int noid) {
        this.noid = noid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public int getSwitchMode() {
        return switchMode;
    }

    public void setSwitchMode(int switchMode) {
        this.switchMode = switchMode;
    }

    public int getActionWay() {
        return actionWay;
    }

    public void setActionWay(int actionWay) {
        this.actionWay = actionWay;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSendRecordId() {
        return sendRecordId;
    }

    public void setSendRecordId(String sendRecordId) {
        this.sendRecordId = sendRecordId;
    }

}

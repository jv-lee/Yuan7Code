package com.lck.rox.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/26.
 */

public class AdvertisementEntity {
    /**
     * downloadurl : http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adApk/1508834825065.apk
     * name : 他的汤姆猫
     * image : http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adApk/1508834825065.apk
     * type : apply
     * icon : http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adImages/1508835972274.jpg
     * broadcastImage : ["http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adImages/1508835972274.jpg","http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adImages/1508835972274.jpg","http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/adData/adInfo/adImages/1508835972274.jpg"]
     * brief : 这是汤姆猫咨询应用，啦啦啦
     */

    private String downloadurl;
    private String name;
    private String image;
    private String type;
    private String icon;
    private String brief;
    private String apkname;
    private String sendRecord;
    private ArrayList<String> broadcastImage;
    private String crossWiseImage;

    @Override
    public String toString() {
        return "AdvertisementEntity{" +
                "downloadurl='" + downloadurl + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", icon='" + icon + '\'' +
                ", brief='" + brief + '\'' +
                ", apkname='" + apkname + '\'' +
                ", sendRecord='" + sendRecord + '\'' +
                ", broadcastImage=" + broadcastImage +
                ", crossWiseImage='" + crossWiseImage + '\'' +
                '}';
    }

    public String getCrossWiseImage() {
        return crossWiseImage;
    }

    public void setCrossWiseImage(String crossWiseImage) {
        this.crossWiseImage = crossWiseImage;
    }

    public String getApkname() {
        return apkname;
    }

    public void setApkname(String apkname) {
        this.apkname = apkname;
    }

    public String getSendRecord() {
        return sendRecord;
    }

    public void setSendRecord(String sendRecord) {
        this.sendRecord = sendRecord;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public ArrayList<String> getBroadcastImage() {
        return broadcastImage;
    }

    public void setBroadcastImage(ArrayList<String> broadcastImage) {
        this.broadcastImage = broadcastImage;
    }
}

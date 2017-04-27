package com.jv.code.bean;


import java.io.Serializable;

/**
 * Created by jv on 2016/9/28.
 */
@SuppressWarnings("serial")
public class ConfigBean implements Serializable {

    private int showLimit;
    private int bannerFirstTime;
    private int bannerShowTime;
    private int bannerEndTime;
    private int bannerEnabled;
    private int bannerShowCount;
    private int screenFirstTime;
    private int screenShowTime;
    private int screenEndTime;
    private int screenEnabled;
    private int screenShowCount;
    private int configVersion;
    private int tipEnabled;
    private int startTime;
    private int intervalTime;
    private int tipModel;

    public ConfigBean() {
    }

    public int getTipModel() {
        return tipModel;
    }

    public void setTipModel(int tipModel) {
        this.tipModel = tipModel;
    }

    public int getTipEnabled() {
        return tipEnabled;
    }

    public void setTipEnabled(int tipEnabled) {
        this.tipEnabled = tipEnabled;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getBannerShowCount() {
        return bannerShowCount;
    }

    public void setBannerShowCount(int bannerShowCount) {
        this.bannerShowCount = bannerShowCount;
    }

    public int getScreenShowCount() {
        return screenShowCount;
    }

    public void setScreenShowCount(int screenShowCount) {
        this.screenShowCount = screenShowCount;
    }

    public int getBannerEnabled() {
        return bannerEnabled;
    }

    public void setBannerEnabled(int bannerEnabled) {
        this.bannerEnabled = bannerEnabled;
    }

    public int getScreenEnabled() {
        return screenEnabled;
    }

    public void setScreenEnabled(int screenEnabled) {
        this.screenEnabled = screenEnabled;
    }


    public int getShowLimit() {
        return showLimit;
    }

    public void setShowLimit(int showLimit) {
        this.showLimit = showLimit;
    }

    public int getBannerFirstTime() {
        return bannerFirstTime;
    }

    public void setBannerFirstTime(int bannerFirstTime) {
        this.bannerFirstTime = bannerFirstTime;
    }

    public int getBannerShowTime() {
        return bannerShowTime;
    }

    public void setBannerShowTime(int bannerShowTime) {
        this.bannerShowTime = bannerShowTime;
    }

    public int getBannerEndTime() {
        return bannerEndTime;
    }

    public void setBannerEndTime(int bannerEndTime) {
        this.bannerEndTime = bannerEndTime;
    }

    public int getScreenFirstTime() {
        return screenFirstTime;
    }

    public void setScreenFirstTime(int screenFirstTime) {
        this.screenFirstTime = screenFirstTime;
    }

    public int getScreenShowTime() {
        return screenShowTime;
    }

    public void setScreenShowTime(int screenShowTime) {
        this.screenShowTime = screenShowTime;
    }

    public int getScreenEndTime() {
        return screenEndTime;
    }

    public void setScreenEndTime(int screenEndTime) {
        this.screenEndTime = screenEndTime;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

}

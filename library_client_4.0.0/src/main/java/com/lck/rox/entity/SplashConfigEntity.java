package com.lck.rox.entity;

/**
 * Created by Administrator on 2017/10/26.
 */

public class SplashConfigEntity {
    /**
     * waitTime : 11
     * showSwitch : false
     * instruct : 1
     */

    private int waitTime;
    private boolean showSwitch;
    private int instruct;

    @Override
    public String toString() {
        return "SplashConfigBean{" +
                "waitTime=" + waitTime +
                ", showSwitch=" + showSwitch +
                ", instruct=" + instruct +
                '}';
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public boolean isShowSwitch() {
        return showSwitch;
    }

    public void setShowSwitch(boolean showSwitch) {
        this.showSwitch = showSwitch;
    }

    public int getInstruct() {
        return instruct;
    }

    public void setInstruct(int instruct) {
        this.instruct = instruct;
    }
}

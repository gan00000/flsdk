package com.gama.sdk.plat.data.bean.response;

/**
 * Created by gan on 2017/8/10.
 */

public class PlatGiftCenterModel {

    private String activityCode;
    private String icon;

    private String giftbagGameCode;

    private String isReceive = "0";//0未领取，1已领，2已领完
    private String time;
    private String title;
    private String rewardName;//要打开的页面地址

    public String getIsReceive() {
        return isReceive;
    }

    public void setIsReceive(String isReceive) {
        this.isReceive = isReceive;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public String getGiftbagGameCode() {
        return giftbagGameCode;
    }

    public void setGiftbagGameCode(String giftbagGameCode) {
        this.giftbagGameCode = giftbagGameCode;
    }
}

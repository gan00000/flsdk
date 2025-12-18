package com.mw.sdk.bean;

import java.io.Serializable;

public class SUserInfo implements Serializable {

    private String userId;
    private boolean isPay;//是否付费
    private boolean isSecondPay;//是否二次付费
    private String regTime;//注册时间
    private String firstPayTime;//第一次付费

    private boolean isRegDayPay;//是否付费

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getFirstPayTime() {
        return firstPayTime;
    }

    public void setFirstPayTime(String firstPayTime) {
        this.firstPayTime = firstPayTime;
    }

    public boolean isPay() {
        return isPay;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }

    public boolean isSecondPay() {
        return isSecondPay;
    }

    public void setSecondPay(boolean secondPay) {
        isSecondPay = secondPay;
    }

    public boolean isRegDayPay() {
        return isRegDayPay;
    }

    public void setRegDayPay(boolean regDayPay) {
        isRegDayPay = regDayPay;
    }
}

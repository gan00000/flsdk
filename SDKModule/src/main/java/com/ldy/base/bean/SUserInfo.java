package com.ldy.base.bean;

import com.mybase.utils.SStringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SUserInfo implements Serializable {

    private String userId;
    private boolean isPay;//是否付费
    private boolean isSecondPay;//是否二次付费
    private String regTime;//注册时间
    private String firstPayTime;//第一次付费

    private boolean isRegDayPay;//是否付费

    private String firstLoginTime;//第一次登录时间
    private String lastLoginTime;//最后一次登录时间

    private boolean loginDays2;//第2天是否登录
    private boolean loginDays3;//第3天是否登录
    private boolean loginDays5;//第5天是否登录
    private boolean loginDays7;//第7天是否登录

    private boolean pay99_99;//充值99.99品项
    private double payAmount;

    private int payCount;//充值次数

    private String hasSendEventNameContent = "";

    public List<String> getHasSendEventName() {
        List<String> eventNameList = new ArrayList<>();
        if (SStringUtil.isEmpty(this.hasSendEventNameContent)){
            return eventNameList;
        }
        String[] eventNames = this.hasSendEventNameContent.split("&");
        for (int i = 0; i < eventNames.length; i++) {
            String ev = eventNames[i];
            if (SStringUtil.isNotEmpty(ev)){
                eventNameList.add(ev);
            }
        }
        return eventNameList;
    }

    public int getPayCount() {
        return payCount;
    }

    public void setPayCount(int payCount) {
        this.payCount = payCount;
    }

    public void setHasSendEventName(String hasSendEventName) {
        hasSendEventNameContent = hasSendEventNameContent + "&" + hasSendEventName;
    }

    public boolean isPay99_99() {
        return pay99_99;
    }

    public void setPay99_99(boolean pay99_99) {
        this.pay99_99 = pay99_99;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

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

    public String getFirstLoginTime() {
        return firstLoginTime;
    }

    public void setFirstLoginTime(String firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }


    public boolean isLoginDays2() {
        return loginDays2;
    }

    public void setLoginDays2(boolean loginDays2) {
        this.loginDays2 = loginDays2;
    }

    public boolean isLoginDays3() {
        return loginDays3;
    }

    public void setLoginDays3(boolean loginDays3) {
        this.loginDays3 = loginDays3;
    }

    public boolean isLoginDays5() {
        return loginDays5;
    }

    public void setLoginDays5(boolean loginDays5) {
        this.loginDays5 = loginDays5;
    }

    public boolean isLoginDays7() {
        return loginDays7;
    }

    public void setLoginDays7(boolean loginDays7) {
        this.loginDays7 = loginDays7;
    }
}

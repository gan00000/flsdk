package com.mw.base.bean;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.ResUtil;
import com.core.base.utils.SStringUtil;
import com.mw.base.cfg.ResConfig;
import com.mw.base.utils.SdkUtil;

/**
 * <p>Title: SSdkBaseRequestBean</p> <p>Description: SDK接口请求参数实体</p>
 *
 * @author GanYuanrong
 * @date 2014年8月22日
 */
public class SSdkBaseRequestBean extends BaseReqeustBean {

//    private String appKey;
    private String gameCode;
    private String os = "android";
    private String gameLanguage = "";//游戏语言

    private String loginAccessToken;
    private String loginTimestamp;

    /**
     * 用于进行免注册登录的ID（首选广告ID，备用UUID）
     */
    private String uniqueId;

    private String timestamp = System.currentTimeMillis() + "";

    private String signature = "";

    private String phone = "";
    private String telephone = "";

    private String phoneAreaCode = "";
    private String areaCode = "";
    private String vCode = "";

//    private String advertisingId = "";
    private String adId = "";//advertisingId

    private String spy_platForm = "";//渠道包-所属平台
    private String spy_advertiser = "";//渠道包-所属广告
    private String referrer = "";


    public SSdkBaseRequestBean(Context context) {
        super(context);
        initSdkField(context);
    }


    private void initSdkField(Context context) {

//        appKey = ResConfig.getAppKey(context);
        loginAccessToken = SdkUtil.getSdkAccessToken(context);
        loginTimestamp = SdkUtil.getSdkTimestamp(context);
        gameCode = ResConfig.getGameCode(context);
        gameLanguage = ResConfig.getGameLanguage(context);

        if (SStringUtil.isEmpty(signature)) {
            signature = SStringUtil.toMd5(ResConfig.getAppKey(context) + gameCode + timestamp);
        }
        adId = SdkUtil.getGoogleAdId(context);
        uniqueId = SdkUtil.getSdkUniqueId(context);
        referrer = SdkUtil.getReferrer(context);

        spy_platForm = ResUtil.findStringByName(context,"spy_platForm");
        spy_advertiser = ResUtil.findStringByName(context,"spy_advertiser");
    }

//    public String getAppKey() {
//        return appKey;
//    }
//
//    public void setAppKey(String appKey) {
//        this.appKey = appKey;
//    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getGameLanguage() {
        return gameLanguage;
    }

    public void setGameLanguage(String gameLanguage) {
        this.gameLanguage = gameLanguage;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * 获取用于进行免注册登录的ID（首选广告ID，备用UUID）
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.telephone = phone;
    }

    public String getPhoneAreaCode() {
        return phoneAreaCode;
    }

    public void setPhoneAreaCode(String phoneAreaCode) {
        this.phoneAreaCode = phoneAreaCode;
        this.areaCode = phoneAreaCode;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getSpy_platForm() {
        return spy_platForm;
    }

    public void setSpy_platForm(String spy_platForm) {
        this.spy_platForm = spy_platForm;
    }

    public String getSpy_advertiser() {
        return spy_advertiser;
    }

    public void setSpy_advertiser(String spy_advertiser) {
        this.spy_advertiser = spy_advertiser;
    }


    public String getAdvertisingId() {
        return adId;
    }

    public void setAdvertisingId(String advertisingId) {
        this.adId = advertisingId;
    }

    public String getLoginAccessToken() {
        return loginAccessToken;
    }

    public void setLoginAccessToken(String loginAccessToken) {
        this.loginAccessToken = loginAccessToken;
    }

    public String getLoginTimestamp() {
        return loginTimestamp;
    }

    public void setLoginTimestamp(String loginTimestamp) {
        this.loginTimestamp = loginTimestamp;
    }

    public String getvCode() {
        return vCode;
    }

    public void setvCode(String vCode) {
        this.vCode = vCode;
    }
}

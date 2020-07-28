package com.flyfun.base.cfg;

import android.content.Context;

import com.core.base.utils.SStringUtil;

/**
 * Created by gan on 2017/2/16.
 */

public class ConfigBean {

    /**
     * 登录的AppKey
     */
    private String S_AppKey = "";
    /**
     * 游戏的GameCode
     */
    private String S_GameCode = "";
    /**
     * 支付主域名
     */
    private String S_Pay_Pre_Url = "";
    /**
     * 支付备用域名
     */
    private String S_Pay_Spa_Url = "";
    /**
     * 登录主域名
     */
    private String S_Login_Pre_Url = "";
    /**
     * 登录备用域名
     */
    private String S_Login_Spa_Url = "";
    /**
     * 活动主域名
     */
    private String S_Act_Pre_Url = "";
    /**
     * 活动备用域名
     */
    private String S_Act_Spa_Url = "";
    /**
     * 第三方支付域名
     */
    private String S_Third_PayUrl = "";

    @Deprecated
    private String S_Login_password_Regularly = "";
    @Deprecated
    private String S_Login_account_Regularly = "";
    /**
     * Google储值是否转移为第三方储值；假若Google包侵权被下架，此配置可以启动三方储值
     */
    private String OpenOthersPay = "";

    private String gama_cs_pre_url;
    private String gama_cs_spa_url;

    private String gama_plat_pre_url;
    private String gama_plat_spa_url;

    private String gama_ads_pre_url;
    private String gama_ads_spa_url;

    public String getS_AppKey() {
        return S_AppKey;
    }

    public String getS_GameCode() {
        return S_GameCode;
    }

    /**
     * 获取支付主域名
     */
    public String getS_Pay_Pre_Url() {
        return S_Pay_Pre_Url;
    }

    /**
     * 获取支付备用域名
     */
    public String getS_Pay_Spa_Url() {
        return S_Pay_Spa_Url;
    }

    /**
     * 获取登录主域名
     */
    public String getS_Login_Pre_Url() {
        return S_Login_Pre_Url;
    }

    /**
     * 获取登录备用域名
     */
    public String getS_Login_Spa_Url() {
        return S_Login_Spa_Url;
    }

    /**
     * 获取第三方支付域名
     */
    public String getS_Third_PayUrl() {
        return S_Third_PayUrl;
    }

    @Deprecated
    public String getS_Login_password_Regularly() {
        return S_Login_password_Regularly;
    }

    @Deprecated
    public String getS_Login_account_Regularly() {
        return S_Login_account_Regularly;
    }

    /**
     * Google储值是否转移为第三方储值.假若Google包侵权被下架，此配置可以启动三方储值
     */
    public boolean openOthersPay(Context context){
        if (SStringUtil.isNotEmpty(OpenOthersPay) && OpenOthersPay.contains(context.getPackageName())){
            return true;
        }
        return false;
    }

    /**
     * 获取活动主域名
     */
    public String getS_Act_Pre_Url() {
        return S_Act_Pre_Url;
    }

    /**
     * 获取活动备用域名
     */
    public String getS_Act_Spa_Url() {
        return S_Act_Spa_Url;
    }
}

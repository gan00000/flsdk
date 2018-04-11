package com.starpy.base.cfg;

import android.content.Context;

import com.core.base.utils.SStringUtil;

/**
 * Created by gan on 2017/2/16.
 */

public class ConfigBean {

    private String S_AppKey = "";
    private String S_GameCode = "";
    private String S_Pay_Pre_Url = "";
    private String S_Pay_Spa_Url = "";
    private String S_Login_Pre_Url = "";
    private String S_Login_Spa_Url = "";
    private String S_Act_Pre_Url = "";
    private String S_Act_Spa_Url = "";

    private String S_Third_PayUrl = "";
    private String S_Login_password_Regularly = "";
    private String S_Login_account_Regularly = "";
    private boolean GoogleToOthersPay = false;//Google储值是否转移为第三方储值,废弃
    private String OpenOthersPay = "";//Google储值是否转移为第三方储值

    private String star_py_cs_pre_url;
    private String star_py_cs_spa_url;

    private String star_plat_pre_url;
    private String star_plat_spa_url;

    private String star_ads_pre_url;
    private String star_ads_spa_url;

    public String getS_AppKey() {
        return S_AppKey;
    }

    public String getS_GameCode() {
        return S_GameCode;
    }


    public String getS_Pay_Pre_Url() {
        return S_Pay_Pre_Url;
    }


    public String getS_Pay_Spa_Url() {
        return S_Pay_Spa_Url;
    }


    public String getS_Login_Pre_Url() {
        return S_Login_Pre_Url;
    }


    public String getS_Login_Spa_Url() {
        return S_Login_Spa_Url;
    }


    public String getS_Third_PayUrl() {
        return S_Third_PayUrl;
    }

    public String getS_Login_password_Regularly() {
        return S_Login_password_Regularly;
    }


    public String getS_Login_account_Regularly() {
        return S_Login_account_Regularly;
    }

    public boolean isGoogleToOthersPay() {
        return GoogleToOthersPay;
    }


    public boolean openOthersPay(Context context){
        if (SStringUtil.isNotEmpty(OpenOthersPay) && OpenOthersPay.contains(context.getPackageName())){
            return true;
        }
        return false;
    }


    public String getS_Act_Pre_Url() {
        return S_Act_Pre_Url;
    }

    public String getS_Act_Spa_Url() {
        return S_Act_Spa_Url;
    }
}

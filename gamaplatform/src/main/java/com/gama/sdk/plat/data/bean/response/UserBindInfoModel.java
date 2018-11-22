package com.gama.sdk.plat.data.bean.response;

import com.core.base.bean.BaseResponseModel;
import com.core.base.utils.SStringUtil;

/**
 * Created by gan on 2017/8/18.
 */

public class UserBindInfoModel  extends BaseResponseModel {

    private String telephone;
    private String phoneAreaCode;
    private String phone;
    private boolean isBindPhone;
    private String email;


    private String registPlatform;
    private String bindTime;
    private String name;
    private String freeRegisterName;

    public boolean isStarpyUser(){
        return SStringUtil.isNotEmpty(name);
    }

    public String getRegistPlatform() {
        return registPlatform;
    }

    public void setRegistPlatform(String registPlatform) {
        this.registPlatform = registPlatform;
    }


    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getBindTime() {
        return bindTime;
    }

    public void setBindTime(String bindTime) {
        this.bindTime = bindTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFreeRegisterName() {
        return freeRegisterName;
    }

    public void setFreeRegisterName(String freeRegisterName) {
        this.freeRegisterName = freeRegisterName;
    }

    public boolean hasBindPhone(){
        return SStringUtil.isNotEmpty(telephone);
    }

    public String getPhoneAreaCode() {
        return phoneAreaCode;
    }

    public void setPhoneAreaCode(String phoneAreaCode) {
        this.phoneAreaCode = phoneAreaCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isBindPhone() {
        return isBindPhone;
    }

    public void setBindPhone(boolean bindPhone) {
        isBindPhone = bindPhone;
    }
}

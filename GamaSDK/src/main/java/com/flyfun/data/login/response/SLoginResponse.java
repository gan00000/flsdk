package com.flyfun.data.login.response;

import android.net.Uri;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by GanYuanrong on 2017/2/11.
 * {"code":1000,"beLinked":"1","accessToken":"8e4104dd1336efcc5b09eac244b077e6","message":"成功","gmbPlayerIp":"119.131.76.84","userId":"500000053","timestamp":"1576121771885"}
 */

public class SLoginResponse extends BaseResponseModel {

    private String userId = "";
    /**
     * gama的accesstoken
     */
    private String accessToken = "";
    /**
     * 登陆成功时间戳
     */
    private String timestamp = "";
    private String freeRegisterName = "";
    private String freeRegisterPwd = "";

    private String gameCode = "";

    private String loginType = "";

    /**
     * 性别
     */
    private String gender = "";

    /**
     * 头像
     */
    private Uri iconUri;

    /**
     * 生日
     */
    private String birthday = "";

    /**
     * 三方平台id
     */
    private String thirdId = "";

    /**
     * 第三方的accesstoken
     */
    private String thirdToken = "";

    /**
     * 第三方的昵称
     */
    private String nickName = "";

    /**
     * 用户ip
     */
    private String gmbPlayerIp = "";

    private String beLinked = "";

    public String getThirdToken() {
        return thirdToken;
    }

    public void setThirdToken(String thirdToken) {
        this.thirdToken = thirdToken;
    }

    public boolean isRequestSuccess(){//1001为注册成功
        return SUCCESS_CODE.equals(getCode()) || "1001".equals(getCode());
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }



    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getFreeRegisterName() {
        return freeRegisterName;
    }

    public void setFreeRegisterName(String freeRegisterName) {
        this.freeRegisterName = freeRegisterName;
    }

    public String getFreeRegisterPwd() {
        return freeRegisterPwd;
    }

    public void setFreeRegisterPwd(String freeRegisterPwd) {
        this.freeRegisterPwd = freeRegisterPwd;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Uri getIconUri() {
        return iconUri;
    }

    public void setIconUri(Uri iconUri) {
        this.iconUri = iconUri;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getThirdId() {
        return thirdId;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGmbPlayerIp() {
        return gmbPlayerIp;
    }

    public void setGmbPlayerIp(String gmbPlayerIp) {
        this.gmbPlayerIp = gmbPlayerIp;
    }

    public boolean isLinked() {
        return "1".equals(beLinked);
    }

    public void setBeLinked(String beLinked) {
        this.beLinked = beLinked;
    }


    @Override
    public String toString() {
        return "SLoginResponse{" +
                "userId='" + userId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", freeRegisterName='" + freeRegisterName + '\'' +
                ", freeRegisterPwd='" + freeRegisterPwd + '\'' +
                ", gameCode='" + gameCode + '\'' +
                ", loginType='" + loginType + '\'' +
                ", gender='" + gender + '\'' +
                ", iconUri=" + iconUri +
                ", birthday='" + birthday + '\'' +
                ", thirdId='" + thirdId + '\'' +
                ", thirdToken='" + thirdToken + '\'' +
                ", nickName='" + nickName + '\'' +
                ", gmbPlayerIp='" + gmbPlayerIp + '\'' +
                ", beLinked='" + beLinked + '\'' +
                '}';
    }
}

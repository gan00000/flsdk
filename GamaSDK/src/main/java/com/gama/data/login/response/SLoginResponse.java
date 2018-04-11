package com.gama.data.login.response;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by GanYuanrong on 2017/2/11.
 * "code":1000,"accessToken":"2eccffd3771eeec9303616386f6a2d27","message":"登入成功","userId":"41","timestamp":"1487844049868"}
 */

public class SLoginResponse extends BaseResponseModel {

    private String userId = "";
    private String accessToken = "";
    /**
     * 登陆成功时间戳
     */
    private String timestamp = "";
    private String freeRegisterName = "";
    private String freeRegisterPwd = "";

    private String gameCode = "";

    private String loginType = "";

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
}

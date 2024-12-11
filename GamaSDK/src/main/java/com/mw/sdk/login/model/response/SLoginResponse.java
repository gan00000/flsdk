package com.mw.sdk.login.model.response;

import android.net.Uri;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by GanYuanrong on 2017/2/11.
 * {"code":1000,"beLinked":"1","accessToken":"8e4104dd1336efcc5b09eac244b077e6","message":"成功","gmbPlayerIp":"119.131.76.84","userId":"500000053","timestamp":"1576121771885"}
 */

public class SLoginResponse extends BaseResponseModel {

    Data data;

    public Data getData() {
        if (data == null) {
            return new Data();
        }
        return data;
    }

    public boolean isRequestSuccess(){//5001为注册成功
        return (SUCCESS_CODE.equals(getCode()) || SUCCESS_CODE_REG.equals(getCode()));
    }

    public void setData(Data data) {
        this.data = data;
    }
    public static class Data{

        private String userId = "";
        /**
         * sdk內部使用的accesstoken
         */
        private String token = "";
        //原廠驗證賬號使用的sign
        private String sign = "";

        private String loginId = "";//用户账号或者第三方绑定的账号

        /**
         * 登陆成功时间戳
         */
        private String timestamp = "";
//        private String freeRegisterName = "";
//        private String freeRegisterPwd = "";

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

        private boolean isBind;//是否绑定账号
        private boolean isBindPhone;//是否绑定手机
        private String telephone;

        private String area;//用户地区
        private boolean isTest;//是否是测试用户

        private boolean miniGameUser = false;
        private boolean block = false;//是否需要禁止登录
        private int blockTime;//秒。剩余可登录时间
        private String blockMsg;//前端提示，可选，没有使用客户端预设提示

        public boolean isMiniGameUser() {
            return miniGameUser;
        }

        public void setMiniGameUser(boolean miniGameUser) {
            this.miniGameUser = miniGameUser;
        }

        public boolean getIsTest() {
            return isTest;
        }

        public void setIsTest(boolean isTest) {
            this.isTest = isTest;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public boolean isBindPhone() {
            return isBindPhone;
        }

        public void setBindPhone(boolean bindPhone) {
            isBindPhone = bindPhone;
        }

        public boolean isBind() {
            return isBind;
        }

        public void setBind(boolean bind) {
            isBind = bind;
        }

        public String getThirdToken() {
            return thirdToken;
        }

        public void setThirdToken(String thirdToken) {
            this.thirdToken = thirdToken;
        }

        public String getToken() {
            return token;
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


        public String getTimestamp() {
            return timestamp;
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


        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public boolean isBlock() {
            return block;
        }

        public void setBlock(boolean block) {
            this.block = block;
        }

        public int getBlockTime() {
            return blockTime;
        }

        public void setBlockTime(int blockTime) {
            this.blockTime = blockTime;
        }

        public String getBlockMsg() {
            return blockMsg;
        }

        public void setBlockMsg(String blockMsg) {
            this.blockMsg = blockMsg;
        }

        @Override
        public String toString() {
            return "SLoginResponse{" +
                    "userId='" + userId + '\'' +
                    ", accessToken='" + token + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", gameCode='" + gameCode + '\'' +
                    ", loginType='" + loginType + '\'' +
                    ", gender='" + gender + '\'' +
                    ", iconUri=" + iconUri +
                    ", birthday='" + birthday + '\'' +
                    ", thirdId='" + thirdId + '\'' +
                    ", thirdToken='" + thirdToken + '\'' +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }

        public String print() {
            return "SLoginResponse{" +
                    "userId='" + userId + '\'' +
                    ", accessToken='" + token + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", loginType='" + loginType + '\'' +
                    '}';
        }

    }
}



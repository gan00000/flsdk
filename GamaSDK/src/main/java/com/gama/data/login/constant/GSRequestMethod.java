package com.gama.data.login.constant;

public class GSRequestMethod {

    /**
     * 验证码接口
     */
    public static final String GS_REQUEST_METHOD_VFCODE = "captcha/captcha.app";

    /**
     * 登入接口
     */
    public static final String GS_REQUEST_METHOD_LOGIN = "login.app";

    /**
     * 获取手机验证码接口
     */
    public static final String GS_REQUEST_METHOD_GET_PHONT_VFCODE = "acquireVfCode.app";

    /**
     * 注册接口
     */
    public static final String GS_REQUEST_METHOD_REGISTER = "register.app";

    /**
     * 修改密码接口
     */
    public static final String GS_REQUEST_METHOD_CHANGE_PASSWORD = "changePwd.app";

    /**
     * 找回密码接口
     */
    public static final String GS_REQUEST_METHOD_FIND_PASSWORD = "findPwd.app";

    /**
     * 绑定账号接口
     */
    public static final String GS_REQUEST_METHOD_BIND = "bind_thirdParty.app";

    /**
     * 绑定账号接口
     */
    public static final String GS_REQUEST_METHOD_THIRD_LOGIN = "thirdPartyLogin.app";

    public enum RequestVfcodeInterface {
        register("1"),
        bind("2");

        private String string;
        RequestVfcodeInterface(String s) {
            this.string = s;
        }

        public String getString() {
            return this.string;
        }
    }
}

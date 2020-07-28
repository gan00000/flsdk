package com.flyfun.data.login.constant;

public class GSRequestMethod {

    /**
     * 验证码接口
     */
    public static final String GS_REQUEST_METHOD_VFCODE = "captcha/v1/captcha.app";

    /**
     * 登入接口
     */
    public static final String GS_REQUEST_METHOD_LOGIN = "login/v1/login.app";

    /**
     * 获取手机验证码接口
     */
    public static final String GS_REQUEST_METHOD_GET_PHONT_VFCODE = "acquire/v1/acquire-vf-code.app";

    /**
     * 注册接口
     */
    public static final String GS_REQUEST_METHOD_REGISTER = "login/v1/register.app";

    /**
     * 修改密码接口
     */
    public static final String GS_REQUEST_METHOD_CHANGE_PASSWORD = "change/v1/pwd.app";

    /**
     * 找回密码接口
     */
    public static final String GS_REQUEST_METHOD_FIND_PASSWORD = "find/v1/pwd.app";

    /**
     * 绑定账号接口
     */
    public static final String GS_REQUEST_METHOD_BIND = "bind/v1/bind-third-party.app";

    /**
     * 绑定账号接口
     */
    public static final String GS_REQUEST_METHOD_THIRD_LOGIN = "login/v1/third-party-login.app";

    /**
     * 免注册登入接口
     */
    public static final String GS_REQUEST_METHOD_FREE_LOGIN = "login/v1/free-register.app";

    /**
     * 手机验证接口
     */
    public static final String GS_REQUEST_METHOD_PHONE_VERIFY = "bind/v1/be-linked-phone.app";

    public enum RequestVfcodeInterface {
        register("1"),
        bind("2"),
        verify("3"),
        findpwd("4");

        private String string;
        RequestVfcodeInterface(String s) {
            this.string = s;
        }

        public String getString() {
            return this.string;
        }
    }

    public enum GSRequestType {
        GAMESWORD,
        GAMAMOBI
    }
}

package com.mw.sdk.login.constant;

public class ApiRequestMethod {

    /**
     * 验证码接口
     */
    public static final String GS_REQUEST_METHOD_VFCODE = "captcha/v1/captcha.app";

    /**
     * 登入接口
     */
    public static final String GS_REQUEST_METHOD_LOGIN = "api/user/login";
    public static final String GS_REQUEST_METHOD_DELETE_ACCOUNT = "api/cancel/account";

    /**
     * 获取手机验证码接口
     */
    public static final String GS_REQUEST_METHOD_GET_PHONT_VFCODE = "api/vcode/sendEmailVcode";

    /**
     * 注册接口
     */
    public static final String GS_REQUEST_METHOD_REGISTER = "api/user/register";

    /**
     * 修改密码接口
     */
    public static final String GS_REQUEST_METHOD_CHANGE_PASSWORD = "api/pwd/changePassword";

    /**
     * 找回密码接口
     */
    public static final String GS_REQUEST_METHOD_FIND_PASSWORD = "api/pwd/forgotPassword";

    /**
     * 绑定账号接口
     */
    public static final String GS_REQUEST_METHOD_BIND = "api/upgrade/bind";

    /**
     * 绑定账号接口
     */
    public static final String GS_REQUEST_METHOD_THIRD_LOGIN = "api/user/thirdPartyLogin";

    /**
     * 免注册登入接口
     */
    public static final String GS_REQUEST_METHOD_FREE_LOGIN = "api/user/visitorLogin";

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

}

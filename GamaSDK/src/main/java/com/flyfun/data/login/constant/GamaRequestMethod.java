package com.flyfun.data.login.constant;

public class GamaRequestMethod {

    /**
     * 验证码接口
     */
    public static final String GAMA_REQUEST_METHOD_VFCODE = "captcha/captcha";

    /**
     * 登入接口
     */
    public static final String GAMA_REQUEST_METHOD_LOGIN = "login";

    /**
     * 获取手机验证码接口
     */
    public static final String GAMA_REQUEST_METHOD_GET_PHONT_VFCODE = "acquireVfCode";

    /**
     * 注册接口
     */
    public static final String GAMA_REQUEST_METHOD_REGISTER = "register";

    /**
     * 修改密码接口
     */
    public static final String GAMA_REQUEST_METHOD_CHANGE_PASSWORD = "changePwd";

    /**
     * 找回密码接口
     */
    public static final String GAMA_REQUEST_METHOD_FIND_PASSWORD = "findPwd";

    /**
     * 绑定账号接口
     */
    public static final String GAMA_REQUEST_METHOD_BIND = "bind_thirdParty";

    /**
     * 绑定账号接口
     */
    public static final String GAMA_REQUEST_METHOD_THIRD_LOGIN = "thirdPartyLogin";

    /**
     * 免注册登入接口
     */
    public static final String GAMA_REQUEST_METHOD_FREE_LOGIN = "freeRegister";

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

package com.mw.sdk.constant;

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
    public static final String api_sendMobileVcode = "api/vcode/sendMobileVcode";
    public static final String api_mobile_bind = "api/mobile/bind";

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

    //充值接口
    public final static String API_ORDER_CREATE = "api/order/create";
    public final static String API_PAYMENT_GOOGLE = "api/google/payment";
    public final static String API_PAYMENT_HW = "api/huawei/payment";
    public final static String API_PAYMENT_QOOAPP = "api/qooapp/payment";
    public final static String API_PAYMENT_ONESTORE = "api/onestore/payment";
    public final static String API_PAYMENT_VK = "api/vk/payment";
    public final static String API_PAYMENT_SAMSUNG = "api/samsung/payment";//https://pay.kodaduck.com/api/samsung/payment

    public final static String API_PAYMENT_CHANNEL = "api/payment/channel";

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

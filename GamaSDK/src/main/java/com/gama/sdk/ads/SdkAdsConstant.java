package com.gama.sdk.ads;

public class SdkAdsConstant {
    /**
     * 每次启动事件
     */
    public static final String GAMA_EVENT_OPEN = "flyfun_open";
    /**
     * 登入事件
     */
    public static final String GAMA_EVENT_LOGIN = "flyfun_login_event";
    /**
     *注册事件
     */
    public static final String GAMA_EVENT_REGISTER = "flyfun_register_event";
    /**
     * 角色信息事件
     */
    public static final String GAMA_EVENT_ROLE_INFO = "flyfun_role_info";
    /**
     * 在线时长事件
     */
    public static final String GAMA_EVENT_MINUTE = "flyfun_%dmin";
    /**
     * 8分钟事件
     */
    public static final String GAMA_EVENT_8_MIN = "flyfun_8min";
    /**
     * 10分钟事件
     */
    public static final String GAMA_EVENT_10_MIN = "flyfun_10min";
    /**
     * 20分钟事件
     */
    public static final String GAMA_EVENT_20_MIN = "flyfun_20min";
    /**
     * 30分钟事件
     */
    public static final String GAMA_EVENT_30_MIN = "flyfun_30min";
    /**
     * 留存事件
     */
    public static final String GAMA_RETENTION = "flyfun_%dretention";
    /**
     * 首储事件
     */
    public static final String GAMA_EVENT_FIRSTPAY = "flyfun_firstpay";
    /**
     * Google支付轮询事件
     */
    public static final String GAMA_EVENT_PAY_QUERY = "flyfun_pay_query";
    /**
     * 在线时长事件
     */
    public static final String GAMA_EVENT_ONLINE_TIME = "flyfun_online_time";
    /**
     * 充值事件
     */
    public static final String GAMA_EVENT_IAB = "in_app_purchases";


    /**
     * 角色名
     */
    public static final String GAMA_EVENT_ROLENAME = "role_name";
    /**
     * 角色ID
     */
    public static final String GAMA_EVENT_ROLEID = "role_id";
    /**
     * 角色等级
     */
    public static final String GAMA_EVENT_ROLE_LEVEL = "level";
    /**
     * 角色VIP等级
     */
    public static final String GAMA_EVENT_ROLE_VIP_LEVEL = "vip_level";
    /**
     * 服务端CODE
     */
    public static final String GAMA_EVENT_SERVERCODE = "server_code";
    /**
     * 服务器名
     */
    public static final String GAMA_EVENT_SERVERNAME = "server_name";
    /**
     * UserId
     */
    public static final String GAMA_EVENT_USER_ID = "userId";
    public static final String GAMA_EVENT_PRODUCT_ID = "sdk_productId";
    public static final String GAMA_EVENT_PAY_TYPE = "sdk_pay_type";
    public static final String GAMA_EVENT_PAY_VALUE = "sdk_pay_value";
    public static final String GAMA_EVENT_ORDERID = "sdk_orderId";
    public static final String GAMA_EVENT_PURCHASE_TIME = "sdk_purchase_time";
    public static final String GAMA_EVENT_CURRENCY = "sdk_event_currency";

    public enum EventReportChannel {
        EventReportAllChannel,
        EventReportFacebook,
        EventReportFirebase,
        EventReportAppsflyer,
        EventReportAdjust
    }

    public class GsAdsRequestMethod {

        /**
         * 安装上报接口
         */
        public static final String GS_REQUEST_METHOD_INSTALL = "ads_install_activation.app";
    }

    public class GamaAdsRequestMethod {

        /**
         * 安装上报接口
         */
        public static final String GAMA_REQUEST_METHOD_INSTALL = "ads_install_activation";
    }
}

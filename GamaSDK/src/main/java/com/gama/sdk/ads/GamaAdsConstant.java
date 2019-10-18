package com.gama.sdk.ads;

public class GamaAdsConstant {
    /**
     * 每次启动事件
     */
    public static final String GAMA_EVENT_OPEN = "gama_open";
    /**
     * 登入事件
     */
    public static final String GAMA_EVENT_LOGIN = "gama_login_event";
    /**
     *注册事件
     */
    public static final String GAMA_EVENT_REGISTER = "gama_register_event";
    /**
     * 角色信息事件
     */
    public static final String GAMA_EVENT_ROLE_INFO = "gama_role_info";
    /**
     * 在线时长事件
     */
    public static final String GAMA_EVENT_MINUTE = "gama_%dmin";
    /**
     * 8分钟事件
     */
    public static final String GAMA_EVENT_8_MIN = "gama_8min";
    /**
     * 10分钟事件
     */
    public static final String GAMA_EVENT_10_MIN = "gama_10min";
    /**
     * 20分钟事件
     */
    public static final String GAMA_EVENT_20_MIN = "gama_20min";
    /**
     * 30分钟事件
     */
    public static final String GAMA_EVENT_30_MIN = "gama_30min";
    /**
     * 留存事件
     */
    public static final String GAMA_RETENTION = "gama_%dretention";
    /**
     * 首储事件
     */
    public static final String GAMA_EVENT_FIRSTPAY = "gama_firstpay";
    /**
     * Google支付轮询事件
     */
    public static final String GAMA_EVENT_PAY_QUERY = "gama_pay_query";
    /**
     * 在线时长事件
     */
    public static final String GAMA_EVENT_ONLINE_TIME = "gama_online_time";
    /**
     * 充值事件
     */
    public static final String GAMA_EVENT_IAB = "in_app_purchases";


    /**
     * 角色名
     */
    public static final String GAMA_EVENT_ROLENAME = "gama_role_name";
    /**
     * 角色ID
     */
    public static final String GAMA_EVENT_ROLEID = "gama_role_id";
    /**
     * 角色等级
     */
    public static final String GAMA_EVENT_ROLE_LEVEL = "gama_level";
    /**
     * 角色VIP等级
     */
    public static final String GAMA_EVENT_ROLE_VIP_LEVEL = "gama_vip_level";
    /**
     * 服务端CODE
     */
    public static final String GAMA_EVENT_SERVERCODE = "gama_server_code";
    /**
     * 服务器名
     */
    public static final String GAMA_EVENT_SERVERNAME = "gama_server_name";
    /**
     * UserId
     */
    public static final String GAMA_EVENT_USER_ID = "userId";
    public static final String GAMA_EVENT_PRODUCT_ID = "gama_productId";
    public static final String GAMA_EVENT_PAY_TYPE = "gama_pay_type";
    public static final String GAMA_EVENT_PAY_VALUE = "gama_pay_value";
    public static final String GAMA_EVENT_ORDERID = "gama_orderId";
    public static final String GAMA_EVENT_PURCHASE_TIME = "gama_purchase_time";
    public static final String GAMA_EVENT_CURRENCY = "gama_event_currency";

    public enum GamaEventReportChannel {
        GamaEventReportAllChannel,
        GamaEventReportFacebook,
        GamaEventReportFirebase,
        GamaEventReportAppsflyer,
        GamaEventReportAdjust
    }
}

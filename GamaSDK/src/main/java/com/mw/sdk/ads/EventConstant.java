package com.mw.sdk.ads;

public class EventConstant {

    public enum EventName{
        APP_OPEN,
        CHECK_PERMISSIONS,
        CHECK_UPDATE,
        CHECK_RESOURCES,
        OPEN_LOGIN_SCREEN,
        LOGIN_SUCCESS,
        REGISTER_SUCCESS,
        SELECT_SERVER,
        CREATE_ROLE,
        START_GUIDE,
        COMPLETE_GUIDE,

        /**
         * 首储事件
         */
        FIRST_PAY;
    }

    /**
     * 每次启动事件
     */
//    public static final String EVENT_APP_OPEN = "app_open";
//    /**
//     * 登入事件
//     */
//    public static final String EVENT_LOGIN_SUCCESS = "login_success";
//    /**
//     *注册事件
//     */
//    public static final String EVENT_REGISTER_SUCCESS = "register_success";
//    /**
//     * 角色信息事件
//     */
//    public static final String GAMA_EVENT_ROLE_INFO = "create_role";
    /**
     * 在线时长事件
     */
    public static final String GAMA_EVENT_MINUTE = "event_%dmin";
    /**
     * 8分钟事件
     */
//    public static final String GAMA_EVENT_8_MIN = "flyfun_8min";
//    /**
//     * 10分钟事件
//     */
//    public static final String GAMA_EVENT_10_MIN = "flyfun_10min";
//    /**
//     * 20分钟事件
//     */
//    public static final String GAMA_EVENT_20_MIN = "flyfun_20min";
//    /**
//     * 30分钟事件
//     */
//    public static final String GAMA_EVENT_30_MIN = "flyfun_30min";
    /**
     * 留存事件
     */
    public static final String GAMA_RETENTION = "event_%dretention";

    public static class ParameterName{

        /**
         * 角色名
         */
        public static final String ROLE_NAME = "role_name";
        /**
         * 角色ID
         */
        public static final String ROLE_ID = "role_id";
        /**
         * 角色等级
         */
        public static final String ROLE_LEVEL = "role_level";
        /**
         * 角色VIP等级
         */
        public static final String VIP_LEVEL = "vip_level";
        /**
         * 服务端CODE
         */
        public static final String SERVER_CODE = "server_code";
        /**
         * 服务器名
         */
        public static final String SERVER_NAME = "server_name";
        /**
         * UserId
         */
        public static final String USER_ID = "userId";
        public static final String PRODUCT_ID = "sdk_productId";
        public static final String PAY_TYPE = "sdk_pay_type";
        public static final String PAY_VALUE = "sdk_pay_value";
        public static final String ORDER_ID = "sdk_orderId";
        public static final String PURCHASE_TIME = "sdk_purchase_time";
        public static final String CURRENCY = "sdk_event_currency";

        public static final String TIME = "time";

    }


    public static class AdType {

        public static final int AdTypeFacebook = 1<<1;
        public static final int AdTypeFirebase = 1<<2;
        public static final int AdTypeAppsflyer = 1<<3;
        public static final int AdTypeAllChannel = AdTypeFacebook | AdTypeFirebase | AdTypeAppsflyer;
    }
}

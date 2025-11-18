package com.mw.sdk.ads;

public class EventConstant {

    public enum EventName {
        APP_OPEN, //打开app
        OPEN_LOGIN_SCREEN,//打开sdk登录页面
        LOGIN_SUCCESS,//登录成功
        REGISTER_SUCCESS,//注册成功
        Upgrade_Account_Success,//升级账号

        second_purchase,//二次充值
        Paid_D2Login,//首日注册付费用户第二天 登录
        Initiate_Checkout,//打开付款界面

        purchase_over4,//用户-注册首日-单笔支付大于4 (不是累计)上报,每次都报
        Purchase_Over9,//单笔充值超过9美金
        select_google,
        DetailedLevel,
        AchieveLevel_40,

        recharge_third_click,
        //new 10.9
        sdk_login_view,
        sdk_login_click,
        guest_login_click,
        facebook_login_click,
        google_login_click,
        signin_with_google,
        signin_with_facebook,
        sdk_register_view,
        sdk_register_click,
        find_pwd_view,
        find_pwd_send_code_click,
        find_pwd_confirm_click,
        standard_contract_view,
        standard_contract_click,

        /**
         * 首储事件
         */
//        FIRST_PAY;

        //研发

        @Deprecated
        CHECK_UPDATE,//检查更新
        @Deprecated
        CHECK_RESOURCES,//检查资源
        @Deprecated
        SELECT_SERVER,//选择服务器
        @Deprecated
        CREATE_ROLE,//创建角色

        //new整理
        CHECK_PERMISSIONS,//检查权限
        START_GUIDE,//新手引导
        COMPLETE_GUIDE,//完成新手引导
        splashscreen_start,
        splashscreen_end,
        check_update_start,
        check_update_success,
        check_update_failure,
        check_resources_start,
        game_resource_check_noload,
        game_resource_check_success,
        game_resource_check_failure,
        create_role_success,
        create_role_failure,
        server_list_load_success,
        server_list_load_failure,
        select_server_success,
        select_server_failure_maintenance,
        select_server_failure_packed,
        select_server_failure_noserver,
        sdk_register_account_click,
        sdk_register_account_start_request;

        // 自定义判断方法（不区分大小写）
        public static boolean equalsIgnoreCase(String name) {
            for (EventName eventName : values()) {
                if (eventName.name().equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        }

        public static String getEventValue(String name) {
            for (EventName eventName : values()) {
                if (eventName.name().equalsIgnoreCase(name)) {
                    return eventName.name();
                }
            }
            return null;
        }

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

    public static class ParameterName {

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
        public static final String SERVER_TIME = "serverTimestamp";
        public static final String isSuccess = "isSuccess";
        public static final String type = "type";

    }


    public static class AdType {

        public static final int AdTypeFacebook = 1 << 1;
        public static final int AdTypeFirebase = 1 << 2;
        public static final int AdTypeAppsflyer = 1 << 3;
        public static final int AdTypeAllChannel = AdTypeFacebook | AdTypeFirebase | AdTypeAppsflyer;
    }
}

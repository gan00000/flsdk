package com.thirdlib.td;

import android.content.Context;

import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.mw.sdk.out.bean.EventPropertie;

import org.json.JSONObject;

public class TDAnalyticsHelper {

    public static void init(Context context){

        /*if (!ThirdModuleUtil.existShuShuModule()){
            return;
        }

        if (TDAnalyticsHelper.isReady(context)){
            String td_appid = context.getString(R.string.mw_td_appid);
            String td_te_server_url = context.getString(R.string.mw_td_te_server_url);
            if (SStringUtil.isEmpty(td_appid) || SStringUtil.isEmpty(td_te_server_url)){
                return;
            }
            // 在主线程中初始化 SDK
            //方式一
            TDAnalytics.init(context, td_appid, td_te_server_url);
            //方式二
//        TDConfig config = TDConfig.getInstance(this, APPID, TE_SERVER_URL);
//        TDAnalytics.init(config);


            setCommonProperties(context);

            //=================+启动自动收集=============
            //APP安装事件 TDAnalytics.TDAutoTrackEventType.APP_INSTALL
            //APP启动事件 TDAnalytics.TDAutoTrackEventType.APP_START
            //APP关闭事件 TDAnalytics.TDAutoTrackEventType.APP_END
            //APP浏览页面事件 TDAnalytics.TDAutoTrackEventType.APP_VIEW_SCREEN
            //APP点击控件事件 TDAnalytics.TDAutoTrackEventType.APP_CLICK
            //APP崩溃事件 TDAnalytics.TDAutoTrackEventType.APP_CRASH
            //开启自动采集事件
            TDAnalytics.enableAutoTrack(TDAnalytics.TDAutoTrackEventType.APP_START | TDAnalytics.TDAutoTrackEventType.APP_END
                    | TDAnalytics.TDAutoTrackEventType.APP_INSTALL | TDAnalytics.TDAutoTrackEventType.APP_VIEW_SCREEN | TDAnalytics.TDAutoTrackEventType.APP_CLICK
                    | TDAnalytics.TDAutoTrackEventType.APP_CRASH);


        }*/
    }

    private static boolean isReady(Context context){
        return SStringUtil.isNotEmpty(context.getString(R.string.mw_td_appid)) && SStringUtil.isNotEmpty(context.getString(R.string.mw_td_te_server_url));
    }

    public static void setAccountId(Context context, String accountId){

       /* if (!ThirdModuleUtil.existShuShuModule()){
            return;
        }
        if (!TDAnalyticsHelper.isReady(context)){
            return;
        }
        //在用户进行登录时，可调用 login 来设置用户的账号 ID， TE 平台将会以账号 ID 作为身份识别 ID，
        // 并且设置的账号 ID 将会在调用 logout 之前一直保留。多次调用 login 将覆盖先前的账号 ID 。
        // 用户的登录唯一标识，此数据对应上报数据里的#account_id，此时#account_id的值为TA
        TDAnalytics.login(accountId);*/
    }

    public static void setCommonProperties(Context context){

       /* try {
            if (!ThirdModuleUtil.existShuShuModule()){
                return;
            }
            if (!TDAnalyticsHelper.isReady(context)){
                return;
            }

            String channel_platform = ResConfig.getChannelPlatform(context);

            JSONObject superProperties = new JSONObject();
            superProperties.put("game_code", ResConfig.getGameCode(context));//字符串
            superProperties.put("platform",channel_platform);//字符串
            superProperties.put("channel",channel_platform);//字符串
            superProperties.put("package_name",context.getPackageName());//字符串
            superProperties.put("version_code",ApkInfoUtil.getVersionCode(context));//字符串
            superProperties.put("version_name",ApkInfoUtil.getVersionName(context));//字符串
            superProperties.put("unique_id", SdkUtil.getSdkUniqueId(context));//字符串

            if (DataManager.getInstance().isLogin() || SdkUtil.isLogin(context)) {//是否正在登录状态

                SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
                superProperties.put("user_id", sGameBaseRequestBean.getUserId());//字符串
                superProperties.put("role_id", sGameBaseRequestBean.getRoleId());//字符串
                superProperties.put("role_name", sGameBaseRequestBean.getRoleName());//字符串
                superProperties.put("role_level", sGameBaseRequestBean.getRoleLevel());//字符串
                superProperties.put("role_vip_level", sGameBaseRequestBean.getRoleVipLevel());//字符串

                superProperties.put("server_code", sGameBaseRequestBean.getServerCode());//字符串
                superProperties.put("server_name", sGameBaseRequestBean.getServerName());//字符串

                SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(context);
                if (sLoginResponse != null) {
                    superProperties.put("login_mode", sLoginResponse.getData().getLoginType());//字符串
                }
            }

//            superProperties.put("age",1);//数字
//            superProperties.put("isSuccess",true);//布尔
//            superProperties.put("birthday",new Date());//时间

//            JSONObject object = new JSONObject();
//            object.put("key", "value");
//            superProperties.put("object",object);//对象
//
//            JSONObject object1 = new JSONObject();
//            object1.put("key", "value");
//            JSONArray arr    = new JSONArray();
//            arr.put(object1);
//            superProperties.put("object_arr",arr);//对象组
//
//            JSONArray  arr1    = new JSONArray();
//            arr1.put("value");
//            superProperties.put(arr1);//数组
            //设置公共事件属性
            TDAnalytics.setSuperProperties(superProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    public static void trackEvent(Context context, String eventName, EventPropertie propertieBean){
        if (propertieBean != null) {
            trackEvent(context, eventName, propertieBean.objToJsonObj(), 0);
        }else {
            trackEvent(context, eventName, null, 0);
        }
    }

    public static void trackEvent(Context context, String eventName, JSONObject properties, int no_use){

        /*if (!ThirdModuleUtil.existShuShuModule()){
            return;
        }
        if(TextUtils.isEmpty(eventName)) {
            PL.e("上報事件名為空");
            return;
        }
        if (!TDAnalyticsHelper.isReady(context)){
            return;
        }
        if (properties != null) {
            TDAnalytics.track(eventName, properties);
        }else {
            TDAnalytics.track(eventName);
        }*/
    }

    public static void trackEvent(Context context, String eventName){
        trackEvent(context, eventName, null, 0);
    }
    /*public static void trackEvent(String eventName,  JSONObject otherProperties){

        try {

            if (SStringUtil.isEmpty(eventName)){
                return;
            }

            // JSONObject properties = new JSONObject();
            // properties.put("product_name","商品名");
            if (tdBean != null) {
                Gson gson = new Gson();
                String json = gson.toJson(tdBean);
                JSONObject properties = new JSONObject(json);
                TDAnalytics.track(eventName, properties);
            }else {
                TDAnalytics.track(eventName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/



}


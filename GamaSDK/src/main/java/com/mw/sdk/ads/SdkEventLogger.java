package com.mw.sdk.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.SimpleHttpRequest;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.TimeUtil;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
import com.mw.sdk.bean.AdsRequestBean;
import com.mw.sdk.bean.SUserInfo;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.out.bean.EventPropertie;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;
import com.thirdlib.adjust.AdjustHelper;
import com.thirdlib.af.AFHelper;
import com.thirdlib.facebook.FBEventsConstants;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleProxy;
import com.thirdlib.singular.SingularUtil;
import com.thirdlib.td.TDAnalyticsHelper;
import com.thirdlib.tiktok.TTSdkHelper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gan on 2017/3/3.
 */

public class SdkEventLogger {

    public static void activateApp(Activity activity){

        try {

            AFHelper.activityOnCreate(activity);
            SingularUtil.initSingularSDK(activity);
            //SFacebookProxy.initFbSdk(activity.getApplicationContext());
            sendEventToSever(activity,EventConstant.EventName.APP_OPEN.name());
            trackingWithEventName(activity, EventConstant.EventName.APP_OPEN.name(), null, EventConstant.AdType.AdTypeAppsflyer|EventConstant.AdType.AdTypeFirebase);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Android登录事件上报
     */
    public static void trackinLoginEvent(Context activity, SLoginResponse loginResponse){
        try {
            PL.i("登入上報");
            if (activity == null || loginResponse == null){
                return;
            }
            String userId = loginResponse.getData().getUserId();

            TTSdkHelper.regUserInfo(activity.getApplicationContext(),userId);

            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(EventConstant.ParameterName.USER_ID, userId);
            eventValue.put(EventConstant.ParameterName.SERVER_TIME, SdkUtil.getSdkTimestamp(activity) + "");
            String eventName = EventConstant.EventName.LOGIN_SUCCESS.name();
            sendEventToSever(activity,eventName);
            trackingWithEventName(activity,eventName,eventValue, EventConstant.AdType.AdTypeAppsflyer|EventConstant.AdType.AdTypeFirebase);

            SUserInfo sUserInfo = SdkUtil.getSUserInfo(activity, userId);
            if (sUserInfo != null && sUserInfo.isRegDayPay()){//注册首日付费玩家第二天登录
                Date ydate = TimeUtil.getYesterday(Long.parseLong(loginResponse.getData().getTimestamp()));
                String yesterday = TimeUtil.getDateStr(ydate, "yyyy-MM-dd");
                String regDay = TimeUtil.getDateStr(sUserInfo.getRegTime(), "yyyy-MM-dd");
                if (yesterday.equals(regDay)){
                    //Paid_D2Login要限制为注册首日的付费玩家，新增付费玩家第二天登录时触发，上报AF,FB和Firebase
                    PL.i("tracking Paid_D2Login");
                    trackingWithEventName(activity,EventConstant.EventName.Paid_D2Login.name(),eventValue);
                }
            }

//            EventPropertie tdBean = new EventPropertie();
//            tdBean.setLogin_type(loginResponse.getData().getLoginType());
//            tdBean.setUserId(loginResponse.getData().getUserId());
            TDAnalyticsHelper.trackEvent(EventConstant.EventName.LOGIN_SUCCESS.name());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Android注册事件上报
     */
    public static void trackinRegisterEvent(Context activity, SLoginResponse loginResponse){
        try {
            PL.i("註冊上報");
            if (activity == null || loginResponse == null){
                return;
            }

            String userId = loginResponse.getData().getUserId();
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(EventConstant.ParameterName.USER_ID, userId);
            eventValue.put(EventConstant.ParameterName.SERVER_TIME, SdkUtil.getSdkTimestamp(activity) + "");
            String eventName = EventConstant.EventName.REGISTER_SUCCESS.name();
            sendEventToSever(activity, EventConstant.EventName.REGISTER_SUCCESS.name());
            trackingWithEventName(activity,eventName,eventValue,EventConstant.AdType.AdTypeAppsflyer|EventConstant.AdType.AdTypeFirebase);
            trackingWithEventName(activity, FBEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION,eventValue,EventConstant.AdType.AdTypeFacebook);
            trackingWithEventName(activity, "COMPLETE_REGISTRATION_AND",eventValue,EventConstant.AdType.AdTypeFacebook);

//            EventPropertie tdBean = new EventPropertie();
//            tdBean.setRegister_type(loginResponse.getData().getLoginType());
//            tdBean.setUserId(loginResponse.getData().getUserId());
            TDAnalyticsHelper.trackEvent(EventConstant.EventName.REGISTER_SUCCESS.name());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Android角色信息上报
     */
   /* public static void trackingRoleInfo(Context activity, Map<String, Object> map) {
        try {
            String userId = SdkUtil.getUid(activity);
            if (map == null || map.isEmpty() || TextUtils.isEmpty(userId)) {
                PL.i(TAG, "沒有角色信息");
                return;
            } else {
                PL.i(TAG, "角色信息上報");
            }
            map.put(EventConstant.ParameterName.USER_ID, userId);
            trackingWithEventName(activity,EventConstant.EventName.CREATE_ROLE.name(),map, EventConstant.AdType.AdTypeAllChannel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * eventName为空，使用默认的上报收益名称，eventName不为空则使用该值
     * 统计储值数据
     */
    public static void trackinPayEvent(Context context, String eventName, String orderId, String productId, double usdPrice, String serverTimestamp, boolean linkUser){

        if(SStringUtil.isEmpty(orderId)) {
            PL.i("trackinPay orderId null");
            orderId = "orderId_"+System.currentTimeMillis();
        }

        if(SStringUtil.isEmpty(productId)) {
            PL.i("trackinPay productId null");
            productId = "productId_"+System.currentTimeMillis();
        }

        PL.i("trackinPay eventName:" + eventName);
//        String orderId = payBean.getOrderId() == null ? "unknow" : payBean.getOrderId();
//        String productId = payBean.getProductId() == null ? "unknow" : payBean.getProductId();
//        double usdPrice = payBean.getUsdPrice();
//        String serverTimestamp = payBean.getServerTimestamp();

        try {

            String uid = SdkUtil.getUid(context);
            String channel_platform = ResConfig.getChannelPlatform(context);

            //下面是AppsFlyer自己的事件名
            Map<String, Object> af_eventValues = new HashMap<>();

            af_eventValues.put(AFInAppEventParameterName.CONTENT_ID, productId);
            af_eventValues.put(AFInAppEventParameterName.ORDER_ID, orderId);
            af_eventValues.put(AFInAppEventParameterName.CUSTOMER_USER_ID, uid);
            af_eventValues.put(EventConstant.ParameterName.USER_ID, uid);
            af_eventValues.put(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));
            af_eventValues.put(EventConstant.ParameterName.SERVER_TIME, serverTimestamp);
//            af_eventValues.put("platform", ResConfig.getChannelPlatform(context));
            addEventParameterName(context, af_eventValues);
            PL.i("trackinPay start Purchase af...");
            if (SStringUtil.isEmpty(eventName)){

                af_eventValues.put(AFInAppEventParameterName.REVENUE, usdPrice);
                af_eventValues.put(AFInAppEventParameterName.CURRENCY, "USD");
                AFHelper.logEvent(context, AFInAppEventType.PURCHASE, af_eventValues);
            }else {
                //af除了默认AFInAppEventType.PURCHASE上报额度，别的都不上报，不然影响af后台统计
                AFHelper.logEvent(context, eventName, af_eventValues);
            }

            PL.i("trackinPay end Purchase af... params=" + JsonUtil.map2Json(af_eventValues).toString());


            //FB
            Map<String, Object> fb_eventValues = new HashMap<>();
            //下面是自定义的事件名
            fb_eventValues.put(EventConstant.ParameterName.USER_ID, uid);
            fb_eventValues.put(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));
            fb_eventValues.put(FBEventsConstants.EVENT_PARAM_CURRENCY,"USD");
            fb_eventValues.put(FBEventsConstants.EVENT_PARAM_CONTENT_ID, productId);
            fb_eventValues.put(FBEventsConstants.EVENT_PARAM_ORDER_ID, orderId);
            fb_eventValues.put("platform", ResConfig.getChannelPlatform(context));
            PL.i("trackinPay Purchase fb...");
            if (SStringUtil.isEmpty(eventName)){
                SFacebookProxy.logPurchase(context, new BigDecimal(usdPrice), fb_eventValues);
            }else {
                SFacebookProxy.trackingEvent(context,eventName, usdPrice, fb_eventValues);
            }

            //firebase
            Bundle b = SGoogleProxy.trackPayCC(context, eventName, orderId, productId, usdPrice, uid);


            //adjust
            Map<String, Object> payEventValues = new HashMap<>();
            payEventValues.put("usdPrice", usdPrice);
            payEventValues.put("currency", "USD");
            payEventValues.put("productId", productId);
            payEventValues.put("orderId", orderId);
            payEventValues.put("userId", uid);
            payEventValues.put("roleId", SdkUtil.getRoleId(context));
            payEventValues.put("serverTimestamp", serverTimestamp);
            addEventParameterName(context, payEventValues);
            if (SStringUtil.isEmpty(eventName)){
                AdjustHelper.trackEvent(context, "AJ_Purchase", payEventValues, usdPrice, orderId);
                SingularUtil.logRevenue(context, usdPrice, payEventValues);
            }else {
                AdjustHelper.trackEvent(context, eventName, payEventValues, usdPrice, orderId);
                SingularUtil.logCustomRevenue(context, eventName, usdPrice, payEventValues);
            }
            //tt
            if (SStringUtil.isEmpty(eventName)){

                TTSdkHelper.trackPay(context, uid, SdkUtil.getRoleId(context), orderId, productId, usdPrice, productId);

            }else {
                TTSdkHelper.trackEventRevenue(context, eventName, uid, SdkUtil.getRoleId(context), orderId, productId, usdPrice);
            }

            //shushu
            if (SStringUtil.isEmpty(eventName)){//eventName为空才是正常的储值上报
                EventPropertie eventPropertie = new EventPropertie();
                eventPropertie.setOrder_id(orderId);
                //eventPropertie.setPayment_name(productId);
                eventPropertie.setPay_amount(usdPrice);
                eventPropertie.setProduct_id(productId);
                eventPropertie.setPay_method("google");
                eventPropertie.setCurrency_type("USD");
                TDAnalyticsHelper.trackEvent("pay_success",eventPropertie);
            }

            if (!linkUser){//不跟用户行为关联
                return;
            }
            SUserInfo sUserInfo = SdkUtil.getSUserInfo(context, uid);
            if (sUserInfo != null){
                if (sUserInfo.isPay()){//判断是否付费过

                    if (!sUserInfo.isSecondPay()){
                        sUserInfo.setSecondPay(true);//付费过则为第二次付费
                        SdkUtil.updateUserInfo(context, sUserInfo);
                        //2nd_purchase，第二次充值的时候触发，上报AF,FB和Firebase，传firebase要上报金额，AF和FB不上报金额。

                        PL.i("tracking second_purchase...");
                        trackingWithEventName(context, EventConstant.EventName.second_purchase.name(), null, EventConstant.AdType.AdTypeAppsflyer|EventConstant.AdType.AdTypeFacebook);

                        SGoogleProxy.firebaseAnalytics(context, EventConstant.EventName.second_purchase.name(), b);

                        PL.i("tracking end second_purchase...");

                    }

                }else {
                    sUserInfo.setPay(true);//没付费过,即为首次
                    String firstPayTime = System.currentTimeMillis() + "";
                    sUserInfo.setFirstPayTime(firstPayTime);
                    if (TimeUtil.getDateStr(firstPayTime,"yyyy-MM-dd").equals(TimeUtil.getDateStr(sUserInfo.getRegTime(),"yyyy-MM-dd"))){
                        sUserInfo.setRegDayPay(true);
                    }else {
                        sUserInfo.setRegDayPay(false);
                    }
                    SdkUtil.updateUserInfo(context, sUserInfo);
                }

                if (usdPrice > 4) {
                    String curPayTime = System.currentTimeMillis() + "";
                    if (TimeUtil.getDateStr(curPayTime,"yyyy-MM-dd").equals(TimeUtil.getDateStr(sUserInfo.getRegTime(),"yyyy-MM-dd"))){
                        SdkEventLogger.trackingWithEventName(context, EventConstant.EventName.purchase_over4.name(), payEventValues);
                    }
                }

            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void trackingWithEventName(Context context, String eventName){
        trackingWithEventName(context,eventName,null,EventConstant.AdType.AdTypeAllChannel);
    }
    public static void trackingWithEventName(Context context, String eventName, Map<String, Object> map){
        trackingWithEventName(context,eventName,map, EventConstant.AdType.AdTypeAllChannel);
    }
    public static void trackingWithEventName(Context context, String eventName, Map<String, Object> map, int adType, boolean isOnce) {
        if (SStringUtil.isEmpty(eventName)){
            return;
        }

        if (isOnce) {
            String sp_key_event = "ThirdPlat_SPKEY_" + eventName;
            if (SStringUtil.isNotEmpty(SPUtil.getSimpleString(context,SdkUtil.SDK_SP_FILE,sp_key_event))){
                PL.i("trackingWithEventName exist eventname=" + eventName);
                return;
            }
            SPUtil.saveSimpleInfo(context,SdkUtil.SDK_SP_FILE, sp_key_event, "true");
        }

        trackingWithEventName(context, eventName, map, adType);
    }
    public static void trackingWithEventName(Context context, String eventName, Map<String, Object> map, int adType) {
        if(TextUtils.isEmpty(eventName)) {
            PL.e("上報事件名為空");
            return;
        }
        try {
            PL.i("tracking EventName:" + eventName);
            if(map == null) { //appsflyer的属性列表
                map = new HashMap<>();
            }
            addEventParameterName(context, map);

            //facebook和firebase的属性列表
            Bundle b = new Bundle();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                b.putString(entry.getKey(), entry.getValue().toString());
            }

            if(adType == 0 || (adType & EventConstant.AdType.AdTypeAllChannel) == EventConstant.AdType.AdTypeAllChannel) {
                PL.i("上报全部媒体");
                //AppsFlyer上报
                AFHelper.logEvent(context, eventName, map);

                //Facebook上报
                SFacebookProxy.trackingEvent(context, eventName, null, b);

                //firebase上报,
                SGoogleProxy.firebaseAnalytics(context, eventName, b);

                //adjust
//                GamaAj.trackEvent(context, eventName, map);
            } else {
                if((adType & EventConstant.AdType.AdTypeFacebook)==EventConstant.AdType.AdTypeFacebook) {
                    PL.i("上报媒体EventReportFacebook");
                    //Facebook上报
                    SFacebookProxy.trackingEvent(context, eventName, null, b);
                }
                if((adType & EventConstant.AdType.AdTypeFirebase)==EventConstant.AdType.AdTypeFirebase) {
                    PL.i("上报媒体EventReportFirebase");
                    //firebase上报,
                    SGoogleProxy.firebaseAnalytics(context, eventName, b);
                }
                if((adType & EventConstant.AdType.AdTypeAppsflyer)==EventConstant.AdType.AdTypeAppsflyer) {
                    PL.i("上报媒体AdTypeAppsflyer");
                    //AppsFlyer上报
                    AFHelper.logEvent(context, eventName, map);
                }
//                if(mediaSet.contains(EventConstant.EventReportChannel.EventReportAdjust)) {
//                    PL.i("上报媒体4");
                    //adjust上报
//                    GamaAj.trackEvent(context, eventName, map);
//                }
            }
            SingularUtil.logEvent(context, eventName, map);
            //adjust
            AdjustHelper.trackEvent(context, eventName, map);
            //TikTok
            TTSdkHelper.trackEvent(context, eventName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> addEventParameterName(Context context, Map<String, Object> map) {

        String adId = SdkUtil.getGoogleAdId(context);
        String uniqueId = SdkUtil.getSdkUniqueId(context);
        String androidId = ApkInfoUtil.getAndroidId(context);

        map.put(EventConstant.ParameterName.TIME, System.currentTimeMillis() + "");
        map.put("adId", adId);
        map.put("uniqueId", uniqueId);
        map.put("androidId", androidId);
        map.put("platform", ResConfig.getChannelPlatform(context));

        return map;
    }

    /**
     * 获取Google ads id，不能在主线程调用
     */
    public static void registerGoogleAdId(final Context context){
        PL.i("start registerGoogleAdId" );
        /*new  Thread(new Runnable() {
            @Override
            public void run() {
                String googleAdId = SGoogleProxy.getAdvertisingId(context.getApplicationContext());
                PL.i("get google ad id-->" + googleAdId);
                SdkUtil.saveGoogleAdId(context,googleAdId);
            }
        }).start();*/

        String googleAdId = SGoogleProxy.getAdvertisingId(context.getApplicationContext());
        PL.i("get google ad id-->" + googleAdId);
        SdkUtil.saveGoogleAdId(context,googleAdId);
    }

    //发送事件到服务器记录，只是发一次
    public static void sendEventToSever(Context context, String eventName){
        sendEventToServer(context, eventName, null,true, false);
    }

    public static void sendEventToServer(final Context context, String eventName, Map<String, String> otherParams, boolean isDeviceOnce, boolean isUserOnce) {
        try {
            Request.sendEventToServer(context, eventName, otherParams, isDeviceOnce, isUserOnce);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 本地保存的安装上报标识
     */
    private static final String SDK_INSTALL_ACTIVATION = "SDK_INSTALL_ACTIVATION";

    /**
     * 自家平台广告：安装上报
     */
    public static void reportInstallActivation(final Context context){

        if (SStringUtil.isNotEmpty(SPUtil.getSimpleString(context,SdkUtil.SDK_SP_FILE,SDK_INSTALL_ACTIVATION))){
            return;
        }
        adsInstallActivation(context);
    }

    private static void adsInstallActivation(final Context context) {
//        GsInstallReferrer.initReferrerClient(context, new GsInstallReferrer.GsInstallReferrerCallback() {
//            @Override
//            public void onResult(GsInstallReferrerBean bean) {
//
//        });

        final AdsRequestBean adsRequestBean = new AdsRequestBean(context);

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("eventName","install");
        requestParams.put("appTime", adsRequestBean.getTimestamp());
        requestParams.put("gameCode",adsRequestBean.getGameCode());
        requestParams.put("packageName",adsRequestBean.getPackageName());
        requestParams.put("uniqueId",adsRequestBean.getUniqueId());
        requestParams.put("system",adsRequestBean.getSystemVersion());
        requestParams.put("platform",adsRequestBean.getPlatform());
        requestParams.put("deviceType",adsRequestBean.getDeviceType());
        requestParams.put("gameLanguage",adsRequestBean.getGameLanguage());
        requestParams.put("versionCode",adsRequestBean.getVersionCode());
        requestParams.put("versionName",adsRequestBean.getVersionName());
        requestParams.put("androidId",adsRequestBean.getAndroidId());
        requestParams.put("adId",adsRequestBean.getAdvertisingId());
        requestParams.put("appsflyerId",adsRequestBean.getAppsflyerId());

        adsRequestBean.setRequestParamsMap(requestParams);
        adsRequestBean.setRequestUrl(ResConfig.getLogPreferredUrl(context)); //日志记录
        adsRequestBean.setRequestMethod("sdk/event/log");

//        if (bean != null) {
//            adsRequestBean.setAppInstallTime(bean.getAppInstallTime());
//            adsRequestBean.setReferrerClickTime(bean.getReferrerClickTime());
//            adsRequestBean.setReferrer(bean.getReferrerUrl());
//        }
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest();
        simpleHttpRequest.setGetMethod(true,true);
        simpleHttpRequest.setBaseReqeustBean(adsRequestBean);
        simpleHttpRequest.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                PL.i("SDK_INSTALL_ACTIVATION rawResult:" + rawResult);
                SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, SDK_INSTALL_ACTIVATION, "adsInstallActivation");
            }

            @Override
            public void timeout(String code) {
                PL.i("ADS timeout");
            }

            @Override
            public void noData() {
                PL.i("SDK_INSTALL_ACTIVATION rawResult noData");
                SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, SDK_INSTALL_ACTIVATION, "adsInstallActivation");
            }

            @Override
            public void cancel() {
            }
        });
        simpleHttpRequest.excute();
    }

    /*private static void getVfSwitch(final Activity activity) {
        GamaVfcodeSwitchRequestTask task = new GamaVfcodeSwitchRequestTask(activity);
        task.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                if (responseModel != null && !TextUtils.isEmpty(rawResult)) {
                    GamaUtil.saveSwitchJson(activity, rawResult);
                }
            }

            @Override
            public void timeout(String code) {
            }

            @Override
            public void noData() {
            }

            @Override
            public void cancel() {
            }

        });
        task.excute();
    }*/
	
	public static void onResume(Activity activity) {
//        GamaAj.onResume(activity);
//        TapDB.onResume(activity);
    }

    public static void onPause(Activity activity) {
//        GamaAj.onPause(activity);
//        TapDB.onStop(activity);
    }

}

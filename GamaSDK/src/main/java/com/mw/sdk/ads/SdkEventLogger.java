package com.mw.sdk.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.SimpleHttpRequest;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.TimeUtil;
import com.facebook.appevents.AppEventsConstants;
import com.mw.base.bean.SUserInfo;
import com.mw.sdk.pay.gp.bean.res.BasePayBean;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mw.base.bean.AdsRequestBean;
import com.mw.base.cfg.ResConfig;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleProxy;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by gan on 2017/3/3.
 */

public class SdkEventLogger {

    public static void activateApp(Activity activity){

        try {
            AppsFlyerLib.getInstance().setCollectIMEI(false);
            AppsFlyerLib.getInstance().setCollectAndroidID(false);
            String afDevKey = ResConfig.getConfigInAssetsProperties(activity,"sdk_ads_appflyer_dev_key");
            if(!TextUtils.isEmpty(afDevKey)) {
//                AppsFlyerLib.getInstance().startTracking(activity.getApplication(), afDevKey);
                AppsFlyerLib.getInstance().init(afDevKey,null,activity.getApplication());//应用层调用
                AppsFlyerLib.getInstance().start(activity.getApplicationContext());
                if (BuildConfig.DEBUG) {//debug打印日志
                    AppsFlyerLib.getInstance().setDebugLog(true);
                }
            } else {
                PL.e("af dev key empty!");
            }

            SFacebookProxy.initFbSdk(activity.getApplicationContext());
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
            trackingWithEventName(activity, AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION,eventValue,EventConstant.AdType.AdTypeFacebook);
            trackingWithEventName(activity, "COMPLETE_REGISTRATION_AND",eventValue,EventConstant.AdType.AdTypeFacebook);
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
     * 统计储值数据
     */
    public static void trackinPayEvent(Context context, BasePayBean payBean){
        if(payBean == null) {
            PL.i("trackinPay bundle null");
            return;
        }
//        BasePayBean payBean = (BasePayBean) bundle.getSerializable(GamaCommonKey.PURCHASE_DATA);

        PL.i("trackinPay Purchase");
        String orderId = payBean.getOrderId() == null ? "unknow" : payBean.getOrderId();
        String productId = payBean.getProductId() == null ? "unknow" : payBean.getProductId();
        double usdPrice = payBean.getUsdPrice();

        try {

            String uid = SdkUtil.getUid(context);

            //下面是AppsFlyer自己的事件名
            Map<String, Object> af_eventValues = new HashMap<>();
            af_eventValues.put(AFInAppEventParameterName.REVENUE, usdPrice);
            af_eventValues.put(AFInAppEventParameterName.CURRENCY, "USD");
            af_eventValues.put(AFInAppEventParameterName.CONTENT_ID, productId);
            af_eventValues.put(AFInAppEventParameterName.ORDER_ID, orderId);
            af_eventValues.put(AFInAppEventParameterName.CUSTOMER_USER_ID, uid);
            af_eventValues.put(EventConstant.ParameterName.USER_ID, uid);
            af_eventValues.put(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));
            af_eventValues.put(EventConstant.ParameterName.SERVER_TIME, payBean.getServerTimestamp() + "");
//            af_eventValues.put("platform", context.getResources().getString(R.string.channel_platform));
            addEventParameterName(context, af_eventValues);
            PL.i("trackinPay start Purchase af...");
            AppsFlyerLib.getInstance().logEvent(context.getApplicationContext(), AFInAppEventType.PURCHASE, af_eventValues);
            PL.i("trackinPay end Purchase af... params=" + JsonUtil.map2Json(af_eventValues).toString());


            //FB
            Map<String, Object> fb_eventValues = new HashMap<>();
            //下面是自定义的事件名
            fb_eventValues.put(EventConstant.ParameterName.USER_ID, uid);
            fb_eventValues.put(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));
            fb_eventValues.put(AppEventsConstants.EVENT_PARAM_CURRENCY,"USD");
            fb_eventValues.put(AppEventsConstants.EVENT_PARAM_CONTENT_ID, productId);
            fb_eventValues.put(AppEventsConstants.EVENT_PARAM_ORDER_ID, orderId);
            fb_eventValues.put("platform", context.getResources().getString(R.string.channel_platform));
            PL.i("trackinPay Purchase fb...");
            SFacebookProxy.logPurchase(context, new BigDecimal(usdPrice), fb_eventValues);

            //Firebase
            Map<String, Object> firebaseValues = new HashMap<>();
            //下面是自定义的事件名
            firebaseValues.put(EventConstant.ParameterName.USER_ID, uid);
            firebaseValues.put(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));

            Bundle b = new Bundle();
            for (Map.Entry<String, Object> entry : firebaseValues.entrySet()) {
                b.putString(entry.getKey(), entry.getValue().toString());
            }
            b.putString(FirebaseAnalytics.Param.ITEM_ID,productId);
//            b.putDouble(FirebaseAnalytics.Param.PRICE,usdPrice);
            b.putDouble(FirebaseAnalytics.Param.VALUE,usdPrice);
            b.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
            b.putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId);
            b.putString("platform", context.getResources().getString(R.string.channel_platform));
            PL.i("trackinPay Purchase firebase...");
            SGoogleProxy.firebaseAnalytics(context, FirebaseAnalytics.Event.PURCHASE, b);


            if(!SdkUtil.getFirstPay(context)) {//检查是否首次充值
//                trackingWithEventName(context, EventConstant.EventName.FIRST_PAY.name(), eventValues, EventConstant.AdType.AdTypeAllChannel);
                SdkUtil.saveFirstPay(context);
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

                AppsFlyerLib.getInstance().logEvent(context.getApplicationContext(), eventName, map);

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
                    AppsFlyerLib.getInstance().logEvent(context.getApplicationContext(), eventName, map);
                }
//                if(mediaSet.contains(EventConstant.EventReportChannel.EventReportAdjust)) {
//                    PL.i("上报媒体4");
                    //adjust上报
//                    GamaAj.trackEvent(context, eventName, map);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> addEventParameterName(Context context, Map<String, Object> map) {

        String adId = SdkUtil.getGoogleAdId(context);
        String uniqueId = SdkUtil.getSdkUniqueId(context);
        String androidId = ApkInfoUtil.getAndroidId(context);

        map.put(EventConstant.ParameterName.TIME, System.currentTimeMillis() + "");
        map.put("adId", adId);
        map.put("uniqueId", uniqueId);
        map.put("androidId", androidId);
        map.put("platform", context.getResources().getString(R.string.channel_platform));

        return map;
    }

    /**
     * 获取Google ads id，不能在主线程调用
     */
    public static void registerGoogleAdId(final Context context){
        new  Thread(new Runnable() {
            @Override
            public void run() {
                String googleAdId = SGoogleProxy.getAdvertisingId(context.getApplicationContext());
                PL.i("get google ad id-->" + googleAdId);
                SdkUtil.saveGoogleAdId(context,googleAdId);
            }
        }).start();
    }

    //发送事件到服务器记录，只是发一次
    public static void sendEventToSever(Context context, String eventName){
        try {
            Request.sendEventToServer(context, eventName);
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

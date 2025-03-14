package com.mw.sdk.ads;

import android.content.Context;

import com.appsflyer.AFInAppEventParameterName;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.thirdlib.af.AFHelper;
import com.thirdlib.facebook.FBEventsConstants;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleProxy;
import com.thirdlib.tiktok.TTSdkHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TrackEventHelper {

    public static void trackRevenueFirebase(Context context, String eventName, double usdPrice, String currency, String uid,String roleId, String productId, String orderId, String channel_platform, Map<String, Object> otherParams){

        SGoogleProxy.trackRevenueFirebase(context, eventName, usdPrice, currency, uid, roleId, productId, orderId, channel_platform, otherParams);

    }

    public static void trackRevenueFB(Context context, boolean logPurchase, String eventName, double usdPrice, String currency, String uid,String roleId, String productId, String orderId, String channel_platform, Map<String, Object> otherParams){

        if (context == null || SStringUtil.isEmpty(eventName)){
            return;
        }
        //FB
        Map<String, Object> fb_eventValues = new HashMap<>();
        //下面是自定义的事件名
        fb_eventValues.put(EventConstant.ParameterName.USER_ID, uid);
        fb_eventValues.put(EventConstant.ParameterName.ROLE_ID, roleId);
        fb_eventValues.put(FBEventsConstants.EVENT_PARAM_CURRENCY, currency);
        fb_eventValues.put(FBEventsConstants.EVENT_PARAM_CONTENT_ID, productId);
        fb_eventValues.put(FBEventsConstants.EVENT_PARAM_ORDER_ID, orderId);
        fb_eventValues.put("platform", channel_platform);

        if (otherParams != null && !otherParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
                fb_eventValues.put(entry.getKey(), entry.getValue().toString());
            }
        }

        PL.i("trackRevenueFB start name=" + eventName);
        if (logPurchase){
            SFacebookProxy.logPurchase(context, new BigDecimal(usdPrice), fb_eventValues);
        }else {
            SFacebookProxy.trackingEvent(context,eventName, usdPrice, fb_eventValues);
        }

    }


    public static void trackRevenueAF(Context context, String eventName, double usdPrice, String currency, String uid,String roleId, String productId, String orderId, String channel_platform, Map<String, Object> otherParams){

        if (context == null || SStringUtil.isEmpty(eventName)){
            return;
        }
        //下面是AppsFlyer自己的事件名
        Map<String, Object> af_eventValues = new HashMap<>();

        af_eventValues.put(AFInAppEventParameterName.CONTENT_ID, productId);
        af_eventValues.put(AFInAppEventParameterName.ORDER_ID, orderId);
        af_eventValues.put(AFInAppEventParameterName.CUSTOMER_USER_ID, uid);
        af_eventValues.put(EventConstant.ParameterName.USER_ID, uid);
        af_eventValues.put(EventConstant.ParameterName.ROLE_ID, roleId);
        //af_eventValues.put(EventConstant.ParameterName.SERVER_TIME, serverTimestamp);

        //af_eventValues.put(AFInAppEventParameterName.REVENUE, usdPrice);
        af_eventValues.put(AFInAppEventParameterName.PRICE, usdPrice);
        af_eventValues.put(AFInAppEventParameterName.CURRENCY, currency);

        af_eventValues.put("platform", channel_platform);
        //addEventParameterName(context, af_eventValues);

        if (otherParams != null && !otherParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
                af_eventValues.put(entry.getKey(), entry.getValue().toString());
            }
        }

        PL.i("trackRevenueAF start name=" + eventName);
//        if (SStringUtil.isEmpty(eventName)){
//
//            af_eventValues.put(AFInAppEventParameterName.REVENUE, usdPrice);
//            af_eventValues.put(AFInAppEventParameterName.CURRENCY, "USD");
//            AFHelper.logEvent(context.getApplicationContext(), AFInAppEventType.PURCHASE, af_eventValues);
//        }else {
//            //af除了默认AFInAppEventType.PURCHASE上报额度，别的都不上报，不然影响af后台统计
//            AFHelper.logEvent(context.getApplicationContext(), eventName, af_eventValues);
//        }

        AFHelper.logEvent(context, eventName, af_eventValues);
    }

    public static void trackRevenueTT(Context context, String eventName, double usdPrice, String currency, String uid,String roleId, String productId, String orderId, String channel_platform, Map<String, Object> otherParams){
        TTSdkHelper.trackEventRevenue(context, eventName, uid, roleId, orderId, productId, usdPrice);
    }
}

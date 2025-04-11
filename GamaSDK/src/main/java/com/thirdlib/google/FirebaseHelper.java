package com.thirdlib.google;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mw.sdk.R;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

import java.util.Map;

public class FirebaseHelper {

    private static FirebaseAnalytics analytics;
    protected static void firebaseAnalytics(Context context, String eventName, Bundle params) {
        if (context == null || TextUtils.isEmpty(eventName)) {
            return;
        }
        if (analytics == null){
            analytics = FirebaseAnalytics.getInstance(context);
        }
        if (analytics != null){
            PL.i("-----track event firebase start name=" + eventName);
            analytics.logEvent(eventName, params);
        }
    }

    protected static void trackRevenueFirebase(Context context, String eventName, double usdPrice, String currency, String uid,String roleId, String productId, String orderId, String channel_platform, Map<String, Object> otherParams){

        if (context == null || SStringUtil.isEmpty(eventName)){
            return;
        }
        //Firebase
//        Map<String, Object> firebaseValues = new HashMap<>();
        //下面是自定义的事件名
//        firebaseValues.put(EventConstant.ParameterName.USER_ID, uid);
//        firebaseValues.put(EventConstant.ParameterName.ROLE_ID, roleId);

        Bundle b = new Bundle();
        if (otherParams != null && !otherParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
                b.putString(entry.getKey(), entry.getValue().toString());
            }
        }
        b.putString(EventConstant.ParameterName.USER_ID,uid);
        b.putString(EventConstant.ParameterName.ROLE_ID,roleId);

        b.putString(FirebaseAnalytics.Param.ITEM_ID,productId);
//            b.putDouble(FirebaseAnalytics.Param.PRICE,usdPrice);
        b.putDouble(FirebaseAnalytics.Param.VALUE,usdPrice);
        b.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        b.putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId);
        b.putString("platform", channel_platform);
        PL.i("trackRevenueFirebase start...name=" + eventName);
        firebaseAnalytics(context, eventName, b);

    }

    @NonNull
    protected static Bundle trackPayCC(Context context, String eventName, String orderId, String productId, double usdPrice, String uid) {

        Bundle b = new Bundle();
        if (context == null){
            return b;
        }

        //Firebase
//        Map<String, Object> firebaseValues = new HashMap<>();
//        //下面是自定义的事件名
//        firebaseValues.put(EventConstant.ParameterName.USER_ID, uid);
//        firebaseValues.put(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));
//

//        for (Map.Entry<String, Object> entry : firebaseValues.entrySet()) {
//            b.putString(entry.getKey(), entry.getValue().toString());
//        }

        b.putString(EventConstant.ParameterName.USER_ID, uid);
        b.putString(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));

        b.putString(FirebaseAnalytics.Param.ITEM_ID, productId);
//            b.putDouble(FirebaseAnalytics.Param.PRICE,usdPrice);
        b.putDouble(FirebaseAnalytics.Param.VALUE, usdPrice);
        b.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
        b.putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId);
        b.putString("platform", ResConfig.getChannelPlatform(context));
        PL.i("trackinPay Purchase firebase...");
        if (SStringUtil.isEmpty(eventName)){
            firebaseAnalytics(context, FirebaseAnalytics.Event.PURCHASE, b);

            firebaseAnalytics(context, "purchase_D14", b);
            firebaseAnalytics(context, "purchase_D30", b);
            firebaseAnalytics(context, "purchase_D45", b);
            firebaseAnalytics(context, "purchase_D60", b);

        }else {
            firebaseAnalytics(context, eventName, b);
        }
        return b;
    }

}

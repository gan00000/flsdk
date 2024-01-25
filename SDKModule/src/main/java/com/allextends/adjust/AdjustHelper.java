package com.allextends.adjust;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.ldy.sdk.BuildConfig;
import com.ldy.sdk.R;
import com.mybase.utils.FileUtil;
import com.mybase.utils.PL;
import com.mybase.utils.SStringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AdjustHelper {

    private static JSONObject adjustInfoJsonObject;

    public static void init(Application application){

        PL.d("AdjustHelper init");
        if (application== null){
            PL.e("AdjustHelper application is null");
            return;
        }
        String appToken = application.getApplicationContext().getString(R.string.dy_adjust_token);//"{YourAppToken}";
        if (SStringUtil.isEmpty(appToken)){
            PL.e("AdjustHelper appToken empty");
            return;
        }

        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        if (BuildConfig.DEBUG) {
            environment = AdjustConfig.ENVIRONMENT_SANDBOX;
        }

        AdjustConfig config = new AdjustConfig(application.getApplicationContext(), appToken, environment);
        config.setLogLevel(LogLevel.DEBUG);
        Adjust.onCreate(config);

        PL.d("AdjustHelper init finish appToken=%s,environment=%s", appToken, environment);
        application.registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }

    private static final class AdjustLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            PL.e("AdjustHelper onActivityResumed");
//            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            PL.e("AdjustHelper onActivityPaused");
//            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }

    }

    public static void trackEvent(Context context, String eventName){
        trackEvent(context, eventName, null, -1, "");
    }

    public static void trackEvent(Context context, String eventName, Map<String,Object> params){
        trackEvent(context, eventName, params, -1, "");
    }

    public static void trackEvent(Context context, String eventName, Map<String,Object> params, double revenue, String orderId){

        PL.i("AdjustHelper trackEvent eventName=" + eventName);
        String appToken = context.getString(R.string.dy_adjust_token);//"{YourAppToken}";
        if (SStringUtil.isEmpty(appToken)){
            PL.e("AdjustHelper appToken empty");
            return;
        }

        String eventNameToken = getEventToken(context, eventName);
        if (SStringUtil.isEmpty(eventNameToken)){
            PL.e("AdjustHelper trackEvent error eventNameToken empty, eventName=" + eventName);
            return;
        }
        PL.i("AdjustHelper trackEvent eventName=%s, token=%s", eventName, eventNameToken);
        AdjustEvent adjustEvent = new AdjustEvent(eventNameToken);
        if (revenue > 0) {
            adjustEvent.setRevenue(revenue,"USD");
            if (SStringUtil.isNotEmpty(orderId)){
                adjustEvent.setOrderId(orderId);
            }
        }
        if (params != null && !params.isEmpty()){

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object mapValue = entry.getValue();
                String mapKey = entry.getKey();
                if (mapValue != null) {
                    adjustEvent.addCallbackParameter(mapKey, mapValue.toString());
                }
            }
        }
        Adjust.trackEvent(adjustEvent);
    }


    public static String getEventToken(Context context, String eventName){

        String eventNameToken = "";
        try {
            if (adjustInfoJsonObject != null){

                eventNameToken = findJsonToken(adjustInfoJsonObject, eventName);
            }
            if (SStringUtil.isEmpty(eventNameToken)){

                String adjustInfo = FileUtil.readAssetsTxtFile(context, "dysdk/adjustEvent");
                if (SStringUtil.isEmpty(adjustInfo)){
                    return eventNameToken;
                }
                adjustInfoJsonObject = new JSONObject(adjustInfo);
                eventNameToken = findJsonToken(adjustInfoJsonObject, eventName);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventNameToken;
    }

    private static String findJsonToken(JSONObject jsonObject, String eventName){
        String eventNameToken = "";
        JSONArray eventsArray = jsonObject.optJSONArray("events");
        if (eventsArray != null){
            for (int i = 0; i < eventsArray.length(); i++) {
                JSONObject eventObj = eventsArray.optJSONObject(i);
                String eName = eventObj.optString("name","");
                if (eventName.equals(eName)){
                    eventNameToken = eventObj.optString("token","");
                    return eventNameToken;
                }
            }
        }
        return eventNameToken;
    }
}

package com.thirdlib.adjust;

import android.app.Application;
import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

public class AdjustHelper {

    private static JSONObject adjustInfoJsonObject;
    public static void init(Application application){

    }


    public static void trackEvent(Context context, String eventName){
    }

    public static void trackEvent(Context context, String eventName, Map<String,Object> params){
    }

    public static void trackEvent(Context context, String eventName, Map<String,Object> params, double revenue, String orderId){

    }


    public static String getEventToken(Context context, String eventName){
        return "";
    }

    private static String findJsonToken(JSONObject jsonObject, String eventName){
        return "";
    }
}

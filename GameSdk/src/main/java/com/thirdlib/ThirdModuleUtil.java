package com.thirdlib;

import android.content.Context;

import com.core.base.utils.PL;

public class ThirdModuleUtil {

    public static void checkModule(Context context){

    }


    private static boolean isExistAppsFlyerModule = false;
    public static boolean existAppsFlyerModule() {

        if (isExistAppsFlyerModule){
            return true;
        }

        try {
            Class<?> clazz = Class.forName("com.appsflyer.AppsFlyerLib");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("AppsFlyer module not exist.");
            return false;
        }
        isExistAppsFlyerModule = true;
        return true;
    }


    private static boolean isExistFbModule = false;
    public static boolean existFbModule() {

        if (isExistFbModule){
            return true;
        }

        try {
            Class<?> clazz = Class.forName("com.facebook.FacebookSdk");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("Facebook module not exist.");
            return false;
        }
        isExistFbModule = true;
        return true;
    }



    private static boolean isExistFirebaseModule = false;
    public static boolean existFirebaseModule() {

        if (isExistFirebaseModule){
            return true;
        }
        try {
            Class<?> clazz = Class.forName("com.google.firebase.analytics.FirebaseAnalytics");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("Firebase module not exist.");
            return false;
        }
        isExistFirebaseModule = true;
        return true;
    }


    private static boolean isExistShuShuModule = false;
    public static boolean existShuShuModule() {

        if (isExistShuShuModule){
            return true;
        }

        try {
            Class<?> clazz = Class.forName("cn.thinkingdata.analytics.TDAnalytics");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("shushu module not exist.");
            return false;
        }
        isExistShuShuModule = true;
        return true;
    }


    private static boolean isExistTikTokModule = false;
    public static boolean existTikTokModule() {

        if (isExistTikTokModule){
            return true;
        }

        try {
            Class<?> clazz = Class.forName("com.tiktok.TikTokBusinessSdk");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("TikTok module not exist.");
            return false;
        }
        isExistTikTokModule = true;
        return true;
    }


    private static boolean isExistRustoreModule = false;
    public static boolean existRustoreModule(){

        if (isExistRustoreModule){
            return true;
        }
        try {
            //Class<?> clazz = Class.forName("ru.rustore.sdk.review.RuStoreReviewManager");
            Class<?> clazz = Class.forName("ru.rustore.sdk.pay.RuStorePayClient");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("rustore module not exist.");
            return false;
        }
        isExistRustoreModule = true;
        return true;
    }

    private static boolean isExistNowggModule = false;
    public static boolean existNowggModule(){

        if (isExistNowggModule){
            return true;
        }

        try {
            Class<?> clazz = Class.forName("gg.now.billingclient.api.BillingClient");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("nowgg module not exist.");
            return false;
        }
        isExistNowggModule = true;
        return true;
    }


    private static boolean isExistXiaomiModule = false;
    public static boolean existXiaomiModule(){

        if (isExistXiaomiModule){
            return true;
        }

        try {
            Class<?> clazz = Class.forName("com.xiaomi.billingclient.api.BillingClient");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("Xiaomi module not exist.");
            return false;
        }
        isExistXiaomiModule = true;
        return true;
    }


    private static boolean isExistSingularModule = false;
    public static boolean existSingularModule() {

        if (isExistSingularModule){
            return true;
        }

        try {
            Class<?> clazz = Class.forName("com.singular.sdk.Singular");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("Singular module not exist.");
            return false;
        }
        isExistSingularModule = true;
        return true;
    }

    public static boolean existBazaarModule() {

        try {
            Class<?> clazz = Class.forName("ir.cafebazaar.poolakey.Payment");
            if (clazz == null){
                return false;
            }
        } catch (ClassNotFoundException e) {
            PL.w("cafebazaar module not exist.");
            return false;
        }
        return true;
    }

    private static boolean isExistAppMetricaModule = false;
    public static boolean existAppMetricaModule() {

        if (isExistAppMetricaModule){
            return true;
        }

        try {
            Class<?> clazz = Class.forName("io.appmetrica.analytics.AppMetrica");
            if (clazz == null){
                return false;
            }

        } catch (ClassNotFoundException e) {
            PL.w("AppMetrica module not exist.");
            return false;
        }
        isExistAppMetricaModule = true;
        return true;
    }
}

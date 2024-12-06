package com.thirdlib.af;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.appsflyer.deeplink.DeepLink;
import com.appsflyer.deeplink.DeepLinkListener;
import com.appsflyer.deeplink.DeepLinkResult;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.R;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AFHelper {

    public static final String LOG_TAG = "PL_LOG_AFHelper";
    // This boolean flag signals between the UDL and GCD callbacks that this deep_link was
    // already processed, and the callback functionality for deep linking can be skipped.
    // When GCD or UDL finds this flag true it MUST set it to false before skipping.

    public static void applicationOnCreate(Application application){
        PL.i("AFHelper applicationOnCreate");
        AppsFlyerLib.getInstance().setCollectIMEI(false);
        AppsFlyerLib.getInstance().setCollectAndroidID(false);
        String afDevKey = ResConfig.getAfDevKey(application.getApplicationContext());
        if(!TextUtils.isEmpty(afDevKey)) {
//           AppsFlyerLib.getInstance().startTracking(activity.getApplication(), afDevKey);
//            AppsFlyerLib.getInstance().init(afDevKey,null, application.getApplicationContext());//应用层调用


            //是否发布欧洲，欧洲特殊处理 https://support.appsflyer.com/hc/zh-cn/articles/22310731147153-%E5%AF%B9Google%E6%AC%A7%E7%9B%9F%E6%84%8F%E8%A7%81%E5%BE%81%E6%B1%82%E6%96%B0%E6%94%BF%E7%9A%84%E6%94%AF%E6%8C%81
            if (SStringUtil.isNotEmpty(application.getApplicationContext().getString(R.string.sdk_release_european))) {
                AppsFlyerLib.getInstance().enableTCFDataCollection(true);
            }

            if (BuildConfig.DEBUG) {//debug打印日志
                PL.i("af debug mode");
                AppsFlyerLib.getInstance().setDebugLog(true);
            }
            //AppsFlyerLib.getInstance().setMinTimeBetweenSessions(0);
            //处理af深度链接
            /*当应用程序在后台运行并且应用启动模式不是标准模式时，不会调用onDeepLinking。
            To correct this, call setIntent(intent) method to set the intent value inside the overridden method onNewIntent if the application is using a non-standard LaunchMode.
            @Override
            protected void onNewIntent(Intent intent)
            {
                super.onNewIntent(intent);
                setIntent(intent);
            }*/
            AppsFlyerLib.getInstance().subscribeForDeepLink(new DeepLinkListener(){
                @Override
                public void onDeepLinking(@NonNull DeepLinkResult deepLinkResult) {
                    PL.i( "af onDeepLinking finish");
                    DeepLinkResult.Status dlStatus = deepLinkResult.getStatus();
                    if (dlStatus == DeepLinkResult.Status.FOUND) {
                        PL.i( "af Deep link found");
                    } else if (dlStatus == DeepLinkResult.Status.NOT_FOUND) {
                        PL.i( "af Deep link not found");
                        return;
                    } else {
                        // dlStatus == DeepLinkResult.Status.ERROR
                        DeepLinkResult.Error dlError = deepLinkResult.getError();
                        PL.i( "af There was an error getting Deep Link data: " + dlError.toString());
                        return;
                    }
                    DeepLink deepLinkObj = deepLinkResult.getDeepLink();
                    try {
                        PL.i("af The DeepLink data is: " + deepLinkObj.toString());

                        // An example for using is_deferred
                        if (Boolean.TRUE.equals(deepLinkObj.isDeferred())) {
                            PL.i( "af This is a deferred deep link");
//                    s        if (deferred_deep_link_processed_flag == true) {
//                                Log.d(LOG_TAG, "Deferred deep link was already processed by GCD. This iteration can be skipped.");
//                                deferred_deep_link_processed_flag = false;
//                                return;
//                            }

                            String deepLinkValue = deepLinkObj.getDeepLinkValue();
                            //JSONObject dlData = deepLinkObj.getClickEvent();
                            PL.i( "This is a deferred deep link. deepLinkValue=" + deepLinkValue);
                            if (SStringUtil.isNotEmpty(deepLinkValue)) {
                                SdkUtil.saveDeepLink(application.getApplicationContext(), deepLinkValue);
                            }

                        } else {
                            PL.i("This is a direct deep link");
                        }

                    } catch (Exception e) {
                        PL.i( "DeepLink data came back null");
                    }
                }
            });

            AppsFlyerConversionListener conversionListener =  new AppsFlyerConversionListener() {
                @Override
                public void onConversionDataSuccess(Map<String, Object> conversionDataMap) {
                    PL.i( "AppsFlyerConversionListener onConversionDataSuccess");
                    if (conversionDataMap == null){
                        return;
                    }
                    for (String attrName : conversionDataMap.keySet()){
                        PL.i("Conversion attribute: " + attrName + " = " + conversionDataMap.get(attrName));
                    }
                    String status = Objects.requireNonNull(conversionDataMap.get("af_status")).toString();
                    if(status.equals("Non-organic")){
                        if( Objects.requireNonNull(conversionDataMap.get("is_first_launch")).toString().equals("true")){
                            PL.i("Conversion: First Launch");
                            //Deferred deep link in case of a legacy link
                            if (conversionDataMap.containsKey("deep_link_value")) { //Not legacy link
                                PL.i("onConversionDataSuccess: Link contains deep_link_value, deep linking with UDL");

                            }
//                            else{ //Legacy link
//                                conversionDataMap.put("deep_link_value", conversionDataMap.get("fruit_name"));
//                                String fruitNameStr = (String) conversionDataMap.get("fruit_name");
//                                DeepLink deepLinkData = mapToDeepLinkObject(conversionDataMap);
//                                goToFruit(fruitNameStr, deepLinkData);
//                            }

                        } else {
                            Log.d(LOG_TAG,"Conversion: Not First Launch");
                        }
                    } else {
                        Log.d(LOG_TAG, "Conversion: This is an organic install.");
                    }
                }

                @Override
                public void onConversionDataFail(String errorMessage) {
                    PL.i( "AppsFlyerConversionListener error getting conversion data: " + errorMessage);
                }

                @Override
                public void onAppOpenAttribution(Map<String, String> attributionData) {
                    PL.i( "AppsFlyerConversionListener onAppOpenAttribution: This is fake call.");
                }

                @Override
                public void onAttributionFailure(String errorMessage) {
                    PL.i("AppsFlyerConversionListener error onAttributionFailure : " + errorMessage);
                }
            };

            AppsFlyerLib.getInstance().init(afDevKey, conversionListener, application.getApplicationContext());//应用层调用

        } else {
            PL.e("af dev key empty!");
        }

    }

    public static void activityOnCreate(Activity activity){

        String afDevKey = ResConfig.getAfDevKey(activity);
        if(!TextUtils.isEmpty(afDevKey)) {

//            延迟SDK启动的典型场景是应用需要先请求用户授权在Main Activity中收集数据，然后在获得授权后调用start。
//            Important notice
//            If the app calls start from an Activity, it should pass the Activity Context to the SDK.
//            Failing to pass the activity context will not trigger the SDK, thus losing attribution data and in-app events.
            AppsFlyerLib.getInstance().start(activity);//在activity中调用，这里必须传activity

        } else {
            PL.e("af dev key empty!");
        }
    }

    public static void logEvent(Context context, String eventName, Map<String, Object> map){

        if (context == null || SStringUtil.isEmpty(eventName)){
            return;
        }
        String afDevKey = ResConfig.getAfDevKey(context);
        if(TextUtils.isEmpty(afDevKey)) {
            return;
        }
        PL.i("af logEvent start name=" + eventName);
        AppsFlyerLib.getInstance().logEvent(context, eventName, map, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                PL.i("af logEvent onSuccess");
            }

            @Override
            public void onError(int i, @NonNull String s) {
                PL.i("af logEvent onError:" + s);
                logEventAgain(context, eventName, map);
            }
        });

    }

    //事件上报失败的话，重新上报一次
    public static void logEventAgain(Context context, String eventName, Map<String, Object> map){
        if (context == null || SStringUtil.isEmpty(eventName)){
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PL.i("af logEventAgain start name=" + eventName);
                AppsFlyerLib.getInstance().logEvent(context, eventName, map);
            }
        }, 2000);

    }
}

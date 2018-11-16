package com.gama.sdk.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.SimpleHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.gama.base.bean.AdsRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.response.SLoginResponse;
import com.gama.pay.gp.constants.GooglePayContant;
import com.gama.pay.gp.util.Purchase;
import com.gama.sdk.R;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGoogleProxy;
import com.google.ads.conversiontracking.AdWordsConversionReporter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gan on 2017/3/3.
 */

public class StarEventLogger {
    private static final String TAG = StarEventLogger.class.getSimpleName();

    public static void activateApp(Activity activity){

        try {
            AppsFlyerLib.getInstance().setCollectIMEI(false);
            AppsFlyerLib.getInstance().setCollectAndroidID(false);
            AppsFlyerLib.getInstance().startTracking(activity.getApplication(), ResConfig.getConfigInAssetsProperties(activity,"gama_ads_appflyer_dev_key"));

//            SFacebookProxy.activateApp(activity.getApplicationContext());

            // Google Android first open conversion tracking snippet
            // Add this code to the onCreate() method of your application activity
            String gama_ads_adword_conversionId = ResConfig.getConfigInAssetsProperties(activity,"gama_ads_adword_conversionId");
            if (SStringUtil.isNotEmpty(gama_ads_adword_conversionId)) {
                AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(),
                        gama_ads_adword_conversionId, ResConfig.getConfigInAssetsProperties(activity,"gama_ads_adword_label"), "0.00", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Android登录事件上报
     */
    public static void trackinLoginEvent(Activity activity, SLoginResponse loginResponse){
        try {
            PL.i(TAG, "登入上報");
            // TODO: 2018/4/16 Android登录事件名 gama_login_event_android
            String userId = loginResponse.getUserId();
            //Facebook上报
            Bundle b = new Bundle();
            b.putString(GamaAdsConstant.GAMA_EVENT_USER_ID, userId);
            SFacebookProxy.trackingEvent(activity, GamaAdsConstant.GAMA_EVENT_LOGIN, null, b);

            //firebase上报
            SGoogleProxy.firebaseAnalytics(activity, GamaAdsConstant.GAMA_EVENT_LOGIN, b);

            //AppsFlyer上报
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(GamaAdsConstant.GAMA_EVENT_USER_ID, userId);
            AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(), GamaAdsConstant.GAMA_EVENT_LOGIN, eventValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Android注册事件上报
     */
    public static void trackinRegisterEvent(Activity activity, SLoginResponse loginResponse){
        try {
            PL.i(TAG, "註冊上報");
            // TODO: 2018/4/16 Android注册事件名 gama_register_event_android
            String userId = loginResponse.getUserId();
            //Facebook上报
            Bundle b = new Bundle();
            b.putString(GamaAdsConstant.GAMA_EVENT_USER_ID, userId);
            SFacebookProxy.trackingEvent(activity, GamaAdsConstant.GAMA_EVENT_REGISTER, null, b);

            //firebase上报
            SGoogleProxy.firebaseAnalytics(activity, GamaAdsConstant.GAMA_EVENT_REGISTER, b);

            //AppsFlyer上报
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(GamaAdsConstant.GAMA_EVENT_USER_ID, userId);
            AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(), GamaAdsConstant.GAMA_EVENT_REGISTER, eventValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Android角色信息上报
     */
    public static void trackingRoleInfo(Activity activity, Map<String, Object> map) {
        try {
            String userId = GamaUtil.getUid(activity);
            if (map == null || map.isEmpty() || TextUtils.isEmpty(userId)) {
                PL.i(TAG, "沒有角色信息");
                return;
            } else {
                PL.i(TAG, "角色信息上報");
            }
            //Facebook上报
            Bundle b = new Bundle();
            b.putString(GamaAdsConstant.GAMA_EVENT_USER_ID, userId);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                b.putString(entry.getKey(), entry.getValue().toString());
            }
            SFacebookProxy.trackingEvent(activity, GamaAdsConstant.GAMA_EVENT_ROLE_INFO, null, b);

            //firebase上报
            SGoogleProxy.firebaseAnalytics(activity, GamaAdsConstant.GAMA_EVENT_ROLE_INFO, b);

            //AppsFlyer上报
            map.put(GamaAdsConstant.GAMA_EVENT_USER_ID, userId);
            AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(), GamaAdsConstant.GAMA_EVENT_ROLE_INFO, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计储值数据
     */
    public static void trackinPayEvent(Context context, Bundle bundle){
        if(bundle == null) {
            PL.i(TAG, "trackinPay bundle null");
            return;
        }
        Purchase purchase = (Purchase) bundle.getSerializable(GooglePayContant.PURCHASE_OBJECT);
        if(purchase == null) {
            PL.i(TAG, "trackinPay Purchase null");
            return;
        }
        String productId = purchase.getSku();
        int price = 0;
        try {
            Pattern p = Pattern.compile("\\d+(usd|USD)");
            Matcher m = p.matcher(productId);
            if(m.find()) {
                price = Integer.parseInt(m.group().toLowerCase().replace("usd", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Appsflyer上报
        Map<String,Object> eventValues = new HashMap<>();
        //下面是自定义的事件名
        eventValues.put(GamaAdsConstant.GAMA_EVENT_USER_ID, GamaUtil.getUid(context));
        eventValues.put(GamaAdsConstant.GAMA_EVENT_PRODUCT_ID, productId);
        eventValues.put(GamaAdsConstant.GAMA_EVENT_ORDERID, purchase.getOrderId());
        eventValues.put(GamaAdsConstant.GAMA_EVENT_PURCHASE_TIME, purchase.getPurchaseTime());
        //下面是AppsFlyer自己的事件名
        eventValues.put(AFInAppEventParameterName.REVENUE, price);
        eventValues.put(AFInAppEventParameterName.CURRENCY, "USD");
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, productId);
        AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.PURCHASE, eventValues);
    }

    public static void trackingWithEventName(Activity context, String eventName) {
        try {
            String userId = GamaUtil.getUid(context);
            HashMap<String, Object> map = new HashMap<>();
            map.put(GamaAdsConstant.GAMA_EVENT_ROLEID, GamaUtil.getRoleId(context));
            map.put(GamaAdsConstant.GAMA_EVENT_ROLENAME, GamaUtil.getRoleName(context));
            map.put(GamaAdsConstant.GAMA_EVENT_ROLE_LEVEL, GamaUtil.getRoleLevel(context));
            map.put(GamaAdsConstant.GAMA_EVENT_ROLE_VIP_LEVEL, GamaUtil.getRoleVip(context));
            map.put(GamaAdsConstant.GAMA_EVENT_SERVERCODE, GamaUtil.getServerCode(context));
            map.put(GamaAdsConstant.GAMA_EVENT_SERVERNAME, GamaUtil.getServerName(context));

            //Facebook上报
            Bundle b = new Bundle();
            b.putString(GamaAdsConstant.GAMA_EVENT_USER_ID, userId);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                b.putString(entry.getKey(), entry.getValue().toString());
            }
            SFacebookProxy.trackingEvent(context, eventName, null, b);

            //firebase上报,
            SGoogleProxy.firebaseAnalytics(context, eventName, b);

            //AppsFlyer上报
            map.put(GamaAdsConstant.GAMA_EVENT_USER_ID, userId);
            AppsFlyerLib.getInstance().trackEvent(context.getApplicationContext(), eventName, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Google ads id，不能在主线程调用
     */
    public static void registerGoogleAdId(final Context context){
        new  Thread(new Runnable() {
            @Override
            public void run() {
                String googleAdId = SGoogleProxy.getAdvertisingId(context.getApplicationContext());
                GamaUtil.saveGoogleAdId(context,googleAdId);
                PL.i("save google ad id-->" + googleAdId);
            }
        }).start();
    }

    /**
     * 本地保存的安装上报标识
     */
    private static final String GAMA_ADSINSTALLACTIVATION = "GAMA_ADSINSTALLACTIVATION";

    /**
     * 自家平台广告：安装上报
     */
    public static void reportInstallActivation(final Context context){

        if (SStringUtil.isNotEmpty(SPUtil.getSimpleString(context,GamaUtil.GAMA_SP_FILE,GAMA_ADSINSTALLACTIVATION))){
            return;
        }
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adsInstallActivation(context);
            }
        },10 * 1000);
    }
    public static void adsInstallActivation(final Context context){

        final AdsRequestBean adsRequestBean = new AdsRequestBean(context);
        adsRequestBean.setRequestUrl(ResConfig.getAdsPreferredUrl(context));
        adsRequestBean.setRequestMethod(context.getString(R.string.gama_ads_install_activation));
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest();
        simpleHttpRequest.setBaseReqeustBean(adsRequestBean);
        simpleHttpRequest.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                PL.i("ADS rawResult:" + rawResult);
                if (responseModel != null && responseModel.isRequestSuccess()){
                    SPUtil.saveSimpleInfo(context,GamaUtil.GAMA_SP_FILE,GAMA_ADSINSTALLACTIVATION,"adsInstallActivation");
                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        simpleHttpRequest.excute();
    }

}

package com.mw.sdk.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.base.bean.BasePayBean;
import com.mw.base.cfg.ResConfig;
import com.mw.base.constant.GamaCommonKey;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by gan on 2017/3/3.
 */

public class SdkEventLogger {
    private static final String TAG = SdkEventLogger.class.getSimpleName();

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

            // Google Android first open conversion tracking snippet
            // Add this code to the onCreate() method of your application activity
//            String gama_ads_adword_conversionId = ResConfig.getConfigInAssetsProperties(activity,"gama_ads_adword_conversionId");
//            if (SStringUtil.isNotEmpty(gama_ads_adword_conversionId)) {
//                AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(),
//                        gama_ads_adword_conversionId, ResConfig.getConfigInAssetsProperties(activity,"gama_ads_adword_label"), "0.00", false);
//            }

            //adjust
//            GamaAj.activeAj(activity);

//            TapDB.init(activity,activity.getString(R.string.tapdb_appId),activity.getString(R.string.tapdb_channel),
//                    activity.getString(R.string.tapdb_gameVersion));

            trackingWithEventName(activity, EventConstant.EventName.APP_OPEN.name(), null, null);

            //获取验证码开关
            if(SdkUtil.isNeedVfSwitch(activity)) {
               // getVfSwitch(activity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Android登录事件上报
     */
    public static void trackinLoginEvent(Context activity, SLoginResponse loginResponse){
        try {
            PL.i(TAG, "登入上報");
            if (activity == null || loginResponse == null){
                return;
            }
            String userId = loginResponse.getData().getUserId();
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(EventConstant.ParameterName.USER_ID, userId);
            String eventName = EventConstant.EventName.LOGIN_SUCCESS.name();
            trackingWithEventName(activity,eventName,eventValue,null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Android注册事件上报
     */
    public static void trackinRegisterEvent(Context activity, SLoginResponse loginResponse){
        try {
            PL.i(TAG, "註冊上報");
            if (activity == null || loginResponse == null){
                return;
            }

            String userId = loginResponse.getData().getUserId();
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(EventConstant.ParameterName.USER_ID, userId);
            String eventName = EventConstant.EventName.REGISTER_SUCCESS.name();
            trackingWithEventName(activity,eventName,eventValue,null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Android角色信息上报
     */
    public static void trackingRoleInfo(Context activity, Map<String, Object> map) {
        try {
            String userId = SdkUtil.getUid(activity);
            if (map == null || map.isEmpty() || TextUtils.isEmpty(userId)) {
                PL.i(TAG, "沒有角色信息");
                return;
            } else {
                PL.i(TAG, "角色信息上報");
            }
            map.put(EventConstant.ParameterName.USER_ID, userId);
            trackingWithEventName(activity,EventConstant.EventName.CREATE_ROLE.name(),map,null);

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
        BasePayBean payBean = (BasePayBean) bundle.getSerializable(GamaCommonKey.PURCHASE_DATA);

        String orderId = "";
        String productId = "";
        long purchaseTime;
        double usdPrice = 0;

        if(payBean != null) {
            PL.i(TAG, "trackinPay Purchase");
            orderId = payBean.getOrderId();
            purchaseTime = payBean.getPurchaseTime();
            productId = payBean.getProductId();
//            try {
//                Pattern p = Pattern.compile("\\d+(usd|USD)");
//                Matcher m = p.matcher(productId);
//                if(m.find()) {
//                    price = Integer.parseInt(m.group().toLowerCase().replace("usd", ""));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            usdPrice = payBean.getUsdPrice();

        } else {
            PL.i(TAG, "trackinPay Purchase null");
            return;
        }

        try {
            //Appsflyer上报
            Map<String, Object> eventValues = new HashMap<>();
            //下面是自定义的事件名
            eventValues.put(EventConstant.ParameterName.USER_ID, SdkUtil.getUid(context));
            eventValues.put(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));
            eventValues.put(EventConstant.ParameterName.PRODUCT_ID, productId);
            eventValues.put(EventConstant.ParameterName.ORDER_ID, orderId);
            eventValues.put(EventConstant.ParameterName.PURCHASE_TIME, purchaseTime);
            eventValues.put(EventConstant.ParameterName.PAY_VALUE, usdPrice);
            eventValues.put(EventConstant.ParameterName.CURRENCY, "USD");
            //下面是AppsFlyer自己的事件名
            eventValues.put(AFInAppEventParameterName.REVENUE, usdPrice);
            eventValues.put(AFInAppEventParameterName.CURRENCY, "USD");
            eventValues.put(AFInAppEventParameterName.CONTENT_ID, productId);

            //AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.PURCHASE, eventValues);

            //adjust
//            GamaAj.trackEvent(context, SdkAdsConstant.GAMA_EVENT_IAB, eventValues);

            if(!SdkUtil.getFirstPay(context)) {//检查是否首次充值
                trackingWithEventName(context, EventConstant.EventName.FIRST_PAY.name(), null, null);
                SdkUtil.saveFirstPay(context);
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void trackingWithEventName(Context context, String eventName){
        trackingWithEventName(context,eventName,null,null);
    }
    public static void trackingWithEventName(Context context, String eventName, Map<String, Object> map){
        trackingWithEventName(context,eventName,map,null);
    }
    public static void trackingWithEventName(Context context, String eventName, Map<String, Object> map, Set<EventConstant.EventReportChannel> mediaSet) {
        if(TextUtils.isEmpty(eventName)) {
            PL.e("上報事件名為空");
            return;
        }
        try {

            if(map == null) { //appsflyer的属性列表
                map = new HashMap<>();
            }
            map.put(EventConstant.ParameterName.TIME, System.currentTimeMillis() + "");
//            addEventParameterName(context, map);

            //facebook和firebase的属性列表
            Bundle b = new Bundle();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                b.putString(entry.getKey(), entry.getValue().toString());
            }

            if(mediaSet == null || mediaSet.isEmpty() || mediaSet.contains(EventConstant.EventReportChannel.EventReportAllChannel)) {
                PL.i("上报全部媒体");
                //Facebook上报
                SFacebookProxy.trackingEvent(context, eventName, null, b);

                //firebase上报,
                SGoogleProxy.firebaseAnalytics(context, eventName, b);

                //AppsFlyer上报
                AppsFlyerLib.getInstance().logEvent(context.getApplicationContext(), eventName, map);

                //adjust
//                GamaAj.trackEvent(context, eventName, map);
            } else {
                if(mediaSet.contains(EventConstant.EventReportChannel.EventReportFacebook)) {
                    PL.i("上报媒体1");
                    //Facebook上报
                    SFacebookProxy.trackingEvent(context, eventName, null, b);
                }
                if(mediaSet.contains(EventConstant.EventReportChannel.EventReportFirebase)) {
                    PL.i("上报媒体2");
                    //firebase上报,
                    SGoogleProxy.firebaseAnalytics(context, eventName, b);
                }
                if(mediaSet.contains(EventConstant.EventReportChannel.EventReportAppsflyer)) {
                    PL.i("上报媒体3");
                    //AppsFlyer上报
                    AppsFlyerLib.getInstance().logEvent(context.getApplicationContext(), eventName, map);
                }
                if(mediaSet.contains(EventConstant.EventReportChannel.EventReportAdjust)) {
                    PL.i("上报媒体4");
                    //adjust上报
//                    GamaAj.trackEvent(context, eventName, map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static Map<String, Object> addEventParameterName(Context context, Map<String, Object> map) {
//        if (!map.containsKey(EventConstant.ParameterName.ROLE_ID) && SStringUtil.isNotEmpty(SdkUtil.getRoleId(context))) {
//            map.put(EventConstant.ParameterName.ROLE_ID, SdkUtil.getRoleId(context));
//            map.put(EventConstant.ParameterName.ROLE_NAME, SdkUtil.getRoleName(context));
//            map.put(EventConstant.ParameterName.ROLE_LEVEL, SdkUtil.getRoleLevel(context));
//            map.put(EventConstant.ParameterName.VIP_LEVEL, SdkUtil.getRoleVip(context));
//            map.put(EventConstant.ParameterName.SERVER_CODE, SdkUtil.getServerCode(context));
//            map.put(EventConstant.ParameterName.SERVER_NAME, SdkUtil.getServerName(context));
//        }
//        String userId = SdkUtil.getUid(context);
//        if (SStringUtil.isNotEmpty(userId)) {
//            map.put(EventConstant.ParameterName.USER_ID, userId);
//        }
//        map.put(EventConstant.ParameterName.TIME, System.currentTimeMillis() + "");
//        return map;
//    }

    /**
     * 获取Google ads id，不能在主线程调用
     */
    public static void registerGoogleAdId(final Context context){
        new  Thread(new Runnable() {
            @Override
            public void run() {
                String googleAdId = SGoogleProxy.getAdvertisingId(context.getApplicationContext());
                SdkUtil.saveGoogleAdId(context,googleAdId);
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
   /* public static void reportInstallActivation(final Context context){

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
        GsInstallReferrer.initReferrerClient(context, new GsInstallReferrer.GsInstallReferrerCallback() {
            @Override
            public void onResult(GsInstallReferrerBean bean) {
                    final AdsRequestBean adsRequestBean = new AdsRequestBean(context);
                    adsRequestBean.setRequestUrl(ResConfig.getAdsPreferredUrl(context));
                    if (GamaUtil.isInterfaceSurfixWithApp(context)) {
                        adsRequestBean.setRequestMethod(SdkAdsConstant.GsAdsRequestMethod.GS_REQUEST_METHOD_INSTALL);
                    } else {
                        adsRequestBean.setRequestMethod(SdkAdsConstant.GamaAdsRequestMethod.GAMA_REQUEST_METHOD_INSTALL);
                    }
                    if (bean != null) {
                        adsRequestBean.setAppInstallTime(bean.getAppInstallTime());
                        adsRequestBean.setReferrerClickTime(bean.getReferrerClickTime());
                        adsRequestBean.setReferrer(bean.getReferrerUrl());
                    }
                    SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest();
                    simpleHttpRequest.setBaseReqeustBean(adsRequestBean);
                    simpleHttpRequest.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
                        @Override
                        public void success(BaseResponseModel responseModel, String rawResult) {
                            PL.i(TAG, "ADS rawResult:" + rawResult);
                            if (responseModel != null && responseModel.isRequestSuccess()){
                                SPUtil.saveSimpleInfo(context,GamaUtil.GAMA_SP_FILE,GAMA_ADSINSTALLACTIVATION,"adsInstallActivation");
                            }
                        }

                        @Override
                        public void timeout(String code) {
                            PL.i(TAG, "ADS timeout");
                        }

                        @Override
                        public void noData() {
                        }

                        @Override
                        public void cancel() {
                        }
                    });
                    simpleHttpRequest.excute();
                }
        });
    }*/

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

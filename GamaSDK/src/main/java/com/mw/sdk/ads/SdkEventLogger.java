package com.mw.sdk.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.SimpleHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.mw.base.bean.AdsRequestBean;
import com.mw.base.bean.BasePayBean;
import com.mw.base.cfg.ConfigBean;
import com.mw.base.cfg.ResConfig;
import com.mw.base.constant.GamaCommonKey;
import com.mw.base.utils.GamaUtil;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.login.execute.GamaVfcodeSwitchRequestTask;
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

            trackingWithEventName(activity, SdkAdsConstant.GAMA_EVENT_OPEN, null, null);

            //获取验证码开关
            if(GamaUtil.isNeedVfSwitch(activity)) {
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
            // TODO: 2018/4/16 Android登录事件名 gama_login_event_android
            String userId = loginResponse.getData().getUserId();
            //Facebook上报
            Bundle b = new Bundle();
            b.putString(SdkAdsConstant.GAMA_EVENT_USER_ID, userId);
            SFacebookProxy.trackingEvent(activity, SdkAdsConstant.GAMA_EVENT_LOGIN, null, b);

            //firebase上报
            SGoogleProxy.firebaseAnalytics(activity, SdkAdsConstant.GAMA_EVENT_LOGIN, b);

            //AppsFlyer上报
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(SdkAdsConstant.GAMA_EVENT_USER_ID, userId);
           // AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(), SdkAdsConstant.GAMA_EVENT_LOGIN, eventValue);

            //adjust
//            GamaAj.trackEvent(activity.getApplicationContext(), SdkAdsConstant.GAMA_EVENT_LOGIN, eventValue);

//            TapDB.setUser(userId);

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
            // TODO: 2018/4/16 Android注册事件名 gama_register_event_android
            String userId = loginResponse.getData().getUserId();
            //Facebook上报
            Bundle b = new Bundle();
            b.putString(SdkAdsConstant.GAMA_EVENT_USER_ID, userId);
            SFacebookProxy.trackingEvent(activity, SdkAdsConstant.GAMA_EVENT_REGISTER, null, b);

            //firebase上报
            SGoogleProxy.firebaseAnalytics(activity, SdkAdsConstant.GAMA_EVENT_REGISTER, b);

            //AppsFlyer上报
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(SdkAdsConstant.GAMA_EVENT_USER_ID, userId);
           // AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(), SdkAdsConstant.GAMA_EVENT_REGISTER, eventValue);

            //adjust
//            GamaAj.trackEvent(activity.getApplicationContext(), SdkAdsConstant.GAMA_EVENT_REGISTER, eventValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Android角色信息上报
     */
    public static void trackingRoleInfo(Context activity, Map<String, Object> map) {
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
            b.putString(SdkAdsConstant.GAMA_EVENT_USER_ID, userId);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                b.putString(entry.getKey(), entry.getValue().toString());
            }
            SFacebookProxy.trackingEvent(activity, SdkAdsConstant.GAMA_EVENT_ROLE_INFO, null, b);

            //firebase上报
            SGoogleProxy.firebaseAnalytics(activity, SdkAdsConstant.GAMA_EVENT_ROLE_INFO, b);

            //AppsFlyer上报
            map.put(SdkAdsConstant.GAMA_EVENT_USER_ID, userId);
           // AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(), SdkAdsConstant.GAMA_EVENT_ROLE_INFO, map);
            //adjust
//            GamaAj.trackEvent(activity, SdkAdsConstant.GAMA_EVENT_ROLE_INFO, map);
            //计算留存
//            GamaAdsUtils.caculateRetention(activity, userId);
            //计算在线时长
//            GamaAdsUtils.uploadOnlineTime(activity, GamaAdsUtils.GamaOnlineType.TYPE_CHANGE_ROLE);
            //上报给gama服务器
//            GamaAdsUtils.upLoadRoleInfo(activity, map);

//            TapDB.setName(map.get(GamaAdsConstant.GAMA_EVENT_ROLENAME).toString());
//            TapDB.setServer(map.get(GamaAdsConstant.GAMA_EVENT_SERVERCODE).toString());
//            TapDB.setLevel(map.get(GamaAdsConstant.GAMA_EVENT_ROLE_LEVEL).toString());
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
            eventValues.put(SdkAdsConstant.GAMA_EVENT_USER_ID, GamaUtil.getUid(context));
            eventValues.put(SdkAdsConstant.GAMA_EVENT_PRODUCT_ID, productId);
            eventValues.put(SdkAdsConstant.GAMA_EVENT_ORDERID, orderId);
            eventValues.put(SdkAdsConstant.GAMA_EVENT_PURCHASE_TIME, purchaseTime);
            eventValues.put(SdkAdsConstant.GAMA_EVENT_PAY_VALUE, usdPrice);
            eventValues.put(SdkAdsConstant.GAMA_EVENT_CURRENCY, "USD");
            //下面是AppsFlyer自己的事件名
            eventValues.put(AFInAppEventParameterName.REVENUE, usdPrice);
            eventValues.put(AFInAppEventParameterName.CURRENCY, "USD");
            eventValues.put(AFInAppEventParameterName.CONTENT_ID, productId);

            //AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.PURCHASE, eventValues);

            //adjust
//            GamaAj.trackEvent(context, SdkAdsConstant.GAMA_EVENT_IAB, eventValues);

            if(!GamaUtil.getFirstPay(context)) {
                trackingWithEventName(context, SdkAdsConstant.GAMA_EVENT_FIRSTPAY, null, null);
                GamaUtil.saveFirstPay(context);
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void trackingWithEventName(Context context, String eventName, Map<String, Object> map, Set<SdkAdsConstant.EventReportChannel> mediaSet) {
        if(TextUtils.isEmpty(eventName)) {
            PL.e("上報事件名為空");
            return;
        }
        try {
            String userId = GamaUtil.getUid(context);
            if(map == null) { //appsflyer的属性列表
                map = new HashMap<>();
            }
            if (!map.containsKey(SdkAdsConstant.GAMA_EVENT_ROLEID) && SStringUtil.isNotEmpty(GamaUtil.getRoleId(context))) {
                map.put(SdkAdsConstant.GAMA_EVENT_ROLEID, GamaUtil.getRoleId(context));
                map.put(SdkAdsConstant.GAMA_EVENT_ROLENAME, GamaUtil.getRoleName(context));
                map.put(SdkAdsConstant.GAMA_EVENT_ROLE_LEVEL, GamaUtil.getRoleLevel(context));
                map.put(SdkAdsConstant.GAMA_EVENT_ROLE_VIP_LEVEL, GamaUtil.getRoleVip(context));
                map.put(SdkAdsConstant.GAMA_EVENT_SERVERCODE, GamaUtil.getServerCode(context));
                map.put(SdkAdsConstant.GAMA_EVENT_SERVERNAME, GamaUtil.getServerName(context));
            }

            map.put(SdkAdsConstant.GAMA_EVENT_USER_ID, userId);

            //facebook和firebase的属性列表
            Bundle b = new Bundle();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                b.putString(entry.getKey(), entry.getValue().toString());
            }

            if(mediaSet == null || mediaSet.isEmpty() || mediaSet.contains(SdkAdsConstant.EventReportChannel.EventReportAllChannel)) {
                PL.i("上报全部媒体");
                //Facebook上报
                SFacebookProxy.trackingEvent(context, eventName, null, b);

                //firebase上报,
                SGoogleProxy.firebaseAnalytics(context, eventName, b);

                //AppsFlyer上报
              //  AppsFlyerLib.getInstance().trackEvent(context.getApplicationContext(), eventName, map);

                //adjust
//                GamaAj.trackEvent(context, eventName, map);
            } else {
                if(mediaSet.contains(SdkAdsConstant.EventReportChannel.EventReportFacebook)) {
                    PL.i("上报媒体1");
                    //Facebook上报
                    SFacebookProxy.trackingEvent(context, eventName, null, b);
                }
                if(mediaSet.contains(SdkAdsConstant.EventReportChannel.EventReportFirebase)) {
                    PL.i("上报媒体2");
                    //firebase上报,
                    SGoogleProxy.firebaseAnalytics(context, eventName, b);
                }
                if(mediaSet.contains(SdkAdsConstant.EventReportChannel.EventReportAppsflyer)) {
                    PL.i("上报媒体3");
                    //AppsFlyer上报
                  //  AppsFlyerLib.getInstance().trackEvent(context.getApplicationContext(), eventName, map);
                }
                if(mediaSet.contains(SdkAdsConstant.EventReportChannel.EventReportAdjust)) {
                    PL.i("上报媒体4");
                    //adjust上报
//                    GamaAj.trackEvent(context, eventName, map);
                }
            }
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
    }

    private static void getVfSwitch(final Activity activity) {
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
    }
	
	public static void onResume(Activity activity) {
//        GamaAj.onResume(activity);
//        TapDB.onResume(activity);
    }

    public static void onPause(Activity activity) {
//        GamaAj.onPause(activity);
//        TapDB.onStop(activity);
    }

}

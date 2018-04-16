package com.gama.sdk.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.appsflyer.AppsFlyerLib;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.SimpleHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.gama.base.bean.AdsRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGoogleProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gan on 2017/3/3.
 */

public class StarEventLogger {

    public static void activateApp(Activity activity){

        try {

            AppsFlyerLib.getInstance().startTracking(activity.getApplication(), ResConfig.getConfigInAssets(activity,"gama_ads_appflyer_dev_key"));

            SFacebookProxy.activateApp(activity.getApplicationContext());

            // Google Android first open conversion tracking snippet
            // Add this code to the onCreate() method of your application activity
            String gama_ads_adword_conversionId = ResConfig.getConfigInAssets(activity,"gama_ads_adword_conversionId");
            if (SStringUtil.isNotEmpty(gama_ads_adword_conversionId)) {
                AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(),
                        gama_ads_adword_conversionId, ResConfig.getConfigInAssets(activity,"gama_ads_adword_label"), "0.00", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Android登录事件上报
     */
    public static void trackinLoginEvent(Activity activity){
        // TODO: 2018/4/16 Android登录事件名 gama_login_event_android
        SFacebookProxy.trackingEvent(activity,"gama_login_event_android");

        Map<String, Object> eventValue = new HashMap<String, Object>();
        AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(),"gama_login_event_android",eventValue);

    }

    /**
     * Android注册事件上报
     */
    public static void trackinRegisterEvent(Activity activity){
        // TODO: 2018/4/16 Android注册事件名 gama_register_event_android
        SFacebookProxy.trackingEvent(activity,"gama_register_event_android");

        Map<String, Object> eventValue = new HashMap<String, Object>();
//        eventValue.put(AFInAppEventParameterName.REVENUE,1);
        AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(),"gama_register_event_android",eventValue);

    }

    public static void trackinPayEvent(Activity activity, double payVaule){
        SFacebookProxy.trackingEvent(activity,"pay_android", payVaule);
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

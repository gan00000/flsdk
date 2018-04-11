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
import com.gama.base.utils.StarPyUtil;
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

            AppsFlyerLib.getInstance().startTracking(activity.getApplication(), ResConfig.getConfigInAssets(activity,"star_ads_appflyer_dev_key"));

            SFacebookProxy.activateApp(activity.getApplicationContext());

            // Google Android first open conversion tracking snippet
            // Add this code to the onCreate() method of your application activity
            String star_ads_adword_conversionId = ResConfig.getConfigInAssets(activity,"star_ads_adword_conversionId");
            if (SStringUtil.isNotEmpty(star_ads_adword_conversionId)) {
                AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(),
                        star_ads_adword_conversionId, ResConfig.getConfigInAssets(activity,"star_ads_adword_label"), "0.00", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void trackinLoginEvent(Activity activity){
        SFacebookProxy.trackingEvent(activity,"starpy_login_event_android");

        Map<String, Object> eventValue = new HashMap<String, Object>();
        AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(),"starpy_login_event_android",eventValue);

    }

    public static void trackinRegisterEvent(Activity activity){
        SFacebookProxy.trackingEvent(activity,"starpy_register_event_android");

        Map<String, Object> eventValue = new HashMap<String, Object>();
//        eventValue.put(AFInAppEventParameterName.REVENUE,1);
        AppsFlyerLib.getInstance().trackEvent(activity.getApplicationContext(),"starpy_register_event_android",eventValue);

    }

    public static void trackinPayEvent(Activity activity, double payVaule){
        SFacebookProxy.trackingEvent(activity,"pay_android", payVaule);
    }

    public static void registerGoogleAdId(final Context context){
        new  Thread(new Runnable() {
            @Override
            public void run() {
                String googleAdId = SGoogleProxy.getAdvertisingId(context.getApplicationContext());
                StarPyUtil.saveGoogleAdId(context,googleAdId);
                PL.i("save google ad id-->" + googleAdId);
            }
        }).start();
    }

    private static final String STAR_PY_ADSINSTALLACTIVATION = "STAR_PY_ADSINSTALLACTIVATION";
    public static void reportInstallActivation(final Context context){

        if (SStringUtil.isNotEmpty(SPUtil.getSimpleString(context,StarPyUtil.STAR_PY_SP_FILE,STAR_PY_ADSINSTALLACTIVATION))){
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
        adsRequestBean.setRequestMethod(context.getString(R.string.star_ads_install_activation));
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest();
        simpleHttpRequest.setBaseReqeustBean(adsRequestBean);
        simpleHttpRequest.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                PL.i("ADS rawResult:" + rawResult);
                if (responseModel != null && responseModel.isRequestSuccess()){
                    SPUtil.saveSimpleInfo(context,StarPyUtil.STAR_PY_SP_FILE,STAR_PY_ADSINSTALLACTIVATION,"adsInstallActivation");
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

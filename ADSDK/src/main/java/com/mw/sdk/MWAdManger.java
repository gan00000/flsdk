package com.mw.sdk;

import android.app.Activity;

import com.applovin.sdk.AppLovinSdk;
import com.mw.sdk.ad.IMwAd;
import com.mw.sdk.ad.applovin.ApplovinManager;

public class MWAdManger {

    IMwAd iMwAd;
    private static MWAdManger mMWAdManger;

    public static MWAdManger getInstance(){
        if (mMWAdManger == null){
            mMWAdManger = new MWAdManger();
        }
        return mMWAdManger;
    }

    public void initAd(Activity activity) {

        /*ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        // 1. 配置请求参数（设置是否面向EEA/UK用户）
        ConsentRequestParameters params = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false) // 如果用户未满法定年龄，设为true
                .build();
        Log.d("PL_LOG","start requestConsentInfoUpdate");
        consentInformation.requestConsentInfoUpdate(activity, params, new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
            @Override
            public void onConsentInfoUpdateSuccess() {
                Log.d("PL_LOG","onConsentInfoUpdateSuccess");
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, new ConsentForm.OnConsentFormDismissedListener() {
                    @Override
                    public void onConsentFormDismissed(@Nullable FormError formError) {
                        Log.d("PL_LOG","onConsentFormDismissed");
                    }
                });
            }
        }, new ConsentInformation.OnConsentInfoUpdateFailureListener() {
            @Override
            public void onConsentInfoUpdateFailure(@NonNull FormError formError) {
                Log.d("PL_LOG","onConsentInfoUpdateFailure:" + formError.getMessage());
            }
        });*/

        iMwAd = new ApplovinManager();
        iMwAd.initAd(activity);
    }

    public  void showAd(Activity activity, AdCallback adCallback) {
        if (iMwAd != null){
            iMwAd.setAdCallback(adCallback);
            iMwAd.showAdView(activity);
        }
    }

    public void showMediationDebugger(Activity activity){
        AppLovinSdk.getInstance( activity ).showMediationDebugger();
    }

    public void destroy(Activity activity){
        if (iMwAd != null){
            iMwAd.destroy(activity);
        }
    }

}

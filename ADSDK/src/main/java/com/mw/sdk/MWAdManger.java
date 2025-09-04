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

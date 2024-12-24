package com.mw.sdk.ad.applovin;
import android.app.Activity;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.AdCallback;
import com.mw.sdk.ad.IMwAd;
import com.mw.sdk.ad.R;

import java.util.concurrent.TimeUnit;

public class ApplovinManager implements MaxRewardedAdListener, MaxAdRevenueListener , IMwAd {

    private MaxRewardedAd rewardedAd;
    private int retryAttempt;

    private AdCallback adCallback;

//    private static ApplovinManager applovinManager;


    public void setAdCallback(AdCallback adCallback) {
        this.adCallback = adCallback;
    }

//    public synchronized static ApplovinManager getInstance(){
//        if (applovinManager == null){
//            applovinManager = new ApplovinManager();
//        }
//        return applovinManager;
//    }

    public void initAd(Activity activity) {
        if (activity == null) {
            return;
        }
        String adKey = activity.getString(R.string.sdk_app_ad_key);
        if (SStringUtil.isEmpty(adKey)){
            PL.i("initAd adKey is empty");
            return;
        }
        PL.i("initAd adKey=" + adKey);
        // Create the initialization configuration
        AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration.builder(adKey, activity)
                .setMediationProvider(AppLovinMediationProvider.MAX)
//                .setTestDeviceAdvertisingIds(Arrays.asList(SdkUtil.getGoogleAdId(activity)))
                .build();

        // Initialize the SDK with the configuration
        AppLovinSdk.getInstance(activity).initialize(initConfig, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration sdkConfig) {
                // Start loading ads
                PL.i("AppLovinSdk onSdkInitialized finish");
                startLoadAds(activity);
            }
        });

    }

    private void startLoadAds(Activity activity) {

        if (activity == null) {
            return;
        }
        PL.i("startLoadAds...");
        String adUnitId = activity.getString(R.string.sdk_ad_unit_id);
        if (SStringUtil.isEmpty(adUnitId)){
            return;
        }
        PL.i("startLoadAds adUnitId=" + adUnitId);
        rewardedAd = MaxRewardedAd.getInstance(adUnitId, activity);

        rewardedAd.setListener(this);
        rewardedAd.setRevenueListener(this);

        // Load the first ad.
        rewardedAd.loadAd();
    }

    public void showAdView(Activity activity) {
        if (activity != null && rewardedAd != null && rewardedAd.isReady()) {
            rewardedAd.showAd(activity);
        }
    }

    public void destroy(Activity activity) {
        if (rewardedAd != null) {
            rewardedAd.setListener(null);
            rewardedAd.setRevenueListener(null);
        }
    }


    @Override
    public void onUserRewarded(@NonNull MaxAd maxAd, @NonNull MaxReward maxReward) {
        // Rewarded ad was displayed and user should receive the reward
        PL.i("onUserRewarded..." + "Rewarded user: " + maxReward.getAmount() + " " + maxReward.getLabel());
        if (adCallback != null){
            adCallback.onUserRewarded("");
        }
    }

    @Override
    public void onAdLoaded(@NonNull MaxAd maxAd) {
        PL.i("onAdLoaded...");
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'
        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd maxAd) {
        PL.i("onAdDisplayed...");
        if (adCallback != null){
            adCallback.onAdDisplayed("");
        }
    }

    @Override
    public void onAdHidden(@NonNull MaxAd maxAd) {
        PL.i("onAdHidden...");
        // Rewarded ad is hidden. Pre-load the the next ad
        if (adCallback != null){
            adCallback.onAdHidden("");
        }
        if (rewardedAd == null){
            return;
        }
        rewardedAd.loadAd();
    }

    @Override
    public void onAdClicked(@NonNull MaxAd maxAd) {
        PL.i("onAdClicked...");
        if (adCallback != null){
            adCallback.onAdClicked("");
        }
    }

    @Override
    public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError maxError) {
        PL.i("onAdLoadFailed..adUnitId=" + adUnitId + ", maxError=" + maxError.getMessage());
        // Rewarded ad failed to load. We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds).
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rewardedAd != null) {
                    rewardedAd.loadAd();
                }
            }
        }, delayMillis);

    }

    @Override
    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
        PL.i("onAdDisplayFailed...");
        // Rewarded ad failed to display. We recommend loading the next ad.
        if (rewardedAd == null){
            return;
        }
        rewardedAd.loadAd();
    }

    @Override
    public void onAdRevenuePaid(@NonNull MaxAd maxAd) {//广告收入
        PL.i("onAdRevenuePaid...");
    }
}

package com.mw.sdk.ad.applovin;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

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
import com.mw.sdk.AdCallback;
import com.mw.sdk.ad.IMwAd;
import com.mw.sdk.ad.R;

import java.util.concurrent.TimeUnit;

public class ApplovinManager implements MaxRewardedAdListener, MaxAdRevenueListener , IMwAd {

    private MaxRewardedAd rewardedAd;
    private int retryAttempt;

    private AdCallback adCallback;

//    private static ApplovinManager applovinManager;
    public static final String TAG = "ApplovinManager";


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
        if (TextUtils.isEmpty(adKey)){
            Log.i(TAG,"initAd adKey is empty");
            return;
        }
        Log.i(TAG,"initAd adKey=" + adKey);
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
                Log.i(TAG,"AppLovinSdk onSdkInitialized finish");
                startLoadAds(activity);
            }
        });

    }

    private void startLoadAds(Activity activity) {

        if (activity == null) {
            return;
        }
        Log.i(TAG,"startLoadAds...");
        String adUnitId = activity.getString(R.string.sdk_ad_unit_id);
        if (TextUtils.isEmpty(adUnitId)){
            return;
        }
        Log.i(TAG,"startLoadAds adUnitId=" + adUnitId);
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
        Log.i(TAG,"onUserRewarded..." + "Rewarded user: " + maxReward.getAmount() + " " + maxReward.getLabel());
        if (adCallback != null){
            adCallback.onUserRewarded("");
        }
    }

    @Override
    public void onAdLoaded(@NonNull MaxAd maxAd) {
        Log.i(TAG,"onAdLoaded...");
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'
        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd maxAd) {
        Log.i(TAG,"onAdDisplayed...");
        if (adCallback != null){
            adCallback.onAdDisplayed("");
        }
    }

    @Override
    public void onAdHidden(@NonNull MaxAd maxAd) {
        Log.i(TAG,"onAdHidden...");
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
        Log.i(TAG,"onAdClicked...");
        if (adCallback != null){
            adCallback.onAdClicked("");
        }
    }

    @Override
    public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError maxError) {
        Log.i(TAG,"onAdLoadFailed..adUnitId=" + adUnitId + ", maxError=" + maxError.getMessage());
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
        Log.i(TAG,"onAdDisplayFailed...");
        // Rewarded ad failed to display. We recommend loading the next ad.
        if (rewardedAd == null){
            return;
        }
        rewardedAd.loadAd();
    }

    @Override
    public void onAdRevenuePaid(@NonNull MaxAd maxAd) {//广告收入
        Log.i(TAG,"onAdRevenuePaid...");
    }
}

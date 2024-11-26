package com.thirdlib.applovin;

import android.app.Activity;

import com.mw.sdk.callback.AdCallback;

public interface IMwAd {

    public void initAd(Activity activity);

    public void setAdCallback(AdCallback adCallback);

    public void showAdView(Activity activity);

    public void destroy(Activity activity);

}

package com.mw.sdk;

public interface AdCallback {

    void onAdClicked(String msg);
    void onAdDisplayed(String msg);
    void onAdHidden(String msg);
    void onUserRewarded(String msg);
}

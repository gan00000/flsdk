package com.core.base.js;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.pay.MWWebPayActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class WebViewJsObj {

    Activity activity;

    public WebViewJsObj(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void close(){
        PL.i("js close");
        if (activity != null){
            activity.finish();
        }
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void trackEvent(String eventName){
        PL.i("js trackEvent:" + eventName);
        if (activity != null && SStringUtil.isNotEmpty(eventName)){
//            if (SStringUtil.isNotEmpty(dataJson)){
//
//            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SdkEventLogger.sendEventToSever(activity, eventName);
                    SdkEventLogger.trackingWithEventName(activity, eventName, null, EventConstant.AdType.AdTypeAllChannel);
                }
            });
        }
    }
}

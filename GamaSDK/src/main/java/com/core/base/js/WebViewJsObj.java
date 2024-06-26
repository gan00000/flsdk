package com.core.base.js;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;

public class WebViewJsObj {

    Activity activity;
    Dialog dialog;

    public WebViewJsObj(Activity activity) {
        this.activity = activity;
    }

    public WebViewJsObj(Activity activity, Dialog dialog) {
        this.activity = activity;
        this.dialog = dialog;
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void close(){
        PL.i("js close");

        if (activity != null && dialog != null){//如果网页是使用dialog打开，则只关闭dialog
            dialog.dismiss();
            return;
        }
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

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void openSysBrowser(String url){
        PL.i("js openSysBrowser");

        if (activity != null){
            Intent  intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        }
    }
}

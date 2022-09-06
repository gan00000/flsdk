package com.core.base.js;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.core.base.utils.PL;
import com.mw.sdk.pay.MWWebPayActivity;

public class WebViewJsObj {

    Activity activity;

    public WebViewJsObj(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void close(){
        if (activity != null){
            activity.finish();
        }
    }
}

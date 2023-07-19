package com.mybase.js;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.webkit.JavascriptInterface;

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

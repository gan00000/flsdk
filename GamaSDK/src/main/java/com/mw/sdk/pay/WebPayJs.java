package com.mw.sdk.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.core.base.utils.PL;
import com.mw.base.bean.SPayType;
import com.mw.sdk.out.BaseSdkImpl;

public class WebPayJs {

    MWWebPayActivity activity;

    public WebPayJs(MWWebPayActivity activity) {
        this.activity = activity;
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void googlePay(String productId)
    {
        PL.i("js googlePay productId=" + productId);
        if (this.activity != null){
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.googlePay(productId);
                }
            });
        }
    }

    /**
     * 充值通知，提供给网页使用
     * @param success  是否成功
     * @param productId  商品id
     */
    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void onPayFinish(boolean success,String productId)
    {
        PL.i("js onPayFinish productId=" + productId);
        if (this.activity != null){
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.payFinish(success,productId);
                }
            });
        }
    }
}

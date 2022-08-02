package com.core.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.core.base.utils.AppUtil;
import com.core.base.utils.MarketUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by gan on 2016/12/2.
 */

public class BaseWebViewClient extends WebViewClient {

    protected static boolean DISABLE_SSL_CHECK_FOR_TESTING = false;

    Activity activity;

    private BaseWebViewClient() {
    }

    public BaseWebViewClient(Activity activity) {
        this.activity = activity;
    }

    /**
     * @param view
     * @param url
     * @deprecated
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return overrideUrlLoading(view,url);
//        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return overrideUrlLoading(view,request.getUrl().toString());
//        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        PL.i("onPageFinished url:" + url);
    }

    public boolean overrideUrlLoading(WebView webView,String url){

        PL.i("overrideUrlLoading url:" + url);

        /*if (url.startsWith("sms:")) {
            try {
                url = URLDecoder.decode(url, "utf-8");
                String[] str = url.split("\\?");
                if (str != null && str.length >= 2) {

                    String[]  str1 = str[0].split("\\:");
                    String[]  str2 = str[1].split("\\=");
                    AppUtil.sendMessage(activity,str1[1], str2[1]);
                }
            } catch (UnsupportedEncodingException e) {
                webView.loadUrl(url);
            }

        }else if(url.startsWith("https://line.naver.jp/R/msg/text/?") || url.startsWith("https://line.me/R/msg/text/?")){
            AppUtil.openInOsWebApp(activity, url);

        }else if(url.startsWith("whatsapp//")){

            AppUtil.openInOsWebApp(activity, url);

        } else */if (url.toLowerCase().startsWith("http:") || url.toLowerCase().startsWith("https:") || url.toLowerCase().startsWith("file")) {

            if (url.startsWith("https://play.google.com/store/apps/details?id=")){
                MarketUtil.toGooglePaly(activity, url);
            }else{
                webView.loadUrl(url);
            }
            return true;
        } else {
            Intent intent;
            String packageName = "";
            try {
                if (url.startsWith("intent://")) { //MyCard、Line
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    packageName = intent.getPackage();
                } else { //第三方支付
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                }
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);

            } catch (Exception ex) {

                if (SStringUtil.isNotEmpty(packageName)) {
                    MarketUtil.openMarket(activity, packageName);
                }else{
//                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    PL.e("overrideUrlLoading error:" + ex.getMessage());
                }
            }
        }
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (DISABLE_SSL_CHECK_FOR_TESTING) {
            handler.cancel();
        } else {//永远只走这里，这么写是为了躲过google警告
            handler.proceed();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        PL.w("onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)");
//        handleReceivedError(view, request.getUrl().toString());
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        PL.w("onReceivedError(WebView view, int errorCode, String description, String failingUrl)");
//        handleReceivedError(view, failingUrl);
    }

    public void handleReceivedError(WebView webView,String url){
        try {
            PL.w("ERROR URL:" + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.mw.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.core.base.js.WebViewJsObj;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.mw.base.widget.SWebView;

/**
 * Created by GanYuanrong on 2016/12/1.
 */

public class MWBaseWebActivity extends SBaseSdkActivity {

    public static final String PLAT_WEBVIEW_URL = "PLAT_WEBVIEW_URL";
    public static final String PLAT_WEBVIEW_TITLE = "PLAT_WEBVIEW_TITLE";
    protected String webUrl;
    protected String webTitle;
//    private ProgressBar progressBar;

    protected SWebViewLayout sWebViewLayout;
    protected SWebView sWebView;

    private WebViewJsObj webViewJsObj;

    public static Intent create(Activity activity, String title, String url){

        Intent intent = new Intent(activity, MWBaseWebActivity.class);
        intent.putExtra(PLAT_WEBVIEW_TITLE,title);
        intent.putExtra(PLAT_WEBVIEW_URL,url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_web_act_layout);

        if (getIntent() != null) {
            webUrl = getIntent().getStringExtra(PLAT_WEBVIEW_URL);
            webTitle = getIntent().getStringExtra(PLAT_WEBVIEW_TITLE);
        }

        sWebViewLayout = findViewById(R.id.sl_web_act);
        sWebView = sWebViewLayout.getsWebView();
        sWebViewLayout.getTitleHeaderView().setVisibility(View.GONE);


        webViewJsObj = new WebViewJsObj(this);
        sWebView.addJavascriptInterface(webViewJsObj,"MWSDK");
        if (superAutoLoadUrl()) {
            if (TextUtils.isEmpty(webUrl)){
                ToastUtils.toast(getApplicationContext(),"url error");
                PL.i("webUrl is empty");
            }else{
                sWebView.loadUrl(webUrl);
            }
        }
    }


    protected boolean superAutoLoadUrl(){
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (sWebView != null){
            sWebView.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sWebView != null){
            sWebView.clearCache(true);
            sWebView.clearHistory();
            sWebView.destroy();
            sWebView = null;
            PL.i("destory webview");
        }
    }

}

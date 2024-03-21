package com.ldy.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.mybase.js.WebViewJsObj;
import com.mybase.utils.PL;
import com.mybase.utils.ToastUtils;
import com.ldy.base.widget.SWebView;

/**
 * Created by GanYuanrong on 2016/12/1.
 */

public class DyBaseWebActivity extends SBaseSdkActivity {

    public static final String PLAT_WEBVIEW_URL = "PLAT_WEBVIEW_URL";
    public static final String PLAT_WEBVIEW_TITLE = "PLAT_WEBVIEW_TITLE";
    protected String webUrl;
    protected String webTitle;
//    private ProgressBar progressBar;

    protected SWebViewLayout sWebViewLayout;
    protected SWebView sWebView;

    private WebViewJsObj webViewJsObj;

    public static Intent create(Activity activity, String title, String url){

        Intent intent = new Intent(activity, DyBaseWebActivity.class);
        intent.putExtra(PLAT_WEBVIEW_TITLE,title);
        intent.putExtra(PLAT_WEBVIEW_URL,url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sady_such65758);

        if (getIntent() != null) {
            webUrl = getIntent().getStringExtra(PLAT_WEBVIEW_URL);
            webTitle = getIntent().getStringExtra(PLAT_WEBVIEW_TITLE);
        }

        sWebViewLayout = findViewById(R.id.mId_collectionfic_nausitude);
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

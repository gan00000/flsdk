package com.mw.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.mw.base.widget.SWebView;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;

/**
 * Created by GanYuanrong on 2016/12/1.
 */

public class SWebViewActivity extends SBaseSdkActivity {

    public static final String PLAT_WEBVIEW_URL = "PLAT_WEBVIEW_URL";
    public static final String PLAT_WEBVIEW_TITLE = "PLAT_WEBVIEW_TITLE";
    private String webUrl;
    String webTitle;
    private ProgressBar progressBar;

    private SWebViewLayout sWebViewLayout;
    private SWebView sWebView;

    public static Intent create(Activity activity, boolean title, boolean url){

        Intent intent = new Intent(activity, SWebViewActivity.class);
        intent.putExtra(PLAT_WEBVIEW_TITLE,title);
        intent.putExtra(PLAT_WEBVIEW_URL,url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.s_web_act_layout);
        sWebViewLayout = findViewById(R.id.sl_web_act);
        sWebView = sWebViewLayout.getsWebView();

        sWebViewLayout.getCloseImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent() != null) {
            webUrl = getIntent().getStringExtra(PLAT_WEBVIEW_URL);
            webTitle = getIntent().getStringExtra(PLAT_WEBVIEW_TITLE);
//            initTitle(webTitle);
        }

        if (TextUtils.isEmpty(webUrl)){
            ToastUtils.toast(getApplicationContext(),"url error");
            PL.i("webUrl is empty");
        }else{
            sWebView.loadUrl(webUrl);
        }

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
            PL.i("dialog destory webview");
        }
    }
}

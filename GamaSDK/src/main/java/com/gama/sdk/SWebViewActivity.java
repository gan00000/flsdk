package com.gama.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.core.base.BaseWebChromeClient;
import com.core.base.BaseWebViewClient;
import com.core.base.SWebView;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;

/**
 * Created by GanYuanrong on 2016/12/1.
 */

public class SWebViewActivity extends SBaseSdkActivity {

    public static final String PLAT_WEBVIEW_URL = "PLAT_WEBVIEW_URL";
    public static final String PLAT_WEBVIEW_TITLE = "PLAT_WEBVIEW_TITLE";
    private SWebView sWebView;
    private String webUrl;
    String webTitle;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.s_web_view_with_title_layout);

        if (getIntent() != null) {
            webUrl = getIntent().getStringExtra(PLAT_WEBVIEW_URL);
            webTitle = getIntent().getStringExtra(PLAT_WEBVIEW_TITLE);
            initTitle(webTitle);
        }


        if (TextUtils.isEmpty(webUrl)){
            ToastUtils.toast(getApplicationContext(),"url error");
            PL.i("webUrl is empty");
            finish();
            return;
        }


        progressBar = (ProgressBar) findViewById(R.id.s_webview_pager_loading_percent);
        sWebView = (SWebView) findViewById(R.id.s_webview_id);

        sWebView.setBaseWebChromeClient(new BaseWebChromeClient(progressBar,this));
        sWebView.setWebViewClient(new BaseWebViewClient(this));
        PL.i("SWebViewActivity url:" + webUrl);
        sWebView.loadUrl(webUrl);

    }

    private void initTitle(String webTitle) {

       View titleLayout = findViewById(R.id.py_title_layout_id);
        if (SStringUtil.isNotEmpty(webTitle)){
            titleLayout.setVisibility(View.VISIBLE);
            titleLayout.setBackgroundResource(R.drawable.gama_title_sdk_bg);
        }else{
            titleLayout.setVisibility(View.GONE);
            return;
        }

        findViewById(R.id.py_back_button).setVisibility(View.GONE);
        TextView titleTextView = (TextView) findViewById(R.id.py_title_id);
        titleTextView.setText(webTitle);
        View rightCloseView = findViewById(R.id.py_title_right_button);
        rightCloseView.setVisibility(View.VISIBLE);
        rightCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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
            sWebView.destroy();
            sWebView = null;
        }
    }
}

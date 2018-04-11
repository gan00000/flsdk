package com.gama.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.core.base.BaseWebChromeClient;
import com.core.base.BaseWebViewClient;
import com.core.base.SWebView;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;


public class SWebViewFragment extends SSdkBaseFragment {

    private SWebView sWebView;
    private String webUrl;

    private ProgressBar progressBar;

    private View contentView;

    private RelativeLayout titleContentLayout;
    private View closeContentLayout;

    private boolean showWebViewCloseViwe = false;

    public void setShowWebViewCloseViwe(boolean showWebViewCloseViwe) {
        this.showWebViewCloseViwe = showWebViewCloseViwe;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PL.i("fragment onCreateView");
        contentView = inflater.inflate(R.layout.s_base_web_view_with_customer_title_layout,container,false);

        progressBar = (ProgressBar) contentView.findViewById(R.id.s_webview_pager_loading_percent);
        sWebView = (SWebView) contentView.findViewById(R.id.s_webview_id);

        titleContentLayout = (RelativeLayout) contentView.findViewById(R.id.s_base_web_view_title_content);
        closeContentLayout = contentView.findViewById(R.id.s_base_web_view_close_layout);

        View titleLayout = onCreateViewForTitle(inflater, container,contentView);

        if (titleLayout != null){
            titleContentLayout.removeAllViews();
            titleContentLayout.addView(titleLayout);
        }

        if (showWebViewCloseViwe){
            closeContentLayout.setVisibility(View.VISIBLE);
        }else {
            closeContentLayout.setVisibility(View.GONE);
        }
        closeContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return contentView;


    }

    public View onCreateViewForTitle(LayoutInflater inflater, @Nullable ViewGroup container,View contentView) {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PL.i("fragment onViewCreated");
        if (TextUtils.isEmpty(webUrl)){
            ToastUtils.toast(getActivity(),"url error");
            PL.i("webUrl is empty");
            return;
        }
        sWebView.setBaseWebChromeClient(new BaseWebChromeClient(progressBar,this));
        sWebView.setWebViewClient(new BaseWebViewClient(getActivity()));
        PL.i("SWebViewActivity url:" + webUrl);
        sWebView.loadUrl(webUrl);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PL.i("fragment onActivityResult");
        if (sWebView != null){
            sWebView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sWebView != null){
            sWebView.destroy();
            sWebView = null;
        }
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

}

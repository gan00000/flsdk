package com.mw.sdk.login.widget.v2;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.core.base.BaseWebViewClient;
import com.core.base.utils.PL;
import com.mw.base.cfg.ResConfig;
import com.mw.base.widget.SWebView;
import com.mw.sdk.R;
import com.mw.sdk.SWebViewLayout;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.out.ISdkCallBack;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class NoticeView extends SLoginBaseRelativeLayout {

    private View contentView;

    private ISdkCallBack iSdkCallBack;

    public void setiSdkCallBack(ISdkCallBack iSdkCallBack) {
        this.iSdkCallBack = iSdkCallBack;
    }

    //    private View goTermView;
//    private CheckBox agreeCheckBox;
    private View closeButton;
    private SWebViewLayout sWebViewLayout;
    private SWebView webView;
    String serviceUrl;

    public SWebViewLayout getsWebViewLayout() {
        return sWebViewLayout;
    }

    public SWebView getSWebView() {
        return webView;
    }


    public NoticeView(Context context) {
        super(context);

    }

    public NoticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoticeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.mw_notice, null);

        closeButton = contentView.findViewById(R.id.iv_notice_close);

        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sBaseDialog != null){
                    sBaseDialog.dismiss();
                }
            }
        });

        serviceUrl = ResConfig.getServiceTermUrl(getContext());
        serviceUrl = String.format(serviceUrl, ResConfig.getGameCode(getContext()));
//        String privateUrl = ResConfig.getPrivateTermUrl(getContext());
        PL.i("serviceUrl=" + serviceUrl);
        sWebViewLayout = contentView.findViewById(R.id.svl_notice_layout);
        sWebViewLayout.getTitleHeaderView().setVisibility(GONE);
        webView = sWebViewLayout.getsWebView();

        webView.clearCache(true);
        webView.setWebViewClient(new BaseWebViewClient((Activity) getActivity()));
        webView.loadUrl(serviceUrl);

        return contentView;
    }



    @Override
    public void onViewVisible() {
        super.onViewVisible();
        webView.loadUrl(serviceUrl);
    }

    public void reloadUrl(){
        if (webView != null) {
            webView.loadUrl(serviceUrl);
        }
    }
}
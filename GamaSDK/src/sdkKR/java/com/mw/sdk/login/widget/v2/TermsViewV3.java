package com.mw.sdk.login.widget.v2;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.core.base.BaseWebViewClient;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.bean.res.ConfigBean;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;
import com.mw.sdk.out.ISdkCallBack;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class TermsViewV3 extends SLoginBaseRelativeLayout {

    private View contentView;

    private ISdkCallBack iSdkCallBack;

    public void setiSdkCallBack(ISdkCallBack iSdkCallBack) {
        this.iSdkCallBack = iSdkCallBack;
    }

    //    private View goTermView;
//    private CheckBox agreeCheckBox;
    private Button okButton;
    private Button closeButton;
    private WebView termsView1;
    String serviceUrl;//服务条款
    String privacyPolicyUrl;//隐私政策

    private TextView tv_privacy_policy;
    private TextView tv_sdk_terms_title;

    public TermsViewV3(Context context) {
        super(context);

    }

    public TermsViewV3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TermsViewV3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.v3_sdk_term, null);

        okButton = contentView.findViewById(R.id.btn_term_agree);
        closeButton = contentView.findViewById(R.id.btn_term_close);
        tv_privacy_policy = contentView.findViewById(R.id.tv_privacy_policy);
        tv_sdk_terms_title = contentView.findViewById(R.id.tv_sdk_terms_title);

        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iSdkCallBack != null){
                    iSdkCallBack.success();
                }
            }
        });

        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (iSdkCallBack != null){
                    iSdkCallBack.failure();
                }

            }
        });
        tv_privacy_policy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_privacy_policy.setTextColor(getResources().getColor(R.color.c_F13A12));
                tv_sdk_terms_title.setTextColor(getResources().getColor(R.color.c_C0C0C0));

                if (SStringUtil.isNotEmpty(privacyPolicyUrl) && termsView1!= null){
                    termsView1.loadUrl(privacyPolicyUrl);
                }
            }
        });
        tv_sdk_terms_title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_sdk_terms_title.setTextColor(getResources().getColor(R.color.c_F13A12));
                tv_privacy_policy.setTextColor(getResources().getColor(R.color.c_C0C0C0));
                if (SStringUtil.isNotEmpty(serviceUrl) && termsView1!= null){
                    termsView1.loadUrl(serviceUrl);
                }
            }
        });

        ConfigBean configBean = SdkUtil.getSdkCfg(getContext());
        if (configBean != null && configBean.getUrl() != null && SStringUtil.isNotEmpty(configBean.getUrl().getAgreementUrl())) {
            String serviceUrlTemp = configBean.getUrl().getAgreementUrl();
            if (serviceUrlTemp.contains(",")){
                String[] urls = serviceUrlTemp.split(",");
                if (urls.length > 1){
                    serviceUrl = urls[0];//第一个服务条款
                    privacyPolicyUrl = urls[1];
                }
            }else {
                serviceUrl = serviceUrlTemp;
            }

        }else{
            serviceUrl = ResConfig.getServiceTermUrl(getContext());
            serviceUrl = String.format(serviceUrl, ResConfig.getGameCode(getContext()));
//          String privateUrl = ResConfig.getPrivateTermUrl(getContext());
            privacyPolicyUrl = serviceUrl;
        }

        PL.i("serviceUrl=" + serviceUrl);
        termsView1 = (WebView) contentView.findViewById(R.id.sdk_terms_webview);
        termsView1.clearCache(true);
//        termsView1.setWebChromeClient(new WebChromeClient());
        termsView1.setWebViewClient(new BaseWebViewClient((Activity) getActivity()));
        termsView1.loadUrl(privacyPolicyUrl);

        return contentView;
    }



    @Override
    public void onViewVisible() {
        super.onViewVisible();
        termsView1.loadUrl(serviceUrl);
    }

    public void reloadUrl(){
        if (termsView1 != null) {
            termsView1.loadUrl(serviceUrl);
        }
    }
}

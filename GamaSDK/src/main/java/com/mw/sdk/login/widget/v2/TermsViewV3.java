package com.mw.sdk.login.widget.v2;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.core.base.BaseWebViewClient;
import com.mw.base.cfg.ResConfig;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class TermsViewV3 extends SLoginBaseRelativeLayout {

    private View contentView;

    private View goTermView;
    private CheckBox agreeCheckBox;
    private Button okButton;
    private WebView termsView1;
    String serviceUrl;

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

        TextView titleTextView = contentView.findViewById(R.id.sdk_head_title);
        titleTextView.setText(R.string.sdk_terms_title);

        backView = contentView.findViewById(R.id.layout_head_back);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(agreeCheckBox.isChecked()) {
//                    GamaUtil.saveStartTermRead(getContext(), true);
//                }else{
//                    GamaUtil.saveStartTermRead(getContext(), false);
//                }

                sLoginDialogv2.toMainHomeView();
            }
        });

/*
        goTermView = contentView.findViewById(R.id.gama_gama_start_term_tv1);//跳轉服務條款
        agreeCheckBox = contentView.findViewById(R.id.gama_gama_start_term_cb1);//跳轉服務條款
        okButton = contentView.findViewById(R.id.sdk_terms_btn_confirm);

        if (GamaUtil.getStartTermRead(getContext())){
            agreeCheckBox.setChecked(true);
        }else {
            agreeCheckBox.setChecked(false);
        }
        goTermView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //服務條款
                agreeCheckBox.performClick();
            }
        });*/

        serviceUrl = ResConfig.getServiceTermUrl(getContext());
        serviceUrl = String.format(serviceUrl, ResConfig.getGameCode(getContext()));
//        String privateUrl = ResConfig.getPrivateTermUrl(getContext());

        termsView1 = (WebView) contentView.findViewById(R.id.sdk_terms_webview);
        termsView1.clearCache(true);
//        termsView1.setWebChromeClient(new WebChromeClient());
        termsView1.setWebViewClient(new BaseWebViewClient((Activity) getActivity()));
        termsView1.loadUrl(serviceUrl);

      /*  okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!agreeCheckBox.isChecked()) {
                    ToastUtils.toast(getContext(), R.string.gama_ui_term_not_read);
                    return;
                }
                GamaUtil.saveStartTermRead(getContext(), true);

                sLoginDialogv2.toLoginWithRegView();
            }
        });*/


        return contentView;
    }



    @Override
    public void refreshViewData() {
        super.refreshViewData();
        termsView1.loadUrl(serviceUrl);
    }

}

package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.core.base.utils.ToastUtils;
import com.flyfun.base.cfg.ResConfig;
import com.flyfun.base.utils.GamaUtil;
import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.R;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class TermsViewV3 extends SLoginBaseRelativeLayout {

    private View contentView;

    private View goTermView;
    private CheckBox agreeCheckBox;
    private Button okButton;
    private WebView termsView1;

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

        backView = contentView.findViewById(R.id.gama_head_iv_back);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(agreeCheckBox.isChecked()) {
                    GamaUtil.saveStartTermRead(getContext(), true);
                }else{
                    GamaUtil.saveStartTermRead(getContext(), false);
                }

                sLoginDialogv2.toMainLoginView();
            }
        });


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
        });

        String serviceUrl = ResConfig.getServiceTermUrl(getContext());
        String privateUrl = ResConfig.getPrivateTermUrl(getContext());

        termsView1 = (WebView) contentView.findViewById(R.id.sdk_terms_webview);
        termsView1.clearCache(true);
        termsView1.setWebChromeClient(new WebChromeClient());
        termsView1.setWebViewClient(new WebViewClient());
        termsView1.loadUrl(serviceUrl);

        TextView goTermView = contentView.findViewById(R.id.gama_gama_start_term_tv1);
        String ssText = getContext().getString(R.string.gama_ui_term_port_read2);
        SpannableString ss = new SpannableString(ssText);
        ss.setSpan(new UnderlineSpan(), ssText.length() - 5, ssText.length(), Paint.UNDERLINE_TEXT_FLAG);
        goTermView.setText(ss);

        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!agreeCheckBox.isChecked()) {
                    ToastUtils.toast(getContext(), R.string.gama_ui_term_not_read);
                    return;
                }
                GamaUtil.saveStartTermRead(getContext(), true);

                sLoginDialogv2.toMainLoginView();
            }
        });


        return contentView;
    }



    @Override
    public void refreshViewData() {
        super.refreshViewData();

    }

}
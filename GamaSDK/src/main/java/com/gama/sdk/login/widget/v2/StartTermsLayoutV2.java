package com.gama.sdk.login.widget.v2;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.core.base.utils.ScreenHelper;
import com.core.base.utils.ToastUtils;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class StartTermsLayoutV2 extends SLoginBaseRelativeLayout {

    private View contentView;
    private WebView termsView1, termsView2;
    private Button confirm;
    private RadioGroup titleGroup;
    private CheckBox checkBox1, checkBox2;
    private boolean isPort;
    private String serviceUrl, privateUrl;
    private FrameLayout layout;

    public StartTermsLayoutV2(Context context) {
        super(context);
    }

    public StartTermsLayoutV2(Context context, FrameLayout contentFrameLayout) {
        super(context);
        layout = contentFrameLayout;
    }

    public StartTermsLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StartTermsLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_start_term, layout, true);

        ScreenHelper screenHelper = new ScreenHelper((Activity) getActivity());
        if(screenHelper.getScreenWidth() > screenHelper.getScreenHeight()) {
            isPort = false;
        } else {
            isPort = true;
        }

        serviceUrl = getResources().getString(R.string.gama_start_terms_service_url);
        privateUrl = getResources().getString(R.string.gama_start_terms_private_url);

        termsView1 = (WebView) contentView.findViewById(R.id.gama_start_term_wv1);
        termsView1.clearCache(true);
        termsView1.setWebChromeClient(new WebChromeClient());
        termsView1.setWebViewClient(new WebViewClient());
        termsView1.loadUrl(serviceUrl);

        checkBox1 = (CheckBox) contentView.findViewById(R.id.gama_gama_start_term_cb1);

        if(isPort) {
            titleGroup = (RadioGroup) contentView.findViewById(R.id.gama_start_term_title_group);
            titleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(i == R.id.gama_start_term_title1) {
                        termsView1.loadUrl(serviceUrl);
                    } else if(i == R.id.gama_start_term_title2) {
                        termsView1.loadUrl(privateUrl);
                    }
                }
            });

        } else {
            termsView2 = (WebView) contentView.findViewById(R.id.gama_start_term_wv2);
            termsView2.clearCache(true);
            termsView2.setWebChromeClient(new WebChromeClient());
            termsView2.setWebViewClient(new WebViewClient());
            termsView2.loadUrl(privateUrl);

            checkBox2 = (CheckBox) contentView.findViewById(R.id.gama_gama_start_term_cb2);
        }

        confirm = (Button) contentView.findViewById(R.id.gama_gama_start_term_confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPort) {
                    if(!checkBox1.isChecked()) {
                        ToastUtils.toast(getContext(), R.string.gama_ui_term_not_read);
                    } else {
                        ToastUtils.toast(getContext(), "已勾选，进行下一步流程");
                    }
                } else {
                    if(!checkBox1.isChecked() || !checkBox2.isChecked()) {
                        ToastUtils.toast(getContext(), R.string.gama_ui_term_not_read);
                    } else {
                        ToastUtils.toast(getContext(), "已勾选，进行下一步流程");
                    }
                }
            }
        });

        return contentView;
    }



    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

}

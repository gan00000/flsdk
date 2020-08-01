package com.flyfun.sdk.login.widget.v2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.core.base.utils.ScreenHelper;
import com.core.base.utils.ToastUtils;
import com.flyfun.base.cfg.ResConfig;
import com.flyfun.sdk.SBaseDialog;
import com.flyfun.sdk.callback.GamaCommonViewCallback;
import com.flyfun.base.utils.GamaUtil;
import com.gama.sdk.R;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class StartTermsLayoutV2 extends SBaseDialog {

//    private View contentView;
    private WebView termsView1, termsView2;
    private TextView gama_gama_start_term_tv1, gama_gama_start_term_tv2;
    private Button confirm;
    private RadioGroup titleGroup;
    private CheckBox checkBox1, checkBox2;
    private boolean isPort;
    private String serviceUrl, privateUrl;
    private FrameLayout layout;
    private Context mContext;
    private GamaCommonViewCallback viewListener;

    public StartTermsLayoutV2(@NonNull Context context, GamaCommonViewCallback listener) {
        super(context);
        this.mContext = context;
        this.viewListener = listener;
        setFullScreen();
    }

    public StartTermsLayoutV2(@NonNull Context context, int themeResId, GamaCommonViewCallback listener) {
        super(context, themeResId);
        this.mContext = context;
        this.viewListener = listener;
        setFullScreen();
    }

    protected StartTermsLayoutV2(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, GamaCommonViewCallback listener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        this.viewListener = listener;
        setFullScreen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.v2_start_term2);
//        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        ScreenHelper screenHelper = new ScreenHelper((Activity) mContext);
        if(screenHelper.getScreenWidth() > screenHelper.getScreenHeight()) {
            isPort = false;
        } else {
            isPort = true;
        }

        serviceUrl = ResConfig.getServiceTermUrl(getContext());
        privateUrl = ResConfig.getPrivateTermUrl(getContext());

        termsView1 = (WebView) findViewById(R.id.gama_start_term_wv1);
        termsView1.clearCache(true);
        termsView1.setWebChromeClient(new WebChromeClient());
        termsView1.setWebViewClient(new WebViewClient());
        termsView1.loadUrl(serviceUrl);

        checkBox1 = (CheckBox) findViewById(R.id.gama_gama_start_term_cb1);
        gama_gama_start_term_tv1 = findViewById(R.id.gama_gama_start_term_tv1);
        gama_gama_start_term_tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox1.performClick();
            }
        });

        if(isPort) {
            titleGroup = (RadioGroup) findViewById(R.id.gama_start_term_title_group);
            final RadioButton mRadioButton1 = titleGroup.findViewById(R.id.gama_start_term_title1);
            final RadioButton mRadioButton2 = titleGroup.findViewById(R.id.gama_start_term_title2);
            titleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(i == R.id.gama_start_term_title1) {
                        termsView1.loadUrl(serviceUrl);
                        mRadioButton1.setTextColor(Color.WHITE);
                        mRadioButton2.setTextColor(getContext().getResources().getColor(R.color.c_fc403f));
                    } else if(i == R.id.gama_start_term_title2) {
                        termsView1.loadUrl(privateUrl);
                        mRadioButton2.setTextColor(Color.WHITE);
                        mRadioButton1.setTextColor(getContext().getResources().getColor(R.color.c_fc403f));
                    }
                }
            });

        } else {
            termsView2 = (WebView) findViewById(R.id.gama_start_term_wv2);
            termsView2.clearCache(true);
            termsView2.setWebChromeClient(new WebChromeClient());
            termsView2.setWebViewClient(new WebViewClient());
            termsView2.loadUrl(privateUrl);

            checkBox2 = (CheckBox) findViewById(R.id.gama_gama_start_term_cb2);
            gama_gama_start_term_tv2 = findViewById(R.id.gama_gama_start_term_tv2);
            gama_gama_start_term_tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox2.performClick();
                }
            });
        }

        confirm = (Button) findViewById(R.id.gama_gama_start_term_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPort) {
                    if(!checkBox1.isChecked()) {
                        ToastUtils.toast(getContext(), R.string.gama_ui_term_not_read);
                    } else {
//                        ToastUtils.toast(getContext(), "已勾选，进行下一步流程");
                        GamaUtil.saveStartTermRead(getContext(), true);
                        StartTermsLayoutV2.this.dismiss();
                        if(viewListener != null) {
                            viewListener.onSuccess();
                        }
                    }
                } else {
                    if(!checkBox1.isChecked() || !checkBox2.isChecked()) {
                        ToastUtils.toast(getContext(), R.string.gama_ui_term_not_read);
                    } else {
//                        ToastUtils.toast(getContext(), "已勾选，进行下一步流程");
                        GamaUtil.saveStartTermRead(getContext(), true);
                        StartTermsLayoutV2.this.dismiss();
                        if(viewListener != null) {
                            viewListener.onSuccess();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(viewListener != null) {
            viewListener.onFailure();
        }
    }

}

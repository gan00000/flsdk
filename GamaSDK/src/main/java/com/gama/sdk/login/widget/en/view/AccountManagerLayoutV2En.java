package com.gama.sdk.login.widget.en.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.login.widget.en.view.base.SLoginBaseRelativeLayoutEn;


public class AccountManagerLayoutV2En extends SLoginBaseRelativeLayoutEn implements View.OnClickListener {

    private View contentView;
    private Button uniqueRegBindBtn;
    private Button fbRegBindBtn;
    private Button googleRegBindBtn;
    private Button twitterRegBindBtn;


    public AccountManagerLayoutV2En(Context context) {
        super(context);
    }

    public AccountManagerLayoutV2En(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountManagerLayoutV2En(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_gama_account_manager_en, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);
        backView.setOnClickListener(this);

        uniqueRegBindBtn = contentView.findViewById(R.id.gama_manager_btn_guest);
        fbRegBindBtn = contentView.findViewById(R.id.gama_manager_btn_facebook);
        googleRegBindBtn =  contentView.findViewById(R.id.gama_manager_btn_google);
        twitterRegBindBtn = contentView.findViewById(R.id.gama_manager_btn_twitter);

        uniqueRegBindBtn.setOnClickListener(this);
        fbRegBindBtn.setOnClickListener(this);
        googleRegBindBtn.setOnClickListener(this);
        twitterRegBindBtn.setOnClickListener(this);

        if (GamaUtil.isMainland(getContext())){
            fbRegBindBtn.setVisibility(GONE);
            googleRegBindBtn.setVisibility(GONE);

        }

        if(GamaUtil.hasTwitter(getContext())) {
            twitterRegBindBtn.setVisibility(VISIBLE);
        } else {
            twitterRegBindBtn.setVisibility(GONE);
        }

        return contentView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {

        if (v == uniqueRegBindBtn) {

            sLoginDialogv2.toBindUniqueView();

        }else if (v == fbRegBindBtn) {
            sLoginDialogv2.toBindFbView();

        }else if (v == googleRegBindBtn) {
            sLoginDialogv2.toBindGoogleView();

        } else if (v == backView) {//返回键
            sLoginDialogv2.toAccountLoginView();
        } else if(v == twitterRegBindBtn) {
            sLoginDialogv2.toBindTwitterView();
        }

    }


}

package com.gama.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;


public class AccountManagerLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private TextView changePwdBtn;
    private TextView uniqueRegBindBtn;
    private TextView fbRegBindBtn;
    private TextView googleRegBindBtn;


    public AccountManagerLayoutV2(Context context) {
        super(context);
    }

    public AccountManagerLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountManagerLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_py_account_manager, null);

        backView = contentView.findViewById(R.id.py_back_button_v2);
        backView.setOnClickListener(this);

        changePwdBtn = (TextView) contentView.findViewById(R.id.v2_account_manager_changepwd_btn);
        uniqueRegBindBtn = (TextView) contentView.findViewById(R.id.v2_account_manager_free_reg_bind_btn);
        fbRegBindBtn = (TextView) contentView.findViewById(R.id.v2_account_manager_fb_reg_bind_btn);
        googleRegBindBtn = (TextView) contentView.findViewById(R.id.v2_account_manager_google_reg_bind_btn);


        changePwdBtn.setOnClickListener(this);
        uniqueRegBindBtn.setOnClickListener(this);
        fbRegBindBtn.setOnClickListener(this);
        googleRegBindBtn.setOnClickListener(this);

        if (GamaUtil.isMainland(getContext())){
            fbRegBindBtn.setVisibility(GONE);
            googleRegBindBtn.setVisibility(GONE);

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

        if (v == changePwdBtn) {
            sLoginDialogv2.toChangePwdView();

        } else if (v == uniqueRegBindBtn) {

            sLoginDialogv2.toBindUniqueView();

        }else if (v == fbRegBindBtn) {
            sLoginDialogv2.toBindFbView();

        }else if (v == googleRegBindBtn) {
            sLoginDialogv2.toBindGoogleView();

        } else if (v == backView) {//返回键
            sLoginDialogv2.toAccountLoginView();
        }

    }


}

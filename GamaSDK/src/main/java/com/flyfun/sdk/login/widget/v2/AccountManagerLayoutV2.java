package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyfun.data.login.constant.GSLoginCommonConstant;
import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;


public class AccountManagerLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private View uniqueRegBindBtn;
    private View fbRegBindBtn;
    private View googleRegBindBtn;


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
        contentView = inflater.inflate(R.layout.v2_account_manager, null);

        backView = contentView.findViewById(R.id.layout_head_back);
        backView.setOnClickListener(this);

        TextView titleTextView = contentView.findViewById(R.id.sdk_head_title);
        titleTextView.setText(R.string.py_login_page_account_bind);

        uniqueRegBindBtn = contentView.findViewById(R.id.gama_manager_btn_guest);
        fbRegBindBtn = contentView.findViewById(R.id.gama_manager_btn_facebook);
        googleRegBindBtn = contentView.findViewById(R.id.gama_manager_btn_google);

        uniqueRegBindBtn.setOnClickListener(this);
        fbRegBindBtn.setOnClickListener(this);
        googleRegBindBtn.setOnClickListener(this);

        /*if (GamaUtil.isMainland(getContext())) {
            fbRegBindBtn.setVisibility(GONE);
            googleRegBindBtn.setVisibility(GONE);

        }*/

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

//        if (v == changePwdBtn) {
//            sLoginDialogv2.toChangePwdView();
//
//        } else
        if (v == uniqueRegBindBtn) {

            sLoginDialogv2.toBindUniqueView(GSLoginCommonConstant.GsLoginUiPageNumber.GS_PAGE_BIND);

        } else if (v == fbRegBindBtn) {
            sLoginDialogv2.toBindFbView();

        } else if (v == googleRegBindBtn) {
            sLoginDialogv2.toBindGoogleView();

        } else if (v == backView) {//返回键
            sLoginDialogv2.toAccountLoginView();
        }

    }


}

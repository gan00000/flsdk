package com.ldy.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ldy.sdk.R;
import com.ldy.sdk.login.widget.SLoginBaseRelativeLayout;


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
        contentView = inflater.inflate(R.layout.sady_agrsome59310, null);

        backView = contentView.findViewById(R.id.mId_whilekin_domeous);
        backView.setOnClickListener(this);

//        TextView titleTextView = contentView.findViewById(R.id.mId_vovery_especiallyage);
//        titleTextView.setText(R.string.mstr_usette_collectionfic);
//
//        uniqueRegBindBtn = contentView.findViewById(R.id.mId_oscilluous_plos);
//        fbRegBindBtn = contentView.findViewById(R.id.mId_deadling_commonally);
//        googleRegBindBtn = contentView.findViewById(R.id.mId_vestform_stinguine);
//
//        uniqueRegBindBtn.setOnClickListener(this);
//        fbRegBindBtn.setOnClickListener(this);
//        googleRegBindBtn.setOnClickListener(this);


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

//            sLoginDialogv2.toBindUniqueView(GSLoginCommonConstant.GsLoginUiPageNumber.GS_PAGE_BIND);

        } else if (v == fbRegBindBtn) {
//            sLoginDialogv2.toBindFbView();

        } else if (v == googleRegBindBtn) {
//            sLoginDialogv2.toBindGoogleView();

        } else if (v == backView) {//返回键
//            sLoginDialogv2.toAccountLoginView();
        }

    }


}

package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.utils.ToastUtils;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.R;
import com.mw.sdk.SBaseRelativeLayout;
import com.mw.sdk.login.widget.SDKInputEditTextView;
import com.mw.sdk.login.widget.SDKInputType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;


public class AgeQuaLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;

    private String account;
//    private String email;

    View closeView,ageUpView;

    public AgeQuaLayoutV2(Context context) {
        super(context);
    }

    public AgeQuaLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AgeQuaLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }



    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.mw_age_qua, null);

        closeView = contentView.findViewById(R.id.btn_age_close);
        ageUpView = contentView.findViewById(R.id.btn_age_ok);
        closeView.setOnClickListener(this);
        ageUpView.setOnClickListener(this);

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

        if (v == closeView) {
            sLoginDialogv2.showMainHomeView();
        }else if (v == ageUpView){
            sLoginDialogv2.showMainHomeView();
        }

    }





    @Override
    public void statusCallback(int operation) {
    }

    @Override
    public void dataCallback(Object o) {
    }

    @Override
    public void alertTime(int remainTimeSeconds) {
    }


    @Override
    protected void onSetDialog() {
        super.onSetDialog();
    }

    private void setDefaultAreaInfo() {
    }

    @Override
    public void onViewVisible() {
        super.onViewVisible();
    }
}

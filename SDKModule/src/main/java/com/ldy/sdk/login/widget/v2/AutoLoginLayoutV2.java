package com.ldy.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ldy.sdk.R;
import com.ldy.sdk.SBaseRelativeLayout;
import com.ldy.sdk.login.widget.SLoginBaseRelativeLayout;


public class AutoLoginLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;

    private String autoLoginTips;

    public void setAutoLoginTips(String autoLoginTips) {
        this.autoLoginTips = autoLoginTips;
        if (autoLoginTipsTV != null){
            autoLoginTipsTV.setText(autoLoginTips);
        }
    }

    Button autoLoginSwitch;
    TextView autoLoginTipsTV;

    public AutoLoginLayoutV2(Context context) {
        super(context);
    }

    public AutoLoginLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoLoginLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }



    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.sady_sup11336, null);

        autoLoginSwitch = contentView.findViewById(R.id.mId_nephrfic_chelonfy);
        autoLoginTipsTV = contentView.findViewById(R.id.mId_followance_oncoose);
        autoLoginTipsTV.setText(autoLoginTips + "");
        autoLoginSwitch.setOnClickListener(this);

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

        if (v == autoLoginSwitch) {
            sLoginDialogv2.getLoginPresenter().autoLoginChangeAccount(sLoginDialogv2.getActivity());
        }

    }



    @Override
    public void statusCallback(int operation) {
        if (TIME_LIMIT == operation) {
        } else if (TIME_OUT == operation) {
        }
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

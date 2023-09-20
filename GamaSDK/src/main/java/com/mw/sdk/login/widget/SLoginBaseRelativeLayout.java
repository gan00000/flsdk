package com.mw.sdk.login.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.widget.SBaseDialog;
import com.mw.sdk.widget.SBaseRelativeLayout;
import com.mw.sdk.utils.Localization;
import com.mw.sdk.login.SLoginDialogV2;
import com.mw.sdk.constant.ViewType;

/**
 * Created by gan on 2017/4/12.
 */

public abstract class SLoginBaseRelativeLayout extends SBaseRelativeLayout {

    Context context;
    LayoutInflater inflater;

    protected SLoginDialogV2 sLoginDialogv2;
    protected SBaseDialog sBaseDialog;

    protected View backView;
    protected TextView titleTextView;
    protected ViewType fromView;

    public void setFromView(ViewType fromView) {
        this.fromView = fromView;
    }

    protected int remainTimeSeconds;

//    protected String errorStrAccount, errorStrPassword;

    public SLoginBaseRelativeLayout(Context context) {
        super(context);

        initView(context);
    }

    public SLoginBaseRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public SLoginBaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SLoginBaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PL.i(this.getClass().getCanonicalName() + " onAttachedToWindow");
    }

    private void initView(Context context) {
        this.context = context;

        Localization.updateSGameLanguage(getActivity());

        inflater = LayoutInflater.from(context);
        View contentView = createView(this.context, inflater);

        if (contentView != null) {

            LayoutParams l = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            l.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(contentView, l);
        }

//        String accountError1 = getActivity().getResources().getString(R.string.py_account_error) + ":";
//        String accountError2 = getActivity().getResources().getString(R.string.py_register_account_hit);
//        errorStrAccount = accountError1 + accountError2;
//
//        String passwordError1 = getActivity().getResources().getString(R.string.py_password_error) + ":";
//        String passwordError2 = getActivity().getResources().getString(R.string.py_register_password_hit);
//        errorStrPassword = passwordError1 + passwordError2;
    }

    protected Context getActivity() {
        return getContext();
    }

    protected abstract View createView(Context context, LayoutInflater layoutInflater);

    public void setLoginDialogV2(SLoginDialogV2 sLoginDialog) {
        this.sLoginDialogv2 = sLoginDialog;
        onSetDialog();
    }

    public SBaseDialog getsBaseDialog() {
        return sBaseDialog;
    }

    public void setsBaseDialog(SBaseDialog sBaseDialog) {
        this.sBaseDialog = sBaseDialog;
    }

    /**
     * 各界面的返回按钮
     */
    public View getBackView() {
        return backView;
    }

    protected void onSetDialog() {}

    //用于刷新验证码
    public void refreshVfCode() {}

    /**
     * 当view 从 dialog隐藏时被调用
     */
    public void onViewGone() {}

    /**
     * 当view 从 dialog content remove时被调用
     */
    public void onViewRemove() {}

    /**
     * 当view 从 dialog content 设置可见时被调用
     */
    public void onViewVisible(){
        PL.i(this.getClass().getCanonicalName() + " onViewVisible");
    }

    protected void toast(int msgId){
        ToastUtils.toast(getActivity(),msgId);
    }
    protected void toast(String msg){
        ToastUtils.toast(getActivity(),msg);
    }
}

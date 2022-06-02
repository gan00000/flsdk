package com.flyfun.sdk.login.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.base.utils.ToastUtils;
import com.flyfun.sdk.SBaseRelativeLayout;
import com.flyfun.base.utils.Localization;
import com.gama.sdk.R;
import com.flyfun.sdk.login.SLoginDialogV2;

/**
 * Created by gan on 2017/4/12.
 */

public abstract class SLoginBaseRelativeLayout extends SBaseRelativeLayout {

    Context context;
    LayoutInflater inflater;

    protected SLoginDialogV2 sLoginDialogv2;

    protected View backView;
    protected TextView titleTextView;
    public int from;

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
        doSomething();
    }

    /**
     * 各界面的返回按钮
     */
    public View getBackView() {
        return backView;
    }

    protected void doSomething() {}

    public void refreshViewData(){}

    //用于刷新验证码
    public void refreshVfCode() {}

    protected void toast(int msgId){
        ToastUtils.toast(getActivity(),msgId);
    }
}

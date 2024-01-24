package com.ldy.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mybase.utils.SStringUtil;
import com.mybase.utils.ToastUtils;
import com.ldy.base.utils.SdkUtil;
import com.ldy.sdk.R;
import com.ldy.sdk.login.widget.SDKInputEditTextView;
import com.ldy.sdk.login.widget.SDKInputType;
import com.ldy.sdk.login.widget.SLoginBaseRelativeLayout;


public class AccountChangePwdLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private Button btn_confire_change;

    private SDKInputEditTextView sdkinputview_changepwd_new_again, oldPwdSdkInputEditTextView, newPwdSdkInputEditTextView;

    private EditText againPwdEditText, changePwdOldEditText, changePwdNewEditText;

    private String account;
    private String oldPassword;
    private String newPassword;
    private String againPassword;

    public void setAccount(String account) {
        this.account = account;
    }

    public AccountChangePwdLayoutV2(Context context) {
        super(context);
    }

    public AccountChangePwdLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountChangePwdLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.okokok_proctature, null);

        backView = contentView.findViewById(R.id.mId_whilekin_domeous);
        TextView titleTextView = contentView.findViewById(R.id.mId_vovery_especiallyage);
        titleTextView.setText(R.string.mstr_skin_behind);

        oldPwdSdkInputEditTextView = contentView.findViewById(R.id.mId_horoier_scabo);
        newPwdSdkInputEditTextView = contentView.findViewById(R.id.mId_scandrecentlyous_athroidwifeatory);
        sdkinputview_changepwd_new_again = contentView.findViewById(R.id.mId_artististic_watch);

        oldPwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Old_Password);
        newPwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_New_Password);
        sdkinputview_changepwd_new_again.setInputType(SDKInputType.SDKInputType_Password_Again);

        changePwdOldEditText = oldPwdSdkInputEditTextView.getInputEditText();
        changePwdNewEditText = newPwdSdkInputEditTextView.getInputEditText();
        againPwdEditText = sdkinputview_changepwd_new_again.getInputEditText();


        btn_confire_change = contentView.findViewById(R.id.mId_medicalist_tredec);

        backView.setOnClickListener(this);
        btn_confire_change.setOnClickListener(this);

        return contentView;
    }

    @Override
    public void onViewVisible() {
        super.onViewVisible();
        againPwdEditText.setText("");
        changePwdOldEditText.setText("");
        changePwdNewEditText.setText("");
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

       if (v == btn_confire_change) {
            changePwd();
        } else if (v == backView) {//返回键
           sLoginDialogv2.toWelcomeBackView();
           sLoginDialogv2.distoryView(this);
        }

    }

    private void changePwd() {

        oldPassword = changePwdOldEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(oldPassword)) {
            ToastUtils.toast(getActivity(), R.string.mstr_multaton_uxoriatic);
            return;
        }

        newPassword = changePwdNewEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) {
            ToastUtils.toast(getActivity(), R.string.mstr_multaton_uxoriatic);
            return;
        }

        againPassword = againPwdEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(againPassword)) {
            ToastUtils.toast(getActivity(), R.string.mstr_multaton_uxoriatic);
            return;
        }


//        if (SStringUtil.isEqual(account, newPassword)) {
//            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
//            return;
//        }

        if (!SdkUtil.checkAccount(account)) {
            toast(R.string.mstr_downsive_strategyness);
            return;
        }
        if (!SdkUtil.checkPassword(newPassword)) {
            toast(R.string.mstr_vasety_parvitor);
            return;
        }

        if (SStringUtil.isEqual(oldPassword,newPassword)){
            ToastUtils.toast(getActivity(), R.string.mstr_matri_nightness);
            return;
        }
        if (!SStringUtil.isEqual(newPassword,againPassword)){
            ToastUtils.toast(getActivity(), R.string.mstr_bitesque_nugaivity);
            return;
        }

        sLoginDialogv2.getLoginPresenter().changePwd(sLoginDialogv2.getActivity(), account, oldPassword, newPassword);
    }


}

package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.flyfun.base.utils.GamaUtil;
import com.mw.sdk.R;
import com.flyfun.sdk.login.widget.SDKInputEditTextView;
import com.flyfun.sdk.login.widget.SDKInputType;
import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;


public class AccountChangePwdLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private Button btn_confire_change;

    private SDKInputEditTextView sdkinputview_changepwd_new_again, oldPwdSdkInputEditTextView, newPwdSdkInputEditTextView;

    private EditText againPwdEditText, changePwdOldEditText, changePwdNewEditText;

    private String account;
    private String password;
    private String newPassword;


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
        contentView = inflater.inflate(R.layout.mw_change_pwd, null);

        backView = contentView.findViewById(R.id.layout_head_back);
        TextView titleTextView = contentView.findViewById(R.id.sdk_head_title);
        titleTextView.setText(R.string.text_change_pwd);

        oldPwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_old_pwd);
        newPwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_changepwd_new);
        sdkinputview_changepwd_new_again = contentView.findViewById(R.id.sdkinputview_changepwd_new_again);

        sdkinputview_changepwd_new_again.setInputType(SDKInputType.SDKInputType_Password_Again);
        oldPwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Old_Password);
        newPwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_New_Password);

        againPwdEditText = sdkinputview_changepwd_new_again.getInputEditText();
        changePwdOldEditText = oldPwdSdkInputEditTextView.getInputEditText();
        changePwdNewEditText = newPwdSdkInputEditTextView.getInputEditText();


        btn_confire_change = contentView.findViewById(R.id.btn_confire_change);

        backView.setOnClickListener(this);
        btn_confire_change.setOnClickListener(this);

        return contentView;
    }

    @Override
    public void refreshViewData() {
        super.refreshViewData();
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
           sLoginDialogv2.toAccountLoginView();
        }

    }

    private void changePwd() {

        account = againPwdEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        password = changePwdOldEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }

        newPassword = changePwdNewEditText.getEditableText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }


        if (SStringUtil.isEqual(account, newPassword)) {
            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            return;
        }
        if (!GamaUtil.checkPassword(newPassword)) {
            return;
        }

        if (SStringUtil.isEqual(password,newPassword)){
            ToastUtils.toast(getActivity(), R.string.py_old_equel_new_pwd);
            return;
        }

        sLoginDialogv2.getLoginPresenter().changePwd(sLoginDialogv2.getActivity(), account, password, newPassword);
    }


}

package com.gama.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.utils.StarPyUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;


public class AccountChangePwdLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private TextView changePwdConfireBtn;

    private EditText changePwdAccountEditText, changePwdOldEditText, changePwdNewEditText;

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
        contentView = inflater.inflate(R.layout.v2_account_change_pwd, null);

        backView = contentView.findViewById(R.id.py_back_button_v2);

        changePwdAccountEditText = (EditText) contentView.findViewById(R.id.py_changepwd_account);
        changePwdOldEditText = (EditText) contentView.findViewById(R.id.py_changepwd_old_password);
        changePwdNewEditText = (EditText) contentView.findViewById(R.id.py_changepwd_new_password);

        changePwdConfireBtn = (TextView) contentView.findViewById(R.id.py_changepwd_confirm);


//        if (Localization.getSGameLanguage(getActivity()) == SGameLanguage.en_US){
//            ((ImageView)contentView.findViewById(R.id.py_change_pwd_title)).setImageResource(R.drawable.bg_title_chang_pwd_en);
//        }

        backView.setOnClickListener(this);
        changePwdConfireBtn.setOnClickListener(this);

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
        changePwdOldEditText.setText("");
        changePwdNewEditText.setText("");
    }

    @Override
    public void onClick(View v) {

       if (v == changePwdConfireBtn) {
            changePwd();
        } else if (v == backView) {//返回键
           sLoginDialogv2.toAccountManagerCenter();
        }

    }

    private void changePwd() {

        account = changePwdAccountEditText.getEditableText().toString().trim();
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

        if (!StarPyUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_error);
            return;
        }
        if (!StarPyUtil.checkPassword(newPassword)) {
            ToastUtils.toast(getActivity(), R.string.py_password_error);
            return;
        }

        if (SStringUtil.isEqual(password,newPassword)){
            ToastUtils.toast(getActivity(), R.string.py_old_equel_new_pwd);
            return;
        }

        sLoginDialogv2.getLoginPresenter().changePwd(sLoginDialogv2.getActivity(), account, password, newPassword);
    }


}

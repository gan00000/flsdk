package com.starpy.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.utils.ToastUtils;
import com.starpy.base.utils.StarPyUtil;
import com.starpy.sdk.R;
import com.starpy.sdk.login.widget.SLoginBaseRelativeLayout;
import com.starpy.sdk.utils.Validator;


public class AccountFindPwdLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private TextView findPwdConfireBtn;

    private EditText findPwdAccountEditText, findPwdEmailEditText;

    private String account;
    private String email;

    public AccountFindPwdLayoutV2(Context context) {
        super(context);
    }

    public AccountFindPwdLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountFindPwdLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_py_findpwd, null);

        backView = contentView.findViewById(R.id.py_back_button_v2);

        findPwdAccountEditText = (EditText) contentView.findViewById(R.id.py_findpwd_account_v2);
        findPwdEmailEditText = (EditText) contentView.findViewById(R.id.py_findpwd_mail);

        findPwdConfireBtn = (TextView) contentView.findViewById(R.id.v2_findpwd_btn);

//        if (Localization.getSGameLanguage(getActivity()) == SGameLanguage.en_US){
//            ((ImageView)contentView.findViewById(R.id.py_findpwd_account_title)).setImageResource(R.drawable.bg_title_forgot_pwd_en);
//        }

        backView.setOnClickListener(this);
        findPwdConfireBtn.setOnClickListener(this);

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
        findPwdAccountEditText.setText("");
        findPwdEmailEditText.setText("");
    }

    @Override
    public void onClick(View v) {

       if (v == findPwdConfireBtn) {
           findPwd();
        } else if (v == backView) {//返回键
           sLoginDialogv2.toAccountLoginView();
        }

    }

    private void findPwd() {

        account = findPwdAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        email = findPwdEmailEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_empty);
            return;
        }

        if (!StarPyUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_error);
            return;
        }
        if (!Validator.isEmail(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_format_error);
            return;
        }


        sLoginDialogv2.getLoginPresenter().findPwd(sLoginDialogv2.getActivity(), account, email);
    }


}

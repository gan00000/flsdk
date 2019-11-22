package com.gama.sdk.login.widget.en.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.utils.ToastUtils;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.login.widget.en.view.base.SLoginBaseRelativeLayoutEn;
import com.gama.sdk.utils.Validator;


public class AccountFindPwdLayoutV2En extends SLoginBaseRelativeLayoutEn implements View.OnClickListener {

    private View contentView;
    private TextView findPwdConfireBtn;

    private EditText findPwdAccountEditText, gama_find_et_email;

    private String account;
    private String email;

    public AccountFindPwdLayoutV2En(Context context) {
        super(context);
    }

    public AccountFindPwdLayoutV2En(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountFindPwdLayoutV2En(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }



    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_gama_findpwd_en, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);

        findPwdAccountEditText = contentView.findViewById(R.id.gama_find_et_account);

        findPwdConfireBtn = contentView.findViewById(R.id.gama_find_btn_confirm);
        gama_find_et_email = contentView.findViewById(R.id.gama_find_et_email);

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
            ToastUtils.toast(getActivity(), R.string.en_py_account_empty);
            return;
        }

        email = gama_find_et_email.getEditableText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtils.toast(getActivity(), R.string.en_py_email_empty);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), R.string.en_py_account_error);
            return;
        }
        if (!Validator.isEmail(email)) {
            ToastUtils.toast(getActivity(), R.string.en_py_email_format_error);
            return;
        }



        sLoginDialogv2.getLoginPresenter().findPwd(sLoginDialogv2.getActivity(), account, email);
    }

}

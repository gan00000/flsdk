package com.gama.sdk.login.widget.v2;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;


public class AccountChangePwdLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private TextView changePwdConfireBtn;

    private EditText changePwdAccountEditText, changePwdOldEditText, changePwdNewEditText;

    private String account;
    private String password;
    private String newPassword;

    /**
     * 眼睛、保存密码
     */
    private ImageView gama_change_iv_eye, gama_change_iv_eye2;

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

        backView = contentView.findViewById(R.id.gama_head_iv_back);

        changePwdAccountEditText = contentView.findViewById(R.id.gama_change_et_account);
        changePwdOldEditText = contentView.findViewById(R.id.gama_change_et_password);
        changePwdNewEditText = contentView.findViewById(R.id.gama_change_et_password2);

        changePwdOldEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        changePwdNewEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        gama_change_iv_eye = contentView.findViewById(R.id.gama_change_iv_eye);
        gama_change_iv_eye2 = contentView.findViewById(R.id.gama_change_iv_eye2);

        changePwdConfireBtn = contentView.findViewById(R.id.gama_change_btn_confirm);

        backView.setOnClickListener(this);
        changePwdConfireBtn.setOnClickListener(this);
        gama_change_iv_eye.setOnClickListener(this);
        gama_change_iv_eye2.setOnClickListener(this);

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

       if (v == changePwdConfireBtn) {
            changePwd();
        } else if (v == backView) {//返回键
           sLoginDialogv2.toAccountLoginView();
        } else if (v == gama_change_iv_eye) {
           if (gama_change_iv_eye.isSelected()) {
               gama_change_iv_eye.setSelected(false);
               // 显示为密码
               changePwdOldEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
           } else {
               gama_change_iv_eye.setSelected(true);
               // 显示为普通文本
               changePwdOldEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
           }
           // 使光标始终在最后位置
           Editable etable = changePwdOldEditText.getText();
           Selection.setSelection(etable, etable.length());
       } else if (v == gama_change_iv_eye2) {
           if (gama_change_iv_eye2.isSelected()) {
               gama_change_iv_eye2.setSelected(false);
               // 显示为密码
               changePwdNewEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
           } else {
               gama_change_iv_eye2.setSelected(true);
               // 显示为普通文本
               changePwdNewEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
           }
           // 使光标始终在最后位置
           Editable etable = changePwdNewEditText.getText();
           Selection.setSelection(etable, etable.length());
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

        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), errorStrAccount, Toast.LENGTH_LONG);
            return;
        }
        if (!GamaUtil.checkPassword(newPassword)) {
            ToastUtils.toast(getActivity(), errorStrPassword, Toast.LENGTH_LONG);
            return;
        }

        if (SStringUtil.isEqual(password,newPassword)){
            ToastUtils.toast(getActivity(), R.string.py_old_equel_new_pwd);
            return;
        }

        sLoginDialogv2.getLoginPresenter().changePwd(sLoginDialogv2.getActivity(), account, password, newPassword);
    }


}

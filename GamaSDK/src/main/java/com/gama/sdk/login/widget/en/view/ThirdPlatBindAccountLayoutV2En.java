package com.gama.sdk.login.widget.en.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.login.widget.en.view.base.SLoginBaseRelativeLayoutEn;
import com.gama.sdk.utils.Validator;


public class ThirdPlatBindAccountLayoutV2En extends SLoginBaseRelativeLayoutEn implements View.OnClickListener {

    private View contentView;
    private ImageView eyeImageView;
    private Button bindConfirm;

    /**
     * 密码、账号、邮箱
     */
    private EditText registerPasswordEditText, registerAccountEditText, gama_bind_et_vfcode;

    private String account;
    private String password;

    private int bindTpye = 0;

    public ThirdPlatBindAccountLayoutV2En(Context context) {
        super(context);
    }

    public ThirdPlatBindAccountLayoutV2En(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThirdPlatBindAccountLayoutV2En(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_account_bind_en, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);

        eyeImageView = contentView.findViewById(R.id.gama_bind_iv_eye);
        registerPasswordEditText = contentView.findViewById(R.id.gama_bind_et_password);
        registerAccountEditText = contentView.findViewById(R.id.gama_bind_et_account);
        gama_bind_et_vfcode = contentView.findViewById(R.id.gama_bind_et_vfcode);

        bindConfirm = contentView.findViewById(R.id.gama_bind_btn_confirm);

        eyeImageView.setOnClickListener(this);
        backView.setOnClickListener(this);
        bindConfirm.setOnClickListener(this);

        return contentView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (registerAccountEditText != null){
            registerAccountEditText.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {

        if (v == bindConfirm) {

            accountBind();

        } else if (v == backView) {//返回键

            sLoginDialogv2.toAccountManagerCenter();

        } else if (v == eyeImageView) {//密码眼睛

            if (eyeImageView.isSelected()) {
                eyeImageView.setSelected(false);
                // 显示为普通文本
                registerPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                eyeImageView.setSelected(true);
                // 显示为密码
                registerPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // 使光标始终在最后位置
            Editable etable = registerPasswordEditText.getText();
            Selection.setSelection(etable, etable.length());

        }

    }

    private void accountBind() {

        account = registerAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.en_py_account_empty);
            return;
        }

        password = registerPasswordEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.en_py_password_empty);
            return;
        }

        if (SStringUtil.isEqual(account, password)) {
            ToastUtils.toast(getActivity(), R.string.en_py_password_equal_account);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), R.string.en_py_account_error);
            return;
        }
        if (!GamaUtil.checkPasswordOld(password)) {
            ToastUtils.toast(getActivity(), R.string.en_py_password_error);
            return;
        }

        String email = gama_bind_et_vfcode.getEditableText().toString().trim();
        if (SStringUtil.isNotEmpty(email) && !Validator.isEmail(email)){
            ToastUtils.toast(getActivity(), R.string.en_py_email_format_error);
            return;
        }

        sLoginDialogv2.getLoginPresenter().accountBind(sLoginDialogv2.getActivity(), account, password, email, bindTpye);
    }


    public void setBindTpye(int bindTpye) {
        this.bindTpye = bindTpye;
    }

}

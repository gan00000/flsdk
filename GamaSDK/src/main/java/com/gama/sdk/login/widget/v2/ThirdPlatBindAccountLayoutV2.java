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

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.bean.SLoginType;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.utils.Validator;


public class ThirdPlatBindAccountLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private ImageView termsSelectImageView, eyeImageView;
    private TextView termsTextView, bindConfirm;

    private EditText registerPasswordEditText, registerAccountEditText,registerMailEditText;

    private String account;
    private String password;

    private int bindTpye = 0;


    public ThirdPlatBindAccountLayoutV2(Context context) {
        super(context);
    }

    public ThirdPlatBindAccountLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThirdPlatBindAccountLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_account_bind, null);

        backView = contentView.findViewById(R.id.py_back_button_v2);

        termsSelectImageView = (ImageView) contentView.findViewById(R.id.py_register_account_terms_check);
        termsTextView = (TextView) contentView.findViewById(R.id.py_register_terms_text_id);


        eyeImageView = (ImageView) contentView.findViewById(R.id.py_eye_imageview_id);
        registerPasswordEditText = (EditText) contentView.findViewById(R.id.py_bind_password);
        registerAccountEditText = (EditText) contentView.findViewById(R.id.py_bind_account);
        registerMailEditText = (EditText) contentView.findViewById(R.id.py_bind_account_mail);

        bindConfirm = (TextView) contentView.findViewById(R.id.py_bind_account_confirm);

//        if (Localization.getSGameLanguage(getActivity()) == SGameLanguage.en_US){
//            ((ImageView)contentView.findViewById(R.id.py_bind_account_title)).setImageResource(R.drawable.bg_title_bind_account_en);
//        }

        termsSelectImageView.setSelected(true);

        termsSelectImageView.setOnClickListener(this);
        eyeImageView.setOnClickListener(this);
        termsTextView.setOnClickListener(this);
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

        if (v == termsSelectImageView) {
            if (termsSelectImageView.isSelected()) {
                termsSelectImageView.setSelected(false);
            } else {
                termsSelectImageView.setSelected(true);
            }
        } else if (v == termsTextView) {

            if (bindTpye == SLoginType.bind_unique) {
                sLoginDialogv2.toRegisterTermsView(2);
            }else {
                sLoginDialogv2.toRegisterTermsView(3);
            }

        } else if (v == bindConfirm) {

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
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        password = registerPasswordEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }

        String email = registerMailEditText.getEditableText().toString().trim();

        if (!termsSelectImageView.isSelected()) {
            ToastUtils.toast(getActivity(), R.string.py_select_terms);
            return;
        }

        if (SStringUtil.isEqual(account, password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_error);
            return;
        }
        if (!GamaUtil.checkPassword(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_error);
            return;
        }

        if (SStringUtil.isNotEmpty(email) && !Validator.isEmail(email)){
            ToastUtils.toast(getActivity(), R.string.py_email_format_error);
            return;
        }

        sLoginDialogv2.getLoginPresenter().accountBind(sLoginDialogv2.getActivity(), account, password, email, bindTpye);
    }


    public void setBindTpye(int bindTpye) {
        this.bindTpye = bindTpye;
    }
}

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
import com.gama.base.utils.StarPyUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.utils.Validator;


public class AccountRegisterLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private ImageView termsSelectImageView, eyeImageView;
    private TextView termsTextView, registerConfirm;

    private EditText registerPasswordEditText, registerAccountEditText,registerMailEditText;

    private String account;
    private String password;

    public AccountRegisterLayoutV2(Context context) {
        super(context);
    }

    public AccountRegisterLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountRegisterLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_account_reg, null);

        backView = contentView.findViewById(R.id.py_back_button_v2);

        termsSelectImageView = (ImageView) contentView.findViewById(R.id.py_register_account_terms_check);
        termsTextView = (TextView) contentView.findViewById(R.id.py_register_terms_text_id);


        eyeImageView = (ImageView) contentView.findViewById(R.id.py_eye_imageview_id);
        registerPasswordEditText = (EditText) contentView.findViewById(R.id.py_register_password);
        registerAccountEditText = (EditText) contentView.findViewById(R.id.py_register_account);
        registerMailEditText = (EditText) contentView.findViewById(R.id.py_register_account_mail);

        registerConfirm = (TextView) contentView.findViewById(R.id.py_register_account_confirm);

        termsSelectImageView.setSelected(true);

        termsSelectImageView.setOnClickListener(this);
        eyeImageView.setOnClickListener(this);
        termsTextView.setOnClickListener(this);
        backView.setOnClickListener(this);
        registerConfirm.setOnClickListener(this);

//        if (StarPyUtil.isXM(getContext())){//星盟标题
//            ((ImageView)contentView.findViewById(R.id.v2_bg_title_register_iv)).setImageResource(R.drawable.bg_xm_title_register);
//        }
//
//        if (Localization.getSGameLanguage(getActivity()) == SGameLanguage.en_US){//星盟--星彼英文一样
//            ((ImageView)contentView.findViewById(R.id.v2_bg_title_register_iv)).setImageResource(R.drawable.bg_xm_title_register_en);
//        }

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

            sLoginDialogv2.toRegisterTermsView(1);

        } else if (v == registerConfirm) {
            register();
        } else if (v == backView) {//返回键
//            sLoginActivity.popBackStack();
            if (from == 2){
                sLoginDialogv2.toAccountLoginView();
            }else{
                sLoginDialogv2.toMainLoginView();
            }
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

    private void register() {

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

        if (!StarPyUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_error);
            return;
        }
        if (!StarPyUtil.checkPassword(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_error);
            return;
        }

        if (SStringUtil.isNotEmpty(email) && !Validator.isEmail(email)){
            ToastUtils.toast(getActivity(), R.string.py_email_format_error);
            return;
        }

        sLoginDialogv2.getLoginPresenter().register(sLoginDialogv2.getActivity(), account, password, email);
    }


}

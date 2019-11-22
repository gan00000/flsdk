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

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class PyAccountLoginV2En extends SLoginBaseRelativeLayoutEn {

    private View contentView;

    private Button loginMainLoginBtn;

    /**
     * 眼睛、保存密码
     */
    private ImageView eyeImageView, savePwdCheckBox;

    /**
     * 密码、账号
     */
    private EditText loginPasswordEditText, loginAccountEditText;
    private String account;
    private String password;
    private View loginMainGoRegisterBtn;
    private View loginMainGoFindPwd;
    private View loginMainGoAccountCenter;
    private View loginMainGoChangePassword;

    public PyAccountLoginV2En(Context context) {
        super(context);

    }


    public PyAccountLoginV2En(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PyAccountLoginV2En(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.v2_gama_account_login_en, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toMainLoginView();
            }
        });

        loginMainGoRegisterBtn = contentView.findViewById(R.id.gama_login_tv_register);
        loginMainGoFindPwd = contentView.findViewById(R.id.gama_login_tv_forget_password);
        loginMainGoAccountCenter = contentView.findViewById(R.id.gama_login_tv_link);
        loginMainGoChangePassword = contentView.findViewById(R.id.gama_login_tv_change_password);

        eyeImageView = contentView.findViewById(R.id.gama_login_iv_eye);

        loginAccountEditText = contentView.findViewById(R.id.gama_login_et_account);
        loginPasswordEditText = contentView.findViewById(R.id.gama_login_et_password);

        loginMainLoginBtn = contentView.findViewById(R.id.gama_login_btn_confirm);

        savePwdCheckBox = contentView.findViewById(R.id.gama_login_iv_remember_account);

        savePwdCheckBox.setSelected(true);

        savePwdCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savePwdCheckBox.isSelected()) {
                    savePwdCheckBox.setSelected(false);
                } else {
                    savePwdCheckBox.setSelected(true);
                }
            }
        });

        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginActivity.replaceFragment(new AccountLoginFragment());
                sLoginDialogv2.toMainLoginView();
            }
        });

        loginMainGoRegisterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginActivity.replaceFragmentBackToStack(new AccountRegisterFragment());

                sLoginDialogv2.toRegisterView(2);
            }
        });

        loginMainGoFindPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toFindPwdView();
            }
        });

        loginMainGoAccountCenter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toAccountManagerCenter();
            }
        });

        loginMainGoChangePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toChangePwdView();
            }
        });

        eyeImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eyeImageView.isSelected()) {
                    eyeImageView.setSelected(false);
                    // 显示为普通文本
                    loginPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    eyeImageView.setSelected(true);
                    // 显示为密码
                    loginPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // 使光标始终在最后位置
                Editable etable = loginPasswordEditText.getText();
                Selection.setSelection(etable, etable.length());
            }
        });

        loginMainLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        account = GamaUtil.getAccount(getContext());
        password = GamaUtil.getPassword(getContext());
        if (TextUtils.isEmpty(account)){
            account = GamaUtil.getMacAccount(getContext());
            password = GamaUtil.getMacPassword(getContext());
        }
        if (!TextUtils.isEmpty(account)){
            loginAccountEditText.setText(account);
            loginPasswordEditText.setText(password);
        }

        return contentView;
    }

    private void login() {

        account = loginAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.en_py_account_empty);
            return;
        }

        password = loginPasswordEditText.getEditableText().toString().trim();
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
//        if (!GamaUtil.checkPassword(password)) {
//            ToastUtils.toast(getActivity(), R.string.en_py_password_error);
//            return;
//        }

        sLoginDialogv2.getLoginPresenter().starpyAccountLogin(sLoginDialogv2.getActivity(),account,password, savePwdCheckBox.isSelected());

    }
}

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
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class PyAccountLoginV2 extends SLoginBaseRelativeLayout {

    private View contentView;

    private TextView loginMainLoginBtn;
    private ImageView eyeImageView, savePwdCheckBox;

    private EditText loginPasswordEditText, loginAccountEditText;
    private String account;

    private String password;

    private View loginMainGoRegisterBtn;
    private View loginMainGoFindPwd;
    private View loginMainFreeRegLogin;
    private View loginMainGoAccountCenter;



    public PyAccountLoginV2(Context context) {
        super(context);

    }


    public PyAccountLoginV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PyAccountLoginV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.v2_py_account_login, null);

        backView = contentView.findViewById(R.id.py_back_button_v2);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toMainLoginView();
            }
        });

        loginMainGoRegisterBtn = contentView.findViewById(R.id.py_login_go_reg_v2);
        loginMainGoFindPwd = contentView.findViewById(R.id.py_login_go_findpwd_v2);
        loginMainGoAccountCenter = contentView.findViewById(R.id.py_login_go_account_center);
        loginMainFreeRegLogin = contentView.findViewById(R.id.py_login_free_reg_login);//遊客登錄


        eyeImageView = (ImageView) contentView.findViewById(R.id.py_login_password_eye_v2);

        loginAccountEditText = (EditText) contentView.findViewById(R.id.py_login_account_v2);
        loginPasswordEditText = (EditText) contentView.findViewById(R.id.py_login_password_v2);

        loginMainLoginBtn = (TextView) contentView.findViewById(R.id.v2_member_btn_login);


        if (GamaUtil.isMainland(getContext())){
            loginMainFreeRegLogin.setVisibility(View.VISIBLE);
            backView.setVisibility(GONE);
        }

        savePwdCheckBox = (ImageView) contentView.findViewById(R.id.py_save_pwd_text_check_id);

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

        loginMainFreeRegLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.getLoginPresenter().macLogin(sLoginDialogv2.getActivity());
            }
        });

        loginMainGoAccountCenter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toAccountManagerCenter();
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
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        password = loginPasswordEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
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

        sLoginDialogv2.getLoginPresenter().starpyAccountLogin(sLoginDialogv2.getActivity(),account,password);

    }


}

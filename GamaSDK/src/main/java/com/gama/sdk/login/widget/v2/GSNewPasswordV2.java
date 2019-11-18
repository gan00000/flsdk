package com.gama.sdk.login.widget.v2;

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

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class GSNewPasswordV2 extends SLoginBaseRelativeLayout {

    private View contentView;

    private Button confirmBtn;
    private ImageView eyeImageView, eyeImageView2;

    private EditText newPasswordEditText, newPasswordEditText2;
    private String password1;

    private String password2;



    public GSNewPasswordV2(Context context) {
        super(context);

    }


    public GSNewPasswordV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GSNewPasswordV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.v2_account_new_password, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toMainLoginView();
            }
        });

        eyeImageView = contentView.findViewById(R.id.gama_newpassword_iv_eye1);
        eyeImageView2 = contentView.findViewById(R.id.gama_newpassword_iv_eye2);

        newPasswordEditText = contentView.findViewById(R.id.gama_newpassword_et_password1);
        newPasswordEditText2 = contentView.findViewById(R.id.gama_newpassword_et_password2);

        confirmBtn = contentView.findViewById(R.id.gama_login_btn_confirm);

        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginActivity.replaceFragment(new AccountLoginFragment());
                sLoginDialogv2.toMainLoginView();
            }
        });

        eyeImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eyeImageView.isSelected()) {
                    eyeImageView.setSelected(false);
                    // 显示为普通文本
                    newPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    eyeImageView.setSelected(true);
                    // 显示为密码
                    newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // 使光标始终在最后位置
                Editable etable = newPasswordEditText.getText();
                Selection.setSelection(etable, etable.length());
            }
        });

        eyeImageView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eyeImageView2.isSelected()) {
                    eyeImageView2.setSelected(false);
                    // 显示为普通文本
                    newPasswordEditText2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    eyeImageView2.setSelected(true);
                    // 显示为密码
                    newPasswordEditText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // 使光标始终在最后位置
                Editable etable = newPasswordEditText2.getText();
                Selection.setSelection(etable, etable.length());
            }
        });

        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        return contentView;
    }

    private void send() {

        password1 = newPasswordEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password1)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }

        password2 = newPasswordEditText2.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password2)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }

        if (!SStringUtil.isEqual(password1, password2)) {
            ToastUtils.toast(getActivity(), R.string.py_password_same);
            return;
        }

//        if (!GamaUtil.checkAccount(password1)) {
//            ToastUtils.toast(getActivity(), R.string.py_account_error);
//            return;
//        }
        if (!GamaUtil.checkPassword(password2)) {
            ToastUtils.toast(getActivity(), R.string.py_password_error);
            return;
        }

//        sLoginDialogv2.getLoginPresenter().starpyAccountLogin(sLoginDialogv2.getActivity(), password1, password2);

    }


}

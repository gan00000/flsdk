package com.mw.sdk.act;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.constant.BindType;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.SDKInputEditTextView;
import com.mw.sdk.login.widget.SDKInputType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.widget.SBaseRelativeLayout;


public class FloatChangePwdLayout extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private AccountModel accountModel;

    public void setAccountModel(AccountModel accountModel) {
        this.accountModel = accountModel;
    }

    private View contentView;

    private Button bindConfirm;

    /**
     * 密码、账号、手机、验证码
     */
    private EditText newPasswordEditText;
    private EditText oldPwdEditText;
    private EditText passwordAgaindEditText;

    private String oldPwd;
    private String password;

    SDKInputEditTextView oldSdkInputEditTextView;
    SDKInputEditTextView pwdSdkInputEditTextView;

    SDKInputEditTextView pwdAgainSdkInputEditTextView;

    private View iv_bind_phone_close;

    private SFCallBack sfCallBack;

    public void setSFCallBack(SFCallBack sfCallBack) {
        this.sfCallBack = sfCallBack;
    }

    public FloatChangePwdLayout(Context context) {
        super(context);
    }

    public FloatChangePwdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatChangePwdLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    //new实例的时候调用
    private View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.float_change_pwd, null);

        oldSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_change_old_pwd);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_password);
        pwdAgainSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_password_again);

        bindConfirm = contentView.findViewById(R.id.btn_confirm);

        oldSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);
        pwdAgainSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);

        oldSdkInputEditTextView.setEyeVisable(View.GONE);//不显示眼睛
        pwdSdkInputEditTextView.setEyeVisable(View.GONE);
        pwdAgainSdkInputEditTextView.setEyeVisable(View.GONE);

        oldSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);
        pwdSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);
        pwdAgainSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);

        oldSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.img_lock_pwd_3);
        pwdSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.img_lock_pwd_3);
        pwdAgainSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.img_lock_pwd_again);

        oldPwdEditText = oldSdkInputEditTextView.getInputEditText();
        oldPwdEditText.setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
        oldPwdEditText.setTextColor(getResources().getColor(R.color.black_s));
        oldPwdEditText.setHint(R.string.py_input_old_password);

        newPasswordEditText = pwdSdkInputEditTextView.getInputEditText();
        newPasswordEditText.setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
        newPasswordEditText.setTextColor(getResources().getColor(R.color.black_s));
        newPasswordEditText.setHint(R.string.text_input_new_pwd);

        passwordAgaindEditText = pwdAgainSdkInputEditTextView.getInputEditText();
        passwordAgaindEditText.setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
        passwordAgaindEditText.setTextColor(getResources().getColor(R.color.black_s));
        passwordAgaindEditText.setHint(R.string.text_input_new_pwd_confire);

        iv_bind_phone_close = contentView.findViewById(R.id.iv_bind_phone_close);


        bindConfirm.setOnClickListener(this);

        iv_bind_phone_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sfCallBack != null) {
                    sfCallBack.fail(null, "back");
                }
            }
        });

        return contentView;
    }

    @Override
    public void onViewVisible() {
        super.onViewVisible();
        oldPwdEditText.setText("");
        newPasswordEditText.setText("");
        passwordAgaindEditText.setText("");

//        SdkUtil.setAccountWithIcon(accountModel,sdkinputview_third_account.getIconImageView(),thirdAccountEditText);

    }


    @Override
    public void onClick(View v) {

        if (v == bindConfirm) {

//            if (has_bind_view.getVisibility() == VISIBLE){
//                if (sBaseDialog != null) {
//                    sBaseDialog.dismiss();
//                }
//                return;
//            }
            doChangePwd();

        }

    }

    private void doChangePwd() {

        oldPwd = oldPwdEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtils.toast(getActivity(), R.string.py_input_old_password);
            return;
        }

        password = newPasswordEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.text_input_new_pwd);
            return;
        }

        String againPassword = passwordAgaindEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(againPassword)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }


        if (!SStringUtil.isEqual(againPassword, password)) {
            ToastUtils.toast(getActivity(), R.string.text_pwd_not_equel);
            return;
        }

        if (!SdkUtil.checkPassword(oldPwd)) {
            toast(R.string.text_pwd_format);
            return;
        }
        if (!SdkUtil.checkPassword(password)) {
            toast(R.string.text_pwd_format);
            return;
        }
        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());
        if (sLoginResponse == null || sLoginResponse.getData() == null || SStringUtil.isEmpty(sLoginResponse.getData().getLoginId())){
            PL.i("getCurrentUserLoginResponse is emypt");
            toast(R.string.text_account_not_exist);
            return;
        }
        Request.changePwd(getActivity(), sLoginResponse.getData().getLoginId(), oldPwd, password, new SFCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String msg) {

                if (sfCallBack != null) {
                    sfCallBack.success(SdkUtil.getCurrentUserLoginResponse(getContext()),"");
                }
            }

            @Override
            public void fail(SLoginResponse sLoginResponse, String msg) {

            }
        });
    }


    @Override
    protected void onSetDialog() {
        super.onSetDialog();
//        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
//        remainTimeSeconds = sLoginDialogv2.getLoginPresenter().getRemainTimeSeconds();
    }

}

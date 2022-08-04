package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.SBaseRelativeLayout;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.constant.BindType;
import com.mw.sdk.login.model.AccountModel;
import com.mw.sdk.login.widget.SDKInputEditTextView;
import com.mw.sdk.login.widget.SDKInputType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;


public class ThirdPlatBindAccountLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private BindType bindTpye;
    private AccountModel accountModel;

    public void setBindTpye(BindType bindTpye) {
        this.bindTpye = bindTpye;
    }

    public void setAccountModel(AccountModel accountModel) {
        this.accountModel = accountModel;
    }

    private View contentView;
    private Button bindConfirm;

    /**
     * 密码、账号、手机、验证码
     */
    private EditText registerPasswordEditText;
    private EditText registerAccountEditText;

    private String account;
    private String password;

    SDKInputEditTextView accountSdkInputEditTextView;
    SDKInputEditTextView pwdSdkInputEditTextView;

    private View iv_bind_phone_close;

    private ILoginCallBack iLoginCallBack;

    public void setiLoginCallBack(ILoginCallBack iLoginCallBack) {
        this.iLoginCallBack = iLoginCallBack;
    }

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

    //new实例的时候调用
    private View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.mw_update_account, null);

        accountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_account);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_password);

//        sdkinputview_third_account = contentView.findViewById(R.id.sdkinputview_third_account);

        bindConfirm = contentView.findViewById(R.id.btn_confirm);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);
//        sdkinputview_third_account.setInputType(SDKInputType.SDKInputType_Account);

        accountSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);
        pwdSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);

        accountSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.mw_smail_icon2);
        pwdSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.mw_passowrd_icon2);

        registerAccountEditText = accountSdkInputEditTextView.getInputEditText();
        registerAccountEditText.setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
        registerAccountEditText.setTextColor(getResources().getColor(R.color.black_s));
        registerPasswordEditText = pwdSdkInputEditTextView.getInputEditText();
        registerPasswordEditText.setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
        registerPasswordEditText.setTextColor(getResources().getColor(R.color.black_s));
//        thirdAccountEditText = sdkinputview_third_account.getInputEditText();


        iv_bind_phone_close = contentView.findViewById(R.id.iv_bind_phone_close);

        bindConfirm.setOnClickListener(this);

        iv_bind_phone_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sBaseDialog != null) {
                    sBaseDialog.dismiss();
                }
            }
        });

        return contentView;
    }

    @Override
    public void onViewVisible() {
        super.onViewVisible();
        registerAccountEditText.setText("");
        registerPasswordEditText.setText("");

//        SdkUtil.setAccountWithIcon(accountModel,sdkinputview_third_account.getIconImageView(),thirdAccountEditText);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        if (thirdAccountEditText != null){
//            thirdAccountEditText.requestFocus();
//        }
    }

    @Override
    public void onClick(View v) {

        if (v == bindConfirm) {

            accountBind();

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


//        if (SStringUtil.isEqual(account, password)) {
//            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
//            return;
//        }

        if (!SdkUtil.checkAccount(account)) {
            toast(R.string.text_account_format);
            return;
        }
        if (!SdkUtil.checkPassword(password)) {
            toast(R.string.text_pwd_format);
            return;
        }

        Request.bindAcountInGame(getActivity(), true, SdkUtil.getPreviousLoginType(getContext()), account, password, new SFCallBack() {
            @Override
            public void success(Object result, String msg) {
                toast(R.string.text_account_bind_success2);
                if (sBaseDialog != null) {
                    sBaseDialog.dismiss();
                }
            }

            @Override
            public void fail(Object result, String msg) {

            }
        });
    }


    @Override
    public void statusCallback(int operation) {
    }

    @Override
    public void alertTime(int remainTimeSeconds) {
    }

    @Override
    public void dataCallback(Object o) {
    }

    @Override
    protected void onSetDialog() {
        super.onSetDialog();
//        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
//        remainTimeSeconds = sLoginDialogv2.getLoginPresenter().getRemainTimeSeconds();
    }

}

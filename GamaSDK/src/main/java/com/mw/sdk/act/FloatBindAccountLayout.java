package com.mw.sdk.act;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.core.base.callback.SFCallBack;
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


public class FloatBindAccountLayout extends SLoginBaseRelativeLayout implements View.OnClickListener{

    private BindType bindTpye;
    private AccountModel accountModel;

    public void setBindTpye(BindType bindTpye) {
        this.bindTpye = bindTpye;
    }

    public void setAccountModel(AccountModel accountModel) {
        this.accountModel = accountModel;
    }

    private View contentView;

    private View bind_view;
//    private View has_bind_view;


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

//    SDKInputEditTextView thirdAccountSdkInputEditTextView;
//    SDKInputEditTextView hasBindAccountSdkInputEditTextView;

    private View iv_bind_phone_close;

    private SFCallBack sfCallBack;

    public void setSFCallBack(SFCallBack sfCallBack) {
        this.sfCallBack = sfCallBack;
    }

    public FloatBindAccountLayout(Context context) {
        super(context);
    }

    public FloatBindAccountLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatBindAccountLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    //new实例的时候调用
    private View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.float_bind_account, null);

        bind_view = contentView.findViewById(R.id.ll_bind_view);
//        has_bind_view = contentView.findViewById(R.id.ll_has_bind_view);

        accountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_account);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_password);

        bindConfirm = contentView.findViewById(R.id.btn_confirm);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);
        pwdSdkInputEditTextView.setEyeVisable(View.GONE);//不显示眼睛

        accountSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);
        pwdSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);

        accountSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.img_persion_4);
        pwdSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.img_lock_pwd_3);

        registerAccountEditText = accountSdkInputEditTextView.getInputEditText();
        registerAccountEditText.setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
        registerAccountEditText.setTextColor(getResources().getColor(R.color.black_s));
        registerPasswordEditText = pwdSdkInputEditTextView.getInputEditText();
        registerPasswordEditText.setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
        registerPasswordEditText.setTextColor(getResources().getColor(R.color.black_s));

        iv_bind_phone_close = contentView.findViewById(R.id.iv_bind_phone_close);

//        thirdAccountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_has_bind_third_account);
//        hasBindAccountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_has_bind_account);
//        thirdAccountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
//        hasBindAccountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);

//        thirdAccountSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);
//        hasBindAccountSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.sdk_bg_input2);
//        thirdAccountSdkInputEditTextView.getInputEditText().setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
//        thirdAccountSdkInputEditTextView.getInputEditText().setTextColor(getResources().getColor(R.color.black_s));
//        hasBindAccountSdkInputEditTextView.getInputEditText().setHintTextColor(getResources().getColor(R.color.c_B8B8B8));
//        hasBindAccountSdkInputEditTextView.getInputEditText().setTextColor(getResources().getColor(R.color.black_s));

//        hasBindAccountSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.img_persion);

//        thirdAccountSdkInputEditTextView.getInputEditText().setEnabled(false);
//        hasBindAccountSdkInputEditTextView.getInputEditText().setEnabled(false);

        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());

        if (sLoginResponse != null && sLoginResponse.getData() != null && sLoginResponse.getData().isBind()){
//            bind_view.setVisibility(GONE);
//            has_bind_view.setVisibility(VISIBLE);
//            AccountModel accountModel = new AccountModel();
//            accountModel.setLoginType(sLoginResponse.getData().getLoginType());
//            accountModel.setAccount(sLoginResponse.getData().getLoginId());
//            setAccountWithIcon2(accountModel, thirdAccountSdkInputEditTextView.getIconImageView(), thirdAccountSdkInputEditTextView.getInputEditText());
//            hasBindAccountSdkInputEditTextView.getInputEditText().setText(sLoginResponse.getData().getLoginId());

        }else{
//            bind_view.setVisibility(VISIBLE);
//            has_bind_view.setVisibility(GONE);
        }

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
        registerAccountEditText.setText("");
        registerPasswordEditText.setText("");

//        SdkUtil.setAccountWithIcon(accountModel,sdkinputview_third_account.getIconImageView(),thirdAccountEditText);

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

                if (sfCallBack != null) {
                    sfCallBack.success(SdkUtil.getCurrentUserLoginResponse(getContext()),"");
                }

            }

            @Override
            public void fail(Object result, String msg) {
                if (result != null){
                    SLoginResponse response = (SLoginResponse)result;
                    toast("" + response.getMessage());
                }
                if (sfCallBack != null){

//                    sfCallBack.fail(null,"");
                }
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

package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.base.bean.GamaAreaInfoBean;
import com.mw.sdk.SBaseRelativeLayout;
import com.mw.base.utils.GamaUtil;
import com.mw.sdk.R;
import com.mw.sdk.login.constant.BindType;
import com.mw.sdk.login.constant.ViewType;
import com.mw.sdk.login.widget.SDKInputEditTextView;
import com.mw.sdk.login.widget.SDKInputType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;


public class ThirdPlatBindAccountLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private BindType bindTpye;

    public void setBindTpye(BindType bindTpye) {
        this.bindTpye = bindTpye;
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
    SDKInputEditTextView sdkinputview_third_account;

//    SDKInputEditTextView vfSdkInputEditTextView;
//    SDKPhoneInputEditTextView mSdkPhoneInputEditTextView;

    //选中的区域信息
//    private GamaAreaInfoBean selectedBean;

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
        contentView = inflater.inflate(R.layout.mw_update_account, null);

        backView = contentView.findViewById(R.id.layout_head_back);
        TextView titleTextView = contentView.findViewById(R.id.sdk_head_title);
        titleTextView.setText(R.string.text_update_account);

        accountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_account);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_password);
        sdkinputview_third_account = contentView.findViewById(R.id.sdkinputview_third_account);

        bindConfirm = contentView.findViewById(R.id.btn_confirm_bind);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);
        sdkinputview_third_account.setInputType(SDKInputType.SDKInputType_Account);

        registerAccountEditText = accountSdkInputEditTextView.getInputEditText();
        registerPasswordEditText = pwdSdkInputEditTextView.getInputEditText();



        backView.setOnClickListener(this);
        bindConfirm.setOnClickListener(this);

        return contentView;
    }

    @Override
    public void refreshViewData() {
        super.refreshViewData();
        registerAccountEditText.setText("");
        registerPasswordEditText.setText("");
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


        if (SStringUtil.isEqual(account, password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            return;
        }
        if (!GamaUtil.checkPassword(password)) {
            return;
        }


//        sLoginDialogv2.getLoginPresenter().accountBind(sLoginDialogv2.getActivity(), account, password, areaCode,
//                phone, vfcode, bindTpye);
    }



    private void getAndShowArea() {
        sLoginDialogv2.getLoginPresenter().getAreaInfo(sLoginDialogv2.getActivity());
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
    protected void doSomething() {
        super.doSomething();
        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
        remainTimeSeconds = sLoginDialogv2.getLoginPresenter().getRemainTimeSeconds();
    }

}

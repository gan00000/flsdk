package com.gama.sdk.login.widget.v2;

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
import com.gama.base.bean.GamaAreaInfoBean;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.constant.GSLoginCommonConstant;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.sdk.R;
import com.gama.sdk.SBaseRelativeLayout;
import com.gama.sdk.login.widget.SDKInputEditTextView;
import com.gama.sdk.login.widget.SDKInputType;
import com.gama.sdk.login.widget.SDKPhoneInputEditTextView;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;


public class ThirdPlatBindAccountLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;
    private Button bindConfirm, gama_bind_btn_get_vfcode;

    /**
     * 密码、账号、手机、验证码
     */
    private EditText registerPasswordEditText;
    private EditText registerAccountEditText;

    /**
     * 区号, 手机接收限制提示
     */
    private TextView gama_bind_tv_limit_hint;

    private String account;
    private String password;

    private int bindTpye = 0;
    private int fromPage = 0;

    SDKInputEditTextView accountSdkInputEditTextView;
    SDKInputEditTextView pwdSdkInputEditTextView;
    SDKInputEditTextView pwdAgainSdkInputEditTextView;
    SDKInputEditTextView vfSdkInputEditTextView;
    SDKPhoneInputEditTextView mSdkPhoneInputEditTextView;

    //选中的区域信息
    private GamaAreaInfoBean selectedBean;

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

        backView = contentView.findViewById(R.id.gama_head_iv_back);
        TextView titleTextView = contentView.findViewById(R.id.sdk_head_title);
        titleTextView.setText(R.string.py_login_page_account_bind);

        accountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_account);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_password);
        pwdAgainSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_password_again);
        vfSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_bind_vf);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);
        pwdAgainSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);
        vfSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Vf_Code);

        registerAccountEditText = accountSdkInputEditTextView.getInputEditText();
        registerPasswordEditText = pwdSdkInputEditTextView.getInputEditText();

        gama_bind_btn_get_vfcode = contentView.findViewById(R.id.gama_bind_btn_get_vfcode);
        bindConfirm = contentView.findViewById(R.id.gama_bind_btn_confirm);


        gama_bind_tv_limit_hint = contentView.findViewById(R.id.gama_bind_tv_limit_hint);
        String phoneMsgLimitHint = GamaUtil.getPhoneMsgLimitHint(getContext());
        if(!TextUtils.isEmpty(phoneMsgLimitHint)) {
            gama_bind_tv_limit_hint.setText(phoneMsgLimitHint);
        }

        backView.setOnClickListener(this);
        bindConfirm.setOnClickListener(this);
        gama_bind_btn_get_vfcode.setOnClickListener(this);

        setDefaultAreaInfo();

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
            if (fromPage == GSLoginCommonConstant.GsLoginUiPageNumber.GS_PAGE_MAIN) {
                sLoginDialogv2.toMainLoginView();
            } else {
                sLoginDialogv2.toAccountManagerCenter();
            }

        } else if(v == gama_bind_btn_get_vfcode) {
//            sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
//            getVfcode();

//            getVfcodeByEmail();
        }

    }

    private void accountBind() {

        if (!accountSdkInputEditTextView.checkAccount()){
            return;
        }
        if (!pwdSdkInputEditTextView.checkPassword()){
            return;
        }

        account = registerAccountEditText.getEditableText().toString().trim();
        password = registerPasswordEditText.getEditableText().toString().trim();
        String vfcode = vfSdkInputEditTextView.getInputEditText().getEditableText().toString().trim();

        if (SStringUtil.isEqual(account, password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
            return;
        }

        if (!password.equals(pwdAgainSdkInputEditTextView.getInputEditText().getEditableText().toString().trim())){
            ToastUtils.toast(getActivity(), R.string.py_password_not_equals);
            return;
        }

        if (!mSdkPhoneInputEditTextView.checkPhoneOk()){
            return;
        }

        if (!vfSdkInputEditTextView.checkVfCode()){
            return;
        }

      /*  String email = emailEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_empty);
            return;
        }

        String vfcode = gama_bind_et_vfcode.getEditableText().toString();
        if(TextUtils.isEmpty(vfcode)) {
            ToastUtils.toast(getActivity(), R.string.py_vfcode_empty);
            return;
        }
        if (SStringUtil.isNotEmpty(email) && !Validator.isEmail(email)){
            ToastUtils.toast(getActivity(), R.string.py_email_format_error);
            return;
        }*/

        sLoginDialogv2.getLoginPresenter().accountBind(sLoginDialogv2.getActivity(), account, password, mSdkPhoneInputEditTextView.getPhoneAreaCode(),
                mSdkPhoneInputEditTextView.getPhoneNumber(), vfcode, bindTpye);
//        sLoginDialogv2.getLoginPresenter().accountBind(sLoginDialogv2.getActivity(), account, password, "", "", "", bindTpye);
    }


    public void setBindTpye(int bindTpye) {
        this.bindTpye = bindTpye;
    }

    public void setFromPage(int fromPage) {
        this.fromPage = fromPage;
    }

    private void getAndShowArea() {
        sLoginDialogv2.getLoginPresenter().getAreaInfo(sLoginDialogv2.getActivity());
    }

    private void getVfcode() {
        if (!mSdkPhoneInputEditTextView.checkPhoneOk()){
            return;
        }
        String interfaceName = GSRequestMethod.RequestVfcodeInterface.bind.getString();

        sLoginDialogv2.getLoginPresenter().getPhoneVfcode(sLoginDialogv2.getActivity(), mSdkPhoneInputEditTextView.getPhoneAreaCode(), mSdkPhoneInputEditTextView.getPhoneNumber(), interfaceName);
    }


   /* private void getVfcodeByEmail() {

        String email = emailEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_empty);
            return;
        }

        if (!Validator.isEmail(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_format_error);
            return;
        }

        String interfaceName = GSRequestMethod.RequestVfcodeInterface.bind.getString();

        sLoginDialogv2.getLoginPresenter().getEmailVfcode(sLoginDialogv2.getActivity(), this, email, interfaceName);
    }*/

    @Override
    public void statusCallback(int operation) {
        if(TIME_LIMIT == operation) {
            gama_bind_btn_get_vfcode.setBackgroundResource(R.drawable.gama_ui_bg_btn_unclickable);
            gama_bind_btn_get_vfcode.setClickable(false);
        } else if(TIME_OUT == operation) {
            gama_bind_btn_get_vfcode.setBackgroundResource(R.drawable.bg_192d3f_46);
            gama_bind_btn_get_vfcode.setClickable(true);
            gama_bind_btn_get_vfcode.setText(R.string.py_register_account_get_vfcode);
        }
    }

    @Override
    public void alertTime(int remainTimeSeconds) {
        if(gama_bind_btn_get_vfcode.isClickable()) {
            gama_bind_btn_get_vfcode.setClickable(false);
        }
        gama_bind_btn_get_vfcode.setText(remainTimeSeconds + "s");
    }

    @Override
    public void dataCallback(Object o) {
        if (o instanceof GamaAreaInfoBean) {
            selectedBean = (GamaAreaInfoBean) o;
            String text = selectedBean.getValue();
        }
    }

    @Override
    protected void doSomething() {
        super.doSomething();
        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
        remainTimeSeconds = sLoginDialogv2.getLoginPresenter().getRemainTimeSeconds();
        if(remainTimeSeconds > 0) {
            gama_bind_btn_get_vfcode.setBackgroundResource(R.drawable.gama_ui_bg_btn_unclickable);
            gama_bind_btn_get_vfcode.setClickable(false);
            gama_bind_btn_get_vfcode.setText(remainTimeSeconds + "s");
        }
    }

    private void setDefaultAreaInfo() {
        selectedBean = new GamaAreaInfoBean();
        selectedBean.setValue(getResources().getString(R.string.py_default_area_num));
        selectedBean.setPattern(getResources().getString(R.string.py_default_area_num_pattern));
    }
}

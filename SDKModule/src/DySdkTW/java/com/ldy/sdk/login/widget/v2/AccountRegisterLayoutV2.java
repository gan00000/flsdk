package com.ldy.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ldy.sdk.BuildConfig;
import com.mybase.utils.ToastUtils;
import com.ldy.sdk.SBaseRelativeLayout;
import com.ldy.base.utils.SdkUtil;
import com.ldy.sdk.R;
import com.ldy.sdk.login.widget.SDKInputEditTextView;
import com.ldy.sdk.login.widget.SDKInputType;
import com.ldy.sdk.login.widget.SLoginBaseRelativeLayout;


public class AccountRegisterLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;

    private Button registerConfirm, vfCodeBtn;

    /**
     * 密码、账号、手机、验证码
     */
    private EditText registerPasswordEditText, registerAccountEditText;
    private EditText vfCodeEditTextView;
    /**
     * 区号,手机接收限制提示
     */
    private String account;
    private String password;


    private SDKInputEditTextView accountSdkInputEditTextView;
    private SDKInputEditTextView pwdSdkInputEditTextView;

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
        contentView = inflater.inflate(R.layout.sady_once15846, null);

        accountSdkInputEditTextView = contentView.findViewById(R.id.mId_angukidit_shortfier);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.mId_findose_soldieral);

        //設置類型
        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);


        registerAccountEditText = accountSdkInputEditTextView.getInputEditText();
        registerPasswordEditText = pwdSdkInputEditTextView.getInputEditText();

        registerConfirm = contentView.findViewById(R.id.mId_plasmuchia_felicade);

        View ll_vfcode = contentView.findViewById(R.id.mId_ll_vfcode);
        if (BuildConfig.reg_is_need_vfcode){
            ll_vfcode.setVisibility(VISIBLE);
            vfCodeBtn = contentView.findViewById(R.id.mId_federalaneous_receiveality);
            vfCodeBtn.setOnClickListener(this);
            vfCodeEditTextView = contentView.findViewById(R.id.mId_catchie_voladropess);

        }else {
            ll_vfcode.setVisibility(GONE);
        }

        registerConfirm.setOnClickListener(this);

        return contentView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (registerAccountEditText != null) {
            registerAccountEditText.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {

        if (v == registerConfirm) {
            register();
        } else if (v == backView) {//返回键
//            sLoginActivity.popBackStack();
//            if (from == 2) {
//                sLoginDialogv2.toAccountLoginView();
//            } else {
//                sLoginDialogv2.toLoginWithRegView();
//            }
        } else if (v == vfCodeBtn) {
            getVfcodeByEmail();
        }

    }

    private void register() {

        account = registerAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.mstr_egri_sacceur);
            return;
        }

        if (!SdkUtil.checkAccount(account)) {
            toast(R.string.mstr_downsive_strategyness);
            return;
        }

        password = registerPasswordEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.mstr_multaton_uxoriatic);
            return;
        }

        if (!SdkUtil.checkPassword(password)) {
            toast(R.string.mstr_vasety_parvitor);
            return;
        }

        String vfCode = "";
        if(BuildConfig.reg_is_need_vfcode && vfCodeEditTextView != null){
            //判断验证码
            vfCode = vfCodeEditTextView.getEditableText().toString().trim();

            if (TextUtils.isEmpty(vfCode)) {
                ToastUtils.toast(getActivity(), R.string.mstr_chryso_expectition);
                return;
            }
        }

        sLoginDialogv2.getLoginPresenter().register(sLoginDialogv2.getActivity(), account, password, "areaCode", "phone", vfCode, "");
    }


    private void getVfcodeByEmail() {

        String xaccount = registerAccountEditText.getText().toString().trim();
        if (TextUtils.isEmpty(xaccount)) {
            ToastUtils.toast(getContext(), R.string.mstr_egri_sacceur);
            return;
        }

        if (!SdkUtil.checkAccount(xaccount)) {
            ToastUtils.toast(getContext(),R.string.mstr_downsive_strategyness);
            return;
        }
//        if (TextUtils.isEmpty(password)) {
//            ToastUtils.toast(getActivity(), R.string.mstr_multaton_uxoriatic);
//            return;
//        }
//        if (!GamaUtil.checkPassword(password)) {
//            ToastUtils.toast(activity,R.string.mstr_vasety_parvitor);
//            return;
//        }

        sLoginDialogv2.getLoginPresenter().getEmailVfcode(sLoginDialogv2.getActivity(),this,xaccount,"1");
    }

    @Override
    public void statusCallback(int operation) {

        if (vfCodeBtn == null){
            return;
        }
        if (TIME_LIMIT == operation) {
            vfCodeBtn.setClickable(false);
        } else if (TIME_OUT == operation) {
            vfCodeBtn.setClickable(true);
            vfCodeBtn.setText(R.string.mstr_appear_agog);
        }

    }

    @Override
    public void dataCallback(Object o) {
    }

    @Override
    public void alertTime(int remainTimeSeconds) {
        if (vfCodeBtn == null){
            return;
        }
        if(vfCodeBtn.isClickable()) {
            vfCodeBtn.setClickable(false);
        }
        vfCodeBtn.setText(remainTimeSeconds + "s");

    }

    @Override
    protected void onSetDialog() {
        super.onSetDialog();

        if (vfCodeBtn == null){
            return;
        }
        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
        remainTimeSeconds = sLoginDialogv2.getLoginPresenter().getRemainTimeSeconds();
        if(remainTimeSeconds > 0) {
            vfCodeBtn.setClickable(false);
            vfCodeBtn.setText(remainTimeSeconds + "s");
        }

    }

}

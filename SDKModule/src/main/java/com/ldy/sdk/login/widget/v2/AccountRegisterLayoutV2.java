//package com.mw.sdk.login.widget.v2;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.mybase.utils.ToastUtils;
//import com.mw.sdk.SBaseRelativeLayout;
//import com.mw.base.utils.SdkUtil;
//import com.mw.sdk.R;
//import com.mw.sdk.login.widget.SDKInputEditTextView;
//import com.mw.sdk.login.widget.SDKInputType;
//import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
//
//
//public class AccountRegisterLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {
//
//    private View contentView;
//
//    private Button registerConfirm;
//
//    /**
//     * 密码、账号、手机、验证码
//     */
//    private EditText registerPasswordEditText, registerAccountEditText;
//    /**
//     * 区号,手机接收限制提示
//     */
//    private String account;
//    private String password;
//
//
//    private SDKInputEditTextView accountSdkInputEditTextView;
//    private SDKInputEditTextView pwdSdkInputEditTextView;
//
//    public AccountRegisterLayoutV2(Context context) {
//        super(context);
//    }
//
//    public AccountRegisterLayoutV2(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public AccountRegisterLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected View createView(Context context, LayoutInflater layoutInflater) {
//        return onCreateView(layoutInflater);
//    }
//
//    private View onCreateView(LayoutInflater inflater) {
//        contentView = inflater.inflate(R.layout.okokok_flexior, null);
//
//        accountSdkInputEditTextView = contentView.findViewById(R.id.mId_angukidit_shortfier);
//        pwdSdkInputEditTextView = contentView.findViewById(R.id.mId_findose_soldieral);
//
//        //設置類型
//        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
//        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);
//
//
//        registerAccountEditText = accountSdkInputEditTextView.getInputEditText();
//        registerPasswordEditText = pwdSdkInputEditTextView.getInputEditText();
//
//        registerConfirm = contentView.findViewById(R.id.mId_plasmuchia_felicade);
//
//        registerConfirm.setOnClickListener(this);
//
//        return contentView;
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (registerAccountEditText != null) {
//            registerAccountEditText.requestFocus();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        if (v == registerConfirm) {
//            register();
//        } else if (v == backView) {//返回键
////            sLoginActivity.popBackStack();
////            if (from == 2) {
////                sLoginDialogv2.toAccountLoginView();
////            } else {
////                sLoginDialogv2.toLoginWithRegView();
////            }
//        }
//
//    }
//
//    private void register() {
//
//        account = registerAccountEditText.getEditableText().toString().trim();
//        if (TextUtils.isEmpty(account)) {
//            ToastUtils.toast(getActivity(), R.string.mstr_egri_sacceur);
//            return;
//        }
//
//        password = registerPasswordEditText.getEditableText().toString().trim();
//        if (TextUtils.isEmpty(password)) {
//            ToastUtils.toast(getActivity(), R.string.mstr_multaton_uxoriatic);
//            return;
//        }
//
//
//        if (!SdkUtil.checkAccount(account)) {
//            toast(R.string.mstr_downsive_strategyness);
//            return;
//        }
//        if (!SdkUtil.checkPassword(password)) {
//            toast(R.string.mstr_vasety_parvitor);
//            return;
//        }
//
//        sLoginDialogv2.getLoginPresenter().register(sLoginDialogv2.getActivity(), account, password, "areaCode", "phone", "vfcode", "");
//    }
//
//    @Override
//    public void statusCallback(int operation) {
//    }
//
//    @Override
//    public void dataCallback(Object o) {
//    }
//
//    @Override
//    public void alertTime(int remainTimeSeconds) {
//    }
//
//    @Override
//    protected void onSetDialog() {
//        super.onSetDialog();
//    }
//
//}

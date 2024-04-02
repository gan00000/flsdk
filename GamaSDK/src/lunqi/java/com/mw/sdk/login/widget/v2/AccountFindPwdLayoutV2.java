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
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.widget.SBaseRelativeLayout;
import com.mw.sdk.R;
import com.mw.sdk.login.widget.SDKInputEditTextView;
import com.mw.sdk.login.widget.SDKInputType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;


public class AccountFindPwdLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;
    private TextView findPwdConfireBtn;

    private EditText findPwdAccountEditText;

    private SDKInputEditTextView accountSdkInputEditTextView, vfCodeSdkInputEditTextView;
    private SDKInputEditTextView newPwdSdkInputEditTextView, newPwdAgainSdkInputEditTextView;
//    private SDKPhoneInputEditTextView mSdkPhoneInputEditTextView;

    private String account;
//    private String email;

    Button btn_find_get_vfcode;

    public AccountFindPwdLayoutV2(Context context) {
        super(context);
    }

    public AccountFindPwdLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountFindPwdLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }



    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.mw_find_pwd, null);

        backView = contentView.findViewById(R.id.layout_head_back);

        TextView titleTextView = contentView.findViewById(R.id.sdk_head_title);
        titleTextView.setText(R.string.py_login_page_forgot_pwd);

        accountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_findpwd_account);
        vfCodeSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_findpwd_vf);
        newPwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_findpwd_new);
        newPwdAgainSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_findpwd_new_again);
//        mSdkPhoneInputEditTextView = contentView.findViewById(R.id.sdkinputview_findpwd_phone);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        vfCodeSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Vf_Code);

        newPwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_New_Password);
        newPwdAgainSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password_Again);

        findPwdAccountEditText = accountSdkInputEditTextView.getInputEditText();

        findPwdConfireBtn = contentView.findViewById(R.id.gama_find_btn_confirm);
        btn_find_get_vfcode = contentView.findViewById(R.id.btn_find_get_vfcode);

        backView.setOnClickListener(this);
        findPwdConfireBtn.setOnClickListener(this);
        btn_find_get_vfcode.setOnClickListener(this);

        return contentView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {

        if (v == findPwdConfireBtn) {
            findPwd();
        } else if (v == backView) {//返回键
            sLoginDialogv2.toLoginWithRegView(null);
            sLoginDialogv2.distoryView(this);
        }
        else if (v == btn_find_get_vfcode) {
            getVfcodeByEmail();
        }

    }

    private void getVfcodeByEmail() {

        account = findPwdAccountEditText.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getContext(), R.string.py_account_empty);
            return;
        }

        if (!SdkUtil.checkAccount(account)) {
            ToastUtils.toast(getContext(),R.string.text_account_format);
            return;
        }
//        if (TextUtils.isEmpty(password)) {
//            ToastUtils.toast(getActivity(), R.string.py_password_empty);
//            return;
//        }
//        if (!GamaUtil.checkPassword(password)) {
//            ToastUtils.toast(activity,R.string.text_pwd_format);
//            return;
//        }

        sLoginDialogv2.getLoginPresenter().getEmailVfcode(sLoginDialogv2.getActivity(),this,account,"");
    }


    private void findPwd() {

        account = findPwdAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        String password = newPwdSdkInputEditTextView.getInputEditText().getEditableText().toString().trim();
        String againPassword = newPwdAgainSdkInputEditTextView.getInputEditText().getEditableText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }

//        if (TextUtils.isEmpty(againPassword)) {
//            ToastUtils.toast(getActivity(), R.string.py_password_empty);
//            return;
//        }

        if (!SdkUtil.checkPassword(password)) {
            ToastUtils.toast(getContext(),R.string.text_pwd_format);
            return;
        }

//        if (!SStringUtil.isEqual(password,againPassword)){
//            ToastUtils.toast(getActivity(), R.string.text_pwd_not_equel);
//            return;
//        }

        String vfCode = vfCodeSdkInputEditTextView.getInputEditText().getEditableText().toString().trim();

        if (TextUtils.isEmpty(vfCode)) {
            ToastUtils.toast(getActivity(), R.string.py_vfcode_empty);
            return;
        }

        sLoginDialogv2.getLoginPresenter().findPwd(sLoginDialogv2.getActivity(), account, password, account, vfCode);
    }


    @Override
    public void statusCallback(int operation) {
        if (TIME_LIMIT == operation) {
//            gama_register_btn_get_vfcode.setBackgroundResource(R.drawable.gama_ui_bg_btn_unclickable);
            btn_find_get_vfcode.setClickable(false);
        } else if (TIME_OUT == operation) {
//            gama_register_btn_get_vfcode.setBackgroundResource(R.drawable.bg_192d3f_46);
            btn_find_get_vfcode.setClickable(true);
            btn_find_get_vfcode.setText(R.string.py_register_account_get_vfcode);
        }
    }

    @Override
    public void dataCallback(Object o) {
    }

    @Override
    public void alertTime(int remainTimeSeconds) {
        if(btn_find_get_vfcode.isClickable()) {
            btn_find_get_vfcode.setClickable(false);
        }
        btn_find_get_vfcode.setText(remainTimeSeconds + "s");
    }


    @Override
    protected void onSetDialog() {
        super.onSetDialog();
        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
        remainTimeSeconds = sLoginDialogv2.getLoginPresenter().getRemainTimeSeconds();
        if(remainTimeSeconds > 0) {
            btn_find_get_vfcode.setClickable(false);
            btn_find_get_vfcode.setText(remainTimeSeconds + "s");
        }
    }

    private void setDefaultAreaInfo() {
    }

    @Override
    public void onViewVisible() {
        super.onViewVisible();
        findPwdAccountEditText.setText("");
//        gama_find_et_phone.setText("");
        vfCodeSdkInputEditTextView.getInputEditText().setText("");
    }
}
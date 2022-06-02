package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.flyfun.sdk.SBaseRelativeLayout;
import com.flyfun.sdk.login.widget.SDKInputEditTextView;
import com.flyfun.sdk.login.widget.SDKInputType;
import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.R;


public class WelcomeBackLayout extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;
    private View layout_need_update_account,layout_has_update_account;
    private TextView tv_account_update_tips;
    private ImageView iv_update_account;

    private EditText findPwdAccountEditText;

    private SDKInputEditTextView accountSdkInputEditTextView;

    private String account;

    Button btn_login_game,btn_swith_account,btn_update_account,btn_change_pwd,btn_swith_account2;

    public WelcomeBackLayout(Context context) {
        super(context);
    }

    public WelcomeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WelcomeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }



    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.mw_welcome_back, null);

        backView = contentView.findViewById(R.id.layout_head_back);

        TextView titleTextView = contentView.findViewById(R.id.sdk_head_title);
        titleTextView.setText(R.string.text_welcome_back);

        accountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_welcome_account);
        tv_account_update_tips = contentView.findViewById(R.id.tv_account_update_tips);
        iv_update_account = contentView.findViewById(R.id.iv_update_account);
        btn_login_game = contentView.findViewById(R.id.btn_login_game);
        layout_need_update_account = contentView.findViewById(R.id.layout_need_update_account);
        layout_has_update_account = contentView.findViewById(R.id.layout_has_update_account);
        btn_swith_account = contentView.findViewById(R.id.btn_swith_account);
        btn_update_account = contentView.findViewById(R.id.btn_update_account);
        btn_change_pwd = contentView.findViewById(R.id.btn_change_pwd);
        btn_swith_account2 = contentView.findViewById(R.id.btn_swith_account2);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        findPwdAccountEditText = accountSdkInputEditTextView.getInputEditText();

        backView.setOnClickListener(this);
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

        if (v == btn_login_game) {
            findPwd();
        } else if (v == backView) {//返回键
            sLoginDialogv2.toAccountLoginView();
        }
//        else if (v == gama_find_btn_get_vfcode || v == mSdkPhoneInputEditTextView.getAraeCodeMoreListView()) {
//            getVfcodeByPhone();
//        }

    }

    private void findPwd() {

        account = findPwdAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }


        String areaCode = "";//gama_find_tv_area.getText().toString();
        if(TextUtils.isEmpty(areaCode)) {
            ToastUtils.toast(getActivity(), R.string.py_area_code_empty);
            return;
        }
        String phone = "";//gama_find_et_phone.getEditableText().toString().trim();
        if (SStringUtil.isEmpty(phone)){
            ToastUtils.toast(getActivity(), R.string.py_register_account_phone);
            return;
        }
//        if(!phone.matches(selectedBean.getPattern())) {
//            ToastUtils.toast(getActivity(), R.string.py_phone_error);
//            return;
//        }
//
        String vfCode = "";//vfCodeSdkInputEditTextView.getInputEditText().getEditableText().toString().trim();

        if (TextUtils.isEmpty(vfCode)) {
            ToastUtils.toast(getActivity(), R.string.py_vfcode_empty);
            return;
        }

        sLoginDialogv2.getLoginPresenter().findPwd(sLoginDialogv2.getActivity(), account, areaCode, phone, "vfCode");
    }


    @Override
    public void statusCallback(int operation) {
    }

    @Override
    public void dataCallback(Object o) {
    }

    @Override
    public void alertTime(int remainTimeSeconds) {
    }


    @Override
    protected void doSomething() {
        super.doSomething();
    }


    @Override
    public void refreshViewData() {
        super.refreshViewData();
        findPwdAccountEditText.setText("");
    }
}

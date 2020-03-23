package com.gama.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.utils.ToastUtils;
import com.gama.base.bean.GamaAreaInfoBean;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.sdk.R;
import com.gama.sdk.SBaseRelativeLayout;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.utils.Validator;


public class PhoneVerifyLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;
    private Button registerConfirm, gama_verify_btn_get_vfcode;

    /**
     * 密码、账号、手机、验证码
     */
    private EditText gama_verify_et_phone, gama_verify_et_vfcode, emailEditText;
    /**
     * 区号, 手机接收限制提示
     */
    private TextView gama_verify_tv_area, gama_verify_tv_limit_hint;

    //选中的区域信息
    private GamaAreaInfoBean selectedBean;
    private String loginType, thirdId;

    public PhoneVerifyLayoutV2(Context context) {
        super(context);
    }

    public PhoneVerifyLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneVerifyLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_phone_verification, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);

        gama_verify_et_phone = contentView.findViewById(R.id.gama_verify_et_phone);
        gama_verify_et_vfcode = contentView.findViewById(R.id.gama_verify_et_vfcode);
        gama_verify_tv_area = contentView.findViewById(R.id.gama_verify_tv_area);

        registerConfirm = contentView.findViewById(R.id.gama_verify_btn_confirm);
        gama_verify_btn_get_vfcode = contentView.findViewById(R.id.gama_verify_btn_get_vfcode);

        gama_verify_tv_limit_hint = contentView.findViewById(R.id.gama_verify_tv_limit_hint);
        String phoneMsgLimitHint = GamaUtil.getPhoneMsgLimitHint(getContext());
        if(!TextUtils.isEmpty(phoneMsgLimitHint)) {
            gama_verify_tv_limit_hint.setText(phoneMsgLimitHint);
        }

        backView.setOnClickListener(this);
        registerConfirm.setOnClickListener(this);
        gama_verify_btn_get_vfcode.setOnClickListener(this);
        gama_verify_tv_area.setOnClickListener(this);

        setDefaultAreaInfo();

        return contentView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onClick(View v) {

        if (v == registerConfirm) {
            register();
        } else if (v == backView) {//返回键,直接返回主登入页面
            sLoginDialogv2.toMainLoginView();
        } else if (v == gama_verify_btn_get_vfcode) {
//            getVfcode();

            getVfcodeByEmail();
        } else if (v == gama_verify_tv_area) {
            getAndShowArea();
        }

    }

    private void getAndShowArea() {
        sLoginDialogv2.getLoginPresenter().getAreaInfo(sLoginDialogv2.getActivity());
    }

    private void register() {

//        String areaCode = gama_verify_tv_area.getText().toString();
//        if (TextUtils.isEmpty(areaCode)) {
//            ToastUtils.toast(getActivity(), R.string.py_area_code_empty);
//            return;
//        }
//
//        String phone = gama_verify_et_phone.getEditableText().toString().trim();
//        if (!phone.matches(selectedBean.getPattern())) {
//            ToastUtils.toast(getActivity(), R.string.py_phone_error);
//            return;
//        }

        String email = emailEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_empty);
            return;
        }

        if (!Validator.isEmail(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_format_error);
            return;
        }

        String vfcode = gama_verify_et_vfcode.getEditableText().toString();
        if (TextUtils.isEmpty(vfcode)) {
            ToastUtils.toast(getActivity(), R.string.py_vfcode_empty);
            return;
        }

        sLoginDialogv2.getLoginPresenter().phoneVerify(sLoginDialogv2.getActivity(), "", email, vfcode, thirdId, loginType);
    }

    private void getVfcode() {
        String areaCode = gama_verify_tv_area.getText().toString();
        if (TextUtils.isEmpty(areaCode)) {
            ToastUtils.toast(getActivity(), R.string.py_area_code_empty);
            return;
        }
        String phone = gama_verify_et_phone.getEditableText().toString().trim();
        if (!phone.matches(selectedBean.getPattern())) {
            ToastUtils.toast(getActivity(), R.string.py_phone_error);
            return;
        }
        String interfaceName = GSRequestMethod.RequestVfcodeInterface.verify.getString();

        sLoginDialogv2.getLoginPresenter().getPhoneVfcode(sLoginDialogv2.getActivity(), areaCode, phone, interfaceName);
    }

    private void getVfcodeByEmail() {

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
    }


    @Override
    public void statusCallback(int operation) {
        if (TIME_LIMIT == operation) {
            gama_verify_btn_get_vfcode.setBackgroundResource(R.drawable.gama_ui_bg_btn_unclickable);
            gama_verify_btn_get_vfcode.setClickable(false);
        } else if (TIME_OUT == operation) {
            gama_verify_btn_get_vfcode.setBackgroundResource(R.drawable.bg_192d3f_46);
            gama_verify_btn_get_vfcode.setClickable(true);
            gama_verify_btn_get_vfcode.setText(R.string.py_register_account_get_vfcode);
        }
    }

    @Override
    public void dataCallback(Object o) {
        if (o instanceof GamaAreaInfoBean) {
            selectedBean = (GamaAreaInfoBean) o;
            String text = selectedBean.getValue();
            gama_verify_tv_area.setText(text);
        }
    }

    @Override
    public void alertTime(int remainTimeSeconds) {
        if(gama_verify_btn_get_vfcode.isClickable()) {
            gama_verify_btn_get_vfcode.setClickable(false);
        }
        gama_verify_btn_get_vfcode.setText(remainTimeSeconds + "s");
    }

    @Override
    protected void doSomething() {
        super.doSomething();
        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
        remainTimeSeconds = sLoginDialogv2.getLoginPresenter().getRemainTimeSeconds();
        if(remainTimeSeconds > 0) {
            gama_verify_btn_get_vfcode.setBackgroundResource(R.drawable.gama_ui_bg_btn_unclickable);
            gama_verify_btn_get_vfcode.setClickable(false);
            gama_verify_btn_get_vfcode.setText(remainTimeSeconds + "s");
        }
    }

    private void setDefaultAreaInfo() {
        selectedBean = new GamaAreaInfoBean();
        selectedBean.setValue(getResources().getString(R.string.py_default_area_num));
        selectedBean.setPattern(getResources().getString(R.string.py_default_area_num_pattern));
    }

    public void setLoginTpye(String loginType) {
        this.loginType = loginType;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId;
    }
}

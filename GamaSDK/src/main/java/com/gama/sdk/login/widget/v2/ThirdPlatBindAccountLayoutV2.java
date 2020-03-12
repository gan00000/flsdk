package com.gama.sdk.login.widget.v2;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.bean.GamaAreaInfoBean;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.constant.GSLoginCommonConstant;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.sdk.R;
import com.gama.sdk.SBaseRelativeLayout;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.utils.Validator;


public class ThirdPlatBindAccountLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;
    private ImageView eyeImageView;
    private Button bindConfirm, gama_bind_btn_get_vfcode;

    /**
     * 密码、账号、手机、验证码
     */
    private EditText registerPasswordEditText, registerAccountEditText, gama_bind_et_phone, gama_bind_et_vfcode;

    /**
     * 区号, 手机接收限制提示
     */
    private TextView gama_bind_tv_area, gama_bind_tv_limit_hint;

    private String account;
    private String password;

    private int bindTpye = 0;
    private int fromPage = 0;

    private EditText emailEditText;

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

        eyeImageView = contentView.findViewById(R.id.gama_bind_iv_eye);

        registerAccountEditText = contentView.findViewById(R.id.gama_bind_et_account);
        registerPasswordEditText = contentView.findViewById(R.id.gama_bind_et_password);
        registerPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        gama_bind_et_phone = contentView.findViewById(R.id.gama_bind_et_phone);
        gama_bind_et_vfcode = contentView.findViewById(R.id.gama_bind_et_vfcode);
        gama_bind_tv_area = contentView.findViewById(R.id.gama_bind_tv_area);
        gama_bind_btn_get_vfcode = contentView.findViewById(R.id.gama_bind_btn_get_vfcode);
//        registerMailEditText = (EditText) contentView.findViewById(R.id.py_bind_account_mail);

        bindConfirm = contentView.findViewById(R.id.gama_bind_btn_confirm);

        emailEditText = contentView.findViewById(R.id.gama_bind_et_email);

        gama_bind_tv_limit_hint = contentView.findViewById(R.id.gama_bind_tv_limit_hint);
        String phoneMsgLimitHint = GamaUtil.getPhoneMsgLimitHint(getContext());
        if(!TextUtils.isEmpty(phoneMsgLimitHint)) {
            gama_bind_tv_limit_hint.setText(phoneMsgLimitHint);
        }

        eyeImageView.setOnClickListener(this);
        backView.setOnClickListener(this);
        bindConfirm.setOnClickListener(this);
        gama_bind_btn_get_vfcode.setOnClickListener(this);
        gama_bind_tv_area.setOnClickListener(this);

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

        } else if (v == eyeImageView) {//密码眼睛

            if (eyeImageView.isSelected()) {
                eyeImageView.setSelected(false);
                // 显示为密码
                registerPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                eyeImageView.setSelected(true);
                // 显示为普通文本
                registerPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            // 使光标始终在最后位置
            Editable etable = registerPasswordEditText.getText();
            Selection.setSelection(etable, etable.length());

        } else if (v == gama_bind_tv_area) {
//            sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
            getAndShowArea();
        } else if(v == gama_bind_btn_get_vfcode) {
//            sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
//            getVfcode();

            getVfcodeByEmail();
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

//        String email = registerMailEditText.getEditableText().toString().trim();

        if (SStringUtil.isEqual(account, password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), errorStrAccount, Toast.LENGTH_LONG);
            return;
        }
        if (!GamaUtil.checkPassword(password)) {
            ToastUtils.toast(getActivity(), errorStrPassword, Toast.LENGTH_LONG);
            return;
        }

//        String areaCode = gama_bind_tv_area.getText().toString();
//        if(TextUtils.isEmpty(areaCode)) {
//            ToastUtils.toast(getActivity(), R.string.py_area_code_empty);
//            return;
//        }
//        String phone = gama_bind_et_phone.getEditableText().toString().trim();
//        if(!phone.matches(selectedBean.getPattern())) {
//            ToastUtils.toast(getActivity(), R.string.py_phone_error);
//            return;
//        }

//        if (SStringUtil.isNotEmpty(email) && !Validator.isEmail(email)){
//            ToastUtils.toast(getActivity(), R.string.py_email_format_error);
//            return;
//        }

        String email = emailEditText.getEditableText().toString().trim();
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
        }

//        sLoginDialogv2.getLoginPresenter().accountBind(sLoginDialogv2.getActivity(), account, password, areaCode, phone, vfcode, bindTpye);
        sLoginDialogv2.getLoginPresenter().accountBind(sLoginDialogv2.getActivity(), account, password, "", email, vfcode, bindTpye);
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
        String areaCode = gama_bind_tv_area.getText().toString();
        if (TextUtils.isEmpty(areaCode)) {
            ToastUtils.toast(getActivity(), R.string.py_area_code_empty);
            return;
        }
        String phone = gama_bind_et_phone.getEditableText().toString().trim();
        if (!phone.matches(selectedBean.getPattern())) {
            ToastUtils.toast(getActivity(), R.string.py_phone_error);
            return;
        }
        String interfaceName = GSRequestMethod.RequestVfcodeInterface.bind.getString();

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

        sLoginDialogv2.getLoginPresenter().getEmailVfcode(sLoginDialogv2.getActivity(), email, interfaceName);
    }

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
            gama_bind_tv_area.setText(text);
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

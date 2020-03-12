package com.gama.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.core.base.utils.ToastUtils;
import com.gama.base.bean.GamaAreaInfoBean;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.SBaseRelativeLayout;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.utils.Validator;


public class AccountFindPwdLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;
    private TextView findPwdConfireBtn;

    private EditText findPwdAccountEditText, gama_find_et_phone, findPwdEmailEditText;

    private String account;
//    private String email;

    //选中的区域信息
    private GamaAreaInfoBean selectedBean;

    /**
     * 区号
     */
    private TextView gama_find_tv_area;

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
        contentView = inflater.inflate(R.layout.v2_gama_findpwd, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);

        findPwdAccountEditText = contentView.findViewById(R.id.gama_find_et_account);

        findPwdConfireBtn = contentView.findViewById(R.id.gama_find_btn_confirm);
        gama_find_tv_area = contentView.findViewById(R.id.gama_find_tv_area);
        gama_find_et_phone = contentView.findViewById(R.id.gama_find_et_phone);

        findPwdEmailEditText = contentView.findViewById(R.id.gama_find_et_email);

        backView.setOnClickListener(this);
        findPwdConfireBtn.setOnClickListener(this);
//        gama_find_tv_area.setOnClickListener(this);
//
//        setDefaultAreaInfo();
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
            sLoginDialogv2.toAccountLoginView();
        } else if (v == gama_find_tv_area) {
//            sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
            getAndShowArea();
        }

    }

    private void getAndShowArea() {
        sLoginDialogv2.getLoginPresenter().getAreaInfo(sLoginDialogv2.getActivity());
    }

    private void findPwd() {

        account = findPwdAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        String email = findPwdEmailEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_empty);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), errorStrAccount, Toast.LENGTH_LONG);
            return;
        }
        if (!Validator.isEmail(email)) {
            ToastUtils.toast(getActivity(), R.string.py_email_format_error);
            return;
        }

//        String areaCode = gama_find_tv_area.getText().toString();
//        if(TextUtils.isEmpty(areaCode)) {
//            ToastUtils.toast(getActivity(), R.string.py_area_code_empty);
//            return;
//        }
//        String phone = gama_find_et_phone.getEditableText().toString().trim();
//        if(!phone.matches(selectedBean.getPattern())) {
//            ToastUtils.toast(getActivity(), R.string.py_phone_error);
//            return;
//        }

        sLoginDialogv2.getLoginPresenter().findPwd(sLoginDialogv2.getActivity(), account, "", email);
    }


    @Override
    public void statusCallback(int operation) {

    }

    @Override
    public void dataCallback(Object o) {
        if (o instanceof GamaAreaInfoBean) {
            selectedBean = (GamaAreaInfoBean) o;
            String text = selectedBean.getValue();
            gama_find_tv_area.setText(text);
        }
    }

    @Override
    public void alertTime(int remainTimeSeconds) {

    }

    @Override
    protected void doSomething() {
        super.doSomething();
        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
    }

    private void setDefaultAreaInfo() {
        selectedBean = new GamaAreaInfoBean();
        selectedBean.setValue(getResources().getString(R.string.py_default_area_num));
        selectedBean.setPattern(getResources().getString(R.string.py_default_area_num_pattern));
    }
}

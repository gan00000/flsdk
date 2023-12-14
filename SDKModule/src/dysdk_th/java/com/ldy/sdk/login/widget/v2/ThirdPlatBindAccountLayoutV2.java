package com.ldy.sdk.login.widget.v2;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ldy.sdk.login.model.response.SLoginResult;
import com.ldy.callback.SFCallBack;
import com.mybase.utils.ToastUtils;
import com.ldy.base.bean.SLoginType;
import com.ldy.sdk.SBaseRelativeLayout;
import com.ldy.base.utils.SdkUtil;
import com.ldy.sdk.R;
import com.ldy.sdk.api.Request;
import com.ldy.sdk.login.constant.BindType;
import com.ldy.sdk.login.model.AccountModel;
import com.ldy.sdk.login.widget.SDKInputEditTextView;
import com.ldy.sdk.login.widget.SDKInputType;
import com.ldy.sdk.login.widget.SLoginBaseRelativeLayout;


public class ThirdPlatBindAccountLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

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
    private View has_bind_view;


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

    SDKInputEditTextView thirdAccountSdkInputEditTextView;
    SDKInputEditTextView hasBindAccountSdkInputEditTextView;

    private View iv_bind_phone_close;

    private SFCallBack sfCallBack;

    public void setSFCallBack(SFCallBack sfCallBack) {
        this.sfCallBack = sfCallBack;
    }

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

    //new实例的时候调用
    private View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.okokok_forefewious, null);

        bind_view = contentView.findViewById(R.id.mId_satitor_calidive);
        has_bind_view = contentView.findViewById(R.id.mId_sphenitious_courtety);

        accountSdkInputEditTextView = contentView.findViewById(R.id.mId_motenne_paintingsure);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.mId_parent_glassule);

        bindConfirm = contentView.findViewById(R.id.mId_scleritor_specseemward);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);

        accountSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.okokok_lotic);
        pwdSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.okokok_lotic);

        accountSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.okokok_dodec);
        pwdSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.okokok_nausing);

        registerAccountEditText = accountSdkInputEditTextView.getInputEditText();
        registerAccountEditText.setHintTextColor(getResources().getColor(R.color.mcolor_filmaneous_lotion));
        registerAccountEditText.setTextColor(getResources().getColor(R.color.mcolor_tag_tetrice));
        registerPasswordEditText = pwdSdkInputEditTextView.getInputEditText();
        registerPasswordEditText.setHintTextColor(getResources().getColor(R.color.mcolor_filmaneous_lotion));
        registerPasswordEditText.setTextColor(getResources().getColor(R.color.mcolor_tag_tetrice));

        iv_bind_phone_close = contentView.findViewById(R.id.mId_pugnior_exper);

        thirdAccountSdkInputEditTextView = contentView.findViewById(R.id.mId_quartivity_allowarian);
        hasBindAccountSdkInputEditTextView = contentView.findViewById(R.id.mId_yeah_vomern);
        thirdAccountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        hasBindAccountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);

        thirdAccountSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.okokok_lotic);
        hasBindAccountSdkInputEditTextView.getContentView().setBackgroundResource(R.drawable.okokok_lotic);
        thirdAccountSdkInputEditTextView.getInputEditText().setHintTextColor(getResources().getColor(R.color.mcolor_filmaneous_lotion));
        thirdAccountSdkInputEditTextView.getInputEditText().setTextColor(getResources().getColor(R.color.mcolor_tag_tetrice));
        hasBindAccountSdkInputEditTextView.getInputEditText().setHintTextColor(getResources().getColor(R.color.mcolor_filmaneous_lotion));
        hasBindAccountSdkInputEditTextView.getInputEditText().setTextColor(getResources().getColor(R.color.mcolor_tag_tetrice));

        hasBindAccountSdkInputEditTextView.getIconImageView().setImageResource(R.mipmap.okokok_dodec);

        thirdAccountSdkInputEditTextView.getInputEditText().setEnabled(false);
        hasBindAccountSdkInputEditTextView.getInputEditText().setEnabled(false);

        SLoginResult sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());

        if (sLoginResponse != null && sLoginResponse.getData() != null && sLoginResponse.getData().isBind()){
            bind_view.setVisibility(GONE);
            has_bind_view.setVisibility(VISIBLE);
            AccountModel accountModel = new AccountModel();
            accountModel.setLoginType(sLoginResponse.getData().getLoginType());
            accountModel.setAccount(sLoginResponse.getData().getLoginId());
            setAccountWithIcon2(accountModel, thirdAccountSdkInputEditTextView.getIconImageView(), thirdAccountSdkInputEditTextView.getInputEditText());
            hasBindAccountSdkInputEditTextView.getInputEditText().setText(sLoginResponse.getData().getLoginId());

        }else{
            bind_view.setVisibility(VISIBLE);
            has_bind_view.setVisibility(GONE);
        }

        bindConfirm.setOnClickListener(this);

        iv_bind_phone_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sBaseDialog != null) {
                    sBaseDialog.dismiss();
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.sBaseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == bindConfirm) {

            if (has_bind_view.getVisibility() == VISIBLE){
                if (sBaseDialog != null) {
                    sBaseDialog.dismiss();
                }
                return;
            }
            accountBind();

        }

    }

    private void accountBind() {

        account = registerAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.mstr_egri_sacceur);
            return;
        }

        password = registerPasswordEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.mstr_multaton_uxoriatic);
            return;
        }


//        if (SStringUtil.isEqual(account, password)) {
//            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
//            return;
//        }

        if (!SdkUtil.checkAccount(account)) {
            toast(R.string.mstr_downsive_strategyness);
            return;
        }
        if (!SdkUtil.checkPassword(password)) {
            toast(R.string.mstr_vasety_parvitor);
            return;
        }

        Request.bindAcountInGame(getActivity(), true, SdkUtil.getPreviousLoginType(getContext()), account, password, new SFCallBack() {
            @Override
            public void success(Object result, String msg) {
                toast(R.string.mstr_geni_bulbship);

                if (sfCallBack != null) {
                    sfCallBack.success(SdkUtil.getCurrentUserLoginResponse(getContext()),"");
                }

                if (sBaseDialog != null) {
                    sBaseDialog.dismiss();
                }
            }

            @Override
            public void fail(Object result, String msg) {
                if (result != null){
                    SLoginResult response = (SLoginResult)result;
                    toast("" + response.getMessage());
                }
                if (sfCallBack != null){

//                    sfCallBack.fail(null,"");
                }
            }
        });
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
    protected void onSetDialog() {
        super.onSetDialog();
//        sLoginDialogv2.getLoginPresenter().setOperationCallback(this);
//        remainTimeSeconds = sLoginDialogv2.getLoginPresenter().getRemainTimeSeconds();
    }


    public void setAccountWithIcon2(AccountModel accountModel,  ImageView imageView, EditText editText){
        int imageResId = R.mipmap.okokok_dodec;
        String showName = "";
        if (SLoginType.LOGIN_TYPE_FB.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.okokok_lotose;
            showName = getContext().getResources().getString(R.string.mstr_uxori_thusably);

        }else  if (SLoginType.LOGIN_TYPE_GOOGLE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.okokok_occureer;
            showName = getContext().getResources().getString(R.string.mstr_turbinetic_secreous);

        }else  if (SLoginType.LOGIN_TYPE_GUEST.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.okokok_veteracity;
            showName = getContext().getResources().getString(R.string.mstr_scrupdiseasearium_taxful);
        }else if (SLoginType.LOGIN_TYPE_LINE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.okokok_piece;
            showName = getContext().getResources().getString(R.string.mstr_factorful_omenable);
        }else if (SLoginType.LOGIN_TYPE_MG.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.okokok_dodec;
            showName = getContext().getResources().getString(R.string.mstr_needward_hormate);
        }

        imageView.setImageResource(imageResId);
        editText.setText(showName);
    }

}

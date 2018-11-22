package com.gama.sdk.plat.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.bean.SLoginType;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.R;
import com.gama.sdk.SSdkBaseFragment;
import com.gama.sdk.login.LoginContract;
import com.gama.sdk.login.p.LoginPresenterImpl;
import com.gama.sdk.plat.data.bean.response.UserBindInfoModel;
import com.gama.sdk.utils.DialogUtil;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGoogleSignIn;


public class NotStarpyAccountManagerFragment extends SSdkBaseFragment implements LoginContract.ILoginView {

    private Dialog mDialog;

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    private UserBindInfoModel userBindInfoModel;

    private TextView accountTextView;
    private TextView bangPhoneTextView;
    private TextView phoneTipsTextView;
    private EditText accountEditText;
    private EditText pwdEditText;

    View goAccountBindView;
    View accountBindView;

    TextView goBindTextView;

    TextView startBindTextView;

    int bindType;

    private LoginPresenterImpl iLoginPresenter;
    private SFacebookProxy sFacebookProxy;
    private SGoogleSignIn sGoogleSignIn;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PL.d("onCreateView");

        View contenView = inflater.inflate(R.layout.plat_account_manage_not_starpy, container, false);

        TextView titleTextView = (TextView) contenView.findViewById(R.id.plat_title_tv);
        titleTextView.setText(title);
        contenView.findViewById(R.id.plat_title_close_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        accountTextView = (TextView) contenView.findViewById(R.id.plat_account);
        bangPhoneTextView = (TextView) contenView.findViewById(R.id.plat_phone_bang_tv);
        phoneTipsTextView = (TextView) contenView.findViewById(R.id.plat_phone_tips_tv);
        accountEditText = (EditText) contenView.findViewById(R.id.plat_account_edt);
        pwdEditText = (EditText) contenView.findViewById(R.id.plat_new_pwd_edt);

        goAccountBindView = contenView.findViewById(R.id.plat_go_account_bind_layout);
        accountBindView = contenView.findViewById(R.id.plat_bind_account_layout);


        goBindTextView = (TextView) contenView.findViewById(R.id.plat_go_bind_account_tv);

        startBindTextView = (TextView) contenView.findViewById(R.id.plat_confirm_btn);


        mDialog = DialogUtil.createLoadingDialog(getActivity(),"Loading...");

        String loginTypeTips = "";
        //不同的登錄方式顯示不同的提示
        if (SLoginType.LOGIN_TYPE_FB.equals(userBindInfoModel.getRegistPlatform())){

            loginTypeTips = getString(R.string.plat_fb_reg_account);
            goBindTextView.setText(getString(R.string.plat_account_bind_fb));
            bindType = SLoginType.bind_fb;
            // 2.实例SFacebookProxy
            sFacebookProxy = new SFacebookProxy(this.getActivity());

        }else if (SLoginType.LOGIN_TYPE_GOOGLE.equals(userBindInfoModel.getRegistPlatform())){

            loginTypeTips = getString(R.string.plat_google_reg_account);
            goBindTextView.setText(getString(R.string.plat_account_bind_google));
            bindType = SLoginType.bind_google;

            sGoogleSignIn = new SGoogleSignIn(getActivity(), this, mDialog);



        }else if (SLoginType.LOGIN_TYPE_UNIQUE.equals(userBindInfoModel.getRegistPlatform())){
            loginTypeTips = String.format(getString(R.string.plat_free_reg_account),userBindInfoModel.getFreeRegisterName());
            goBindTextView.setText(getString(R.string.plat_account_bind_unique));
            bindType = SLoginType.bind_unique;
        }
        accountTextView.setText(loginTypeTips);

        if (SStringUtil.isNotEmpty(userBindInfoModel.getTelephone())){
            bangPhoneTextView.setText(getString(R.string.plat_already_bind_phone));
            bangPhoneTextView.setClickable(false);
            phoneTipsTextView.setText(userBindInfoModel.getTelephone());
        }else{

            bangPhoneTextView.setText(getString(R.string.plat_starpy_account_not_bind_phone));
            bangPhoneTextView.setClickable(true);
            phoneTipsTextView.setText(getString(R.string.plat_starpy_account_not_bind_phone_tips));

            bangPhoneTextView.setTextColor(getResources().getColor(R.color.e_ff0000));
        }

        goBindTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAccountBindView.setVisibility(View.GONE);
                accountBindView.setVisibility(View.VISIBLE);
            }
        });

        startBindTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PL.i("startBindTextView");

                startBind();
            }
        });

        iLoginPresenter = new LoginPresenterImpl();
        iLoginPresenter.setBaseView(this);


        iLoginPresenter.setSFacebookProxy(sFacebookProxy);
        iLoginPresenter.setSGoogleSignIn(sGoogleSignIn);

        return contenView;

    }

    String account;
    private void startBind() {

        account = accountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        String password = pwdEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }


        if (SStringUtil.isEqual(account, password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_error);
            return;
        }
        if (!GamaUtil.checkPassword(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_error);
            return;
        }
//        startActivityForResult(new Intent(getContext(), SLoginActivity.class),90);
        this.iLoginPresenter.setFragment(this);
        this.iLoginPresenter.accountBind(getActivity(), account, password, "", bindType);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PL.d("onViewCreated -- tag:" + this.getTag());

    }

    public void setUserBindInfoModel(UserBindInfoModel userBindInfoModel) {
        this.userBindInfoModel = userBindInfoModel;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PL.i("fragment onActivityResult");
        if (sFacebookProxy != null) {
            sFacebookProxy.onActivityResult(getActivity(), requestCode, resultCode, data);
        }
        if (sGoogleSignIn != null){
            sGoogleSignIn.handleActivityResult(getActivity(),requestCode,resultCode,data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (sFacebookProxy != null) {
            sFacebookProxy.onDestroy(getActivity());
        }
    }

    @Override
    public void LoginSuccess(SLoginResponse sLoginResponse) {

    }

    @Override
    public void changePwdSuccess(SLoginResponse sLoginResponse) {

    }

    @Override
    public void findPwdSuccess(SLoginResponse sLoginResponse) {

    }

    @Override
    public void showAutoLoginTips(String tips) {

    }

    @Override
    public void showAutoLoginView() {

    }

    @Override
    public void showLoginView() {

    }

    @Override
    public void showAutoLoginWaitTime(String time) {

    }

    @Override
    public void accountBindSuccess(SLoginResponse sLoginResponse) {
        PL.i("NotStarpyAccountManagerFragment accountBindSuccess");
        if (sLoginResponse != null && sLoginResponse.isRequestSuccess()){
            userBindInfoModel.setName(account);
        }
    }

    @Override
    public void showMainLoginView() {
    }
}

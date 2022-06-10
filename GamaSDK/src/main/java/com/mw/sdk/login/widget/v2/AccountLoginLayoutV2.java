package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.core.base.utils.ToastUtils;
import com.mw.base.bean.SLoginType;
import com.mw.base.cfg.ConfigBean;
import com.mw.base.utils.GamaUtil;
import com.mw.sdk.login.AccountPopupWindow;
import com.mw.sdk.login.model.AccountModel;
import com.mw.sdk.login.widget.SDKInputEditTextView;
import com.mw.sdk.login.widget.SDKInputType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class AccountLoginLayoutV2 extends SLoginBaseRelativeLayout {

    private View contentView;

    private Button loginMainLoginBtn;

    /**
     * 眼睛、保存密码、验证码
     */
//    private ImageView savePwdCheckBox;
//    private CheckBox agreeCheckBox;

    /**
     * 密码、账号、验证码
     */
    private EditText loginPasswordEditText, loginAccountEditText;
    private String account;
    private String password;
    List<AccountModel> accountModels;
    private View loginMainGoRegisterBtn;
    private View loginMainGoFindPwd;
    private View loginMainGoAccountCenter;
    private View loginMainGoChangePassword;

//    private TextView goTermView;

    private SDKInputEditTextView accountSdkInputEditTextView;
    private SDKInputEditTextView pwdSdkInputEditTextView;

    View historyAccountListBtn;

    private View fbLoginView, macLoginView, googleLoginView, lineLoginView;

    private AccountPopupWindow accountPopupWindow;
    private AccountModel currentAccountModel;

    public AccountLoginLayoutV2(Context context) {
        super(context);

    }


    public AccountLoginLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountLoginLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.mw_account_login, null);

        backView = contentView.findViewById(R.id.layout_head_back);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginDialogv2.toLoginWithRegView();
            }
        });


        accountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_account_login_account);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_account_login_password);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);

        loginMainGoRegisterBtn = contentView.findViewById(R.id.gama_login_tv_register);
        loginMainGoFindPwd = contentView.findViewById(R.id.gama_login_tv_forget_password);
        loginMainGoAccountCenter = contentView.findViewById(R.id.gama_login_tv_link);
        loginMainGoChangePassword = contentView.findViewById(R.id.gama_login_tv_change_password);


        loginAccountEditText = accountSdkInputEditTextView.getInputEditText();
        loginPasswordEditText = pwdSdkInputEditTextView.getInputEditText();
//        pwdSdkInputEditTextView.setEyeVisable(View.VISIBLE);

        historyAccountListBtn = contentView.findViewById(R.id.sdk_input_item_account_history);
        historyAccountListBtn.setVisibility(VISIBLE);

        loginMainLoginBtn = contentView.findViewById(R.id.gama_login_btn_confirm);
//        goTermView = contentView.findViewById(R.id.gama_gama_start_term_tv1);//跳轉服務條款
//        agreeCheckBox = contentView.findViewById(R.id.gama_gama_start_term_cb1);//跳轉服務條款
//
//        GamaUtil.saveStartTermRead(getContext(), true);//默认设置为勾选
//        agreeCheckBox.setChecked(true);

       /* savePwdCheckBox = contentView.findViewById(R.id.gama_login_iv_remember_account);

        savePwdCheckBox.setSelected(true);

        savePwdCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savePwdCheckBox.isSelected()) {
                    savePwdCheckBox.setSelected(false);
                } else {
                    savePwdCheckBox.setSelected(true);
                }
            }
        });*/


        loginMainGoRegisterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginActivity.replaceFragmentBackToStack(new AccountRegisterFragment());

//                sLoginDialogv2.toRegisterView(2);
            }
        });

        loginMainGoFindPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toFindPwdView();
            }
        });

        loginMainGoAccountCenter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toAccountManagerCenter();
            }
        });

        loginMainGoChangePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginDialogv2.toChangePwdView();
            }
        });

        loginMainLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        fbLoginView = contentView.findViewById(R.id.fbLoginView);
        lineLoginView = contentView.findViewById(R.id.lineLoginView);
        macLoginView = contentView.findViewById(R.id.guestLoginView);
        googleLoginView = contentView.findViewById(R.id.ggLoginView);

        ConfigBean configBean = GamaUtil.getSdkCfg(getContext());
        if (configBean != null){
            ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(getContext());
            if (versionData != null){
                if(!versionData.isVisitorLogin()){
                    macLoginView.setVisibility(View.GONE);
                }
                if(!versionData.isFbLogin()){
                    fbLoginView.setVisibility(View.GONE);
                }
                if(!versionData.isGoogleLogin()){
                    googleLoginView.setVisibility(View.GONE);
                }
                if(!versionData.isLineLogin()){
                    lineLoginView.setVisibility(View.GONE);
                }
            }
        }

        fbLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
            }
        });
//        bindAccountView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sLoginDialogv2.toAccountManagerCenter();
//            }
//        });
        macLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.getLoginPresenter().guestLogin(sLoginDialogv2.getActivity());
            }
        });
        googleLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //google+登录
                sLoginDialogv2.getLoginPresenter().googleLogin(sLoginDialogv2.getActivity());
            }
        });
        lineLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //google+登录
                sLoginDialogv2.getLoginPresenter().lineLogin(sLoginDialogv2.getActivity());
            }
        });

        accountModels = new ArrayList<>();

        //test
//        GamaUtil.saveAccountModel(getContext(), "ccc","xxx",true);
//        GamaUtil.saveAccountModel(getContext(), SLoginType.LOGIN_TYPE_FB,"","", "12","1111222","adfaf@qq.com",true);
//        GamaUtil.saveAccountModel(getContext(), SLoginType.LOGIN_TYPE_GOOGLE,"","", "1332","55555","a3333af@qq.com",true);
//        GamaUtil.saveAccountModel(getContext(), SLoginType.LOGIN_TYPE_GUEST,"","", "1222","111e3331222","111222@qq.com",true);
//        GamaUtil.saveAccountModel(getContext(), SLoginType.LOGIN_TYPE_LINE,"","", "111","334444","",true);

        List<AccountModel> ams = GamaUtil.getAccountModels(getContext());
        accountModels.addAll(ams);
        if (accountModels != null && !accountModels.isEmpty()){//设置按照最好登录时间排序后的第一个账号
            AccountModel lastAccountModel = accountModels.get(0);
            currentAccountModel = lastAccountModel;
            setViewStatue(lastAccountModel);
        }


        accountPopupWindow = new AccountPopupWindow(getActivity());
        accountPopupWindow.setPopWindowListener(new AccountPopupWindow.PopWindowListener() {
            @Override
            public void onRemove(AccountModel accountModel) {

            }

            @Override
            public void onUse(AccountModel accountModel) {

                currentAccountModel = accountModel;
//                if (SLoginType.LOGIN_TYPE_MG.equals(accountModel.getLoginType())){
//                    GamaUtil.setAccountWithIcon(accountModel,accountSdkInputEditTextView.getIconImageView(),loginAccountEditText);
//                    loginPasswordEditText.setText(accountModel.getPassword());
//                }else{
//                    GamaUtil.setAccountWithIcon(accountModel,accountSdkInputEditTextView.getIconImageView(),loginAccountEditText);
//                    loginPasswordEditText.setText("");
//                }

                setViewStatue(currentAccountModel);

            }

            @Override
            public void onEmpty() {
                loginAccountEditText.setText("");
                loginPasswordEditText.setText("");
            }
        });
        historyAccountListBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountPopupWindow.showOnView(accountSdkInputEditTextView);

            }
        });

        return contentView;
    }

    private void setViewStatue(AccountModel accountModel) {

        if (SLoginType.LOGIN_TYPE_MG.equals(currentAccountModel.getLoginType())){
            account = accountModel.getAccount();
            password = accountModel.getPassword();
            if (!TextUtils.isEmpty(account)){ //显示记住的密码
                GamaUtil.setAccountWithIcon(accountModel,accountSdkInputEditTextView.getIconImageView(),loginAccountEditText);
                pwdSdkInputEditTextView.setPwdInputEnable(true);
                loginPasswordEditText.setText(password);
            }
        }else{
            GamaUtil.setAccountWithIcon(accountModel,accountSdkInputEditTextView.getIconImageView(),loginAccountEditText);
            pwdSdkInputEditTextView.setPwdInputEnable(false);
            loginPasswordEditText.setText(R.string.text_free_register);
        }
    }

    private void login() {

        account = loginAccountEditText.getEditableText().toString().trim();
        password = loginPasswordEditText.getEditableText().toString().trim();

        sLoginDialogv2.getLoginPresenter().starpyAccountLogin(sLoginDialogv2.getActivity(),account,password, "", true);

    }

    /*private void loadImage() {
        String vfcodeUrl = ResConfig.getLoginPreferredUrl(getContext()) + GSRequestMethod.GS_REQUEST_METHOD_VFCODE
                + "?timestamp=" + System.currentTimeMillis() + "&operatingSystem=android&uniqueId=" + GamaUtil.getCustomizedUniqueId1AndroidId1Adid(getContext());
        PL.i(vfcodeUrl);
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //构建ImageRequest 实例
        final ImageRequest request = new ImageRequest(vfcodeUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                PL.i("response: " + response.toString());
                //给imageView设置图片
                gama_login_iv_vfcode.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //设置一张错误的图片，临时用ic_launcher代替
                gama_login_iv_vfcode.setImageResource(R.drawable.gama_title_sdk_bg);
            }
        });
        requestQueue.add(request);
    }*/

    @Override
    public void refreshViewData() {
        super.refreshViewData();

//        if (GamaUtil.getStartTermRead(getContext())){
//            agreeCheckBox.setChecked(true);
//        }else {
//            agreeCheckBox.setChecked(false);
//        }

        List<AccountModel>  ams = GamaUtil.getAccountModels(getContext());
        accountModels.clear();
        accountModels.addAll(ams);
//        if (historyAccountCommonAdapter != null) {
//            historyAccountCommonAdapter.notifyDataSetChanged();
//        }
        if (accountModels != null && !accountModels.isEmpty()){
            AccountModel lastAccountModel = accountModels.get(0); //设置按照最好登录时间排序后的第一个账号
            account = lastAccountModel.getAccount();
            password = lastAccountModel.getPassword();

        }

        if (!TextUtils.isEmpty(account)){
            loginAccountEditText.setText(account);
            loginPasswordEditText.setText(password);
        }

//        if (accountModels != null && accountModels.size() > 1){
//            historyAccountListBtn.setVisibility(VISIBLE);
//        }else{
//            historyAccountListBtn.setVisibility(GONE);
//        }
//        if (historyAccountRv != null) {
//            historyAccountRv.setVisibility(GONE);
//        }

    }

    @Override
    public void refreshVfCode() {
        super.refreshVfCode();
        /*if (GamaUtil.getVfcodeSwitchStatus(getContext())) {
            loadImage();
        }*/
    }
}

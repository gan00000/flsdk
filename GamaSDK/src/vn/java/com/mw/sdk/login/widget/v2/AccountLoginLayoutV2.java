package com.mw.sdk.login.widget.v2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.base.bean.SLoginType;
import com.mw.base.cfg.ConfigBean;
import com.mw.base.utils.SdkUtil;
import com.mw.base.utils.SdkVersionUtil;
import com.mw.sdk.SBaseDialog;
import com.mw.sdk.login.AccountPopupWindow;
import com.mw.sdk.login.constant.ViewType;
import com.mw.sdk.login.model.AccountModel;
import com.mw.sdk.login.widget.SDKInputEditTextView;
import com.mw.sdk.login.widget.SDKInputType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class AccountLoginLayoutV2 extends SLoginBaseRelativeLayout {

    private View contentView;

    private Button loginMainLoginBtn;

    private CheckBox cb_agree_term;
    private View tv_login_term;

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
//    private int mWidth;
//    private int mHeight;
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //获取宽度的测量规范模式
//        int specMode = MeasureSpec.getMode(widthMeasureSpec);
//        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
//        PL.i("onMeasure 模式和宽度" + " specMode:"+specMode+"|specWidth:"+specWidth);
//
//        specMode = MeasureSpec.getMode(widthMeasureSpec);
//        int specHeight = MeasureSpec.getSize(widthMeasureSpec);
//        PL.i("onMeasure 模式和宽度" + " specMode:"+specMode+"|specHeight:"+specHeight);
//
//        int contentView_w = contentView.getWidth();
//        int contentView_h = contentView.getHeight();
//
//        PL.i("contentView_w=" + contentView_w + ",  contentView_h=" + contentView_h);
//    }

    boolean isResizeView = false;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        PL.i("view onLayout");
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

        layout_delete_account = contentView.findViewById(R.id.layout_delete_account);
        layout_delete_account_parent = contentView.findViewById(R.id.layout_delete_account_parent);
        layout_delete_account.setVisibility(View.GONE);

        layout_delete_account.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });

        tv_login_term = contentView.findViewById(R.id.tv_login_term);
        cb_agree_term = contentView.findViewById(R.id.cb_agree_term);
        cb_agree_term.setChecked(true);

        loginAccountEditText = accountSdkInputEditTextView.getInputEditText();
        loginAccountEditText.setHint(R.string.py_msg_account_hint);
        loginPasswordEditText = pwdSdkInputEditTextView.getInputEditText();
        loginPasswordEditText.setHint(R.string.py_msg_pwd_hint);
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

        //登录界面添加一个按钮，用来做资质跳转
        View vnGoReviewWebView = contentView.findViewById(R.id.vnGoReviewWebView);

        ConfigBean configBean = SdkUtil.getSdkCfg(getContext());
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
                if(!versionData.isShowContract()){ //服务条款
                    cb_agree_term.setVisibility(View.GONE);
                    tv_login_term.setVisibility(View.GONE);
                }
//                if(versionData.isDeleteAccount()){
//                    layout_delete_account.setVisibility(View.VISIBLE);
//                }else{
//                    layout_delete_account.setVisibility(View.GONE);
//                }
                if(!versionData.isShowForgetPwd()){
                    loginMainGoFindPwd.setVisibility(View.GONE);
                    loginMainGoFindPwd.setTag(100);
                }

                if (versionData.isVnIsReview()){
                    vnGoReviewWebView.setVisibility(VISIBLE);
                }else {
                    vnGoReviewWebView.setVisibility(View.GONE);
                }

            }
            vnGoReviewWebView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (configBean.getUrl() != null && SStringUtil.isNotEmpty(configBean.getUrl().getVnReviewUrl())){
                        Uri uri = Uri.parse(configBean.getUrl().getVnReviewUrl());
                        Intent other_intent = new Intent(Intent.ACTION_VIEW,uri);
                        other_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
                            getActivity().startActivity(other_intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        ToastUtils.toast(getContext(),"Url not configured");
                    }

                }
            });
        }

        fbLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkAgreeTerm()) {
                    return;
                }
                sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
            }
        });
        tv_login_term.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginDialogv2.showTermView(ViewType.LoginWithRegView);

                showTermDialog();

            }
        });

        macLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkAgreeTerm()) {
                    return;
                }
                sLoginDialogv2.getLoginPresenter().guestLogin(sLoginDialogv2.getActivity());
            }
        });
        googleLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkAgreeTerm()) {
                    return;
                }
                //google+登录
                sLoginDialogv2.getLoginPresenter().googleLogin(sLoginDialogv2.getActivity());
            }
        });
        lineLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkAgreeTerm()) {
                    return;
                }
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

        List<AccountModel> ams = SdkUtil.getAccountModels(getContext());
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
                List<AccountModel> ams = SdkUtil.getAccountModels(getContext());
                if (ams != null && !ams.isEmpty()){//设置按照最好登录时间排序后的第一个账号
                    currentAccountModel = ams.get(0);
                    setViewStatue(currentAccountModel);
                }
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
        accountPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                accountSdkInputEditTextView.getIv_account_history().setSelected(false);
            }
        });

        historyAccountListBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountPopupWindow!= null)
                {
                    //要先让popupwindow获得焦点，才能正确获取popupwindow的状态
                    accountPopupWindow.setFocusable(true);
                    if (accountPopupWindow.isShowing()){

                        accountPopupWindow.dismiss();
//                        accountSdkInputEditTextView.getIv_account_history().setSelected(true);
                    }else{
                        accountPopupWindow.showOnView(accountSdkInputEditTextView);
                        accountSdkInputEditTextView.getIv_account_history().setSelected(true);
                    }
                }
            }
        });

        loginAccountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentAccountModel != null && s != null){
                    if (SLoginType.LOGIN_TYPE_MG.equals(currentAccountModel.getLoginType())){

                    }else {
                        //平台帐号登入界面这里，帐号输入框这里如果删除或者编辑修改过原内容，帐号类型自动变成平台帐号类型，
                        // icon也变成平台帐号icon，下方的免注册登入也自动变成可编辑状态
                        PL.i("afterTextChanged...");
                        if(!s.toString().equals(currentAccountModel.getUserId())){
                            AccountModel tempAccountModel = new AccountModel();
                            currentAccountModel = tempAccountModel;
                            tempAccountModel.setLoginType(SLoginType.LOGIN_TYPE_MG);
                            tempAccountModel.setAccount(s.toString());
                            tempAccountModel.setPassword("");
//                            SdkUtil.setAccountWithIcon(tempAccountModel,accountSdkInputEditTextView.getIconImageView(),loginAccountEditText);
//                            pwdSdkInputEditTextView.setPwdInputEnable(true);
//                            pwdSdkInputEditTextView.getInputEditText().setText("");
                            setViewStatue(tempAccountModel);
                        }
                    }

                }
            }
        });
        return contentView;
    }

    private void setViewStatue(AccountModel accountModel) {

        if (SLoginType.LOGIN_TYPE_MG.equals(currentAccountModel.getLoginType())){
            account = accountModel.getAccount();
            password = accountModel.getPassword();
            Object tagObj = loginMainGoFindPwd.getTag();
            if (tagObj != null && (int)tagObj == 100) {//这里是因为远程配置不显示
                loginMainGoFindPwd.setVisibility(GONE);
            }else {
                loginMainGoFindPwd.setVisibility(VISIBLE);
            }
            SdkVersionUtil.setAccountWithIcon(accountModel,accountSdkInputEditTextView.getIconImageView(),loginAccountEditText);
            pwdSdkInputEditTextView.setVisibility(View.VISIBLE);
//                pwdSdkInputEditTextView.setPwdInputEnable(true);
            loginPasswordEditText.setText(password);

        }else{
            SdkVersionUtil.setAccountWithIcon(accountModel,accountSdkInputEditTextView.getIconImageView(),loginAccountEditText);
//            pwdSdkInputEditTextView.setPwdInputEnable(false);
//            loginPasswordEditText.setText(R.string.text_free_register);
            pwdSdkInputEditTextView.setVisibility(View.GONE);
            loginMainGoFindPwd.setVisibility(GONE);
        }
    }

    private void login() {

        if (!checkAgreeTerm()) {
            return;
        }
        if (currentAccountModel != null){
           if (SLoginType.LOGIN_TYPE_FB.equals(currentAccountModel.getLoginType())){
                fbLoginView.performClick();
                return;
           }else if (SLoginType.LOGIN_TYPE_GOOGLE.equals(currentAccountModel.getLoginType())){
               googleLoginView.performClick();
               return;
           }else if (SLoginType.LOGIN_TYPE_GUEST.equals(currentAccountModel.getLoginType())){
               macLoginView.performClick();
               return;
           }else if (SLoginType.LOGIN_TYPE_LINE.equals(currentAccountModel.getLoginType())){
                lineLoginView.performClick();
               return;
           }
        }


        account = loginAccountEditText.getEditableText().toString().trim();
        password = loginPasswordEditText.getEditableText().toString().trim();

        sLoginDialogv2.getLoginPresenter().starpyAccountLogin(sLoginDialogv2.getActivity(),account,password, "", true);

    }

    private boolean checkAgreeTerm(){
        if (cb_agree_term.isChecked()){
            return true;
        }
        toast(R.string.gama_ui_term_not_read);
        this.showTermDialog();
        return false;
    }

    private SBaseDialog termBaseDialog;
    TermsViewV3 termsViewV3;
    private void showTermDialog() {

        if (termBaseDialog != null && termsViewV3 != null){
            termBaseDialog.show();
            termsViewV3.reloadUrl();
            return;
        }

        termBaseDialog = new SBaseDialog(getContext(), R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);

        termsViewV3 = new TermsViewV3(getContext());
        termsViewV3.setiSdkCallBack(new ISdkCallBack() {
            @Override
            public void success() {
                cb_agree_term.setChecked(true);
                if (termBaseDialog != null) {
                    termBaseDialog.dismiss();
                }
            }

            @Override
            public void failure() {
                if (termBaseDialog != null) {
                    termBaseDialog.dismiss();
                }
            }
        });
        termBaseDialog.setContentView(termsViewV3);
        termBaseDialog.getWindow().setWindowAnimations(R.style.dialog_animation);
        termBaseDialog.show();
    }

    @Override
    public void onViewVisible() {
        super.onViewVisible();

       /* List<AccountModel>  ams = SdkUtil.getAccountModels(getContext());
        accountModels.clear();
        accountModels.addAll(ams);
        if (accountModels != null && !accountModels.isEmpty()){
            AccountModel lastAccountModel = accountModels.get(0); //设置按照最好登录时间排序后的第一个账号
            account = lastAccountModel.getAccount();
            password = lastAccountModel.getPassword();

        }

        if (!TextUtils.isEmpty(account)){
            loginAccountEditText.setText(account);
            loginPasswordEditText.setText(password);
        }*/
    }

    @Override
    public void onViewGone() {
        super.onViewGone();
        if (accountPopupWindow != null){
            accountPopupWindow.dismiss();
        }
    }

    @Override
    public void onViewRemove() {
        super.onViewRemove();
        if (accountPopupWindow != null){
            accountPopupWindow.dismiss();
        }
    }

    @Override
    public void refreshVfCode() {
        super.refreshVfCode();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (accountPopupWindow != null){
            accountPopupWindow.dismiss();
        }
    }


    private View layout_delete_account;
    private View layout_delete_account_parent;
    Dialog deleteDialog;
    public void showDeleteDialog() {
        if (deleteDialog == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View contentView = inflater.inflate(R.layout.mw_delete_account_alert, null);
            Button cancelBtn = contentView.findViewById(R.id.btn_delete_cancel);
            cancelBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteDialog != null) {
                        deleteDialog.dismiss();
                    }
                }
            });
            Button confireBtn = contentView.findViewById(R.id.btn_delete_confirm);

            confireBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAccount();
                }
            });

            deleteDialog = DialogUtil.createDialog(getContext(),contentView);
            deleteDialog.show();
        }else{

            deleteDialog.show();
        }
    }

    private void deleteAccount() {
        String account = accountSdkInputEditTextView.getInputEditText().getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        sLoginDialogv2.getLoginPresenter().deleteAccout(sLoginDialogv2.getActivity(), currentAccountModel.getUserId(),
                currentAccountModel.getLoginType(),
                currentAccountModel.getThirdId(),
                currentAccountModel.getLoginAccessToken(),
                currentAccountModel.getLoginTimestamp(), new SFCallBack<String>() {
                    @Override
                    public void success(String result, String msg) {
                        if (deleteDialog != null) {
                            deleteDialog.dismiss();
                        }
                        checkLocalAccount();
                    }

                    @Override
                    public void fail(String result, String msg) {
                        if (deleteDialog != null) {
                            deleteDialog.dismiss();
                        }
                    }
                });
    }

    private void checkLocalAccount(){
        List<AccountModel> accountModels = SdkUtil.getAccountModels(getActivity());
        if (accountModels == null || accountModels.isEmpty()){
//            sLoginDialogv2.toLoginWithRegView(ViewType.WelcomeView);
            AccountModel tempAccountModel = new AccountModel();
            currentAccountModel = tempAccountModel;
            tempAccountModel.setLoginType(SLoginType.LOGIN_TYPE_MG);
            tempAccountModel.setAccount("");
            tempAccountModel.setPassword("");
            setViewStatue(tempAccountModel);
            return;
        }
        currentAccountModel = accountModels.get(0);
        setViewStatue(currentAccountModel);
    }
}

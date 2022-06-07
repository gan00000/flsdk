package com.mw.sdk.login.widget.v2;

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
import com.mw.base.bean.SLoginType;
import com.mw.base.utils.GamaUtil;
import com.mw.sdk.SBaseRelativeLayout;
import com.mw.sdk.login.AccountPopupWindow;
import com.mw.sdk.login.constant.BindType;
import com.mw.sdk.login.constant.ViewType;
import com.mw.sdk.login.model.AccountModel;
import com.mw.sdk.login.widget.SDKInputEditTextView;
import com.mw.sdk.login.widget.SDKInputType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;

import java.util.List;


public class WelcomeBackLayout extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;
    private View layout_need_update_account,layout_has_update_account;
    private TextView tv_account_update_tips;
    private ImageView iv_update_account_icon;

    private EditText accountEditText;

    private SDKInputEditTextView accountSdkInputEditTextView;

//    private String account;
    private AccountModel currentAccountModel;

    View historyAccountListBtn;
    private AccountPopupWindow accountPopupWindow;

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
        iv_update_account_icon = contentView.findViewById(R.id.iv_update_account_icon);
        btn_login_game = contentView.findViewById(R.id.btn_login_game);
        layout_need_update_account = contentView.findViewById(R.id.layout_need_update_account);
        layout_has_update_account = contentView.findViewById(R.id.layout_has_update_account);
        btn_swith_account = contentView.findViewById(R.id.btn_swith_account);
        btn_update_account = contentView.findViewById(R.id.btn_update_account);
        btn_change_pwd = contentView.findViewById(R.id.btn_change_pwd);
        btn_swith_account2 = contentView.findViewById(R.id.btn_swith_account2);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        accountEditText = accountSdkInputEditTextView.getInputEditText();

        historyAccountListBtn = contentView.findViewById(R.id.sdk_input_item_account_history);
        historyAccountListBtn.setVisibility(VISIBLE);
        backView.setVisibility(GONE);


        backView.setOnClickListener(this);
        btn_login_game.setOnClickListener(this);
        btn_swith_account.setOnClickListener(this);
        btn_update_account.setOnClickListener(this);
        btn_change_pwd.setOnClickListener(this);
        btn_swith_account2.setOnClickListener(this);

        accountPopupWindow = new AccountPopupWindow(getActivity());
        accountPopupWindow.setPopWindowListener(new AccountPopupWindow.PopWindowListener() {
            @Override
            public void onRemove(AccountModel accountModel) {

            }

            @Override
            public void onUse(AccountModel accountModel) {
//                accountSdkInputEditTextView.getInputEditText().setText(accountModel.getAccount());
//                GamaUtil.setAccountWithIcon(accountModel,accountSdkInputEditTextView.getIconImageView(),accountEditText);
                currentAccountModel = accountModel;
                setViewStatue();
            }

            @Override
            public void onEmpty() {
                accountSdkInputEditTextView.getInputEditText().setText("");
            }
        });

        historyAccountListBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountPopupWindow.showOnView(accountSdkInputEditTextView);

            }
        });

        List<AccountModel> ams = GamaUtil.getAccountModels(getContext());
        if (ams != null && !ams.isEmpty()){//设置按照最好登录时间排序后的第一个账号
            currentAccountModel = ams.get(0);
            setViewStatue();
        }
        return contentView;
    }

    private void setViewStatue() {
        if (currentAccountModel != null){ //显示记住的密码，待修改
//                account = currentAccountModel.getAccount();
//            String password = lastAccountModel.getPassword();
//                accountSdkInputEditTextView.getInputEditText().setText(account);
            ImageView imageView = accountSdkInputEditTextView.getIconImageView();
            GamaUtil.setAccountWithIcon(currentAccountModel,imageView,accountEditText);

            //判斷是否綁定，顯示是否升級賬號

            if (currentAccountModel.isBind()){
                layout_need_update_account.setVisibility(View.GONE);
                layout_has_update_account.setVisibility(View.VISIBLE);
                tv_account_update_tips.setText(R.string.text_has_update_account_tips);
                iv_update_account_icon.setImageResource(R.mipmap.has_update_account_bg);
//
            }else{
                layout_need_update_account.setVisibility(View.VISIBLE);
                layout_has_update_account.setVisibility(View.GONE);
                tv_account_update_tips.setText(R.string.text_update_account_tips);
                iv_update_account_icon.setImageResource(R.mipmap.nend_update_account_bg);
            }
        }
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
            login();
        } else if (v == backView) {//返回键

        }else if (v == btn_change_pwd) {

            String account = accountEditText.getEditableText().toString().trim();
            if (TextUtils.isEmpty(account)) {
                ToastUtils.toast(getActivity(), R.string.py_account_empty);
                return;
            }
            sLoginDialogv2.toChangePwdView(account);
        }else if (v == btn_swith_account || v == btn_swith_account2) {
            sLoginDialogv2.toLoginWithRegView(ViewType.WelcomeView);

        }else if (v == btn_update_account) {

            String account = accountEditText.getEditableText().toString().trim();
            if (TextUtils.isEmpty(account)) {
                ToastUtils.toast(getActivity(), R.string.py_account_empty);
                return;
            }
            if (SLoginType.LOGIN_TYPE_FB.equals(currentAccountModel.getLoginType())){
                sLoginDialogv2.toBindView(ViewType.WelcomeView, BindType.BIND_FB,currentAccountModel);
            }else  if (SLoginType.LOGIN_TYPE_GOOGLE.equals(currentAccountModel.getLoginType())){
                sLoginDialogv2.toBindView(ViewType.WelcomeView, BindType.BIND_GOOGLE,currentAccountModel);
            }else  if (SLoginType.LOGIN_TYPE_GUEST.equals(currentAccountModel.getLoginType())){
                sLoginDialogv2.toBindView(ViewType.WelcomeView, BindType.BIND_UNIQUE,currentAccountModel);
            }else if (SLoginType.LOGIN_TYPE_LINE.equals(currentAccountModel.getLoginType())){
                sLoginDialogv2.toBindView(ViewType.WelcomeView, BindType.BIND_LINE,currentAccountModel);
            }else if (SLoginType.LOGIN_TYPE_MG.equals(currentAccountModel.getLoginType())){
            }

        }

    }

    private void login() {

        if (currentAccountModel == null){
            return;
        }
        if (SLoginType.LOGIN_TYPE_FB.equals(currentAccountModel.getLoginType())){
            sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
        }else  if (SLoginType.LOGIN_TYPE_GOOGLE.equals(currentAccountModel.getLoginType())){
            sLoginDialogv2.getLoginPresenter().googleLogin(sLoginDialogv2.getActivity());
        }else  if (SLoginType.LOGIN_TYPE_GUEST.equals(currentAccountModel.getLoginType())){
            sLoginDialogv2.getLoginPresenter().guestLogin(sLoginDialogv2.getActivity());
        }else if (SLoginType.LOGIN_TYPE_LINE.equals(currentAccountModel.getLoginType())){
            sLoginDialogv2.getLoginPresenter().lineLogin(sLoginDialogv2.getActivity());
        }else if (SLoginType.LOGIN_TYPE_MG.equals(currentAccountModel.getLoginType())){

            String account = currentAccountModel.getAccount();
            String pwd = currentAccountModel.getPassword();
            sLoginDialogv2.getLoginPresenter().starpyAccountLogin(sLoginDialogv2.getActivity(),account,pwd,"",true);
        }


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
//        accountEditText.setText("");
    }
}
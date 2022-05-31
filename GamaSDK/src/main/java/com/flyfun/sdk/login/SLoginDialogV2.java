package com.flyfun.sdk.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.flyfun.base.bean.SLoginType;
import com.flyfun.base.utils.Localization;
import com.flyfun.data.login.ILoginCallBack;
import com.flyfun.data.login.response.SLoginResponse;
import com.flyfun.sdk.SBaseDialog;
import com.flyfun.sdk.login.p.LoginPresenterImpl;
import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;
import com.flyfun.sdk.login.widget.v2.AccountChangePwdLayoutV2;
import com.flyfun.sdk.login.widget.v2.AccountFindPwdLayoutV2;
import com.flyfun.sdk.login.widget.v2.AccountManagerLayoutV2;
import com.flyfun.sdk.login.widget.v2.LoginWithRegLayout;
import com.flyfun.sdk.login.widget.v2.MainHomeLayout;
import com.flyfun.sdk.login.widget.v2.TermsViewV3;
import com.flyfun.sdk.login.widget.v2.ThirdPlatBindAccountLayoutV2;
import com.flyfun.thirdlib.facebook.SFacebookProxy;
import com.flyfun.thirdlib.google.SGoogleSignIn;
import com.flyfun.thirdlib.twitter.GamaTwitterLogin;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录界面，用Dialog实现
 * Created by gan on 2017/4/12.
 */

public class SLoginDialogV2 extends SBaseDialog implements LoginContract.ILoginView{

    private Context context;
    private Activity activity;

    private FrameLayout rootFrameLayout;
    private FrameLayout contentFrameLayout;
//    private View autoLoginPage;

    //    自動登錄頁面控件
//    private RelativeLayout autoLoginLayout;
//    private TextView autoLoginTips;
//    private TextView autoLoginWaitTime;
//    private TextView autoLoginChangeAccount;

    private SLoginBaseRelativeLayout mainHomwView;
    private SLoginBaseRelativeLayout loginWithRegView;
    private SLoginBaseRelativeLayout accountLoginView;
    private SLoginBaseRelativeLayout registerView;
    private SLoginBaseRelativeLayout changePwdView;
    private SLoginBaseRelativeLayout findPwdView;
    private SLoginBaseRelativeLayout accountManagerCenterView;
//    private SLoginBaseRelativeLayout sdkTermsV3View;

    private SLoginBaseRelativeLayout bindUniqueView;
    private SLoginBaseRelativeLayout bindFbView;
    private SLoginBaseRelativeLayout bindGoogleView;

    private List<SLoginBaseRelativeLayout> viewPageList;

    private SFacebookProxy sFacebookProxy;
    private SGoogleSignIn sGoogleSignIn;
    private GamaTwitterLogin twitterLogin;

    private LoginContract.ILoginPresenter iLoginPresenter;

    private ILoginCallBack iLoginCallBack;

//    private boolean isXM = false;

    public SLoginDialogV2(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SLoginDialogV2(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected SLoginDialogV2(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
        setCanceledOnTouchOutside(false);
        setFullScreen();
        //创建登录控制类
        iLoginPresenter = new LoginPresenterImpl();
        iLoginPresenter.setBaseView(this);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                iLoginPresenter.destory(activity);
            }
        });
    }

    public LoginContract.ILoginPresenter getLoginPresenter() {
        return iLoginPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Localization.updateSGameLanguage(context);
        contentFrameLayout = new FrameLayout(context);
        contentFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));

//        autoLoginLayout = new RelativeLayout(context);
//        autoLoginPage = getLayoutInflater().inflate(R.layout.v2_gama_auto_login_loading,null,false);
//        autoLoginLayout.addView(autoLoginPage,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));

        rootFrameLayout = new FrameLayout(context);
//        rootFrameLayout.setBackgroundColor(Color.BLUE);
        rootFrameLayout.addView(contentFrameLayout);
//        rootFrameLayout.addView(autoLoginLayout);

        setContentView(rootFrameLayout);

        viewPageList = new ArrayList<>();

        //初始化主登录界面
//        toMainLoginView();
        //初始化自动登录界面
//        initAutoLoginView();

        iLoginPresenter.setSFacebookProxy(sFacebookProxy);
        iLoginPresenter.setSGoogleSignIn(sGoogleSignIn);
        iLoginPresenter.setTwitterLogin(twitterLogin);

        iLoginPresenter.autoLogin(activity);
    }

    private void initAutoLoginView() {
       /* if (autoLoginPage == null){
            return;
        }
        autoLoginLayout = (RelativeLayout) autoLoginPage.findViewById(R.id.py_auto_login_page);
        autoLoginTips = (TextView) autoLoginPage.findViewById(R.id.py_auto_login_tips);
        autoLoginWaitTime = (TextView) autoLoginPage.findViewById(R.id.py_auto_login_wait_time);
        autoLoginChangeAccount = (TextView) autoLoginPage.findViewById(R.id.py_auto_login_change);
        autoLoginChangeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iLoginPresenter.autoLoginChangeAccount(activity);
            }
        });*/
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //返回键的回退逻辑
       /* if(autoLoginLayout != null && autoLoginLayout.getVisibility() == View.VISIBLE) {
            if(iLoginPresenter != null) { //如果自动登录就返回登录界面
                iLoginPresenter.autoLoginChangeAccount(activity);
            } else { //无法返回登录界面就回调退出登录界面状态
                super.onBackPressed();
                if(iLoginCallBack != null) { //回调退出登录界面的状态
                    iLoginCallBack.onLogin(null);
                }
            }
        } else */if(loginWithRegView != null && loginWithRegView.getVisibility() == View.VISIBLE) { //如果主界面显示就退出登录
            super.onBackPressed();
            if(iLoginCallBack != null) { //回调退出登录界面的状态
                iLoginCallBack.onLogin(null);
            }
        } else if (viewPageList != null) { //如果在其他页面，就复用当前显示页面的返回按钮
            for (View childView : viewPageList) {
                if (childView == null){
                    continue;
                }
                if (childView.getVisibility() == View.VISIBLE){
                    ((SLoginBaseRelativeLayout)childView).getBackView().performClick();
                    break;
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    public void popBackStack(SLoginBaseRelativeLayout baseRelativeLayout) {

    }

    public void toTermsV3View() {

        for (SLoginBaseRelativeLayout childView : viewPageList) {
            if (childView == null){
                continue;
            }
            childView.setVisibility(View.GONE);
        }

//        sdkTermsV3View = new TermsViewV3(context);
//        sdkTermsV3View.setLoginDialogV2(this);
//        contentFrameLayout.addView(sdkTermsV3View);
    }

    public void toLoginWithRegView() {

        if (loginWithRegView == null || !viewPageList.contains(loginWithRegView)){

            loginWithRegView = new LoginWithRegLayout(context);
            loginWithRegView.setLoginDialogV2(this);
            contentFrameLayout.addView(loginWithRegView);
            viewPageList.add(loginWithRegView);
        }
        setViewPageVisable(loginWithRegView);
    }

    public void toMainHomeView() {

        if (mainHomwView == null || !viewPageList.contains(mainHomwView)){

            mainHomwView = new MainHomeLayout(context);
            mainHomwView.setLoginDialogV2(this);
            contentFrameLayout.addView(mainHomwView);
            viewPageList.add(mainHomwView);
        }
        setViewPageVisable(mainHomwView);
    }

    private void setViewPageVisable(SLoginBaseRelativeLayout baseRelativeLayout) {

        for (SLoginBaseRelativeLayout childView : viewPageList) {
            if (childView == null) {
                continue;
            }
            if (childView == baseRelativeLayout) {
                childView.refreshViewData();
                childView.setVisibility(View.VISIBLE);
            } else {
                childView.setVisibility(View.GONE);
            }
        }
    }

    public void toAccountLoginView() {
/*
        if (accountLoginView == null || !viewPageList.contains(accountLoginView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                accountLoginView = new PyAccountLoginV2En(context);
//            } else {
                accountLoginView = new PyAccountLoginV2(context);
//            }
            accountLoginView.setLoginDialogV2(this);
            contentFrameLayout.addView(accountLoginView);
            viewPageList.add(accountLoginView);
        }

        for (SLoginBaseRelativeLayout childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == accountLoginView){
                childView.refreshViewData();
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }*/

        this.toLoginWithRegView();
    }

    public void toRegisterView(int from) {

       /* getLoginPresenter().stopVfCodeTimer();
        if (registerView == null || !viewPageList.contains(registerView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                registerView = new AccountRegisterLayoutV2En(context);
//            } else {
                registerView = new AccountRegisterLayoutV2(context);
//            }

            registerView.setLoginDialogV2(this);
            contentFrameLayout.addView(registerView);
            viewPageList.add(registerView);
        }

        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == registerView){
                if (from > 0) {
                    registerView.from = from;
                }
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }*/


        this.toLoginWithRegView();
    }

//    public void toRegisterTermsView(int from) {
//
//        if (registerTermsView == null || !viewPageList.contains(registerTermsView)){
//            registerTermsView = new AccountRegisterTermsLayoutV2(context);
//            registerTermsView.setLoginDialogV2(this);
//            contentFrameLayout.addView(registerTermsView);
//            viewPageList.add(registerTermsView);
//        }
//        registerTermsView.from = from;
//        for (View childView : viewPageList) {
//
//            if (childView == null){
//                continue;
//            }
//
//            if (childView == registerTermsView){
//                childView.setVisibility(View.VISIBLE);
//            }else{
//                childView.setVisibility(View.GONE);
//            }
//        }
//    }

    public void toChangePwdView() {

        if (changePwdView == null || !viewPageList.contains(changePwdView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                changePwdView = new AccountChangePwdLayoutV2En(context);
//            } else {
                changePwdView = new AccountChangePwdLayoutV2(context);
//            }

            changePwdView.setLoginDialogV2(this);
            contentFrameLayout.addView(changePwdView);
            viewPageList.add(changePwdView);
        }

        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == changePwdView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }

    }

    public void toFindPwdView() {

        if (findPwdView == null || !viewPageList.contains(findPwdView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                findPwdView = new AccountFindPwdLayoutV2En(context);
//            } else {
                findPwdView = new AccountFindPwdLayoutV2(context);
//            }

            findPwdView.setLoginDialogV2(this);
            contentFrameLayout.addView(findPwdView);
            viewPageList.add(findPwdView);
        }

        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == findPwdView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }

    }


    public void toBindUniqueView(int fromPage) {

        if (bindUniqueView == null || !viewPageList.contains(bindUniqueView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                bindUniqueView = new ThirdPlatBindAccountLayoutV2En(context);
//                ((ThirdPlatBindAccountLayoutV2En)bindUniqueView).setBindTpye(SLoginType.bind_unique);
//            } else {
                bindUniqueView = new ThirdPlatBindAccountLayoutV2(context);
                ((ThirdPlatBindAccountLayoutV2)bindUniqueView).setBindTpye(SLoginType.bind_unique);

//            }

            bindUniqueView.setLoginDialogV2(this);
            contentFrameLayout.addView(bindUniqueView);
            viewPageList.add(bindUniqueView);
        }

        getLoginPresenter().stopVfCodeTimer();

        ((ThirdPlatBindAccountLayoutV2)bindUniqueView).setFromPage(fromPage);
        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == bindUniqueView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }

    }

    public void toBindFbView() {

        if (bindFbView == null || !viewPageList.contains(bindFbView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                bindFbView = new ThirdPlatBindAccountLayoutV2En(context);
//                ((ThirdPlatBindAccountLayoutV2En)bindFbView).setBindTpye(SLoginType.bind_fb);
//            } else {
                bindFbView = new ThirdPlatBindAccountLayoutV2(context);
                ((ThirdPlatBindAccountLayoutV2)bindFbView).setBindTpye(SLoginType.bind_fb);
//            }

            bindFbView.setLoginDialogV2(this);
            contentFrameLayout.addView(bindFbView);
            viewPageList.add(bindFbView);
        }
        getLoginPresenter().stopVfCodeTimer();
        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == bindFbView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }

    }

    public void toBindGoogleView() {

        if (bindGoogleView == null || !viewPageList.contains(bindGoogleView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                bindGoogleView = new ThirdPlatBindAccountLayoutV2En(context);
//                ((ThirdPlatBindAccountLayoutV2En)bindGoogleView).setBindTpye(SLoginType.bind_google);
//            } else {
                bindGoogleView = new ThirdPlatBindAccountLayoutV2(context);
                ((ThirdPlatBindAccountLayoutV2)bindGoogleView).setBindTpye(SLoginType.bind_google);
//            }

            bindGoogleView.setLoginDialogV2(this);
            contentFrameLayout.addView(bindGoogleView);
            viewPageList.add(bindGoogleView);
        }
        getLoginPresenter().stopVfCodeTimer();
        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == bindGoogleView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }

    }

    /*public void toBindTwitterView() {
        if (bindTwitterView == null || !viewPageList.contains(bindTwitterView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                bindTwitterView = new ThirdPlatBindAccountLayoutV2En(context);
//                ((ThirdPlatBindAccountLayoutV2En)bindTwitterView).setBindTpye(SLoginType.bind_twitter);
//            } else {
                bindTwitterView = new ThirdPlatBindAccountLayoutV2(context);
                ((ThirdPlatBindAccountLayoutV2)bindTwitterView).setBindTpye(SLoginType.bind_twitter);
//            }


            bindTwitterView.setLoginDialogV2(this);
            contentFrameLayout.addView(bindTwitterView);
            viewPageList.add(bindTwitterView);
        }
        getLoginPresenter().stopVfCodeTimer();
        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == bindTwitterView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }
    }*/

    public void toAccountManagerCenter() {//AccountManagerLayoutV2

        if (accountManagerCenterView == null || !viewPageList.contains(accountManagerCenterView)){
//            SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//            if (SGameLanguage.en_US == sGameLanguage) {
//                accountManagerCenterView = new AccountManagerLayoutV2En(context);
//            } else {
                accountManagerCenterView = new AccountManagerLayoutV2(context);
//            }

            accountManagerCenterView.setLoginDialogV2(this);
            contentFrameLayout.addView(accountManagerCenterView);
            viewPageList.add(accountManagerCenterView);
        }

        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == accountManagerCenterView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }

    }



    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void LoginSuccess(SLoginResponse sLoginResponse) {
        if (iLoginCallBack != null){
            iLoginCallBack.onLogin(sLoginResponse);
        }
        this.dismiss();
    }

    @Override
    public void showAutoLoginTips(String tips) {
//        autoLoginTips.setText(tips);
    }

    @Override
    public void showAutoLoginView() {

    }


    @Override
    public void showLoginView() {
        if (iLoginPresenter.hasAccountLogin()){
            toAccountLoginView();
        }else{
            toLoginWithRegView();
        }
    }

    @Override
    public void showLoginWithRegView() {
        toLoginWithRegView();
    }

    @Override
    public void showMainHomeView() {
        toMainHomeView();
    }

    @Override
    public void changePwdSuccess(SLoginResponse sLoginResponse) {
        if (changePwdView != null){
            changePwdView.refreshViewData();
        }
        toAccountLoginView();
    }

    @Override
    public void showAutoLoginWaitTime(String time) {
//        autoLoginWaitTime.setText(time);
    }

    public void setLoginCallBack(ILoginCallBack iLoginCallBack) {
        this.iLoginCallBack = iLoginCallBack;
    }

    @Override
    public void findPwdSuccess(SLoginResponse sLoginResponse) {
        if (findPwdView != null){
            findPwdView.refreshViewData();
        }
        toAccountLoginView();

    }

    @Override
    public void accountBindSuccess(SLoginResponse sLoginResponse) {
        if (bindFbView != null){
            bindFbView.refreshViewData();
        }
        if (bindGoogleView != null){
            bindGoogleView.refreshViewData();
        }
        if (bindUniqueView != null){
            bindUniqueView.refreshViewData();
        }
        toAccountLoginView();
    }

    public SGoogleSignIn getGoogleSignIn() {
        return sGoogleSignIn;
    }

    public void setSGoogleSignIn(SGoogleSignIn sGoogleSignIn) {
        this.sGoogleSignIn = sGoogleSignIn;
    }


    public SFacebookProxy getFacebookProxy() {
        return sFacebookProxy;
    }

    public void setSFacebookProxy(SFacebookProxy sFacebookProxy) {
        this.sFacebookProxy = sFacebookProxy;
    }

    public void setTwitterLogin(GamaTwitterLogin twitterLogin) {
        this.twitterLogin = twitterLogin;
    }

    public GamaTwitterLogin getTwitterLogin() {
        return this.twitterLogin;
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        if(sGoogleSignIn != null) {
//            sGoogleSignIn.handleActivityDestroy(this.getContext());
//        }
    }

    @Override
    public void showPhoneVerifyView(String loginType, String thirdId) {

//        phoneVerifyView = new PhoneVerifyLayoutV2(context);
//        ((PhoneVerifyLayoutV2)phoneVerifyView).setLoginTpye(loginType);
//        ((PhoneVerifyLayoutV2)phoneVerifyView).setThirdId(thirdId);
//
//        phoneVerifyView.setLoginDialogV2(this);
//        contentFrameLayout.addView(phoneVerifyView);
//        getLoginPresenter().stopVfCodeTimer();
    }

    @Override
    public void showBindView(int fromPage) {
        toBindUniqueView(fromPage);
    }

    @Override
    public void refreshVfCode() {
        if(accountLoginView != null && accountLoginView.getVisibility() == View.VISIBLE) {
            try {
                accountLoginView.refreshVfCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

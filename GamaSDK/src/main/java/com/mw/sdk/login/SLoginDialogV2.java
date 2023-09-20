package com.mw.sdk.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.mw.sdk.utils.Localization;
import com.mw.base.utils.SdkVersionUtil;
import com.mw.sdk.R;
import com.mw.sdk.widget.SBaseDialog;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.constant.BindType;
import com.mw.sdk.constant.ViewType;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.p.LoginPresenterImpl;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.login.widget.v2.AccountChangePwdLayoutV2;
import com.mw.sdk.login.widget.v2.AccountFindPwdLayoutV2;
import com.mw.sdk.login.widget.v2.LoginWithRegLayout;
import com.mw.sdk.login.widget.v2.MainHomeLayout;
import com.mw.sdk.login.widget.v2.TermsViewV3;
import com.mw.sdk.login.widget.v2.ThirdPlatBindAccountLayoutV2;
import com.mw.sdk.login.widget.v2.WelcomeBackLayout;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleSignIn;
import com.thirdlib.huawei.HuaweiSignIn;
import com.thirdlib.line.SLineSignIn;
import com.thirdlib.twitter.TwitterLogin;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录界面，用Dialog实现
 * Created by gan on 2017/4/12.
 */

public class SLoginDialogV2 extends SBaseDialog implements LoginContract.ILoginView{

    private Context context;
    private Activity activity;

    private View rootView;
    private FrameLayout contentFrameLayout;

    private SLoginBaseRelativeLayout mainHomeView;
    private SLoginBaseRelativeLayout loginWithRegView;
    private SLoginBaseRelativeLayout accountLoginView;
    private SLoginBaseRelativeLayout registerView;
    private SLoginBaseRelativeLayout changePwdView;
    private SLoginBaseRelativeLayout findPwdView;
    private SLoginBaseRelativeLayout accountManagerCenterView;
    private SLoginBaseRelativeLayout sdkTermsV3View;

    private SLoginBaseRelativeLayout bindView;
//    private SLoginBaseRelativeLayout bindUniqueView;
//    private SLoginBaseRelativeLayout bindFbView;
//    private SLoginBaseRelativeLayout bindGoogleView;
    private SLoginBaseRelativeLayout welcomeBackView;

    private SLoginBaseRelativeLayout ageQuaView;

    private List<SLoginBaseRelativeLayout> viewPageList;

    private SFacebookProxy sFacebookProxy;
    private SGoogleSignIn sGoogleSignIn;
    private TwitterLogin twitterLogin;
    private SLineSignIn sLineSignIn;
    private HuaweiSignIn huaweiSignIn;

    private LoginContract.ILoginPresenter iLoginPresenter;

    private ILoginCallBack iLoginCallBack;

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
//        contentFrameLayout = new FrameLayout(context);
//        contentFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));

//        rootView = new FrameLayout(context);
//        rootView.addView(contentFrameLayout);
        rootView = this.activity.getLayoutInflater().inflate(R.layout.mw_dialog_layout, null);
        contentFrameLayout = rootView.findViewById(R.id.sdk_fl_content_layout);
        setContentView(rootView);

        viewPageList = new ArrayList<>();

        //初始化主登录界面
//        toMainLoginView();
        //初始化自动登录界面
//        initAutoLoginView();

        iLoginPresenter.setSFacebookProxy(sFacebookProxy);
        iLoginPresenter.setSGoogleSignIn(sGoogleSignIn);
        iLoginPresenter.setTwitterLogin(twitterLogin);
        iLoginPresenter.setLineLogin(sLineSignIn);

        iLoginPresenter.autoLogin(activity);

        SdkEventLogger.sendEventToSever(activity,EventConstant.EventName.OPEN_LOGIN_SCREEN.name());
        SdkEventLogger.trackingWithEventName(getActivity(), EventConstant.EventName.OPEN_LOGIN_SCREEN.name());
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
        super.onBackPressed();
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
        } else */
       /* if(mainHomeView != null && mainHomeView.getVisibility() == View.VISIBLE) { //如果主界面显示就退出登录
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
        }*/
    }

    public void popBackStack(SLoginBaseRelativeLayout baseRelativeLayout) {

    }

    private void toTermView(ViewType fromViewType) {

        if (sdkTermsV3View == null || !viewPageList.contains(sdkTermsV3View)){

            sdkTermsV3View = new TermsViewV3(activity);
            sdkTermsV3View.setLoginDialogV2(this);
            contentFrameLayout.addView(sdkTermsV3View);
            viewPageList.add(sdkTermsV3View);
        }
        sdkTermsV3View.setFromView(fromViewType);
        setViewPageVisable(sdkTermsV3View);
    }

    public void toLoginWithRegView(ViewType fromViewType) {

        if (loginWithRegView == null || !viewPageList.contains(loginWithRegView)){

            loginWithRegView = new LoginWithRegLayout(context);
            loginWithRegView.setLoginDialogV2(this);
            contentFrameLayout.addView(loginWithRegView);
            viewPageList.add(loginWithRegView);
        }
        if (fromViewType != null) {
            loginWithRegView.setFromView(fromViewType);
        }
        setViewPageVisable(loginWithRegView);
    }

    public void toMainHomeView() {

        if (mainHomeView == null || !viewPageList.contains(mainHomeView)){

            mainHomeView = new MainHomeLayout(context);
            mainHomeView.setLoginDialogV2(this);
            contentFrameLayout.addView(mainHomeView);
            viewPageList.add(mainHomeView);
        }
        setViewPageVisable(mainHomeView);
    }
    public void toWelcomeBackView() {

        if (welcomeBackView == null || !viewPageList.contains(welcomeBackView)){

            welcomeBackView = new WelcomeBackLayout(context);
            welcomeBackView.setLoginDialogV2(this);
            contentFrameLayout.addView(welcomeBackView);
            viewPageList.add(welcomeBackView);
        }
        setViewPageVisable(welcomeBackView);
    }

    private void setViewPageVisable(SLoginBaseRelativeLayout baseRelativeLayout) {

        for (SLoginBaseRelativeLayout childView : viewPageList) {
            if (childView == null) {
                continue;
            }
            if (childView == baseRelativeLayout) {
                childView.onViewVisible();
                childView.setVisibility(View.VISIBLE);
            } else {
                childView.onViewGone();
                childView.setVisibility(View.GONE);
            }
        }
    }

    /*public void toAccountLoginView() {
*//*
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
        }*//*

        this.toLoginWithRegView();
    }*/

   /* public void toRegisterView(int from) {

       *//* getLoginPresenter().stopVfCodeTimer();
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
        }*//*


        this.toLoginWithRegView();
    }*/

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

    public void toChangePwdView(String account) {

        if (changePwdView == null || !viewPageList.contains(changePwdView)){

            changePwdView = new AccountChangePwdLayoutV2(context);
            changePwdView.setLoginDialogV2(this);
            contentFrameLayout.addView(changePwdView);
            viewPageList.add(changePwdView);
        }
        ((AccountChangePwdLayoutV2)changePwdView).setAccount(account);
        setViewPageVisable(changePwdView);

    }

    public void toFindPwdView() {

        if (findPwdView == null || !viewPageList.contains(findPwdView)){
            findPwdView = new AccountFindPwdLayoutV2(context);
            findPwdView.setLoginDialogV2(this);
            contentFrameLayout.addView(findPwdView);
            viewPageList.add(findPwdView);
        }

        setViewPageVisable(findPwdView);

    }

    public void toBindView(ViewType fromView, BindType bindType, AccountModel accountModel) {

        if (bindView == null || !viewPageList.contains(bindView)){
            bindView = new ThirdPlatBindAccountLayoutV2(context);
            bindView.setLoginDialogV2(this);
            contentFrameLayout.addView(bindView);
            viewPageList.add(bindView);
        }
        ((ThirdPlatBindAccountLayoutV2)bindView).setBindTpye(bindType);
        ((ThirdPlatBindAccountLayoutV2)bindView).setFromView(fromView);
        ((ThirdPlatBindAccountLayoutV2)bindView).setAccountModel(accountModel);
        setViewPageVisable(bindView);
    }

   /* public void toBindUniqueView(int fromPage) {

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

    }*/

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

    }



    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void loginSuccess(SLoginResponse sLoginResponse) {
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
    public void showTermView(ViewType fromViewType) {
        toTermView(fromViewType);
    }

    @Override
    public void showAutoLoginView() {

    }


    @Override
    public void showLoginWithRegView(ViewType fromViewType) {
        toLoginWithRegView(fromViewType);
    }

    @Override
    public void showMainHomeView() {
        toMainHomeView();
    }

    @Override
    public void showWelcomeBackView() {
        toWelcomeBackView();
    }

    @Override
    public void showSdkView(ViewType viewType, ViewType fromViewType, String arg1, int arg2) {
        if (viewType == ViewType.AgeQualifiedView){

            if (ageQuaView == null || !viewPageList.contains(ageQuaView)){

                ageQuaView =  SdkVersionUtil.newAgeQualifiedView(context);
                if (ageQuaView == null){
                    return;
                }
                ageQuaView.setLoginDialogV2(this);
                contentFrameLayout.addView(ageQuaView);
                viewPageList.add(ageQuaView);
            }
            setViewPageVisable(ageQuaView);
        }
    }

    @Override
    public void changePwdSuccess(SLoginResponse sLoginResponse) {

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
//        if (findPwdView != null){
//            findPwdView.refreshViewData();
//        }
//        toAccountLoginView();

    }

    @Override
    public void accountBindSuccess(SLoginResponse sLoginResponse) {
//        if (bindFbView != null){
//            bindFbView.refreshViewData();
//        }
//        if (bindGoogleView != null){
//            bindGoogleView.refreshViewData();
//        }
//        if (bindUniqueView != null){
//            bindUniqueView.refreshViewData();
//        }
//        toAccountLoginView();
    }

    public HuaweiSignIn getHuaweiSignIn() {
        return huaweiSignIn;
    }

    public void setHuaweiSignIn(HuaweiSignIn huaweiSignIn) {
        this.huaweiSignIn = huaweiSignIn;
    }

    public SGoogleSignIn getGoogleSignIn() {
        return sGoogleSignIn;
    }

    public void setSGoogleSignIn(SGoogleSignIn sGoogleSignIn) {
        this.sGoogleSignIn = sGoogleSignIn;
    }

    public void setsLineSignIn(SLineSignIn sLineSignIn) {
        this.sLineSignIn = sLineSignIn;
    }

    public SFacebookProxy getFacebookProxy() {
        return sFacebookProxy;
    }

    public void setSFacebookProxy(SFacebookProxy sFacebookProxy) {
        this.sFacebookProxy = sFacebookProxy;
    }

    public void setTwitterLogin(TwitterLogin twitterLogin) {
        this.twitterLogin = twitterLogin;
    }

    public TwitterLogin getTwitterLogin() {
        return this.twitterLogin;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

//    @Override
//    public void showPhoneVerifyView(String loginType, String thirdId) {

//        phoneVerifyView = new PhoneVerifyLayoutV2(context);
//        ((PhoneVerifyLayoutV2)phoneVerifyView).setLoginTpye(loginType);
//        ((PhoneVerifyLayoutV2)phoneVerifyView).setThirdId(thirdId);
//
//        phoneVerifyView.setLoginDialogV2(this);
//        contentFrameLayout.addView(phoneVerifyView);
//        getLoginPresenter().stopVfCodeTimer();
//    }

//    @Override
//    public void showBindView(int fromPage) {
//        toBindUniqueView(fromPage);
//    }

//    @Override
//    public void refreshVfCode() {
//        if(accountLoginView != null && accountLoginView.getVisibility() == View.VISIBLE) {
//            try {
//                accountLoginView.refreshVfCode();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public boolean distoryView(SLoginBaseRelativeLayout view){
        if (viewPageList.contains(view)){
            view.onViewRemove();
            this.contentFrameLayout.removeView(view);
            viewPageList.remove(view);
            return true;
        }
        return false;
    }
}

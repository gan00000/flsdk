package com.gama.sdk.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gama.base.bean.SLoginType;
import com.gama.base.utils.Localization;
import com.gama.data.login.ILoginCallBack;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.R;
import com.gama.sdk.SBaseDialog;
import com.gama.sdk.login.p.LoginPresenterImpl;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.login.widget.v2.AccountChangePwdLayoutV2;
import com.gama.sdk.login.widget.v2.AccountFindPwdLayoutV2;
import com.gama.sdk.login.widget.v2.AccountManagerLayoutV2;
import com.gama.sdk.login.widget.v2.AccountRegisterLayoutV2;
import com.gama.sdk.login.widget.v2.AccountRegisterTermsLayoutV2;
import com.gama.sdk.login.widget.v2.PyAccountLoginV2;
import com.gama.sdk.login.widget.v2.ThirdPlatBindAccountLayoutV2;
import com.gama.sdk.login.widget.v2.XMMainLoginLayoutV2;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGoogleSignIn;

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
    private View autoLoginPage;

    //    自動登錄頁面控件
    private RelativeLayout autoLoginLayout;
    private TextView autoLoginTips;
    private TextView autoLoginWaitTime;
    private TextView autoLoginChangeAccount;

    private SLoginBaseRelativeLayout mainLoginView;
    private SLoginBaseRelativeLayout accountLoginView;
    private SLoginBaseRelativeLayout registerView;
    private SLoginBaseRelativeLayout registerTermsView;
    private SLoginBaseRelativeLayout changePwdView;
    private SLoginBaseRelativeLayout findPwdView;
    private SLoginBaseRelativeLayout bindUniqueView;
    private SLoginBaseRelativeLayout bindFbView;
    private SLoginBaseRelativeLayout bindGoogleView;
    private SLoginBaseRelativeLayout injectionView;
    private SLoginBaseRelativeLayout accountManagerCenterView;

    private List<SLoginBaseRelativeLayout> viewPageList;

    private SFacebookProxy sFacebookProxy;
    private SGoogleSignIn sGoogleSignIn;

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

        autoLoginLayout = new RelativeLayout(context);
        autoLoginPage = getLayoutInflater().inflate(R.layout.v2_gama_auto_login_loading,null,false);
        autoLoginLayout.addView(autoLoginPage,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));

        rootFrameLayout = new FrameLayout(context);
        rootFrameLayout.addView(contentFrameLayout);
        rootFrameLayout.addView(autoLoginLayout);

        setContentView(rootFrameLayout);

        viewPageList = new ArrayList<>();

        //初始化主登录界面
        toMainLoginView();
        //初始化自动登录界面
        initAutoLoginView();

        iLoginPresenter.setSFacebookProxy(sFacebookProxy);
        iLoginPresenter.setSGoogleSignIn(sGoogleSignIn);

        iLoginPresenter.autoLogin(activity);
    }

    private void initAutoLoginView() {
        if (autoLoginPage == null){
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
        });
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
        //返回键的回退逻辑，如果主界面显示就退出登录
        if(mainLoginView != null && mainLoginView.getVisibility() == View.VISIBLE) {
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


    public void toMainLoginView() {
        if (mainLoginView == null || !viewPageList.contains(mainLoginView)){

//            if (isXM) {
//                mainLoginView = new XMMainLoginLayoutV2(context);//星盟
//            }else {
//                mainLoginView = new MainLoginLayoutV2(context);//舊的新玩意
//            }
            mainLoginView = new XMMainLoginLayoutV2(context);//星盟
            mainLoginView.setLoginDialogV2(this);
            contentFrameLayout.addView(mainLoginView);
            viewPageList.add(mainLoginView);
        }
        for (View childView : viewPageList) {
            if (childView == null){
                continue;
            }
            if (childView == mainLoginView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }
    }
    public void toAccountLoginView() {

        if (accountLoginView == null || !viewPageList.contains(accountLoginView)){
            accountLoginView = new PyAccountLoginV2(context);
            accountLoginView.setLoginDialogV2(this);
            contentFrameLayout.addView(accountLoginView);
            viewPageList.add(accountLoginView);
        }

        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == accountLoginView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }
    }

    public void toRegisterView(int from) {

        if (registerView == null || !viewPageList.contains(registerView)){
            registerView = new AccountRegisterLayoutV2(context);
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
        }
    }
    public void toRegisterTermsView(int from) {

        if (registerTermsView == null || !viewPageList.contains(registerTermsView)){
            registerTermsView = new AccountRegisterTermsLayoutV2(context);
            registerTermsView.setLoginDialogV2(this);
            contentFrameLayout.addView(registerTermsView);
            viewPageList.add(registerTermsView);
        }
        registerTermsView.from = from;
        for (View childView : viewPageList) {

            if (childView == null){
                continue;
            }

            if (childView == registerTermsView){
                childView.setVisibility(View.VISIBLE);
            }else{
                childView.setVisibility(View.GONE);
            }
        }
    }

    public void toChangePwdView() {

        if (changePwdView == null || !viewPageList.contains(changePwdView)){
            changePwdView = new AccountChangePwdLayoutV2(context);
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
            findPwdView = new AccountFindPwdLayoutV2(context);
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


    public void toBindUniqueView() {

        if (bindUniqueView == null || !viewPageList.contains(bindUniqueView)){
            bindUniqueView = new ThirdPlatBindAccountLayoutV2(context);
            ((ThirdPlatBindAccountLayoutV2)bindUniqueView).setBindTpye(SLoginType.bind_unique);
            bindUniqueView.setLoginDialogV2(this);
            contentFrameLayout.addView(bindUniqueView);
            viewPageList.add(bindUniqueView);
        }

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
            bindFbView = new ThirdPlatBindAccountLayoutV2(context);
            ((ThirdPlatBindAccountLayoutV2)bindFbView).setBindTpye(SLoginType.bind_fb);
            bindFbView.setLoginDialogV2(this);
            contentFrameLayout.addView(bindFbView);
            viewPageList.add(bindFbView);
        }

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
            bindGoogleView = new ThirdPlatBindAccountLayoutV2(context);
            ((ThirdPlatBindAccountLayoutV2)bindGoogleView).setBindTpye(SLoginType.bind_google);
            bindGoogleView.setLoginDialogV2(this);
            contentFrameLayout.addView(bindGoogleView);
            viewPageList.add(bindGoogleView);
        }

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
    public void toAccountManagerCenter() {//AccountManagerLayoutV2

        if (accountManagerCenterView == null || !viewPageList.contains(accountManagerCenterView)){
            accountManagerCenterView = new AccountManagerLayoutV2(context);
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
        autoLoginTips.setText(tips);
    }

    @Override
    public void showAutoLoginView() {
        contentFrameLayout.setVisibility(View.GONE);
        autoLoginLayout.setVisibility(View.VISIBLE);

    }


    @Override
    public void showLoginView() {
        contentFrameLayout.setVisibility(View.VISIBLE);
        autoLoginLayout.setVisibility(View.GONE);
        if (iLoginPresenter.hasAccountLogin()){
            toAccountLoginView();
        }else{
            toMainLoginView();
        }
    }

    @Override
    public void showMainLoginView() {
        contentFrameLayout.setVisibility(View.VISIBLE);
        autoLoginLayout.setVisibility(View.GONE);
        toMainLoginView();
    }

    @Override
    public void changePwdSuccess(SLoginResponse sLoginResponse) {
        toAccountLoginView();
    }

    @Override
    public void showAutoLoginWaitTime(String time) {
        autoLoginWaitTime.setText(time);
    }

    public void setLoginCallBack(ILoginCallBack iLoginCallBack) {
        this.iLoginCallBack = iLoginCallBack;
    }

    @Override
    public void findPwdSuccess(SLoginResponse sLoginResponse) {
        toAccountLoginView();
    }

    @Override
    public void accountBindSuccess(SLoginResponse sLoginResponse) {
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

    @Override
    public void dismiss() {
        super.dismiss();
        if(sGoogleSignIn != null) {
            sGoogleSignIn.handleActivityDestroy(this.getContext());
        }
    }
}

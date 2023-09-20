package com.mw.sdk.login;

import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.login.model.response.SLoginResponse;

public class QooAppLoginView implements LoginContract.ILoginView{

    private ILoginCallBack iLoginCallBack;

    public ILoginCallBack getiLoginCallBack() {
        return iLoginCallBack;
    }

    public void setiLoginCallBack(ILoginCallBack iLoginCallBack) {
        this.iLoginCallBack = iLoginCallBack;
    }

    @Override
    public void loginSuccess(SLoginResponse sLoginResponse) {
        if (this.iLoginCallBack != null){
            this.iLoginCallBack.onLogin(sLoginResponse);
        }
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
    public void showTermView(ViewType fromViewType) {

    }

    @Override
    public void showAutoLoginWaitTime(String time) {

    }

    @Override
    public void accountBindSuccess(SLoginResponse sLoginResponse) {

    }

    @Override
    public void showLoginWithRegView(ViewType fromViewType) {

    }

    @Override
    public void showMainHomeView() {

    }

    @Override
    public void showWelcomeBackView() {

    }
}

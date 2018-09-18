package com.gama.sdk.login;

import android.app.Activity;

import com.gama.data.login.request.ThirdLoginRegRequestBean;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.BaseView;
import com.gama.IBasePresenter;
import com.gama.data.login.response.SLoginResponse;
import com.gama.thirdlib.google.SGoogleSignIn;

/**
 * Created by gan on 2017/4/13.
 */

public class LoginContract {

    /**
     * 登录View的控制器
     */
    public interface ILoginView extends BaseView {

        void LoginSuccess(SLoginResponse sLoginResponse);
        void changePwdSuccess(SLoginResponse sLoginResponse);
        void findPwdSuccess(SLoginResponse sLoginResponse);

        void showAutoLoginTips(String tips);

        void showAutoLoginView();

        void showLoginView();

        void showAutoLoginWaitTime(String time);

        void accountBindSuccess(SLoginResponse sLoginResponse);

        void showMainLoginView();
    }

    /**
     * 处理登录事务
     */
    public interface ILoginPresenter extends IBasePresenter<ILoginView> {

        void starpyAccountLogin(Activity activity, String account, String pwd);

        void fbLogin(Activity activity);
        void googleLogin(Activity activity);
        void evaLogin(Activity activity);
        void macLogin(Activity activity);

        void thirdPlatLogin(Activity activity, ThirdLoginRegRequestBean thirdLoginRegRequestBean);


        void register(Activity activity, String account, String pwd, String email);
        void changePwd(Activity activity, String account, String oldPwd, String newPwd);
        void findPwd(Activity activity, String account, String email);
        void accountBind(Activity activity, String account, String pwd, String email, int bindType);
        void accountInject(Activity activity, String account, String pwd,String uid);

        void autoLogin(Activity activity);

        void autoLoginChangeAccount(Activity activity);

        boolean hasAccountLogin();

        void destory(Activity activity);

        void setSGoogleSignIn(SGoogleSignIn sGoogleSignIn);
        void setSFacebookProxy(SFacebookProxy sFacebookProxy);

    }

}

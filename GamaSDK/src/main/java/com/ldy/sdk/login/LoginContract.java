package com.ldy.sdk.login;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.ldy.callback.SFCallBack;
import com.ldy.sdk.login.constant.BindType;
import com.ldy.sdk.login.constant.ViewType;
import com.ldy.sdk.login.model.AccountModel;
import com.ldy.sdk.login.model.request.ThirdLoginRegRequestBean;
import com.ldy.sdk.login.model.response.SLoginResult;
import com.ldy.sdk.SBaseRelativeLayout;
import com.allextends.google.SGoogleSignIn;
import com.allextends.facebook.SFacebookProxy;
import com.allextends.huawei.HuaweiSignIn;
import com.allextends.line.SLineSignIn;
import com.allextends.twitter.GamaTwitterLogin;

/**
 * Created by gan on 2017/4/13.
 */

public class LoginContract {

    /**
     * 登录View的控制器
     */
    public interface ILoginView extends BaseView {

        void loginSuccess(SLoginResult sLoginResponse);
        void changePwdSuccess(SLoginResult sLoginResponse);
        void findPwdSuccess(SLoginResult sLoginResponse);

//        void showAutoLoginTips(String tips);

        void showAutoLoginView(String tips);
        void showTermView(ViewType fromViewType);

//        void showLoginView();

        void showAutoLoginWaitTime(String time);

        void accountBindSuccess(SLoginResult sLoginResponse);

        void showLoginWithRegView(ViewType fromViewType);
        void showMainHomeView();
        void showWelcomeBackView();

//        void showPhoneVerifyView(String loginType, String thirdId);
//
//        void showBindView(int fromPage);
//
//        void refreshVfCode();
    }

    /**
     * 处理登录事务
     */
    public interface ILoginPresenter extends IBasePresenter<ILoginView> {

        void fbLogin(Activity activity);

        void googleLogin(Activity activity);

        void thirdPlatLogin(Activity activity, ThirdLoginRegRequestBean thirdLoginRegRequestBean);

        void guestLogin(Activity activity);
        void lineLogin(Activity activity);
        void hwLogin(Activity activity, HuaweiSignIn huaweiSignIn);

        void changePwd(Activity activity, String account, String oldPwd, String newPwd);

        void autoLogin(Activity activity);

        void autoLoginChangeAccount(Activity activity);

        boolean hasAccountLogin();

        void destory(Activity activity);

        void setSGoogleSignIn(SGoogleSignIn sGoogleSignIn);
        void setSFacebookProxy(SFacebookProxy sFacebookProxy);
        void setTwitterLogin(GamaTwitterLogin twitterLogin);
        void setLineLogin(SLineSignIn sLineSignIn);
        /**
         * Twitter登入
         */
        void twitterLogin(Activity activity);

        /**
         * 需要验证码的登入
         */
        void starpyAccountLogin(Activity activity, String account, String pwd, String vfcode, boolean isSaveAccount);

        /**
         * 需要手机验证的注册
         */
        void register(Activity activity, String account, String pwd, String areaCode, String phone, String vfcode, String email);

        /**
         * 需要手机验证的找回密码
         */
        void findPwd(Activity activity, String account, String areaCode, String phone, String vfCode);

        /**
         * 需要手机验证的绑定账号
         */
        void accountBind(Activity activity, AccountModel currentAccountMode, String account, String pwd, String areaCode, String phone, String vfcode, BindType bindType);

        /**
         * 获取手机验证码
         */
        void getPhoneVfcode(Activity activity, String area, String phone, String interfaceName);

        void getEmailVfcode(Activity activity, View callView, String email, String interfaceName);

        /**
         * 获取区码
         */
        void getAreaInfo(Activity activity);

        /**
         * 手机验证
         */
        void phoneVerify(Activity activity, String area, String phone, String vfCode, String thirdId, String loginType);

        /**
         * 游戏内手机验证
         */
        void inGamePhoneVerify(Activity activity, String area, String phone, String vfCode, String thirdId, String loginType);

        /**
         * 界面功能回调
         */
        void setOperationCallback(SBaseRelativeLayout.OperationCallback callback);

        int getRemainTimeSeconds();

        void stopVfCodeTimer();

        void deleteAccout(Context mContext, String userId, String loginMode, String thirdLoginId, String loginAccessToken, String loginTimestamp, SFCallBack<String> sfCallBack);
    }

}

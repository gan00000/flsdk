package com.flyfun.sdk.login;

import android.app.Activity;
import android.view.View;

import com.flyfun.BaseView;
import com.flyfun.IBasePresenter;
import com.flyfun.data.login.request.ThirdLoginRegRequestBean;
import com.flyfun.data.login.response.SLoginResponse;
import com.flyfun.sdk.SBaseRelativeLayout;
import com.flyfun.thirdlib.google.SGoogleSignIn;
import com.flyfun.thirdlib.facebook.SFacebookProxy;
import com.flyfun.thirdlib.twitter.GamaTwitterLogin;

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

        void showPhoneVerifyView(String loginType, String thirdId);

        void showBindView(int fromPage);

        void refreshVfCode();
    }

    /**
     * 处理登录事务
     */
    public interface ILoginPresenter extends IBasePresenter<ILoginView> {

        void fbLogin(Activity activity);

        void googleLogin(Activity activity);

        void thirdPlatLogin(Activity activity, ThirdLoginRegRequestBean thirdLoginRegRequestBean);

        void macLogin(Activity activity);

        void changePwd(Activity activity, String account, String oldPwd, String newPwd);

        void autoLogin(Activity activity);

        void autoLoginChangeAccount(Activity activity);

        boolean hasAccountLogin();

        void destory(Activity activity);

        void setSGoogleSignIn(SGoogleSignIn sGoogleSignIn);
        void setSFacebookProxy(SFacebookProxy sFacebookProxy);
        void setTwitterLogin(GamaTwitterLogin twitterLogin);
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
        void accountBind(Activity activity, String account, String pwd, String areaCode, String phone, String vfcode, int bindType);

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
    }

}

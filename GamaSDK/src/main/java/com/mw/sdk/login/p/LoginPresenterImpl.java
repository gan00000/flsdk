package com.mw.sdk.login.p;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.facebook.internal.ImageRequest;
import com.mw.sdk.bean.PhoneInfo;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.bean.res.ConfigBean;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;
import com.mw.base.utils.SdkVersionUtil;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.utils.DataManager;
import com.mw.sdk.constant.BindType;
import com.mw.sdk.constant.ViewType;
import com.mw.sdk.api.task.AccountLoginRequestTask;
import com.mw.sdk.api.task.AccountRegisterRequestTask;
import com.mw.sdk.api.task.ChangePwdRequestTask;
import com.mw.sdk.api.task.DeleteAccountRequestTask;
import com.mw.sdk.api.task.FindPwdRequestTask;
import com.mw.sdk.api.task.MacLoginRegRequestTask;
import com.mw.sdk.api.task.PhoneVerifyRequestTask;
import com.mw.sdk.api.task.PhoneVfcodeRequestTask;
import com.mw.sdk.api.task.ThirdLoginRegRequestTask;
import com.mw.sdk.api.task.ThirdAccountBindRequestTaskV2;
import com.mw.sdk.bean.req.ThirdLoginRegRequestBean;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.widget.SBaseRelativeLayout;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.login.LoginContract;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.utils.DialogUtil;
import com.thirdlib.IThirdHelper;
import com.thirdlib.ThirdCallBack;
import com.thirdlib.facebook.FaceBookUser;
import com.thirdlib.facebook.FbSp;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleSignIn;
import com.thirdlib.huawei.HuaweiSignIn;
import com.thirdlib.line.SLineSignIn;
import com.thirdlib.td.TDAnalyticsHelper;
import com.thirdlib.twitter.TwitterLogin;
import com.mw.sdk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录功能控制器类
 * Created by gan on 2017/4/13.
 */

public class LoginPresenterImpl implements LoginContract.ILoginPresenter {

//    private static final String TAG = LoginPresenterImpl.class.getSimpleName();
    private Activity mActivity;

    //是否自動登入的狀態
    private boolean isAutoLogin = false;

    /**
     * SLoginDialogV2
     */
    private LoginContract.ILoginView iLoginView;

    private Timer autoLoginTimer;
    //获取手机验证码的timer
    private Timer requestPhoneVfcodeTimer;
    private String requestVfcodeTimerBelong;//标记属于哪个类点击生成的requestPhoneVfcodeTimer
    //获取验证码是否在一分钟时限内
    private boolean isTimeLimit = false;

    /**
     * 默认倒数时间
     */
    private static final int TIME_OUT_SECONDS = 60;

    /**
     * 剩余倒数时间
     */
    private int resetTime;

    //区域json
    private String areaJson;
    //区域bean列表
    private PhoneInfo[] areaBeanList;
    //已选中的区域bean
    private PhoneInfo selectedBean;

    int count = 3;

    private Activity getActivity(){
        return mActivity;
    }

    private Context getContext(){
        return mActivity.getApplicationContext();
    }

    private SFacebookProxy sFacebookProxy;
    private SGoogleSignIn sGoogleSignIn;
    private TwitterLogin twitterLogin;
    private SLineSignIn sLineSignIn;
    private FaceBookUser faceBookUser;

    private HuaweiSignIn huaweiSignIn;

    private IThirdHelper naverHelper;

    public IThirdHelper getNaverHelper() {
        return naverHelper;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getRequestVfcodeTimerBelong() {
        return requestVfcodeTimerBelong;
    }

    public void setRequestVfcodeTimerBelong(String requestVfcodeTimerBelong) {
        this.requestVfcodeTimerBelong = requestVfcodeTimerBelong;
    }

    private Fragment fragment;

//    private SBaseRelativeLayout.OperationCallback callback;
    private ArrayList<SBaseRelativeLayout.OperationCallback> callbackList = new ArrayList<>();

    @Override
    public void setOperationCallback(SBaseRelativeLayout.OperationCallback callback) {
//        this.callback = callback;
        if(!callbackList.contains(callback)) {
            callbackList.add(callback);
        }
    }

    @Override
    public void setSGoogleSignIn(SGoogleSignIn sGoogleSignIn) {
        this.sGoogleSignIn = sGoogleSignIn;
    }

    @Override
    public void setSFacebookProxy(SFacebookProxy sFacebookProxy) {
        this.sFacebookProxy = sFacebookProxy;
    }

    @Override
    public void setTwitterLogin(TwitterLogin twitterLogin) {
        this.twitterLogin = twitterLogin;
    }

    @Override
    public void setLineLogin(SLineSignIn sLineSignIn) {
        this.sLineSignIn = sLineSignIn;
    }

    public void setHuaweiSignIn(HuaweiSignIn huaweiSignIn) {
        this.huaweiSignIn = huaweiSignIn;
    }

    @Override
    public void twitterLogin(final Activity activity) {
        if(twitterLogin != null) {
            twitterLogin.startLogin(activity,new TwitterLogin.TwitterLoginCallBack() {
                @Override
                public void success(String id, String mFullName, String mEmail, String token) {
                    PL.i("twitter login : " + id);
                    if (SStringUtil.isNotEmpty(id)) {
                        SdkUtil.saveTwitterId(activity,id);
                        ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                        thirdLoginRegRequestBean.setThirdPlatId(id);
                        thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_TWITTER);
                        thirdLoginRegRequestBean.setThirdAccount(mFullName);
                        thirdLoginRegRequestBean.setThirdAccessToken(token);
                        thirdPlatLogin(activity, thirdLoginRegRequestBean);
                    }
                }

                @Override
                public void failure(String msg) {

                }
            });
        }
    }

    @Override
    public void naverLogin(Activity activity) {
        if (naverHelper == null){
            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
            naverHelper = sdkVersionUtil.newNaverHelper();
            if (naverHelper == null){
                return;
            }
            naverHelper.init(activity);
        }
        naverHelper.startLogin(activity, new ThirdCallBack() {
            @Override
            public void success(String thirdId, String mFullName, String mEmail, String token) {

                PL.i("naver login : " + thirdId);
                if (SStringUtil.isNotEmpty(thirdId)) {
                    SdkUtil.saveTwitterId(activity,thirdId);
                    ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                    thirdLoginRegRequestBean.setThirdPlatId(thirdId);
                    thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_NAVER);
                    thirdLoginRegRequestBean.setThirdAccount(mFullName);
                    thirdLoginRegRequestBean.setThirdAccessToken(token);
                    thirdPlatLogin(activity, thirdLoginRegRequestBean);
                }
            }

            @Override
            public void failure(String msg) {

            }
        });
    }

    @Override
    public void autoLogin(Activity activity) {
        this.mActivity = activity;
/*
        String previousLoginType = GamaUtil.getPreviousLoginType(activity);

        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MG, previousLoginType)) {//自動登錄
            AccountModel accountModel = GamaUtil.getLastLoginAccount(activity);
            if (accountModel != null){
                startAutoLogin(activity, SLoginType.LOGIN_TYPE_MG, accountModel.getAccount(), accountModel.getPassword());
            }else {
                showLoginWithRegView();
            }

        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GUEST, previousLoginType)) {//免注册没有自動登錄
//            startAutoLogin(activity, SLoginType.LOGIN_TYPE_GAMESWORD, "", "");
            showLoginWithRegView();
        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, previousLoginType)) {//自動登錄
            String fbScopeId = FbSp.getFbId(activity);
            if (SStringUtil.isNotEmpty(fbScopeId)){
                startAutoLogin(activity, SLoginType.LOGIN_TYPE_FB, "", "");
            }else {
                showLoginWithRegView();
            }

        }  else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, previousLoginType)) {//自動登錄
            startAutoLogin(activity, SLoginType.LOGIN_TYPE_GOOGLE, "", "");
        }else if(SStringUtil.isEqual(SLoginType.LOGIN_TYPE_TWITTER, previousLoginType)) {
            startAutoLogin(activity, SLoginType.LOGIN_TYPE_TWITTER, "", "");
        }
        else {//進入登錄頁面
            showMainHomeView();
        }*/

//        if (SdkUtil.getSdkInnerVersion(activity).equals(SdkInnerVersion.KR.getSdkVeriosnName()) && !SdkUtil.getAgeQua14(activity)){
//
//            if (iLoginView != null){
//                iLoginView.showSdkView(ViewType.AgeQualifiedView, null, "",1);
//            }
//            return;
//        }

        ConfigBean configBean = SdkUtil.getSdkCfg(getContext());
        if (configBean != null) {
            ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(getContext());
            if (versionData != null && versionData.isHiden_Guest_Fb_Gg_Line()) {//此处可能还有华为登录，先不管，应该不会出现此情况
                if (iLoginView != null){
                    iLoginView.showLoginWithRegView(ViewType.WelcomeView);
                }
                return;
            }
        }
        PL.d("SdkUtil.getAccountModels start");
        List<AccountModel> accountModels = SdkUtil.getAccountModels(this.mActivity);
        PL.d("SdkUtil.getAccountModels end");
//        iLoginView.showMainHomeView();
        if (accountModels.isEmpty()){
            //是否需要首次静默登录(免注册)
            String silent_login = activity.getString(R.string.sdk_first_silent_login);
            if (SStringUtil.isNotEmpty(silent_login) && "true".equals(silent_login)){
                PL.d("go sdk_first_silent_login...");
                guestLogin(activity, new SFCallBack<String>() {
                    @Override
                    public void success(String result, String msg) {

                    }

                    @Override
                    public void fail(String result, String msg) {
                        if (iLoginView != null){
                            iLoginView.showMainHomeView();
                        }
                    }
                });
                return;
            }

            if (iLoginView != null){
                iLoginView.showMainHomeView();
            }
        }else{
            if (iLoginView != null){
                if (!SdkUtil.isVersion1(activity)){
                    iLoginView.showLoginWithRegView(ViewType.WelcomeView);
                }else{
                    iLoginView.showWelcomeBackView();
                }
            }
        }
    }

    @Override
    public void startLoginView(Activity activity) {
        this.mActivity = activity;

        ConfigBean configBean = SdkUtil.getSdkCfg(getContext());
        if (configBean != null) {
            ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(getContext());
            if (versionData != null && versionData.isHiden_Guest_Fb_Gg_Line()) {//此处可能还有华为登录，先不管，应该不会出现此情况
                if (iLoginView != null){
                    iLoginView.showLoginWithRegView(ViewType.WelcomeView);
                }
                return;
            }
        }
        List<AccountModel> accountModels = SdkUtil.getAccountModels(this.mActivity);
//        iLoginView.showMainHomeView();
        if (accountModels.isEmpty()){
            if (iLoginView != null){
                iLoginView.showMainHomeView();
            }
        }else{
            if (iLoginView != null){
                if (!SdkUtil.isVersion1(activity)){
                    iLoginView.showLoginWithRegView(ViewType.WelcomeView);
                }else{
                    iLoginView.showWelcomeBackView();
                }
            }
        }
    }

    @Override
    public void fbLogin(Activity activity) {
        this.mActivity = activity;
        if (sFacebookProxy != null) {
            sFbLogin(activity, sFacebookProxy, new FbLoginCallBack() {
                @Override
                public void loginSuccess(FaceBookUser user) {
                    if(user != null) {
                        fbThirdLogin(user);
                    } else {
//                        if (isAutoLogin) {
//                            showLoginWithRegView();
//                        }
                    }
                }
            });
        }
    }


    @Override
    public void googleLogin(final Activity activity) {
//        if (sGoogleSignIn == null){
//            if (isAutoLogin) {
//                showLoginView();
//            }
//            return;
//        }
        sGoogleSignIn.setClientId(ResConfig.getGoogleClientId(activity));
        sGoogleSignIn.startSignIn(new SGoogleSignIn.GoogleSignInCallBack() {
            @Override
            public void success(String id, String mFullName, String mEmail, String idTokenString) {
                PL.i("google sign in : " + id);
                if (SStringUtil.isNotEmpty(id)) {
                    SdkUtil.saveGoogleId(activity,id);
                    ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                    thirdLoginRegRequestBean.setThirdPlatId(id);
                    thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GOOGLE);
                    thirdLoginRegRequestBean.setGoogleClientId(ResConfig.getGoogleClientId(activity));
                    thirdLoginRegRequestBean.setGoogleIdToken(idTokenString);
                    thirdLoginRegRequestBean.setThirdAccessToken(idTokenString);

                    thirdLoginRegRequestBean.setThirdAccount(mEmail);
                    thirdPlatLogin(activity, thirdLoginRegRequestBean);
                } else {
//                    if (isAutoLogin) {
//                        showLoginView();
//                    }
                }
            }

            @Override
            public void failure() {
                ToastUtils.toast(activity,"Google sign in error");
                PL.i("google sign in failure");
//                if (isAutoLogin) {
//                    showLoginView();
//                }
            }

            @Override
            public void accountIsExpired(String msg) {
                ToastUtils.toast(activity,"Google account is expired, try again");
            }
        });
    }

    @Override
    public void lineLogin(Activity activity) {
        this.sLineSignIn.startSignIn(getActivity().getString(R.string.line_channelId), new SLineSignIn.LineSignInCallBack() {
            @Override
            public void success(String id, String mFullName, String mEmail, String idTokenString) {

                ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                thirdLoginRegRequestBean.setThirdPlatId(id);
                thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_LINE);
                thirdLoginRegRequestBean.setThirdAccount("");
                thirdLoginRegRequestBean.setLineAccessToken(idTokenString);
                thirdLoginRegRequestBean.setThirdAccessToken(idTokenString);
                thirdPlatLogin(activity, thirdLoginRegRequestBean);
            }

            @Override
            public void failure() {

            }
        });
    }

    @Override
    public void hwLogin(Activity activity, HuaweiSignIn huaweiSignIn) {

        huaweiSignIn.startSignIn(activity, new HuaweiSignIn.HWSignInCallBack() {
            @Override
            public void success(String id, String mFullName, String mEmail, String idTokenString) {

                ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                thirdLoginRegRequestBean.setThirdPlatId(id);
                thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_HUAWEI);
                thirdLoginRegRequestBean.setThirdAccount("");
                thirdPlatLogin(activity, thirdLoginRegRequestBean);

            }

            @Override
            public void failure() {

            }
        });
    }

    //目前Google line twitter登录使用到，fb登录没有使用
    @Override
    public void thirdPlatLogin(final Activity activity, final ThirdLoginRegRequestBean thirdLoginRegRequestBean) {
        this.mActivity = activity;

        ThirdLoginRegRequestTask cmd = new ThirdLoginRegRequestTask(getActivity(), thirdLoginRegRequestBean);
        cmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        cmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {

                    if (sLoginResponse.isRequestSuccess()){

                        SdkUtil.saveAccountModel(getContext(), thirdLoginRegRequestBean.getRegistPlatform(),"","",
                                sLoginResponse.getData().getUserId(),
                                sLoginResponse.getData().getToken(),
                                sLoginResponse.getData().getTimestamp(),
                                thirdLoginRegRequestBean.getThirdPlatId(),
                                thirdLoginRegRequestBean.getThirdAccount(),true,sLoginResponse.getData().isBind());
//                        ToastUtils.toast(getActivity(), R.string.py_login_success);
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, thirdLoginRegRequestBean.getRegistPlatform());
                        return;
                    } else {
                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                        //如果登录出错，有可能是token后端验证错误无法通过，故signOut
                        if (SStringUtil.isEqual(sLoginResponse.getCode(), "1010") && SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, thirdLoginRegRequestBean.getRegistPlatform())) {
                            if (sGoogleSignIn != null) {
                                sGoogleSignIn.signOut();
                            }
                        }
                    }
                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {
            }

            @Override
            public void noData() {
            }

            @Override
            public void cancel() {
            }
        });
        cmd.excute(SLoginResponse.class);

    }

    @Override
    public void guestLogin(final Activity activity, SFCallBack<String> sfCallBack) {
        this.mActivity = activity;
        mMacLogin(activity, sfCallBack);
    }

    @Override
    public void guestLogin(Activity activity) {
        this.mActivity = activity;
        mMacLogin(activity, null);
    }

    @Override
    public void register(Activity activity, String account, String pwd, String areaCode, String phone, String vfcode, String email) {
        this.mActivity = activity;
        registerAccout(activity, account, pwd, areaCode, phone, vfcode, email);
    }

    @Override
    public void getPhoneVfcode(Activity activity, String area, String phone, String interfaceName) {
        this.mActivity = activity;
        if(isTimeLimit) {
            if(callbackList != null && callbackList.size() > 0) {
                for(SBaseRelativeLayout.OperationCallback callback : callbackList) {
                    callback.statusCallback(SBaseRelativeLayout.OperationCallback.TIME_LIMIT);
                }
            }
            return;
        }
        getPhoneVfcodeRequest(activity, area, phone, interfaceName);
    }

    @Override
    public void getEmailVfcode(Activity activity, View callView, String email, String interfaceName) {
        this.mActivity = activity;

        this.requestVfcodeTimerBelong = callView.getClass().getSimpleName();

        if(isTimeLimit) {
            if(callbackList != null && callbackList.size() > 0) {
                for(SBaseRelativeLayout.OperationCallback callback : callbackList) {
                    callback.statusCallback(SBaseRelativeLayout.OperationCallback.TIME_LIMIT);
                }
            }
            return;
        }
        getEmailVfcodeRequest(activity, email, interfaceName);
    }

    @Override
    public void setBaseView(LoginContract.ILoginView view) {
        iLoginView = view;
    }

    @Override
    public void autoLoginChangeAccount(Activity activity) {
        this.mActivity = activity;
        if (autoLoginTimer != null){
            autoLoginTimer.cancel();
        }
//        if(sFacebookProxy != null) {
//            PL.i(TAG, "取消自動登錄，進行Facebook登出");
//            sFacebookProxy.fbLogout(activity);
//        } else {
//            PL.i(TAG, "sFacebookProxy为null，无法進行Facebook登出");
//        }
//        if(sGoogleSignIn != null) {
//            sGoogleSignIn.handleActivityDestroy(this.getContext());
//        } else {
//            PL.i(TAG, "sGoogleSignIn为null，无法進行Google登出");
//        }
//        showLoginView();
    }

    @Override
    public boolean hasAccountLogin() {

        String account = "";//GamaUtil.getAccount(mActivity);
        String password = "";//GamaUtil.getPassword(mActivity);
        AccountModel accountModel = SdkUtil.getLastLoginAccount(mActivity);
        if (accountModel == null) {
//            account = GamaUtil.getMacAccount(mActivity);
//            password = GamaUtil.getMacPassword(mActivity);
        }else {
            account = accountModel.getAccount();
            password = accountModel.getPassword();
        }

        if (SStringUtil.hasEmpty(account,password)) {
            return false;
        }
        return true;
    }

    @Override
    public void destory(Activity activity) {
        this.mActivity = activity;
        if (autoLoginTimer != null){
            autoLoginTimer.cancel();
        }
        if (requestPhoneVfcodeTimer != null){
            requestPhoneVfcodeTimer.cancel();
        }
    }

    @Override
    public void stopVfCodeTimer() {//根据需求充值计数器

        resetTime = 0;
        isTimeLimit = false;
        if(requestPhoneVfcodeTimer != null) {
            requestPhoneVfcodeTimer.cancel();
            requestPhoneVfcodeTimer = null;
        }
        if(callbackList != null && callbackList.size() > 0) {
            for (SBaseRelativeLayout.OperationCallback callback : callbackList) {
                callback.statusCallback(SBaseRelativeLayout.OperationCallback.TIME_OUT);
            }
        }
    }


    @Override
    public void changePwd(final Activity activity, final String account, String oldPwd, final String newPwd) {
        this.mActivity = activity;
        ChangePwdRequestTask changePwdRequestTask = new ChangePwdRequestTask(activity,account,oldPwd,newPwd);
        changePwdRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        changePwdRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {

            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {

//                        ToastUtils.toast(getActivity(), R.string.text_account_change_pwd_success);

                        iLoginView.changePwdSuccess(sLoginResponse);
                        SdkUtil.saveAccountModel(activity,account,newPwd,sLoginResponse.getData().getUserId(),sLoginResponse.getData().getToken(), sLoginResponse.getData().getTimestamp(),true);
                        //登录成功后直接进入游戏
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_MG);

                    }else {
                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                    }

                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }

            @Override
            public void cancel() {
            }
        });

        changePwdRequestTask.excute(SLoginResponse.class);

    }

    private void sAccountBindV2(ThirdAccountBindRequestTaskV2 bindRequestTask, final String account, final String pwd) {
        bindRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        bindRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
//                        ToastUtils.toast(getActivity(), R.string.text_account_bind_success);

//                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_GAMA);

//                        SdkUtil.updateAccountModel(getActivity(),sLoginResponse.getData().getUserId(),true);

                        String userId = sLoginResponse.getData().getUserId();
                        Map<String, Object> eventValue = new HashMap<String, Object>();
                        eventValue.put(EventConstant.ParameterName.USER_ID, userId);
//                        eventValue.put(EventConstant.ParameterName.SERVER_TIME, SdkUtil.getSdkTimestamp(getActivity()) + "");
                        String eventName = EventConstant.EventName.Upgrade_Account.name();
                        SdkEventLogger.sendEventToSever(getActivity(),eventName);
                        SdkEventLogger.trackingWithEventName(getActivity(),eventName,eventValue);

                        SdkUtil.saveAccountModel(getActivity(), account, pwd,sLoginResponse.getData().getUserId(),sLoginResponse.getData().getToken(),
                                sLoginResponse.getData().getTimestamp(),true);//记住账号密码
                        if (iLoginView != null){
                            iLoginView.accountBindSuccess(sLoginResponse);
                        }
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_MG);

                    }else{

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                    }

                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }

            @Override
            public void cancel() {
            }

        });
        bindRequestTask.excute(SLoginResponse.class);
    }

    private void mMacLogin(final Activity activity, SFCallBack<String> sfCallBack) {
        MacLoginRegRequestTask macLoginRegCmd = new MacLoginRegRequestTask(getActivity());
        macLoginRegCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        macLoginRegCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
//                        ToastUtils.toast(getActivity(), R.string.py_login_success);
                        SdkUtil.saveAccountModel(getContext(), SLoginType.LOGIN_TYPE_GUEST,"","",
                                sLoginResponse.getData().getUserId(),
                                sLoginResponse.getData().getToken(),
                                sLoginResponse.getData().getTimestamp(),
                                macLoginRegCmd.getSdkBaseRequestBean().getUniqueId(),
                                "",true,sLoginResponse.getData().isBind());
                        if (sfCallBack != null){
                            sfCallBack.success("","");
                        }
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_GUEST);
                    }
//                    else if (checkIsMacLoginLimit(activity, sLoginResponse, rawResult)) {
////                        macLoginLimit(activity);
//                    }
                    else {
                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                        if (sfCallBack != null){
                            sfCallBack.fail("",sLoginResponse.getMessage());
                        }
                    }

                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                    if (sfCallBack != null){
                        sfCallBack.fail("","");
                    }
                }
            }

            @Override
            public void timeout(String code) {}

            @Override
            public void noData() {}

            @Override
            public void cancel() {}
        });
        macLoginRegCmd.excute(SLoginResponse.class);
    }

//    private void cteateUserImage(Context context, String freeRegisterName, String freeRegisterPwd) {
//        try {
//            String appName = ApkInfoUtil.getApplicationName(context);
//            String text = String.format(context.getResources().getString(R.string.py_login_mac_tips), appName, freeRegisterName, freeRegisterPwd);
//            PL.i("cteateUserImage:" + text);
//            Bitmap bitmap = BitmapUtil.bitmapAddText(BitmapFactory.decodeResource(context.getResources(),R.drawable.v2_mac_pwd_bg),text);
//            String m = BitmapUtil.saveImageToGallery(getContext(),bitmap);
//            ToastUtils.toast(context, context.getResources().getString(R.string.py_login_mac_saveimage_tips));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Facebook登录实现
     */
    private void sFbLogin(final Activity activity, final SFacebookProxy sFacebookProxy, final FbLoginCallBack fbLoginCallBack) {
        if (sFacebookProxy == null) {
            PL.i("Facebook Proxy為null");
            return;
        }
        SFacebookProxy.FbLoginCallBack fbLoginCallBack1 = new SFacebookProxy.FbLoginCallBack() {
            @Override
            public void onCancel() {
                PL.d("sFbLogin cancel");
//                if(fbLoginCallBack != null) {
//                    fbLoginCallBack.loginSuccess(null);
//                }
            }

            @Override
            public void onError(String message) {
                PL.d("sFbLogin error: " + message);
                if (SStringUtil.isNotEmpty(message)) {
                    ToastUtils.toast(activity,message);
                }
            }

            @Override
            public void onSuccess(final FaceBookUser user) {
                PL.d("fb uid:" + user.getUserFbId());

                final String businessId = user.getUserFbId() + "_" + user.getFacebookAppId();
                PL.d("fb businessId:" + businessId);
                FbSp.saveAppsBusinessId(activity, businessId);
                user.setBusinessId(businessId);
                user.setPictureUri(ImageRequest.getProfilePictureUri(user.getUserFbId(), 300, 300));
                faceBookUser = user;
                if (fbLoginCallBack != null) {
                    fbLoginCallBack.loginSuccess(user);
                }
            }
        };

        if (fragment == null) {
            sFacebookProxy.fbLogin(activity, fbLoginCallBack1);
        }else {

            sFacebookProxy.fbLogin(fragment.getActivity(), fbLoginCallBack1);

        }
    }

    /**
     * 使用FacebookId进行平台登录
     */
    private void fbThirdLogin(FaceBookUser faceBookUser) {

        if (faceBookUser == null){
            return;
        }
//        String accessTokenString = faceBookUser.getAccessTokenString();
        String fbScopeId = faceBookUser.getUserFbId();
        ThirdLoginRegRequestTask cmd = new ThirdLoginRegRequestTask(getActivity(),
                fbScopeId,
                faceBookUser.getBusinessId(),
                faceBookUser.getTokenForBusiness(),
                faceBookUser.getAccessTokenString());
        cmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        cmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {

                    if (sLoginResponse.isRequestSuccess()){
//                        ToastUtils.toast(getActivity(), R.string.py_login_success);
                        SdkUtil.saveAccountModel(getContext(), SLoginType.LOGIN_TYPE_FB,"","",
                                sLoginResponse.getData().getUserId(),
                                sLoginResponse.getData().getToken(),
                                sLoginResponse.getData().getTimestamp(),
                                fbScopeId,
                                "",true,sLoginResponse.getData().isBind());

                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_FB);
                        return;
                    } else {
                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                        //如果登录出错，有可能是token后端验证错误无法通过，故signOut
                        if (SStringUtil.isEqual(sLoginResponse.getCode(), "1010")) {
                            if (sFacebookProxy != null) {
                                sFacebookProxy.fbLogout(getActivity());
                            }
                        }
                    }
                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
//                showLoginWithRegView();
            }

            @Override
            public void timeout(String code) {
//                showLoginWithRegView();
            }

            @Override
            public void noData() {
//                showLoginWithRegView();
            }

            @Override
            public void cancel() {
//                showLoginWithRegView();
            }
        });
        cmd.excute(SLoginResponse.class);
    }

    private void registerAccout(final Activity activity, final String account, final String password, String areaCode, String phone, String vfcode, String email){
        AccountRegisterRequestTask accountRegisterCmd = new AccountRegisterRequestTask(getActivity(), account, password, areaCode, phone, vfcode, email);
        accountRegisterCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        accountRegisterCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
//                        ToastUtils.toast(getActivity(), R.string.py_register_success);

                        SdkUtil.saveAccountModel(activity,account,password,sLoginResponse.getData().getUserId(),sLoginResponse.getData().getToken(),
                                sLoginResponse.getData().getTimestamp(),true);
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_MG);

                    }else{

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                    }

                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {}

            @Override
            public void noData() {}

            @Override
            public void cancel() {}

        });
        accountRegisterCmd.excute(SLoginResponse.class);
    }

    /**
     * 获取手机验证码
     */
    private void getPhoneVfcodeRequest(Activity activity, String area, String phone, String interfaceName) {
        PhoneVfcodeRequestTask task = new PhoneVfcodeRequestTask(getActivity(), area, phone, interfaceName);
        task.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        task.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    String msg = sLoginResponse.getMessage();
                    if (!TextUtils.isEmpty(msg)) {
                        ToastUtils.toast(getActivity(), msg);
//                        if(callback != null) {
//                            callback.statusCallback(SBaseRelativeLayout.OperationCallback.TIME_LIMIT);
//                        }
                        if (!sLoginResponse.isRequestSuccess()){
                            return;
                        }
                        if(callbackList != null && callbackList.size() > 0) {
                            for(SBaseRelativeLayout.OperationCallback callback : callbackList) {
                                callback.statusCallback(SBaseRelativeLayout.OperationCallback.TIME_LIMIT);
                            }
                        }
                        startTimer();
                    }
                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(getActivity(), R.string.py_error_occur);
            }

            @Override
            public void noData() {
                ToastUtils.toast(getActivity(), R.string.py_error_occur);
            }

            @Override
            public void cancel() {}

        });
        task.excute();
    }



    private void getEmailVfcodeRequest(Activity activity, String email, String interfaceName) {
        email = email.toLowerCase();
        PhoneVfcodeRequestTask task = new PhoneVfcodeRequestTask(getActivity(), email, interfaceName);
        task.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        task.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {

                    if (sLoginResponse.isRequestSuccess()) {
                        ToastUtils.toast(getActivity(), R.string.text_send_vf_code_success);
                        if(callbackList != null && callbackList.size() > 0) {
                            for(SBaseRelativeLayout.OperationCallback callback : callbackList) {
                                callback.statusCallback(SBaseRelativeLayout.OperationCallback.TIME_LIMIT);
                            }
                        }
                        startTimer();
                    }else {
                        String msg = sLoginResponse.getMessage();
                        if(SStringUtil.isNotEmpty(msg)){
                            ToastUtils.toast(getActivity(), msg);
                        }
                    }
                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(getActivity(), R.string.py_error_occur);
            }

            @Override
            public void noData() {
                ToastUtils.toast(getActivity(), R.string.py_error_occur);
            }

            @Override
            public void cancel() {}

        });
        task.excute();
    }

/*

    private void startAutoLogin(final Activity activity, final String registPlatform, final String account, final String password) {
        isAutoLogin = true;

        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MG, registPlatform)) {

            if (SStringUtil.hasEmpty(account, password)) {

//                showLoginView();
                return;
            }
            if (SStringUtil.isEqual(account, password)) {
//                showLoginView();
                return;
            }

            if (!SdkUtil.checkAccount(account)) {
//                showLoginView();
                return;
            }
//            if (!GamaUtil.checkPassword(password)) {
//                showLoginView();
//                return;
//            }

            iLoginView.showAutoLoginTips(String.format(activity.getResources().getString(R.string.py_login_autologin_tips),account));
        } else {
            String autoLoginTips = activity.getResources().getString(R.string.py_login_autologin_logining_tips);
            if (registPlatform.equals(SLoginType.LOGIN_TYPE_FB)){
                autoLoginTips = "Facebook" + autoLoginTips;
            } else if (registPlatform.equals(SLoginType.LOGIN_TYPE_GOOGLE)){
                autoLoginTips = "Google" + autoLoginTips;
            }
            else if (registPlatform.equals(SLoginType.LOGIN_TYPE_TWITTER)){
                autoLoginTips = "Twitter" + autoLoginTips;
            }
            iLoginView.showAutoLoginTips(autoLoginTips);
        }
        iLoginView.showAutoLoginView();

        count = 2;

        iLoginView.showAutoLoginWaitTime("(" + count +  ")");

        autoLoginTimer = new Timer();//delay为long,period为long：从现在起过delay毫秒以后，每隔period毫秒执行一次。

        autoLoginTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iLoginView.showAutoLoginWaitTime("(" + count +  ")");
                        if (count == 0){

                            if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MG, registPlatform)) {//免注册或者平台用户自动登录
//                                autoLogin22(mActivity, account, password);

                                starpyAccountLogin(activity,account,password, "", true);

                            }else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, registPlatform)){//fb自动的登录
                                */
/*String fbScopeId = FbSp.getFbId(activity);
                                String fbApps = FbSp.getAppsBusinessId(activity);
                                if(TextUtils.isEmpty(fbApps)) {
                                    fbApps = fbScopeId + "_" + FbResUtil.findStringByName(activity,"facebook_app_id");
                                }
                                fbThirdLogin(fbScopeId, fbApps,FbSp.getTokenForBusiness(activity));*//*

                                fbLogin(mActivity);

                            } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, registPlatform)) {//Google登录
//                                googleLogin(activity);
//                                ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
//                                thirdLoginRegRequestBean.setThirdPlatId(GamaUtil.getGoogleId(activity));
//                                thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GOOGLE);
//                                thirdLoginRegRequestBean.setGoogleClientId(ResConfig.getGoogleClientId(activity));
//                                thirdLoginRegRequestBean.setGoogleIdToken(GamaUtil.getGoogleIdToken(activity));
//                                thirdPlatLogin(activity, thirdLoginRegRequestBean);
                            } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_TWITTER, registPlatform)) {//Google登录

                                ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                                thirdLoginRegRequestBean.setThirdPlatId(SdkUtil.getTwitterId(activity));
                                thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_TWITTER);
                                thirdLoginRegRequestBean.setGoogleClientId(ResConfig.getGoogleClientId(activity));
                                thirdLoginRegRequestBean.setGoogleIdToken(SdkUtil.getGoogleIdToken(activity));
                                thirdLoginRegRequestBean.setThirdAccessToken(SdkUtil.getGoogleIdToken(activity));
                                thirdPlatLogin(activity, thirdLoginRegRequestBean);
                            }

                            if (autoLoginTimer != null){
                                autoLoginTimer.cancel();
                            }
                        }
                        count--;
                    }
                });
            }
        },300,1000);
    }
*/

    /**
     * 顯示登入頁面,自动登录状态重置为false
     */
   /* private void showLoginView() {
        isAutoLogin = false;
        if (iLoginView != null){
//            iLoginView.hildAutoLoginView();
            iLoginView.showLoginView();
        }
    }*/

//    private void showLoginWithRegView() {
//        isAutoLogin = false;
//        if (iLoginView != null){
//            iLoginView.showLoginWithRegView();
//        }
//    }

//    private void showMainHomeView() {
//        isAutoLogin = false;
//        if (iLoginView != null){
//            iLoginView.showMainHomeView();
//        }
//    }

    /*private void showPhoneVerifyView(String loginType, String thirdId) {
        if (iLoginView != null){
            iLoginView.showPhoneVerifyView(loginType, thirdId);
        }
    }

    private void showBindView(int fromPage) {
        if (iLoginView != null){
            iLoginView.showBindView(fromPage);
        }
    }

    private void refreshVfCode() {
        if (iLoginView != null){
            iLoginView.refreshVfCode();
        }
    }*/

    private void handleRegisteOrLoginSuccess(SLoginResponse loginResponse, String rawResult, String loginType) {

//        SdkUtil.saveSdkLoginData(getContext(), loginResponse.getRawResponse());
        loginResponse.getData().setLoginType(loginType);
        loginResponse.getData().setGameCode(ResConfig.getGameCode(getContext()));
        SdkUtil.updateLoginData(getContext(), loginResponse);
        DataManager.getInstance().setLogin(true);

        String deferredAppLinkDataStr = SdkUtil.getDeepLink(getContext());
        if (SStringUtil.isNotEmpty(deferredAppLinkDataStr)){
            loginResponse.getData().setMiniGameUser(true);
        }

        if (SStringUtil.isNotEmpty(loginType)) {//loginType为空时是账号注入登录，不能空时是其他普通登入

            SdkUtil.savePreviousLoginType(mActivity, loginType);
            try {
                if (loginResponse != null) {

                    TDAnalyticsHelper.setCommonProperties(getContext());
                    //5001 注册成功    1000登入成功
                    if (SStringUtil.isEqual(BaseResponseModel.SUCCESS_CODE, loginResponse.getCode())) {
                        SdkEventLogger.trackinLoginEvent(mActivity, loginResponse);
                    } else if (SStringUtil.isEqual(BaseResponseModel.SUCCESS_CODE_REG, loginResponse.getCode())) {
                        SdkEventLogger.trackinRegisterEvent(mActivity, loginResponse);
                        SdkEventLogger.trackinLoginEvent(mActivity, loginResponse);//註冊後直接登入
                        SdkUtil.saveSUserInfo(mActivity, loginResponse);

                    }
                }
                if(SLoginType.LOGIN_TYPE_FB.equals(loginType) && faceBookUser != null) {
                    loginResponse.getData().setThirdToken(faceBookUser.getAccessTokenString());
                    loginResponse.getData().setGender(FbSp.getFbGender(mActivity));
                    loginResponse.getData().setBirthday(FbSp.getFbBirthday(mActivity));
                    loginResponse.getData().setIconUri(faceBookUser.getPictureUri());
                    loginResponse.getData().setThirdId(faceBookUser.getUserFbId());
                    loginResponse.getData().setNickName(faceBookUser.getName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    /*    if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GAMA, loginType)) {

        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MAC, loginType)) {

        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, loginType)) {

        }*/

//        ToastUtils.toast(mActivity, R.string.py_login_success);

        //针对韩国sdk，没有同意14周岁，登录成功后重新回到SDK页面
//        if (SdkUtil.getSdkInnerVersion(getActivity()).equals(SdkInnerVersion.KR.getSdkVeriosnName()) && !SdkUtil.getAgeQua14(getActivity())){
//            autoLogin(getActivity());
//            return;
//        }

        if (iLoginView != null){
            iLoginView.loginSuccess(loginResponse);
        }
    }


    interface FbLoginCallBack{

//        void loginSuccess(String fbScopeId, String businessId, String tokenForBusiness);
        void loginSuccess(FaceBookUser user);
    }

    @Override
    public void findPwd(final Activity activity, final String account, String newPwd, String phoneOrEmail, String vfCode) {
        this.mActivity = activity;
        FindPwdRequestTask findPwdRequestTask = new FindPwdRequestTask(getActivity(), account, newPwd, phoneOrEmail, vfCode);
        findPwdRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        findPwdRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
//                        ToastUtils.toast(getActivity(), R.string.text_account_change_pwd_success);

                        if (iLoginView != null){
                            iLoginView.findPwdSuccess(sLoginResponse);
                        }
                        SdkUtil.saveAccountModel(getActivity(), account, newPwd,sLoginResponse.getData().getUserId(),sLoginResponse.getData().getToken(),
                                sLoginResponse.getData().getTimestamp(),true);//记住账号密码
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_MG);

                    }else{

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                    }

                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }

            @Override
            public void cancel() {

            }
        });
        findPwdRequestTask.excute(SLoginResponse.class);
    }

    @Override
    public void accountBind(final Activity activity,final AccountModel currentAccountMode, final String account, final String pwd, final String areaCode, final String phone, final String vfcode, BindType bindType) {
        this.mActivity = activity;
        if (bindType == BindType.BIND_UNIQUE){
            String uniqueId = SdkUtil.getSdkUniqueId(activity);
            if(TextUtils.isEmpty(uniqueId)){
                PL.d("thirdPlatId:" + uniqueId);
                return;
            }
            ThirdAccountBindRequestTaskV2 bindRequestTask = new ThirdAccountBindRequestTaskV2(getActivity(),
                    SLoginType.LOGIN_TYPE_GUEST,
                    account,
                    pwd,
                    areaCode,
                    phone,
                    vfcode,
                    uniqueId,
                    currentAccountMode.getUserId(),
                    currentAccountMode.getLoginAccessToken(),
                    currentAccountMode.getLoginTimestamp());
            sAccountBindV2(bindRequestTask, account, pwd);

        } else if (bindType == BindType.BIND_FB){
            sFbLogin(activity, sFacebookProxy, new FbLoginCallBack() {
                @Override
                public void loginSuccess(FaceBookUser user) {
                    if(user == null) {
                        return;
                    }
                    ThirdAccountBindRequestTaskV2 bindRequestTask = new ThirdAccountBindRequestTaskV2(getActivity(),
                            account,
                            pwd,
                            areaCode,
                            phone,
                            vfcode,
                            user.getUserFbId(),
                            user.getBusinessId(),
                            user.getAccessTokenString(),
                            user.getTokenForBusiness(),
                            currentAccountMode.getUserId(),
                            currentAccountMode.getLoginAccessToken(),
                            currentAccountMode.getLoginTimestamp());
                    sAccountBindV2(bindRequestTask,account,pwd);
                }
            });

        } else  if (bindType == BindType.BIND_GOOGLE){//Google绑定
            if (sGoogleSignIn == null){
                return;
            }
            final String googleClientId = ResConfig.getGoogleClientId(activity);
            sGoogleSignIn.setClientId(googleClientId);
            sGoogleSignIn.startSignIn(new SGoogleSignIn.GoogleSignInCallBack() {
                @Override
                public void success(String id, String mFullName, String mEmail,String idTokenString) {
                    PL.i("google sign in : " + id);
                    if (SStringUtil.isNotEmpty(id)) {
                        ThirdAccountBindRequestTaskV2 bindGoogleRequestTask = new ThirdAccountBindRequestTaskV2(getActivity(),
                                account,
                                pwd,
                                areaCode,
                                phone,
                                vfcode,
                                id,
                                idTokenString,
                                googleClientId,
                                currentAccountMode.getUserId(),
                                currentAccountMode.getLoginAccessToken(),
                                currentAccountMode.getLoginTimestamp());
                        sAccountBindV2(bindGoogleRequestTask,account,pwd);
                    }
                }

                @Override
                public void failure() {
                    ToastUtils.toast(getActivity(),"Google sign in error");
                    PL.i("google sign in failure");
                }

                @Override
                public void accountIsExpired(String msg) {
                    ToastUtils.toast(activity,"Google account is expired, try again");
                }
            });
        } else if(bindType == BindType.BIND_TWITTER) {
            if(twitterLogin != null) {
                twitterLogin.startLogin(activity, new TwitterLogin.TwitterLoginCallBack() {
                    @Override
                    public void success(String id, String mFullName, String mEmail, String idTokenString) {
                        PL.i("twitter login : " + id);
                        if (SStringUtil.isNotEmpty(id)) {
                            ThirdAccountBindRequestTaskV2 bindGoogleRequestTask = new ThirdAccountBindRequestTaskV2(getActivity(),
                                    SLoginType.LOGIN_TYPE_TWITTER,
                                    account,
                                    pwd,
                                    areaCode,
                                    phone,
                                    vfcode,
                                    id,
                                    currentAccountMode.getUserId(),
                                    currentAccountMode.getLoginAccessToken(),
                                    currentAccountMode.getLoginTimestamp());
                            sAccountBindV2(bindGoogleRequestTask,account,pwd);
                        }
                    }

                    @Override
                    public void failure(String msg) {

                    }
                });
            }
        }else if(bindType == BindType.BIND_LINE) {

           /* this.sLineSignIn.startSignIn(getActivity().getString(R.string.line_channelId), new SLineSignIn.LineSignInCallBack() {
                @Override
                public void success(String id, String mFullName, String mEmail, String idTokenString) {

                    if (SStringUtil.isNotEmpty(id)) {
                        ThirdAccountBindRequestTaskV2 bindLineRequestTask = new ThirdAccountBindRequestTaskV2(getActivity(),
                                account,
                                pwd,
                                id,
                                idTokenString,
                                currentAccountMode.getUserId(),
                                currentAccountMode.getLoginAccessToken(),
                                currentAccountMode.getLoginTimestamp());
                        sAccountBindV2(bindLineRequestTask,account,pwd);
                    }
                }

                @Override
                public void failure() {

                }
            });*/

            if (currentAccountMode != null) {
                ThirdAccountBindRequestTaskV2 bindLineRequestTask = new ThirdAccountBindRequestTaskV2(getActivity(),
                        SLoginType.LOGIN_TYPE_LINE,
                        account,
                        pwd,
                        currentAccountMode.getThirdId(),
                        "",
                        currentAccountMode.getUserId(),
                        currentAccountMode.getLoginAccessToken(), //该账号保存的accessToken
                        currentAccountMode.getLoginTimestamp());//该账号保存的Timestamp
                sAccountBindV2(bindLineRequestTask,account,pwd);
            }
        }
    }

    public void startTimer() {
        isTimeLimit = true;
        if(requestPhoneVfcodeTimer == null) {
            requestPhoneVfcodeTimer = new Timer();
        }
        resetTime = TIME_OUT_SECONDS;
        requestPhoneVfcodeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(resetTime < 1) {
                    isTimeLimit = false;
                    if(requestPhoneVfcodeTimer != null) {
                        requestPhoneVfcodeTimer.cancel();
                        requestPhoneVfcodeTimer = null;
                    }
                    if(callbackList != null && callbackList.size() > 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (SBaseRelativeLayout.OperationCallback callback : callbackList) {
                                    callback.statusCallback(SBaseRelativeLayout.OperationCallback.TIME_OUT);
                                }
                            }
                        });
                    }
                    return;
                }
                if(callbackList != null && callbackList.size() > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (SBaseRelativeLayout.OperationCallback callback : callbackList) {
                                callback.alertTime(resetTime);
                            }
                        }
                    });
                }
                if(resetTime > 0) {
                    resetTime--;
                }
            }
        }, 300, 1000);
    }

    @Override
    public void getAreaInfo(Activity activity) {
        this.mActivity = activity;
//        if(areaBeanList == null || areaBeanList.length < 1) {
//
//            areaJson = FileUtil.readAssetsTxtFile(getContext(), "mwsdk/areaInfo");
//            Gson gson = new Gson();
//            areaBeanList = gson.fromJson(areaJson, PhoneInfo[].class);
//            showAreaDialog();
//        } else {
//            showAreaDialog();
//        }
    }

    private void showAreaDialog() {
        final String[] areaList = new String[areaBeanList.length];
        for(int i = 0; i < areaBeanList.length; i++) {
            areaList[i] = areaBeanList[i].getText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setItems(areaList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedBean = areaBeanList[which];
//                        if(callback != null) {
//                            callback.dataCallback(selectedBean);
//                        }
                        if(callbackList != null && callbackList.size() > 0) {
                            for (SBaseRelativeLayout.OperationCallback callback : callbackList) {
                                callback.dataCallback(selectedBean);
                            }
                        }
                    }
                });
        AlertDialog d = builder.create();

        d.show();
    }

    @Override
    public void starpyAccountLogin(Activity activity, String account, String password, String vfcode, boolean isSaveAccount) {
        this.mActivity = activity;

        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getContext(), R.string.py_account_empty);
            return;
        }
        account = account.trim();
        if (!SdkUtil.checkAccount(account)) {
            ToastUtils.toast(activity,R.string.text_account_format);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }
        password = password.trim();
        if (!SdkUtil.checkPassword(password)) {
            ToastUtils.toast(activity,R.string.text_pwd_format);
            return;
        }


        login(activity, account, password, vfcode, isSaveAccount);
    }

    private void login(final Activity activity, final String account, final String password, String vfcode, final boolean isSaveAccount) {

        AccountLoginRequestTask accountLoginCmd = new AccountLoginRequestTask(getActivity(), account, password, vfcode);
        accountLoginCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(),"Loading..."));
        accountLoginCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null){
                    if (sLoginResponse.isRequestSuccess()) {
//                        GamaUtil.saveAccount(getContext(),account);
                        if(isSaveAccount) {
                            SdkUtil.saveAccountModel(activity,account,password,sLoginResponse.getData().getUserId(),sLoginResponse.getData().getToken(),
                                    sLoginResponse.getData().getTimestamp(),true);
                        }
//                        ToastUtils.toast(getActivity(), R.string.py_login_success);
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_MG);
                    }else{
                        ToastUtils.toast(getActivity(),sLoginResponse.getMessage());
//                        if(isAutoLogin) {
//                            showLoginView();
//                        } else {
//                            refreshVfCode();
//                        }
                    }
                }else{
                    ToastUtils.toast(getActivity(),R.string.py_error_occur);
//                    if(isAutoLogin) {
//                        showLoginView();
//                    } else {
//                        refreshVfCode();
//                    }
                }
            }

            @Override
            public void timeout(String code) {
//                if(isAutoLogin) {
//                    showLoginView();
//                } else {
//                    refreshVfCode();
//                }
            }

            @Override
            public void noData() {
//                if(isAutoLogin) {
//                    showLoginView();
//                } else {
//                    refreshVfCode();
//                }
            }

            @Override
            public void cancel() {
//                if(isAutoLogin) {
//                    showLoginView();
//                } else {
//                    refreshVfCode();
//                }
            }
        });
        accountLoginCmd.excute(SLoginResponse.class);
    }

    /**
     * 进行手机验证
     */
    @Override
    public void phoneVerify(Activity activity, String area, String phone, String vfCode, String thirdId, final String loginType) {
        this.mActivity = activity;
        PhoneVerifyRequestTask accountLoginCmd = new PhoneVerifyRequestTask(getActivity(), area, phone, vfCode, thirdId, loginType);
        accountLoginCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(),"Loading..."));
        accountLoginCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null){
                    if (sLoginResponse.isRequestSuccess()) {
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, loginType);
                    } else {
                        ToastUtils.toast(getActivity(),sLoginResponse.getMessage());
                    }
                }else{
                    ToastUtils.toast(getActivity(),R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {}

            @Override
            public void noData() {}

            @Override
            public void cancel() {}
        });
        accountLoginCmd.excute(SLoginResponse.class);
    }

    /**
     * 进行手机验证
     */
    @Override
    public void inGamePhoneVerify(final Activity activity, String area, String phone, String vfCode, String thirdId, final String loginType) {
        this.mActivity = activity;
        PhoneVerifyRequestTask accountLoginCmd = new PhoneVerifyRequestTask(getActivity(), area, phone, vfCode, thirdId, loginType);
        accountLoginCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(),"Loading..."));
        accountLoginCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null){
                    ToastUtils.toast(getActivity(),sLoginResponse.getMessage());
                    if (sLoginResponse.isRequestSuccess()) {
                        //SdkUtil.setAccountLinked(activity);
                        if(callbackList != null && callbackList.size() > 0) {
                            for (SBaseRelativeLayout.OperationCallback callback : callbackList) {
                                callback.statusCallback(SBaseRelativeLayout.OperationCallback.BIND_OK);
                            }
                        }
                    }
                }else{
                    ToastUtils.toast(getActivity(),R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {}

            @Override
            public void noData() {}

            @Override
            public void cancel() {}
        });
        accountLoginCmd.excute(SLoginResponse.class);
    }

    @Override
    public void deleteAccout(Context mContext, String userId, String loginMode, String thirdLoginId, String loginAccessToken, String loginTimestamp, SFCallBack<String> sfCallBack) {

        DeleteAccountRequestTask cmd = new DeleteAccountRequestTask(getActivity(), userId,loginMode,thirdLoginId,loginAccessToken,loginTimestamp);
        cmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        cmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
                        if (SStringUtil.isNotEmpty(sLoginResponse.getMessage())){
                            ToastUtils.toast(getActivity(), sLoginResponse.getMessage() + "");
                        }
                        SdkUtil.removeAccountModelByUserId(getActivity(),userId);
                        if (sfCallBack != null){
                            sfCallBack.success(rawResult,rawResult);
                        }
//                        GamaUtil.saveAccountModel(activity,account,password,sLoginResponse.getData().getUserId(),sLoginResponse.getData().getToken(),
//                                sLoginResponse.getData().getTimestamp(),true);
//                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_MG);

                    }else{

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage() + "");
                        if (sfCallBack != null){
                            sfCallBack.success(rawResult,rawResult);
                        }
                    }

                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                    if (sfCallBack != null){
                        sfCallBack.success(rawResult,rawResult);
                    }
                }
            }

            @Override
            public void timeout(String code) {}

            @Override
            public void noData() {}

            @Override
            public void cancel() {}

        });
        cmd.excute(SLoginResponse.class);
    }



    @Override
    public int getRemainTimeSeconds() {
        return resetTime;
    }

   /* private boolean checkIsMacLoginLimit(Activity activity, final SLoginResponse sLoginResponse, final String rawResult) {
        boolean isLimit = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String msg = sLoginResponse.getMessage();
        if ("9002".equals(sLoginResponse.getCode())) { //提示绑定的选择
            isLimit = true;
            if(TextUtils.isEmpty(msg)) {
                builder.setTitle(R.string.py_mac_login_limit_hint1);
            } else {
                builder.setTitle(msg);
            }
            builder.setPositiveButton(R.string.py_mac_login_limit_hint_continue, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //继续登入
                    sLoginResponse.setCode(BaseResponseModel.SUCCESS_CODE);
                    handleRegisteOrLoginSuccess(sLoginResponse, rawResult, SLoginType.LOGIN_TYPE_GUEST);
                }
            });
            builder.setNegativeButton(R.string.py_mac_login_limit_hint_bind_now, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //马上绑定
                    showBindView(GSLoginCommonConstant.GsLoginUiPageNumber.GS_PAGE_MAIN);
                }
            });
        } else if ("9001".equals(sLoginResponse.getCode())){ //强制绑定
            isLimit = true;
            if(TextUtils.isEmpty(msg)) {
                builder.setTitle(R.string.py_mac_login_limit_hint2);
            } else {
                builder.setTitle(msg);
            }
            builder.setPositiveButton(R.string.py_mac_login_limit_hint_bind_now2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //立即绑定
                    showBindView(GSLoginCommonConstant.GsLoginUiPageNumber.GS_PAGE_MAIN);
                }
            });
        }
        builder.setCancelable(false);
        builder.create().show();
        return isLimit;
    }
*/
}

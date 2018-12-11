package com.gama.sdk.login.p;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.BitmapUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.SignatureUtil;
import com.core.base.utils.ToastUtils;
import com.facebook.AccessToken;
import com.facebook.internal.ImageRequest;
import com.gama.base.bean.SLoginType;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.execute.AccountInjectionRequestTask;
import com.gama.data.login.execute.AccountLoginRequestTask;
import com.gama.data.login.execute.AccountRegisterRequestTask;
import com.gama.data.login.execute.ChangePwdRequestTask;
import com.gama.data.login.execute.FindPwdRequestTask;
import com.gama.data.login.execute.MacLoginRegRequestTask;
import com.gama.data.login.execute.ThirdAccountBindRequestTask;
import com.gama.data.login.execute.ThirdLoginRegRequestTask;
import com.gama.data.login.request.ThirdLoginRegRequestBean;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.R;
import com.gama.sdk.ads.StarEventLogger;
import com.gama.sdk.login.LoginContract;
import com.gama.sdk.utils.DialogUtil;
import com.gama.thirdlib.facebook.FaceBookUser;
import com.gama.thirdlib.facebook.FbResUtil;
import com.gama.thirdlib.facebook.FbSp;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGoogleSignIn;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录功能控制器类
 * Created by gan on 2017/4/13.
 */

public class LoginPresenterImpl implements LoginContract.ILoginPresenter {

    private static final String TAG = LoginPresenterImpl.class.getSimpleName();
    private Activity mActivity;

    /**
     * SLoginDialogV2
     */
    private LoginContract.ILoginView iLoginView;

    private Timer autoLoginTimer;
    int count = 3;

    private Activity getActivity(){
        return mActivity;
    }

    private Context getContext(){
        return mActivity.getApplicationContext();
    }

    private SFacebookProxy sFacebookProxy;
    private SGoogleSignIn sGoogleSignIn;
    private FaceBookUser faceBookUser;

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    private Fragment fragment;



    @Override
    public void setSGoogleSignIn(SGoogleSignIn sGoogleSignIn) {
        this.sGoogleSignIn = sGoogleSignIn;
    }

    @Override
    public void setSFacebookProxy(SFacebookProxy sFacebookProxy) {
        this.sFacebookProxy = sFacebookProxy;
    }

    @Override
    public void autoLogin(Activity activity) {
        this.mActivity = activity;
        String previousLoginType = GamaUtil.getPreviousLoginType(activity);

        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GAMA, previousLoginType)) {//自動登錄
            String account = GamaUtil.getAccount(activity);
            String password = GamaUtil.getPassword(activity);
            startAutoLogin(activity, SLoginType.LOGIN_TYPE_GAMA, account, password);

        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MAC, previousLoginType)) {//自動登錄
            String account = GamaUtil.getMacAccount(activity);
            String password = GamaUtil.getMacPassword(activity);
            startAutoLogin(activity, SLoginType.LOGIN_TYPE_GAMA, account, password);

        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, previousLoginType)) {//自動登錄
            String fbScopeId = FbSp.getFbId(activity);
            if (TextUtils.isEmpty(fbScopeId)){
                showLoginView();
            }else{
                startAutoLogin(activity, SLoginType.LOGIN_TYPE_FB, "", "");
            }

        }  else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, previousLoginType)) {//自動登錄
//           thirdPlatLogin(mActivity,GamaUtil.getGoogleId(mActivity),SLoginType.LOGIN_TYPE_GOOGLE);
            startAutoLogin(activity, SLoginType.LOGIN_TYPE_GOOGLE, "", "");

        }else {//進入登錄頁面
            showLoginView();
        }

        PL.i("fb keyhash:" + SignatureUtil.getHashKey(activity, activity.getPackageName()));

    }

    @Override
    public void starpyAccountLogin(Activity activity, String account, String pwd) {
        this.mActivity = activity;
        login(activity, account, pwd);
    }

    @Override
    public void fbLogin(Activity activity) {
        this.mActivity = activity;
        if (sFacebookProxy != null) {
            sFbLogin(activity, sFacebookProxy, new FbLoginCallBack() {
                @Override
                public void loginSuccess(FaceBookUser user) {
                    fbThirdLogin(user.getUserFbId(), user.getBusinessId(), "");
                }
            });
        }
    }


    @Override
    public void googleLogin(final Activity activity) {
        if (sGoogleSignIn == null){
            return;
        }
        sGoogleSignIn.setClientId(ResConfig.getGoogleClientId(activity));
        sGoogleSignIn.startSignIn(new SGoogleSignIn.GoogleSignInCallBack() {
            @Override
            public void success(String id, String mFullName, String mEmail, String idTokenString) {
                PL.i("google sign in : " + id);
                if (SStringUtil.isNotEmpty(id)) {
                    GamaUtil.saveGoogleId(activity,id);
                    ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                    thirdLoginRegRequestBean.setThirdPlatId(id);
                    thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GOOGLE);
                    thirdLoginRegRequestBean.setGoogleClientId(ResConfig.getGoogleClientId(activity));
                    thirdLoginRegRequestBean.setGoogleIdToken(idTokenString);

                    thirdPlatLogin(activity, thirdLoginRegRequestBean);
                }
            }

            @Override
            public void failure() {
                ToastUtils.toast(activity,"Google sign in error");
                PL.i("google sign in failure");
            }
        });
    }

    //目前Google登录使用到，fb登录没有使用
    @Override
    public void thirdPlatLogin(Activity activity, final ThirdLoginRegRequestBean thirdLoginRegRequestBean) {
        this.mActivity = activity;

        ThirdLoginRegRequestTask cmd = new ThirdLoginRegRequestTask(getActivity(),thirdLoginRegRequestBean);
        cmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        cmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {

                    if (sLoginResponse.isRequestSuccess()){
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, thirdLoginRegRequestBean.getRegistPlatform());
                        return;
                    }else{

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                    }
                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
                showMainLoginView();
            }

            @Override
            public void timeout(String code) {
                showMainLoginView();
            }

            @Override
            public void noData() {
                showMainLoginView();
            }
        });
        cmd.excute(SLoginResponse.class);

    }

    @Override
    public void macLogin(Activity activity) {
        this.mActivity = activity;
        mMacLogin(activity);
    }

    @Override
    public void register(Activity activity, String account, String pwd, String email) {
        this.mActivity = activity;
        registerAccout(activity, account, pwd, email);
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
//            PL.i(TAG, "取消自動登錄，進行Facebook登出");
//        }
        showLoginView();
    }

    @Override
    public boolean hasAccountLogin() {

        String account = GamaUtil.getAccount(mActivity);
        String password = GamaUtil.getPassword(mActivity);
        if (TextUtils.isEmpty(account)) {
            account = GamaUtil.getMacAccount(mActivity);
            password = GamaUtil.getMacPassword(mActivity);
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
    }


    @Override
    public void changePwd(final Activity activity, final String account, String oldPwd, String newPwd) {

        this.mActivity = activity;
        ChangePwdRequestTask changePwdRequestTask = new ChangePwdRequestTask(activity,account,oldPwd,newPwd);
        changePwdRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        changePwdRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {

            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                        if (account.equals(GamaUtil.getAccount(getContext()))){
                            GamaUtil.savePassword(getContext(),"");
                        }
                        iLoginView.changePwdSuccess(sLoginResponse);

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
        });

        changePwdRequestTask.excute(SLoginResponse.class);

    }

    @Override
    public void findPwd(Activity activity, String account, String email) {
        this.mActivity = activity;
        FindPwdRequestTask findPwdRequestTask = new FindPwdRequestTask(getActivity(), account, email);
        findPwdRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        findPwdRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
                        ToastUtils.toast(getActivity(), R.string.py_findpwd_success);

//                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_GAMA);
                        if (iLoginView != null){
                            iLoginView.findPwdSuccess(sLoginResponse);
                        }
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

        });
        findPwdRequestTask.excute(SLoginResponse.class);
    }


    @Override
    public void accountBind(Activity activity, String account, String pwd, String email, int bindType) {
        this.mActivity = activity;
        final String mAccount = account;
        final String mPwd = pwd;
        final String mEmail = email;
        if (bindType == SLoginType.bind_unique){
            String uniqueId = GamaUtil.getCustomizedUniqueId1AndroidId1Adid(activity);
            if(TextUtils.isEmpty(uniqueId)){
                PL.d("thirdPlatId:" + uniqueId);
                return;
            }
            ThirdAccountBindRequestTask bindRequestTask = new ThirdAccountBindRequestTask(getActivity(), account,pwd, email,SLoginType.LOGIN_TYPE_UNIQUE,uniqueId);
            sAccountBind(bindRequestTask);

        }else if (bindType == SLoginType.bind_fb){
            sFbLogin(activity, sFacebookProxy, new FbLoginCallBack() {
                @Override
                public void loginSuccess(FaceBookUser user) {
                    ThirdAccountBindRequestTask bindRequestTask = new ThirdAccountBindRequestTask(getActivity(),
                            mAccount,mPwd, mEmail, user.getUserFbId(), user.getBusinessId(), "");
                    sAccountBind(bindRequestTask);
                }
            });

        }else  if (bindType == SLoginType.bind_google){//Google绑定
            if (sGoogleSignIn == null){
                return;
            }
            sGoogleSignIn.setClientId(ResConfig.getGoogleClientId(activity));
            sGoogleSignIn.startSignIn(new SGoogleSignIn.GoogleSignInCallBack() {
                @Override
                public void success(String id, String mFullName, String mEmail,String idTokenString) {
                    PL.i("google sign in : " + id);
                    if (SStringUtil.isNotEmpty(id)) {
                        ThirdAccountBindRequestTask bindGoogleRequestTask = new ThirdAccountBindRequestTask(getActivity(), mAccount,mPwd, mEmail,SLoginType.LOGIN_TYPE_GOOGLE,id);
                        sAccountBind(bindGoogleRequestTask);
                    }
                }

                @Override
                public void failure() {
                    ToastUtils.toast(getActivity(),"Google sign in error");
                    PL.i("google sign in failure");
                }
            });
        }


    }

    @Override
    public void accountInject(Activity activity, String account, String pwd, String uid) {
        AccountInjectionRequestTask injectionRequestTask = new AccountInjectionRequestTask(getActivity(),account,pwd,uid);
        injectionRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        injectionRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {

                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, "");

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
        });
        injectionRequestTask.excute(SLoginResponse.class);
    }

    private void sAccountBind(ThirdAccountBindRequestTask bindRequestTask) {
        bindRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        bindRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
                        ToastUtils.toast(getActivity(), R.string.py_success);

//                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_GAMA);
                        if (iLoginView != null){
                            iLoginView.accountBindSuccess(sLoginResponse);
                        }
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

        });
        bindRequestTask.excute(SLoginResponse.class);
    }

    private void mMacLogin(Activity activity) {

        MacLoginRegRequestTask macLoginRegCmd = new MacLoginRegRequestTask(getActivity());
        macLoginRegCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        macLoginRegCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
//                        ToastUtils.toast(getActivity(), R.string.py_login_success);
//                        GamaUtil.saveSdkLoginData(getContext(),rawResult);

                        //1001 注册成功    1000登入成功
                        if (SStringUtil.isEqual("1001", sLoginResponse.getCode())) {//注册写一次

                            cteateUserImage(getActivity(),sLoginResponse.getFreeRegisterName(),sLoginResponse.getFreeRegisterPwd());

                            //mac登录第一次写一次
                        }else if(SStringUtil.isEqual("1000", sLoginResponse.getCode()) && SStringUtil.isEmpty(GamaUtil.getMacAccount(getContext()))) {

                            cteateUserImage(getActivity(),sLoginResponse.getFreeRegisterName(),sLoginResponse.getFreeRegisterPwd());
                        }

                        GamaUtil.saveMacAccount(getContext(),sLoginResponse.getFreeRegisterName());
                        GamaUtil.saveMacPassword(getContext(),sLoginResponse.getFreeRegisterPwd());
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_MAC);

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
        });
        macLoginRegCmd.excute(SLoginResponse.class);

    }

    private void cteateUserImage(Context context, String freeRegisterName, String freeRegisterPwd) {
        try {
            String appName = ApkInfoUtil.getApplicationName(context);
//        String text = "你登入" + appName + "的帳號和密碼如下:\n帳號:" + freeRegisterName + "\n" + "密碼:" + freeRegisterPwd;
            String text = String.format(context.getResources().getString(R.string.py_login_mac_tips), appName, freeRegisterName, freeRegisterPwd);
            PL.i("cteateUserImage:" + text);
            Bitmap bitmap = BitmapUtil.bitmapAddText(BitmapFactory.decodeResource(context.getResources(),R.drawable.v2_mac_pwd_bg),text);
            String m = BitmapUtil.saveImageToGallery(getContext(),bitmap);
            if (SStringUtil.isNotEmpty(m)){
                ToastUtils.toast(context, context.getResources().getString(R.string.py_login_mac_saveimage_tips));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Facebook登录实现
     */
    private void sFbLogin(final Activity activity, final SFacebookProxy sFacebookProxy, final FbLoginCallBack fbLoginCallBack) {
        String fbThirdId = FbSp.getFbId(activity);
        String businessId = FbSp.getAppsBusinessId(activity);
        String tokenBusiness = FbSp.getTokenForBusiness(activity);
        String gender = FbSp.getFbGender(activity);
        String birthday = FbSp.getFbBirthday(activity);
        String name = FbSp.getFbName(activity);
        String fbId = FbResUtil.findStringByName(activity,"facebook_app_id");
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String token = "";
        if(accessToken != null) {
            token = accessToken.getToken();
        }
        if(!TextUtils.isEmpty(fbThirdId)) {
            PL.i(TAG, "Facebook登入使用緩存登入");
            if(TextUtils.isEmpty(businessId)) {
                businessId = fbThirdId + "_" + fbId;
            }
            PL.i(TAG, "Facebook ThirdId: " + fbThirdId);
            PL.i(TAG, "Facebook businessId: " + businessId);
            PL.i(TAG, "Facebook tokenBusiness: " + tokenBusiness);
            PL.i(TAG, "Facebook gender: " + gender);
            PL.i(TAG, "Facebook birthday: " + birthday);
            PL.i(TAG, "Facebook name: " + name);
            PL.i(TAG, "Facebook token: " + token);
            if(fbLoginCallBack != null) {
                faceBookUser = new FaceBookUser();
                faceBookUser.setUserFbId(fbThirdId);
                faceBookUser.setBusinessId(businessId);
                faceBookUser.setName(name);
                faceBookUser.setGender(gender);
                faceBookUser.setBirthday(birthday);
                faceBookUser.setPictureUri(ImageRequest.getProfilePictureUri(fbThirdId, 300, 300));
                faceBookUser.setAccessTokenString(token);
                fbLoginCallBack.loginSuccess(faceBookUser);
            } else {
                PL.i(TAG, "Facebook 登入回調為空");
            }
            return;
        } else {
            PL.i(TAG, "沒有Facebook緩存，正式登入");
        }

        if (sFacebookProxy == null) {
            PL.i(TAG, "Facebook Proxy為null");
            return;
        }
        SFacebookProxy.FbLoginCallBack fbLoginCallBack1 = new SFacebookProxy.FbLoginCallBack() {
            @Override
            public void onCancel() {
                PL.d(TAG, "sFbLogin cancel");
            }

            @Override
            public void onError(String message) {
                PL.d(TAG, "sFbLogin error: " + message);
            }

            @Override
            public void onSuccess(FaceBookUser user) {
                PL.d("fb uid:" + user.getUserFbId());

                final String businessId = user.getUserFbId() + "_" + user.getFacebookAppId();
                PL.d("fb businessId:" + businessId);
                FbSp.saveAppsBusinessId(activity, businessId);

                sFacebookProxy.getMyProfile(activity, new SFacebookProxy.FbLoginCallBack() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onSuccess(FaceBookUser user) {
                        user.setBusinessId(businessId);
                        faceBookUser = user;
                        if (fbLoginCallBack != null){
                            fbLoginCallBack.loginSuccess(user);
                        }
                    }
                });

                // TODO: 2018/6/21 手动拼写businessId
//                final String fbScopeId = user.getUserId();
//                sFacebookProxy.requestBusinessId(activity, new SFacebookProxy.FbBusinessIdCallBack() {
//                    @Override
//                    public void onError() {
//
//                    }
//
//                    @Override
//                    public void onSuccess(String businessId) {
//                        PL.d("fb businessId:" + businessId);
//                        if (fbLoginCallBack != null){
//                            fbLoginCallBack.loginSuccess(fbScopeId,businessId,FbSp.getTokenForBusiness(getActivity()));
//                        }
//                    }
//                });

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
     * @param fbScopeId
     * @param fbApps
     * @param fbTokenBusiness
     */
    private void fbThirdLogin(String fbScopeId, String fbApps, String fbTokenBusiness) {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String accessTokenString = "";
        if (accessToken != null){
            accessTokenString = accessToken.getToken();
        }
        ThirdLoginRegRequestTask cmd = new ThirdLoginRegRequestTask(getActivity(),fbScopeId,fbApps,fbTokenBusiness,accessTokenString);
        cmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        cmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {

                    if (sLoginResponse.isRequestSuccess()){

                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_FB);
                        return;
                    }else{

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                    }
                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
                showMainLoginView();
            }

            @Override
            public void timeout(String code) {
                showMainLoginView();
            }

            @Override
            public void noData() {
                showMainLoginView();
            }
        });
        cmd.excute(SLoginResponse.class);

    }

    private void login(Activity activity, final String account, final String password) {

        AccountLoginRequestTask accountLoginCmd = new AccountLoginRequestTask(getActivity(), account, password);
        accountLoginCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(),"Loading..."));
        accountLoginCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null){
                    if (sLoginResponse.isRequestSuccess()) {
                        GamaUtil.saveAccount(getContext(),account);
                        GamaUtil.savePassword(getContext(),password);
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_GAMA);
                    }else{

                        ToastUtils.toast(getActivity(),sLoginResponse.getMessage());
                    }
                }else{
                    ToastUtils.toast(getActivity(),R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        accountLoginCmd.excute(SLoginResponse.class);

    }

    private void registerAccout(Activity activity, final String account, final String password, String email){

        AccountRegisterRequestTask accountRegisterCmd = new AccountRegisterRequestTask(getActivity(), account, password,email);
        accountRegisterCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        accountRegisterCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
                        ToastUtils.toast(getActivity(), R.string.py_register_success);

                        GamaUtil.saveAccount(getContext(),account);
                        GamaUtil.savePassword(getContext(),password);
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_GAMA);

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

        });
        accountRegisterCmd.excute(SLoginResponse.class);

    }

    private void startAutoLogin(final Activity activity, final String registPlatform, final String account, final String password) {


        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GAMA, registPlatform)) {

            if (SStringUtil.hasEmpty(account, password)) {

                showLoginView();
                return;
            }
            if (SStringUtil.isEqual(account, password)) {
                showLoginView();
                return;
            }

            if (!GamaUtil.checkAccount(account)) {
                showLoginView();
                return;
            }
            if (!GamaUtil.checkPassword(password)) {
                showLoginView();
                return;
            }

            iLoginView.showAutoLoginTips(String.format(activity.getResources().getString(R.string.py_login_autologin_tips),account));
        }else{
            String autoLoginTips = activity.getResources().getString(R.string.py_login_autologin_logining_tips);
            if (registPlatform.equals(SLoginType.LOGIN_TYPE_FB)){
                autoLoginTips = "Facebook" + autoLoginTips;
            }else if (registPlatform.equals(SLoginType.LOGIN_TYPE_GOOGLE)){
                autoLoginTips = "Google" + autoLoginTips;
            }
            iLoginView.showAutoLoginTips(autoLoginTips);
        }
        iLoginView.showAutoLoginView();

        count = 3;

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

                            if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GAMA, registPlatform)) {//免注册或者平台用户自动登录
//                                autoLogin22(mActivity, account, password);

                                starpyAccountLogin(activity,account,password);

                            }else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, registPlatform)){//fb自动的登录
                                /*String fbScopeId = FbSp.getFbId(activity);
                                String fbApps = FbSp.getAppsBusinessId(activity);
                                if(TextUtils.isEmpty(fbApps)) {
                                    fbApps = fbScopeId + "_" + FbResUtil.findStringByName(activity,"facebook_app_id");
                                }
                                fbThirdLogin(fbScopeId, fbApps,FbSp.getTokenForBusiness(activity));*/
                                fbLogin(mActivity);

                            }else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, registPlatform)){//Google登录

                                ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                                thirdLoginRegRequestBean.setThirdPlatId(GamaUtil.getGoogleId(activity));
                                thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GOOGLE);
                                thirdLoginRegRequestBean.setGoogleClientId(ResConfig.getGoogleClientId(activity));
                                thirdLoginRegRequestBean.setGoogleIdToken(GamaUtil.getGoogleIdToken(activity));
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

    private void showLoginView() {
        if (iLoginView != null){
//            iLoginView.hildAutoLoginView();
            iLoginView.showLoginView();
        }
    }

    private void showMainLoginView() {
        if (iLoginView != null){
            iLoginView.showMainLoginView();
        }
    }


    private void handleRegisteOrLoginSuccess(SLoginResponse loginResponse, String rawResult, String loginType) {

        GamaUtil.saveSdkLoginData(getContext(), loginResponse.getRawResponse());
        loginResponse.setLoginType(loginType);
        if (SStringUtil.isNotEmpty(loginType)) {//loginType为空时是账号注入登录，不能空时是其他普通登入

            GamaUtil.savePreviousLoginType(mActivity, loginType);
            try {
                if (loginResponse != null) {
                    //1001 注册成功    1000登入成功
                    if (SStringUtil.isEqual("1000", loginResponse.getCode())) {
                        StarEventLogger.trackinLoginEvent(mActivity, loginResponse);
                    } else if (SStringUtil.isEqual("1001", loginResponse.getCode())) {
                        StarEventLogger.trackinRegisterEvent(mActivity, loginResponse);
                    }
                }
                if(SLoginType.LOGIN_TYPE_FB.equals(loginType) && faceBookUser != null) {
                    loginResponse.setThirdToken(faceBookUser.getAccessTokenString());
                    loginResponse.setGender(FbSp.getFbGender(mActivity));
                    loginResponse.setBirthday(FbSp.getFbBirthday(mActivity));
                    loginResponse.setIconUri(faceBookUser.getPictureUri());
                    loginResponse.setThirdId(faceBookUser.getUserFbId());
                    loginResponse.setNickName(faceBookUser.getName());
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

        if (iLoginView != null){
            iLoginView.LoginSuccess(loginResponse);
        }
    }


    interface FbLoginCallBack{

//        void loginSuccess(String fbScopeId, String businessId, String tokenForBusiness);
        void loginSuccess(FaceBookUser user);
    }

}

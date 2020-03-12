package com.gama.sdk.login.p;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.BitmapUtil;
import com.core.base.utils.FileUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.facebook.AccessToken;
import com.facebook.internal.ImageRequest;
import com.gama.base.bean.GamaAreaInfoBean;
import com.gama.base.bean.SLoginType;
import com.gama.base.cfg.ResConfig;
import com.gama.base.excute.GamaAreaInfoRequestTask;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.constant.GSLoginCommonConstant;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.data.login.execute.AccountLoginRequestTask;
import com.gama.data.login.execute.AccountRegisterRequestTask;
import com.gama.data.login.execute.ChangePwdRequestTask;
import com.gama.data.login.execute.FindPwdRequestTask;
import com.gama.data.login.execute.MacLoginRegRequestTask;
import com.gama.data.login.execute.PhoneVerifyRequestTask;
import com.gama.data.login.execute.PhoneVfcodeRequestTask;
import com.gama.data.login.execute.ThirdLoginRegRequestTask;
import com.gama.data.login.execute2.ThirdAccountBindRequestTaskV2;
import com.gama.data.login.request.ThirdLoginRegRequestBean;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.R;
import com.gama.sdk.SBaseRelativeLayout;
import com.gama.sdk.ads.StarEventLogger;
import com.gama.sdk.login.LoginContract;
import com.gama.sdk.utils.DialogUtil;
import com.gama.thirdlib.facebook.FaceBookUser;
import com.gama.thirdlib.facebook.FbResUtil;
import com.gama.thirdlib.facebook.FbSp;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGoogleSignIn;
import com.gama.thirdlib.twitter.GamaTwitterLogin;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录功能控制器类
 * Created by gan on 2017/4/13.
 */

public class LoginPresenterImpl implements LoginContract.ILoginPresenter {

    private static final String TAG = LoginPresenterImpl.class.getSimpleName();
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
    private GamaAreaInfoBean[] areaBeanList;
    //已选中的区域bean
    private GamaAreaInfoBean selectedBean;

    int count = 3;

    private Activity getActivity(){
        return mActivity;
    }

    private Context getContext(){
        return mActivity.getApplicationContext();
    }

    private SFacebookProxy sFacebookProxy;
    private SGoogleSignIn sGoogleSignIn;
    private GamaTwitterLogin twitterLogin;
    private FaceBookUser faceBookUser;

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
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
    public void setTwitterLogin(GamaTwitterLogin twitterLogin) {
        this.twitterLogin = twitterLogin;
    }

    @Override
    public void twitterLogin(final Activity activity) {
        if(twitterLogin != null) {
            twitterLogin.startLogin(new GamaTwitterLogin.TwitterLoginCallBack() {
                @Override
                public void success(String id, String mFullName, String mEmail, String idTokenString) {
                    PL.i("twitter login : " + id);
                    PL.i("google sign in : " + id);
                    if (SStringUtil.isNotEmpty(id)) {
                        GamaUtil.saveTwitterId(activity,id);
                        ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                        thirdLoginRegRequestBean.setThirdPlatId(id);
                        thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_TWITTER);
                        thirdLoginRegRequestBean.setGoogleClientId(ResConfig.getGoogleClientId(activity));
                        thirdLoginRegRequestBean.setGoogleIdToken(idTokenString);
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
    public void autoLogin(Activity activity) {
        this.mActivity = activity;

//        if(GamaUtil.getVfcodeSwitchStatus(activity)) {
//            showLoginView();
//            return;
//        }

        String previousLoginType = GamaUtil.getPreviousLoginType(activity);

        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GAMESWORD, previousLoginType)) {//自動登錄
            //如果开启了验证码登录就不进行gs自动登录
            if(GamaUtil.getVfcodeSwitchStatus(activity)) {
                showLoginView();
            } else {
                String account = GamaUtil.getAccount(activity);
                String password = GamaUtil.getPassword(activity);
                startAutoLogin(activity, SLoginType.LOGIN_TYPE_GAMESWORD, account, password);
            }

        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MAC, previousLoginType)) {//免注册没有自動登錄
//            String account = GamaUtil.getMacAccount(activity);
//            String password = GamaUtil.getMacPassword(activity);
//            startAutoLogin(activity, SLoginType.LOGIN_TYPE_GAMESWORD, account, password);
            showLoginView();
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

        } else if(SStringUtil.isEqual(SLoginType.LOGIN_TYPE_TWITTER, previousLoginType)) {
            startAutoLogin(activity, SLoginType.LOGIN_TYPE_TWITTER, "", "");
        } else {//進入登錄頁面
            showLoginView();
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
                        fbThirdLogin(user.getUserFbId(), user.getBusinessId(), "");
                    } else {
                        if (isAutoLogin) {
                            showLoginView();
                        }
                    }
                }
            });
        }
    }


    @Override
    public void googleLogin(final Activity activity) {
        if (sGoogleSignIn == null){
            if (isAutoLogin) {
                showLoginView();
            }
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
                } else {
                    if (isAutoLogin) {
                        showLoginView();
                    }
                }
            }

            @Override
            public void failure() {
                ToastUtils.toast(activity,"Google sign in error");
                PL.i("google sign in failure");
                if (isAutoLogin) {
                    showLoginView();
                }
            }
        });
    }

    //目前Google登录使用到，fb登录没有使用
    @Override
    public void thirdPlatLogin(final Activity activity, final ThirdLoginRegRequestBean thirdLoginRegRequestBean) {
        this.mActivity = activity;

        ThirdLoginRegRequestTask cmd = new ThirdLoginRegRequestTask(getActivity(),
                thirdLoginRegRequestBean,
                GSRequestMethod.GSRequestType.GAMESWORD);
        cmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        cmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {

                    if (sLoginResponse.isRequestSuccess()){
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, thirdLoginRegRequestBean.getRegistPlatform());
                        return;
                    } else if ("9000".equals(sLoginResponse.getCode())) { //需要验证手机
                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                        if (isAutoLogin) {
                            //如果是从自动登入过来的，就要先隐藏自动登入界面，显示登入主界面
                            showMainLoginView();
                        }
                        showPhoneVerifyView(thirdLoginRegRequestBean.getRegistPlatform(), thirdLoginRegRequestBean.getThirdPlatId());
                        return;
                    } else {
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

            @Override
            public void cancel() {
                showMainLoginView();
            }
        });
        cmd.excute(SLoginResponse.class);

    }

    @Override
    public void macLogin(final Activity activity) {
        this.mActivity = activity;
        mMacLogin(activity);
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
    public void getEmailVfcode(Activity activity, String email, String interfaceName) {
        this.mActivity = activity;
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
        if(sFacebookProxy != null) {
            PL.i(TAG, "取消自動登錄，進行Facebook登出");
            sFacebookProxy.fbLogout(activity);
        } else {
            PL.i(TAG, "sFacebookProxy为null，无法進行Facebook登出");
        }
        if(sGoogleSignIn != null) {
            sGoogleSignIn.handleActivityDestroy(this.getContext());
        } else {
            PL.i(TAG, "sGoogleSignIn为null，无法進行Google登出");
        }
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
        if (requestPhoneVfcodeTimer != null){
            requestPhoneVfcodeTimer.cancel();
        }
    }


    @Override
    public void changePwd(final Activity activity, final String account, String oldPwd, final String newPwd) {
        this.mActivity = activity;
        ChangePwdRequestTask changePwdRequestTask = new ChangePwdRequestTask(activity,account,oldPwd,newPwd, GSRequestMethod.GSRequestType.GAMESWORD);
        changePwdRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        changePwdRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {

            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                        if (account.equals(GamaUtil.getAccount(getContext()))){
                            GamaUtil.savePassword(getContext(), newPwd);
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

            @Override
            public void cancel() {
            }
        });

        changePwdRequestTask.excute(SLoginResponse.class);

    }

    private void sAccountBindV2(ThirdAccountBindRequestTaskV2 bindRequestTask) {
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

            @Override
            public void cancel() {
            }

        });
        bindRequestTask.excute(SLoginResponse.class);
    }

    private void mMacLogin(final Activity activity) {
        MacLoginRegRequestTask macLoginRegCmd = new MacLoginRegRequestTask(getActivity(), GSRequestMethod.GSRequestType.GAMESWORD);
        macLoginRegCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        macLoginRegCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_MAC);
                    } else if (checkIsMacLoginLimit(activity, sLoginResponse, rawResult)) {
//                        macLoginLimit(activity);
                    } else {
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
        macLoginRegCmd.excute(SLoginResponse.class);
    }

    private void cteateUserImage(Context context, String freeRegisterName, String freeRegisterPwd) {
        try {
            String appName = ApkInfoUtil.getApplicationName(context);
            String text = String.format(context.getResources().getString(R.string.py_login_mac_tips), appName, freeRegisterName, freeRegisterPwd);
            PL.i("cteateUserImage:" + text);
            Bitmap bitmap = BitmapUtil.bitmapAddText(BitmapFactory.decodeResource(context.getResources(),R.drawable.v2_mac_pwd_bg),text);
            String m = BitmapUtil.saveImageToGallery(getContext(),bitmap);
            ToastUtils.toast(context, context.getResources().getString(R.string.py_login_mac_saveimage_tips));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Facebook登录实现
     */
    private void sFbLogin(final Activity activity, final SFacebookProxy sFacebookProxy, final FbLoginCallBack fbLoginCallBack) {
        if (sFacebookProxy == null) {
            PL.i(TAG, "Facebook Proxy為null");
            return;
        }
        SFacebookProxy.FbLoginCallBack fbLoginCallBack1 = new SFacebookProxy.FbLoginCallBack() {
            @Override
            public void onCancel() {
                PL.d(TAG, "sFbLogin cancel");
                if(fbLoginCallBack != null) {
                    fbLoginCallBack.loginSuccess(null);
                }
            }

            @Override
            public void onError(String message) {
                PL.d(TAG, "sFbLogin error: " + message);
                String fbThirdId = FbSp.getFbId(activity);
                if(!TextUtils.isEmpty(fbThirdId)) {
                    PL.i(TAG, "Facebook登入使用緩存登入");
                    String businessId = FbSp.getAppsBusinessId(activity);
                    String tokenBusiness = FbSp.getTokenForBusiness(activity);
                    String gender = FbSp.getFbGender(activity);
                    String birthday = FbSp.getFbBirthday(activity);
                    String name = FbSp.getFbName(activity);
                    String fbId = FbResUtil.findStringByName(activity,"facebook_app_id");
                    String picUrl = ImageRequest.getProfilePictureUri(fbId, 300, 300).toString();
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    String token = "";
                    if(accessToken != null) {
                        token = accessToken.getToken();
                    }
                    if(TextUtils.isEmpty(businessId)) {
                        businessId = fbThirdId + "_" + fbId;
                    }
                    PL.i(TAG, "Facebook ThirdId: " + fbThirdId);
                    PL.i(TAG, "Facebook businessId: " + businessId);
                    PL.i(TAG, "Facebook tokenBusiness: " + tokenBusiness);
                    PL.i(TAG, "Facebook gender: " + gender);
                    PL.i(TAG, "Facebook birthday: " + birthday);
                    PL.i(TAG, "Facebook name: " + name);
                    PL.i(TAG, "Facebook picUrl: " + picUrl);
                    PL.i(TAG, "Facebook token: " + token);
                    if(fbLoginCallBack != null) {
                        faceBookUser = new FaceBookUser();
                        faceBookUser.setUserFbId(fbThirdId);
                        faceBookUser.setBusinessId(businessId);
                        faceBookUser.setName(name);
                        faceBookUser.setGender(gender);
                        faceBookUser.setBirthday(birthday);
                        if(!TextUtils.isEmpty(picUrl)) {
                            faceBookUser.setPictureUri(Uri.parse(picUrl));
                        }
                        faceBookUser.setAccessTokenString(token);
                        fbLoginCallBack.loginSuccess(faceBookUser);
                    } else {
                        PL.i(TAG, "Facebook 登入回調為空");
                    }
                } else { //没有本地记录
                    if (fbLoginCallBack != null) {
                        fbLoginCallBack.loginSuccess(null);
                    }
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
    private void fbThirdLogin(final String fbScopeId, String fbApps, String fbTokenBusiness) {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String accessTokenString = "";
        if (accessToken != null){
            accessTokenString = accessToken.getToken();
        }
        ThirdLoginRegRequestTask cmd = new ThirdLoginRegRequestTask(getActivity(),
                fbScopeId,
                fbApps,
                fbTokenBusiness,
                accessTokenString,
                GSRequestMethod.GSRequestType.GAMESWORD);
        cmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        cmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {

                    if (sLoginResponse.isRequestSuccess()){

                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_FB);
                        return;
                    } else if ("9000".equals(sLoginResponse.getCode())) { //需要验证手机
                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                        if (isAutoLogin) {
                            showMainLoginView();
                        }
                        showPhoneVerifyView(SLoginType.LOGIN_TYPE_FB, fbScopeId);
                        return;
                    } else {
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

            @Override
            public void cancel() {
                showMainLoginView();
            }
        });
        cmd.excute(SLoginResponse.class);
    }

    private void registerAccout(Activity activity, final String account, final String password, String areaCode, String phone, String vfcode, String email){
        AccountRegisterRequestTask accountRegisterCmd = new AccountRegisterRequestTask(getActivity(), account, password, areaCode, phone, vfcode, email);
        accountRegisterCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        accountRegisterCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {
                        ToastUtils.toast(getActivity(), R.string.py_register_success);

                        GamaUtil.saveAccount(getContext(),account);
                        GamaUtil.savePassword(getContext(),password);
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_GAMESWORD);

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
        PhoneVfcodeRequestTask task = new PhoneVfcodeRequestTask(getActivity(), email, interfaceName);
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


    private void startAutoLogin(final Activity activity, final String registPlatform, final String account, final String password) {
        isAutoLogin = true;

        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GAMESWORD, registPlatform)) {

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
            } else if (registPlatform.equals(SLoginType.LOGIN_TYPE_TWITTER)){
                autoLoginTips = "Twitter" + autoLoginTips;
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

                            if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GAMESWORD, registPlatform)) {//免注册或者平台用户自动登录
//                                autoLogin22(mActivity, account, password);

                                starpyAccountLogin(activity,account,password, "", true);

                            }else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, registPlatform)){//fb自动的登录
                                /*String fbScopeId = FbSp.getFbId(activity);
                                String fbApps = FbSp.getAppsBusinessId(activity);
                                if(TextUtils.isEmpty(fbApps)) {
                                    fbApps = fbScopeId + "_" + FbResUtil.findStringByName(activity,"facebook_app_id");
                                }
                                fbThirdLogin(fbScopeId, fbApps,FbSp.getTokenForBusiness(activity));*/
                                fbLogin(mActivity);

                            } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, registPlatform)) {//Google登录
                                googleLogin(activity);
//                                ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
//                                thirdLoginRegRequestBean.setThirdPlatId(GamaUtil.getGoogleId(activity));
//                                thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GOOGLE);
//                                thirdLoginRegRequestBean.setGoogleClientId(ResConfig.getGoogleClientId(activity));
//                                thirdLoginRegRequestBean.setGoogleIdToken(GamaUtil.getGoogleIdToken(activity));
//                                thirdPlatLogin(activity, thirdLoginRegRequestBean);
                            } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_TWITTER, registPlatform)) {//Google登录

                                ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                                thirdLoginRegRequestBean.setThirdPlatId(GamaUtil.getTwitterId(activity));
                                thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_TWITTER);
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

    /**
     * 顯示登入頁面,自动登录状态重置为false
     */
    private void showLoginView() {
        isAutoLogin = false;
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

    private void showPhoneVerifyView(String loginType, String thirdId) {
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

    @Override
    public void findPwd(Activity activity, String account, String areaCode, String phoneOrEmail) {
        this.mActivity = activity;
        FindPwdRequestTask findPwdRequestTask = new FindPwdRequestTask(getActivity(), account, areaCode, phoneOrEmail);
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

            @Override
            public void cancel() {

            }
        });
        findPwdRequestTask.excute(SLoginResponse.class);
    }

    @Override
    public void accountBind(final Activity activity, final String account, final String pwd, final String areaCode, final String phone, final String vfcode, int bindType) {
        this.mActivity = activity;
        if (bindType == SLoginType.bind_unique){
            String uniqueId = GamaUtil.getCustomizedUniqueId1AndroidId1Adid(activity);
            if(TextUtils.isEmpty(uniqueId)){
                PL.d("thirdPlatId:" + uniqueId);
                return;
            }
            ThirdAccountBindRequestTaskV2 bindRequestTask = new ThirdAccountBindRequestTaskV2(getActivity(),
                    SLoginType.LOGIN_TYPE_UNIQUE,
                    account,
                    pwd,
                    areaCode,
                    phone,
                    vfcode,
                    uniqueId,
                    GSRequestMethod.GSRequestType.GAMESWORD);
            sAccountBindV2(bindRequestTask);

        } else if (bindType == SLoginType.bind_fb){
            sFbLogin(activity, sFacebookProxy, new FbLoginCallBack() {
                @Override
                public void loginSuccess(FaceBookUser user) {
                    ThirdAccountBindRequestTaskV2 bindRequestTask = new ThirdAccountBindRequestTaskV2(getActivity(),
                            account,
                            pwd,
                            areaCode,
                            phone,
                            vfcode,
                            user.getUserFbId(),
                            user.getBusinessId(),
                            user.getAccessTokenString(),
                            "",
                            GSRequestMethod.GSRequestType.GAMESWORD);
                    sAccountBindV2(bindRequestTask);
                }
            });

        } else  if (bindType == SLoginType.bind_google){//Google绑定
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
                                GSRequestMethod.GSRequestType.GAMESWORD);
                        sAccountBindV2(bindGoogleRequestTask);
                    }
                }

                @Override
                public void failure() {
                    ToastUtils.toast(getActivity(),"Google sign in error");
                    PL.i("google sign in failure");
                }
            });
        } else if(bindType == SLoginType.bind_twitter) {
            if(twitterLogin != null) {
                twitterLogin.startLogin(new GamaTwitterLogin.TwitterLoginCallBack() {
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
                                    GSRequestMethod.GSRequestType.GAMESWORD);
                            sAccountBindV2(bindGoogleRequestTask);
                        }
                    }

                    @Override
                    public void failure(String msg) {

                    }
                });
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
        if(areaBeanList == null || areaBeanList.length < 1) {
            GamaAreaInfoRequestTask task = new GamaAreaInfoRequestTask(getContext());
            task.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
            task.setReqCallBack(new ISReqCallBack() {
                @Override
                public void success(Object o, String rawResult) {
                    try {
                        if (TextUtils.isEmpty(rawResult)) {
                            areaJson = FileUtil.readAssetsTxtFile(getContext(), "gamesword/areaInfo");
                        } else {
                            JSONArray areaArray = new JSONArray(rawResult);
                            areaJson = areaArray.toString();
                        }

                        Gson gson = new Gson();
                        areaBeanList = gson.fromJson(areaJson, GamaAreaInfoBean[].class);
                        showAreaDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
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
            task.excute();
        } else {
            showAreaDialog();
        }
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
    public void starpyAccountLogin(Activity activity, String account, String pwd, String vfcode, boolean isSaveAccount) {
        this.mActivity = activity;
        login(activity, account, pwd, vfcode, isSaveAccount);
    }

    private void login(final Activity activity, final String account, final String password, String vfcode, final boolean isSaveAccount) {

        AccountLoginRequestTask accountLoginCmd = new AccountLoginRequestTask(getActivity(), account, password, vfcode);
        accountLoginCmd.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(),"Loading..."));
        accountLoginCmd.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null){
                    if (sLoginResponse.isRequestSuccess()) {
                        GamaUtil.saveAccount(getContext(),account);
                        if(isSaveAccount) {
                            GamaUtil.savePassword(getContext(), password);
                        } else {
                            GamaUtil.savePassword(getContext(), "");
                        }
                        handleRegisteOrLoginSuccess(sLoginResponse,rawResult, SLoginType.LOGIN_TYPE_GAMESWORD);
                    }else{
                        ToastUtils.toast(getActivity(),sLoginResponse.getMessage());
                        if(isAutoLogin) {
                            showLoginView();
                        } else {
                            refreshVfCode();
                        }
                    }
                }else{
                    ToastUtils.toast(getActivity(),R.string.py_error_occur);
                    if(isAutoLogin) {
                        showLoginView();
                    } else {
                        refreshVfCode();
                    }
                }
            }

            @Override
            public void timeout(String code) {
                if(isAutoLogin) {
                    showLoginView();
                } else {
                    refreshVfCode();
                }
            }

            @Override
            public void noData() {
                if(isAutoLogin) {
                    showLoginView();
                } else {
                    refreshVfCode();
                }
            }

            @Override
            public void cancel() {
                if(isAutoLogin) {
                    showLoginView();
                } else {
                    refreshVfCode();
                }
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
                        GamaUtil.setAccountLinked(activity);
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
    public int getRemainTimeSeconds() {
        return resetTime;
    }

    private boolean checkIsMacLoginLimit(Activity activity, final SLoginResponse sLoginResponse, final String rawResult) {
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
                    handleRegisteOrLoginSuccess(sLoginResponse, rawResult, SLoginType.LOGIN_TYPE_MAC);
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

}

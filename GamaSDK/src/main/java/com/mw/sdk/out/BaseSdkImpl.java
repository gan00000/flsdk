package com.mw.sdk.out;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;

import com.core.base.ObjFactory;
import com.core.base.utils.AppUtil;
import com.core.base.utils.PL;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.SignatureUtil;
import com.core.base.utils.ToastUtils;
import com.gama.pay.IPay;
import com.gama.pay.IPayCallBack;
import com.gama.pay.IPayFactory;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.gp.bean.res.BasePayBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.bean.SLoginType;
import com.mw.base.bean.SPayType;
import com.mw.base.cfg.ConfigRequest;
import com.mw.base.cfg.ResConfig;
import com.mw.base.utils.Localization;
import com.mw.base.utils.SdkUtil;
import com.mw.base.widget.SWebView;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.R;
import com.mw.sdk.SBaseDialog;
import com.mw.sdk.SWebViewDialog;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.constant.RequestCode;
import com.mw.sdk.login.DialogLoginImpl;
import com.mw.sdk.login.ILogin;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.widget.v2.AccountBindPhoneLayout;
import com.mw.sdk.social.share.ShareUtil;
import com.thirdlib.facebook.SFacebookProxy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


public class BaseSdkImpl implements IMWSDK {

    private static final String TAG = BaseSdkImpl.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 401;
    private ILogin iLogin;

    private long firstClickTime;

    private static boolean isInitSdk = false;

    private SFacebookProxy sFacebookProxy;

    protected SWebViewDialog csSWebViewDialog;
    protected SWebViewDialog otherPayWebViewDialog;

    protected SBaseDialog bindPhoneDialog;

    private IPay iPay;
    protected IPayListener iPayListener;
    private Activity activity;

    private ReviewInfo reviewInfo;

    public BaseSdkImpl() {
        iLogin = ObjFactory.create(DialogLoginImpl.class);
        PL.i("BaseSdkImpl 构造函数");
    }

//    @Deprecated
//    @Override
//    public void initSDK(final Activity activity) {
//        initSDK(activity, SGameLanguage.zh_TW);
//    }

    @Override
    public void initSDK(final Activity activity, final SGameLanguage gameLanguage) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("sdk initSDK");
                Localization.gameLanguage(activity, gameLanguage);
                //清除上一次登录成功的返回值
                //GamaUtil.saveSdkLoginData(activity, "");
                //重置用户登入时长
//                GamaUtil.resetOnlineTimeInfo(activity);
                //获取Google 广告ID
                SdkEventLogger.registerGoogleAdId(activity);
                //Gama平台安装上报
//                StarEventLogger.reportInstallActivation(activity.getApplicationContext());
//                try {
//                    Fresco.initialize(activity.getApplicationContext());//初始化fb Fresco库
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                //广告
                SdkEventLogger.activateApp(activity);

                //时间打点开始
//                LogTimer.getInstance().start(activity);

//                setGameLanguage(activity,SGameLanguage.zh_TW);

//                ConfigRequest.requestBaseCfg(activity.getApplicationContext());//下载配置文件
//                ConfigRequest.requestTermsCfg(activity.getApplicationContext());//下载服务条款
                // 1.初始化fb sdk
//                SFacebookProxy.initFbSdk(activity.getApplicationContext());
                sFacebookProxy = new SFacebookProxy(activity.getApplicationContext());
                isInitSdk = true;
            }
        });
    }

    /*
        语言默认繁体zh-TW，用来设置UI界面语言，提示等
        需要在其他所有方法之前调用
    * */

    @Override
    public void setGameLanguage(final Activity activity, final SGameLanguage gameLanguage) {
        PL.i("IGama setGameLanguage:" + gameLanguage);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Localization.gameLanguage(activity, gameLanguage);
            }
        });

    }

    @Override
    public void registerRoleInfo(final Activity activity, final String roleId, final String roleName, final String roleLevel, final String vipLevel, final String severCode, final String serverName) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama registerRoleInfo");
                PL.i("roleId:" + roleId + ",roleName:" + roleName + ",roleLevel:" + roleLevel + ",vipLevel:" + vipLevel + ",severCode:" + severCode + ",serverName:" + serverName);
                SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
                if (iPay != null){
                    iPay.startQueryPurchase(activity.getApplicationContext());
                }
            }
        });
    }

    @Override
    public void openCs(Activity activity) {
//        openWebPage(activity,GamaOpenWebType.CUSTOM_URL,activity.getString(R.string.emm_service_url));
    }

    @Override
    public void onCreate(final Activity activity) {
        PL.i("sdk onCreate");
        PL.i("the jar version:" + BuildConfig.JAR_VERSION);//打印版本号

        PL.i("fb keyhash:" + SignatureUtil.getHashKey(activity, activity.getPackageName()));
        PL.i("google sha1:" + SignatureUtil.getSignatureSHA1WithColon(activity, activity.getPackageName()));
        this.activity = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                activity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        PL.d("activity onSystemUiVisibilityChange");
                        AppUtil.hideActivityBottomBar(activity);
                    }
                });

                ConfigRequest.requestBaseCfg(activity.getApplicationContext());//加载配置
                ConfigRequest.requestAreaCodeInfo(activity.getApplicationContext());

                if (!isInitSdk) {
                    initSDK(activity, SGameLanguage.zh_TW);
                }
                if (iLogin != null) {
                    iLogin.onCreate(activity);
                }
                iPay = IPayFactory.create(IPayFactory.PAY_GOOGLE);
                iPay.onCreate(activity);
            }
        });

    }

    @Override
    public void onResume(final Activity activity) {
        this.activity = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama onResume");
                if (iLogin != null) {
                    iLogin.onResume(activity);
                }
                if (iPay != null){
                    iPay.onResume(activity);
                }
                //ads
                SdkEventLogger.onResume(activity);

//                Android 13 中引入了用于显示通知的新运行时权限。该项引入会影响在 Android 13 或更高版本上使用 FCM 通知的所有应用。
//                默认情况下，FCM SDK（23.0.6 或更高版本）中包含清单中定义的 POST_NOTIFICATIONS 权限。不过，您的应用还需要通过常量 android.permission.POST_NOTIFICATIONS 请求此权限的运行时版本。
//                在用户授予此权限之前，您的应用将无法显示通知。
                if (Build.VERSION.SDK_INT >= 33) {
                    //todo 现在还没13系统设备，无法测试先注释
//                    PermissionUtil.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, RequestCode.RequestCode_Permission_POST_NOTIFICATIONS);
                }
            }
        });
    }

    @Override
    public void onActivityResult(final Activity activity, final int requestCode, final int resultCode, final Intent data) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama onActivityResult");
                if (iLogin != null) {
                    iLogin.onActivityResult(activity, requestCode, resultCode, data);
                }
                if (iPay != null){
                    iPay.onActivityResult(activity,requestCode,resultCode,data);
                }
                if (otherPayWebViewDialog != null) {
                    otherPayWebViewDialog.onActivityResult(activity, requestCode, resultCode, data);
                }
                if (csSWebViewDialog != null){
                    csSWebViewDialog.onActivityResult(activity, requestCode, resultCode, data);
                }
                if (sFacebookProxy != null && requestCode == SFacebookProxy.Request_Code_Share_Url) {
                    sFacebookProxy.onActivityResult(activity, requestCode, resultCode, data);
                }
                ShareUtil.onActivityResult(activity, requestCode, resultCode, data);
            }
        });
    }

    @Override
    public void onPause(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama onPause");
                if (iLogin != null) {
                    iLogin.onPause(activity);
                }

                if (iPay != null) {
                    iPay.onPause(activity);
                }
                //ads
                SdkEventLogger.onPause(activity);

            }
        });
    }

    @Override
    public void onStop(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama onStop");
                if (iLogin != null) {
                    iLogin.onStop(activity);
                }
                if (iPay != null) {
                    iPay.onStop(activity);
                }
            }
        });
    }

    @Override
    public void onDestroy(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("sdk onDestroy");
                if (iLogin != null) {
                    iLogin.onDestroy(activity);
                }
                if (iPay != null){
                    iPay.onDestroy(activity);
                }
                if (sFacebookProxy != null) {
                    sFacebookProxy.onDestroy(activity);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama onRequestPermissionsResult");
            }
        });
    }

    @Override
    public void onWindowFocusChanged(final Activity activity, final boolean hasFocus) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama onWindowFocusChanged: hasFocus -- " + hasFocus);
                if (hasFocus) {
                    AppUtil.hideActivityBottomBar(activity);
                }
            }
        });
    }

//    @Override
//    public void openWebPage(final Activity activity, final GamaOpenWebType type, final String url, final ISdkCallBack callBack) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (GamaOpenWebType.CUSTOM_URL == type){
//                    csSWebViewDialog =  GamaWebPageHelper.openWebPage(activity, type, url, callBack);
//                }else{
//
//                    GamaWebPageHelper.openWebPage(activity, type, url, callBack);
//                }
//            }
//        });
//    }

//    @Override
//    public void openPlatform(final Activity activity) {
//
//    }

    public void share(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
        this.share(activity, ThirdPartyType.FACEBOOK, hashTag, message, shareLinkUrl,"",iSdkCallBack);
    }

    public void share(final Activity activity, final ThirdPartyType type, String hashTag, final String message, final String shareLinkUrl, final String picPath, final ISdkCallBack iSdkCallBack) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("share type : " + type.name() + "  message : " + message + "  shareLinkUrl : " + shareLinkUrl + "  picPath : " + picPath);
                switch (type) {
                    case FACEBOOK:
                        if (!TextUtils.isEmpty(shareLinkUrl)) {
                            String newShareLinkUrl = shareLinkUrl;
                            if (sFacebookProxy != null) {
                                SFacebookProxy.FbShareCallBack fbShareCallBack = new SFacebookProxy.FbShareCallBack() {
                                    @Override
                                    public void onCancel() {
                                        if (iSdkCallBack != null) {
                                            iSdkCallBack.failure();
                                        }
                                    }

                                    @Override
                                    public void onError(String message) {
                                        if (iSdkCallBack != null) {
                                            iSdkCallBack.failure();
                                        }
                                    }

                                    @Override
                                    public void onSuccess() {
                                        if (iSdkCallBack != null) {
                                            iSdkCallBack.success();
                                        }
                                    }
                                };

//                                if (SStringUtil.isNotEmpty(SdkUtil.getServerCode(activity)) && SStringUtil.isNotEmpty(SdkUtil.getRoleId(activity))) {
//                                    try {
//                                        if (newShareLinkUrl.contains("?")) {//userId+||S||+serverCode+||S||+roleId
//                                            newShareLinkUrl = newShareLinkUrl + "&campaign=" + URLEncoder.encode(SdkUtil.getUid(activity) + "||S||" + SdkUtil.getServerCode(activity) + "||S||" + SdkUtil.getRoleId(activity), "UTF-8");
//                                        } else {
//                                            newShareLinkUrl = newShareLinkUrl + "?campaign=" + URLEncoder.encode(SdkUtil.getUid(activity) + "||S||" + SdkUtil.getServerCode(activity) + "||S||" + SdkUtil.getRoleId(activity), "UTF-8");
//                                        }
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
                                sFacebookProxy.fbShare(activity, hashTag, message, newShareLinkUrl, fbShareCallBack );
                            } else {
                                if (iSdkCallBack != null) {
                                    iSdkCallBack.failure();
                                }
                            }
                        } else if (!TextUtils.isEmpty(picPath)) {
                            if (sFacebookProxy != null) {
                                sFacebookProxy.shareLocalPhoto(activity, new SFacebookProxy.FbShareCallBack() {
                                    @Override
                                    public void onCancel() {
                                        if (iSdkCallBack != null) {
                                            iSdkCallBack.failure();
                                        }
                                    }

                                    @Override
                                    public void onError(String message) {
                                        if (iSdkCallBack != null) {
                                            iSdkCallBack.failure();
                                        }
                                    }

                                    @Override
                                    public void onSuccess() {
                                        if (iSdkCallBack != null) {
                                            iSdkCallBack.success();
                                        }
                                    }
                                }, picPath);
                            } else {
                                if (iSdkCallBack != null) {
                                    iSdkCallBack.failure();
                                }
                            }
                        } else {
                            if (iSdkCallBack != null) {
                                iSdkCallBack.failure();
                            }
                        }
                        break;

                    case FACEBOOK_MESSENGER:
                        if (sFacebookProxy != null) {
                            sFacebookProxy.shareToMessenger(activity, picPath, new SFacebookProxy.FbShareCallBack() {
                                @Override
                                public void onCancel() {
                                    if (iSdkCallBack != null) {
                                        iSdkCallBack.failure();
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    if (iSdkCallBack != null) {
                                        iSdkCallBack.failure();
                                    }
                                }

                                @Override
                                public void onSuccess() {
                                    if (iSdkCallBack != null) {
                                        iSdkCallBack.success();
                                    }
                                }
                            });
                        } else {
                            if (iSdkCallBack != null) {
                                iSdkCallBack.failure();
                            }
                        }
                        break;

                    case LINE:
                    case WHATSAPP:
                    case TWITTER:
                        ShareUtil.share(activity, type, message, shareLinkUrl, picPath, iSdkCallBack);
                        break;
                }
            }
        });
    }

    @Override
    public boolean canShareWithType(Activity activity, ThirdPartyType type) {
        Log.i(TAG, "type : " + type.name());
        return ShareUtil.shouldShareWithType(activity, type);
    }

    @Override
    public void trackEvent(Activity activity, EventConstant.EventName eventName) {
        this.trackEvent(activity,eventName, null);
    }

    @Override
    public void trackEvent(final Activity activity, EventConstant.EventName eventName, final Map<String, Object> map) {
        Log.i(TAG, "trackEvent...");
        if (eventName == null){
            Log.i(TAG, "trackEvent eventName is null");
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SdkEventLogger.trackingWithEventName(activity, eventName.name(), map, EventConstant.AdType.AdTypeAllChannel);
            }
        });
    }

  /*  @Override
    public void trackCreateRoleEvent(final Activity activity, String roleId, String roleName) {
        PL.i("trackCreateRoleEvent roleId:" + roleId + ",roleName:" + roleName);
        if (SStringUtil.isEmpty(roleId)){
            return;
        }
        final HashMap<String, Object> map = new HashMap<>();
        map.put(EventConstant.ParameterName.ROLE_ID, roleId);
        map.put(EventConstant.ParameterName.ROLE_NAME, roleName);
//        map.put(SdkAdsConstant.GAMA_EVENT_ROLE_LEVEL, roleLevel);
//        map.put(SdkAdsConstant.GAMA_EVENT_ROLE_VIP_LEVEL, vipLevel);
//        map.put(SdkAdsConstant.GAMA_EVENT_SERVERCODE, severCode);
//        map.put(SdkAdsConstant.GAMA_EVENT_SERVERNAME, serverName);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SdkEventLogger.trackingRoleInfo(activity, map);
            }
        });

    }
*/

    @Override
    public void login(final Activity activity, final ILoginCallBack iLoginCallBack) {
        PL.i("sdk login");
        this.activity = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (iLogin != null) {
                    //清除上一次登录成功的返回值
//                    GamaUtil.saveSdkLoginData(activity, "");

                    iLogin.initFacebookPro(activity, sFacebookProxy);
                    iLogin.startLogin(activity, iLoginCallBack);
                }
            }
        });


    }

    @Override
    public void logout(Activity activity, ISdkCallBack iSdkCallBack) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String previousLoginType = SdkUtil.getPreviousLoginType(activity);

                if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, previousLoginType)) {//google

                    if (iLogin != null && iLogin.getGoogleSignIn() != null) {
                        iLogin.getGoogleSignIn().signOut();
                    }

                } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_LINE, previousLoginType)) {//line
                } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, previousLoginType)) {//fb
                    if (sFacebookProxy != null) {
                        sFacebookProxy.fbLogout(activity);
                    }
                }
                if (iSdkCallBack != null){
                    iSdkCallBack.success();
                }
            }
        });

    }

    @Override
    public void pay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra, String roleId,String roleName,String roleLevel, String vipLevel, String severCode,String serverName, IPayListener listener) {
        PL.i("sdk pay payType:" + payType.toString() + " ,cpOrderId:" + cpOrderId + ",productId:" + productId + ",extra:" + extra);
        if ((System.currentTimeMillis() - firstClickTime) < 1000) {//防止连续点击
            PL.i("点击过快，无效");
            return;
        }
        iPayListener = listener;
        firstClickTime = System.currentTimeMillis();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
                startPay(activity, payType, cpOrderId, productId, extra, listener);
            }
        });
    }

    private String productId;
    private String cpOrderId;
    private String extra;
    protected void startPay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra, IPayListener listener) {
        this.activity = activity;
        this.productId = productId;
        this.cpOrderId = cpOrderId;
        this.extra = extra;

        GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean = new GooglePayCreateOrderIdReqBean(activity);
        googlePayCreateOrderIdReqBean.setCpOrderId(cpOrderId);
        googlePayCreateOrderIdReqBean.setProductId(productId);
        googlePayCreateOrderIdReqBean.setExtra(extra);

        if (payType == SPayType.GOOGLE) {
            doGooglePay(activity, googlePayCreateOrderIdReqBean);

        } else if(payType == SPayType.WEB) {
            doWebPay(activity,googlePayCreateOrderIdReqBean);
        } else {//默认Google储值
            PL.i("不支持當前類型： " + payType.name());
        }
    }


    private void doGooglePay(Activity activity, GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean) {
        //            googlePay(activity, cpOrderId, productId, extra);
//        Intent i = new Intent(activity, GooglePayActivity2.class);
//        i.putExtra(GooglePayActivity2.GooglePayReqBean_Extra_Key, googlePayCreateOrderIdReqBean);
//        activity.startActivityForResult(i, GooglePayActivity2.GooglePayReqeustCode);

        //设置Google储值的回调
        iPay.setIPayCallBack(new IPayCallBack() {
            @Override
            public void success(BasePayBean basePayBean) {
                PL.i("IPayCallBack success");
                ToastUtils.toast(activity,R.string.text_finish_pay);
                if (otherPayWebViewDialog != null && otherPayWebViewDialog.isShowing()){
                    otherPayWebViewDialog.dismiss();
                }

                SdkEventLogger.trackinPayEvent(activity, basePayBean);

                if (iPayListener != null) {
                    iPayListener.onPaySuccess(basePayBean.getProductId(),basePayBean.getCpOrderId());
                }
            }

            @Override
            public void fail(BasePayBean basePayBean) {
                if (iPayListener != null) {
                    iPayListener.onPayFail();
                }
            }
        });

        iPay.startPay(activity, googlePayCreateOrderIdReqBean);
    }

    public void requestStoreReview(Activity activity, ICompleteListener iCompleteListener){

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ReviewManager manager = ReviewManagerFactory.create(activity);

//                if (BaseSdkImpl.this.reviewInfo != null){
//
//                    Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
//                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            //https://developer.android.com/guide/playcore/in-app-review/kotlin-java?hl=zh-cn
//                            // 如果在应用内评价流程中出现错误，请勿通知用户或更改应用的正常用户流。调用 onComplete 后，继续执行应用的正常用户流。
//                            // The flow has finished. The API does not indicate whether the user
//                            // reviewed or not, or even whether the review dialog was shown. Thus, no
//                            // matter the result, we continue our app flow.
//                            if (iCompleteListener != null) {
//                                iCompleteListener.onComplete();
//                            }
//                        }
//                    });
//
//                    return;
//                }

                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // We can get the ReviewInfo object
                        PL.i("task.isSuccessful We can get the ReviewInfo object");
                        ReviewInfo reviewInfo = task.getResult();
                        BaseSdkImpl.this.reviewInfo = reviewInfo;
                        Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
                        flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //https://developer.android.com/guide/playcore/in-app-review/kotlin-java?hl=zh-cn
                                // 如果在应用内评价流程中出现错误，请勿通知用户或更改应用的正常用户流。调用 onComplete 后，继续执行应用的正常用户流。
                                // The flow has finished. The API does not indicate whether the user
                                // reviewed or not, or even whether the review dialog was shown. Thus, no
                                // matter the result, we continue our app flow.
                                if (iCompleteListener != null) {
                                    iCompleteListener.onComplete();
                                }
                            }
                        });

                    } else {
                        // There was some problem, log or handle the error code.
//                @ReviewErrorCode
                        PL.i("requestReviewFlow There was some problem");
//                        int reviewErrorCode = task.getException().getErrorCode();
                        if (iCompleteListener != null) {
                            iCompleteListener.onComplete();
                        }
                    }
                });
            }
        });

    }


    @Override
    public void showBindPhoneView(Activity activity, ILoginCallBack iLoginCallBack) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (SdkUtil.isVersion2(activity)) {
                    if (bindPhoneDialog != null){
                        bindPhoneDialog.dismiss();
                    }
                    bindPhoneDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                    AccountBindPhoneLayout accountBindPhoneLayout = new AccountBindPhoneLayout(activity);
                    accountBindPhoneLayout.setsBaseDialog(bindPhoneDialog);
                    accountBindPhoneLayout.setiLoginCallBack(iLoginCallBack);
                    bindPhoneDialog.setContentView(accountBindPhoneLayout);
                    bindPhoneDialog.show();
                }
            }
        });

    }

    @SuppressLint("JavascriptInterface")
    private void doWebPay(Activity activity, GooglePayCreateOrderIdReqBean bean) {

        String payThirdUrl = ResConfig.getPayPreferredUrl(activity) + "api/web/payment.page";
        bean.setCompleteUrl(payThirdUrl);

        String webUrl = bean.createPreRequestUrl();

        otherPayWebViewDialog = new SWebViewDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        otherPayWebViewDialog.setWebUrl(webUrl);
        otherPayWebViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                if (iPayListener != null) {
//                    iPayListener.onPayFinish(new Bundle());
//                } else {
//                    PL.i(TAG, "a null occour");
//                }
            }
        });
        otherPayWebViewDialog.setsWebDialogCallback(new SWebViewDialog.SWebDialogCallback() {
            @Override
            public void createFinish(SWebViewDialog sWebViewDialog, SWebView sWebView) {
                sWebView.addJavascriptInterface(BaseSdkImpl.this,"SdkObj");
            }
        });
        otherPayWebViewDialog.show();
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void googlePay(String productId)
    {
        PL.i("js googlePay productId=" + productId);
        if (this.activity != null){
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseSdkImpl.this.startPay(BaseSdkImpl.this.activity,SPayType.GOOGLE,BaseSdkImpl.this.cpOrderId,BaseSdkImpl.this.productId,BaseSdkImpl.this.extra,BaseSdkImpl.this.iPayListener);
                }
            });
        }
    }

    /**
     * 充值通知，提供给网页使用
     * @param success  是否成功
     * @param productId  商品id
     */
    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void onPayFinish(boolean success,String productId)
    {
        PL.i("js onPayFinish productId=" + productId);
        if (this.activity != null){
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (success){
                        if (iPayListener != null) {
                            iPayListener.onPaySuccess(productId, BaseSdkImpl.this.cpOrderId);
                        }
                    }else{
                        if (iPayListener != null) {
                            iPayListener.onPaySuccess(productId, BaseSdkImpl.this.cpOrderId);
                        }
                    }

                    if (otherPayWebViewDialog != null) {
                        otherPayWebViewDialog.finish();
                    }
                }
            });
        }
    }
}

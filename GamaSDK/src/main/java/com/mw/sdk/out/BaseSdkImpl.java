package com.mw.sdk.out;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.core.base.BaseWebViewClient;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.SignatureUtil;
import com.core.base.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.gson.Gson;
import com.mw.base.bean.SPayType;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.MWWebPayActivity;
import com.mw.sdk.R;
import com.mw.sdk.act.ActExpoView;
import com.mw.sdk.act.FloatContentView;
import com.mw.sdk.act.FloatingManager;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.api.ConfigRequest;
import com.mw.sdk.api.Request;
import com.mw.sdk.bean.PhoneInfo;
import com.mw.sdk.bean.SGameBaseRequestBean;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.res.ActDataModel;
import com.mw.sdk.bean.res.BasePayBean;
import com.mw.sdk.bean.res.ConfigBean;
import com.mw.sdk.bean.res.FloatConfigData;
import com.mw.sdk.bean.res.FloatSwitchRes;
import com.mw.sdk.bean.res.MenuData;
import com.mw.sdk.bean.res.ToggleResult;
import com.mw.sdk.callback.FloatButtionClickCallback;
import com.mw.sdk.callback.FloatCallback;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.constant.RequestCode;
import com.mw.sdk.constant.ResultCode;
import com.mw.sdk.constant.SGameLanguage;
import com.mw.sdk.login.DialogLoginImpl;
import com.mw.sdk.login.ILogin;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.v2.AccountBindPhoneLayout;
import com.mw.sdk.login.widget.v2.SelectPayChannelLayout;
import com.mw.sdk.login.widget.v2.ThirdPlatBindAccountLayoutV2;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.IPayFactory;
import com.mw.sdk.utils.DataManager;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.utils.ShareUtil;
import com.mw.sdk.widget.SBaseDialog;
import com.mw.sdk.widget.SWebView;
import com.mw.sdk.widget.SWebViewDialog;
import com.mw.sdk.widget.SWebViewLayout;
import com.thirdlib.adjust.AdjustHelper;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.huawei.HuaweiPayImpl;

import java.util.ArrayList;
import java.util.Map;


public class BaseSdkImpl implements IMWSDK {

    private static final String TAG = BaseSdkImpl.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 401;
    protected ILogin iLogin;

    private long firstClickTime;

    private static boolean isInitSdk = false;

    protected SFacebookProxy sFacebookProxy;

    protected SWebViewDialog sWebViewDialog;
    protected SWebViewDialog otherPayWebViewDialog;

    protected SBaseDialog commonDialog;

    protected SBaseDialog floatViewDialog;

    protected IPay iPay;
    protected IPay iPayOneStore;
    protected IPayListener iPayListener;
    protected Activity activity;

    protected HuaweiPayImpl huaweiPay;

    protected DataManager dataManager;

    private ReviewInfo reviewInfo;

    private boolean isShowAct_M;
    public BaseSdkImpl() {
//        iLogin = ObjFactory.create(DialogLoginImpl.class);
        PL.i("BaseSdkImpl 构造函数");
    }

    @Override
    public void applicationOnCreate(Application application) {
        PL.i("BaseSdkImpl applicationOnCreate");
        AdjustHelper.init(application);
    }

    //    @Deprecated
//    @Override
//    public void initSDK(final Activity activity) {
//        initSDK(activity, SGameLanguage.zh_TW);
//    }

//    @Override
    public void initSDK(final Activity activity, final SGameLanguage gameLanguage) {

        PL.i("sdk initSDK");
        //获取Google 广告ID
        SdkEventLogger.registerGoogleAdId(activity);

//        Localization.gameLanguage(activity, gameLanguage);
        //清除上一次登录成功的返回值
        //GamaUtil.saveSdkLoginData(activity, "");

        //平台安装上报
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //广告
                SdkEventLogger.activateApp(activity);
                SdkEventLogger.reportInstallActivation(activity.getApplicationContext());
            }
        },200);


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

    /*
        语言默认繁体zh-TW，用来设置UI界面语言，提示等
        需要在其他所有方法之前调用
    * */

//    @Override
//    public void setGameLanguage(final Activity activity, final SGameLanguage gameLanguage) {
//        PL.i("IMWSDK setGameLanguage:" + gameLanguage);
//
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Localization.gameLanguage(activity, gameLanguage);
//            }
//        });
//
//    }

    @Override
    public void registerRoleInfo(final Activity activity, final String roleId, final String roleName, final String roleLevel, final String vipLevel, final String severCode, final String serverName) {

        PL.i("IMWSDK registerRoleInfo");
        PL.i("roleId:" + roleId + ",roleName:" + roleName + ",roleLevel:" + roleLevel + ",vipLevel:" + vipLevel + ",severCode:" + severCode + ",serverName:" + serverName);

        if (SStringUtil.isEmpty(roleId) || SStringUtil.isEmpty(severCode)){
            return;
        }
        SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (iPay != null && SStringUtil.isNotEmpty(roleId) && SStringUtil.isNotEmpty(severCode) && SStringUtil.isNotEmpty(SdkUtil.getUid(activity))){
                    iPay.startQueryPurchase(activity.getApplicationContext());
                }
                //showActViewSwitchRequest(activity);//注释掉，先不用

                Request.requestFloatMenus(activity.getApplicationContext(),null);
            }
        });
    }

    public void checkPreRegData(final Activity activity, ISdkCallBack iSdkCallBack) {

        PL.i("IMWSDK checkPreRegData");
        if (SStringUtil.isEmpty(SdkUtil.getUid(activity)) || SStringUtil.isEmpty(SdkUtil.getRoleId(activity)) || SStringUtil.isEmpty(SdkUtil.getServerCode(activity))){
            PL.i("IMWSDK checkPreRegData role info empty");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (iPay != null){
                    iPay.queryPreRegData(activity.getApplicationContext(), iSdkCallBack);
                }
            }
        });
    }

    @Override
    public void openCs(Activity activity) {
        PL.i("sdk openCs");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Request.openCs(activity, 0);
            }
        });
    }

    @Override
    public void openCs(Activity activity, String roleId, String roleName, String roleLevel, String vipLevel, String severCode, String serverName) {
        PL.i("roleId:" + roleId + ",roleName:" + roleName + ",roleLevel:" + roleLevel + ",vipLevel:" + vipLevel + ",severCode:" + severCode + ",serverName:" + serverName);
        if (SStringUtil.isEmpty(roleId) || SStringUtil.isEmpty(severCode)){
            ToastUtils.toast(activity,"roleId and severCode must not empty");
            return;
        }
        SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
        openCs(activity);
    }

    @Override
    public void onCreate(final Activity activity) {
        PL.i("sdk onCreate");
        PL.i("the aar version info:" + SdkUtil.getSdkInnerVersion(activity) + "_" + BuildConfig.JAR_VERSION);//打印版本号

        PL.i("fb keyhash:" + SignatureUtil.getHashKey(activity, activity.getPackageName()));
        PL.i("google sha1:" + SignatureUtil.getSignatureSHA1WithColon(activity, activity.getPackageName()));
        PL.i("app sha256:" + SignatureUtil.getSignatureSHA256WithColon(activity, activity.getPackageName()));
        this.activity = activity;
        dataManager = DataManager.getInstance();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                onCreate_OnUi(activity);
            }
        });

    }

    protected void onCreate_OnUi(Activity activity) {
        if (!isInitSdk) {
            initSDK(activity, SGameLanguage.zh_TW);
        }

        ConfigRequest.requestBaseCfg(activity.getApplicationContext());//加载配置
        ConfigRequest.requestAreaCodeInfo(activity.getApplicationContext());

        Request.requestFloatConfigData(activity.getApplicationContext(),null);

        if (iLogin != null) {
            iLogin.onCreate(activity);
        }
        iPay = IPayFactory.create(activity);
        iPay.onCreate(activity);

    }

    @Override
    public void onResume(final Activity activity) {
        this.activity = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IMWSDK onResume");
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
            }
        });
    }

    @Override
    public void onActivityResult(final Activity activity, final int requestCode, final int resultCode, final Intent data) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("mw onActivityResult");
                if (iLogin != null) {
                    iLogin.onActivityResult(activity, requestCode, resultCode, data);
                }
                if (iPay != null){
                    iPay.onActivityResult(activity,requestCode,resultCode,data);
                }
                if (otherPayWebViewDialog != null) {
                    otherPayWebViewDialog.onActivityResult(activity, requestCode, resultCode, data);
                }
                if (sWebViewDialog != null){
                    sWebViewDialog.onActivityResult(activity, requestCode, resultCode, data);
                }
                if (sFacebookProxy != null) {
                    sFacebookProxy.onActivityResultForShare(activity, requestCode, resultCode, data);
                }
                ShareUtil.onActivityResult(activity, requestCode, resultCode, data);
                //网页充值，或者网页内Google充值回调
                if (requestCode == RequestCode.RequestCode_Web_Pay && resultCode == ResultCode.ResultCode_Web_Pay){
                    if (data != null){
                        String mw_productId = data.getStringExtra("mw_productId");
                        String mw_cpOrderId = data.getStringExtra("mw_cpOrderId");
                        PL.i("web onPaySuccess:mw_productId=" + mw_productId);
                        PL.i("web onPaySuccess:mw_cpOrderId=" + mw_cpOrderId);
                        if (iPayListener != null) {
                            iPayListener.onPaySuccess(mw_productId,mw_cpOrderId);
                        }
                    }else{
                        PL.i("web onPayFail");
                        if (iPayListener != null) {
                            iPayListener.onPayFail();
                        }
                    }
                }

                if (huaweiPay != null){
                    huaweiPay.onActivityResult(activity, requestCode, resultCode, data);
                }
            }
        });
    }

    @Override
    public void onPause(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("mw onPause");
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
                PL.i("IMWSDK onStop");
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
                PL.i("IMWSDK onRequestPermissionsResult");
            }
        });
    }

    @Override
    public void onWindowFocusChanged(final Activity activity, final boolean hasFocus) {
        PL.i("IMWSDK onWindowFocusChanged: hasFocus -- " + hasFocus);
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (hasFocus) {
//                    AppUtil.hideActivityBottomBar(activity);
//                }
//            }
//        });
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

    public void shareFacebook(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
        this.share(activity, ThirdPartyType.FACEBOOK, hashTag, message, shareLinkUrl,"",iSdkCallBack);
    }

    //line分享内容不能带换行符\n
    public void shareLine(final Activity activity, final String content, final ISdkCallBack iSdkCallBack){
        share(activity,ThirdPartyType.LINE,"",content,"","",iSdkCallBack);
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
                           /* sFacebookProxy.shareToMessenger(activity, picPath, new SFacebookProxy.FbShareCallBack() {
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
                            });*/
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
    public void trackEvent(Activity activity, String eventName) {
        Log.i(TAG, "trackEvent eventName...");
        if (eventName == null){
            Log.i(TAG, "trackEvent eventName is null");
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SdkEventLogger.sendEventToSever(activity, eventName);
                SdkEventLogger.trackingWithEventName(activity, eventName, null, EventConstant.AdType.AdTypeAllChannel);
            }
        });
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
                SdkEventLogger.sendEventToSever(activity, eventName.name());
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

                if (Build.VERSION.SDK_INT >= 33) {
                    PermissionUtil.requestPermission(activity, "android.permission.POST_NOTIFICATIONS",RequestCode.RequestCode_Permission_POST_NOTIFICATIONS);
                }

                if (iLogin == null){
                    iLogin = new DialogLoginImpl(activity);
                }

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
    public void switchLogin(Activity activity, ILoginCallBack iLoginCallBack) {
        logout(activity, null);
        login(activity, iLoginCallBack);
    }

    @Override
    public void logout(Activity activity, ISdkCallBack iSdkCallBack) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                String previousLoginType = SdkUtil.getPreviousLoginType(activity);
//
//                if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, previousLoginType)) {//google
//
//                    if (iLogin != null && iLogin.getGoogleSignIn() != null) {
//                        iLogin.getGoogleSignIn().signOut();
//                    }
//
//                } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_LINE, previousLoginType)) {//line
//                } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, previousLoginType)) {//fb
//                    if (sFacebookProxy != null) {
//                        sFacebookProxy.fbLogout(activity);
//                    }
//                }
                if (dataManager == null){
                    dataManager = DataManager.getInstance();
                }
                dataManager.setLogin(false);
                if (iLogin != null){
                    iLogin.signOut(activity);
                }
                if (iSdkCallBack != null){
                    iSdkCallBack.success();
                }
            }
        });

    }

    @Override
    public void pay(Activity activity, String cpOrderId, String productId, String extra, String roleId, String roleName, String roleLevel, String vipLevel, String severCode, String serverName, IPayListener listener) {
        pay(activity, SPayType.GOOGLE, cpOrderId, productId, extra, roleId, roleName, roleLevel, vipLevel, severCode, serverName, listener);
    }

    @Override
    public void pay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra, String roleId,String roleName,String roleLevel, String vipLevel, String severCode,String serverName, IPayListener listener) {
        PL.i("sdk pay payType:" + payType.toString() + " ,cpOrderId:" + cpOrderId + ",productId:" + productId + ",extra:" + extra);
        if ((System.currentTimeMillis() - firstClickTime) < 3000) {//防止连续点击
            PL.i("点击过快，无效");
            return;
        }
        //iPayListener = listener;
        firstClickTime = System.currentTimeMillis();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (SStringUtil.isEmpty(roleId) || SStringUtil.isEmpty(severCode)){
                    ToastUtils.toast(activity,"roleId and severCode must not empty");
                    return;
                }
                SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
                startPay(activity, payType, cpOrderId, productId, extra, listener);
                trackEvent(activity, EventConstant.EventName.Initiate_Checkout);
            }
        });
    }

//    protected String productId;
//    protected String cpOrderId;
//    protected String extra;
    protected void startPay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra, IPayListener listener) {
        this.iPayListener = listener;
        this.activity = activity;
//        this.productId = productId;
//        this.cpOrderId = cpOrderId;
//        this.extra = extra;

        PayCreateOrderReqBean payCreateOrderReqBean = new PayCreateOrderReqBean(activity);
        payCreateOrderReqBean.setCpOrderId(cpOrderId);
        payCreateOrderReqBean.setProductId(productId);
        payCreateOrderReqBean.setExtra(extra);

        String channel_platform = activity.getResources().getString(R.string.channel_platform);

        if(payType == SPayType.WEB || ChannelPlatform.MEOW.getChannel_platform().equals(channel_platform)) {
            doWebPay(activity, payCreateOrderReqBean);
        } else if(payType == SPayType.HUAWEI || ChannelPlatform.HUAWEI.getChannel_platform().equals(channel_platform)) {
            doHuaweiPay(activity, payCreateOrderReqBean);
        } else if(payType == SPayType.QooApp || ChannelPlatform.QOOAPP.getChannel_platform().equals(channel_platform)) {
            doQooAppPay(activity, payCreateOrderReqBean);
        } else if(payType == SPayType.LUNQI || ChannelPlatform.LUNQI.getChannel_platform().equals(channel_platform)) {
            doLunqiPay(activity, payCreateOrderReqBean);
        } else {//默认Google储值
            checkGoogleOrWebPay(activity, payCreateOrderReqBean);
        }
    }
    protected void doQooAppPay(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {}

    protected void doLunqiPay(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {}

    private void doHuaweiPay(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {

        if (huaweiPay == null){
            huaweiPay = new HuaweiPayImpl(activity);

            huaweiPay.setPayCallBack(new IPayCallBack() {
                @Override
                public void success(BasePayBean basePayBean) {
                    PL.i("huawei IPayCallBack success");
                    if (iPayListener != null) {
                        iPayListener.onPaySuccess(basePayBean.getProductId(),basePayBean.getCpOrderId());
                    }
                }

                @Override
                public void fail(BasePayBean basePayBean) {
                    PL.i("huawei IPayCallBack fail");
                    if (iPayListener != null) {
                        iPayListener.onPayFail();
                    }
                }

                @Override
                public void cancel(String msg) {
                    if (iPayListener != null) {
                        iPayListener.onPayFail();
                    }
                }
            });
        }
        huaweiPay.startPay(activity, payCreateOrderReqBean);
    }

    private void checkGoogleOrWebPay(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {

        ConfigBean configBean = SdkUtil.getSdkCfg(activity);
        if (configBean != null) {
            ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(activity);
//            versionData.setTogglePay(true);//test
            if (versionData != null && versionData.isTogglePay()){//检查是否需要切换支付，总开关

                PayCreateOrderReqBean checkPayTypeReqBean = new PayCreateOrderReqBean(activity);
                checkPayTypeReqBean.setCpOrderId(payCreateOrderReqBean.getCpOrderId());
                checkPayTypeReqBean.setProductId(payCreateOrderReqBean.getProductId());
                checkPayTypeReqBean.setExtra(payCreateOrderReqBean.getExtra());

                Request.togglePayRequest(activity, checkPayTypeReqBean, new SFCallBack<ToggleResult>() {
                    @Override
                    public void success(ToggleResult result, String msg) {

//                        result.getData().setTogglePay(true);
//                        result.getData().setHideSelectChannel(true);
                        if (result != null && result.isRequestSuccess() && result.getData() != null && result.getData().isTogglePay()){

                            if (result.getData().isHideSelectChannel()) {//是否显示询问用户
                                doWebPay(activity, payCreateOrderReqBean);
                            }else {//默认弹出显示询问用户

                                showTogglePayDialog(activity, payCreateOrderReqBean);
                            }

                        }else {
                            doGooglePay(activity, payCreateOrderReqBean);
                        }
                    }

                    @Override
                    public void fail(ToggleResult result, String msg) {
                        doGooglePay(activity, payCreateOrderReqBean);
                    }
                });
                return;
            }
        }
        doGooglePay(activity, payCreateOrderReqBean);
    }

    public void showTogglePayDialog(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {

        if (commonDialog != null){
            commonDialog.dismiss();
        }
        commonDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        SelectPayChannelLayout selectPayChannelLayout = new SelectPayChannelLayout(activity);
        selectPayChannelLayout.setsBaseDialog(commonDialog);
        selectPayChannelLayout.setSfCallBack(new SFCallBack() {
            @Override
            public void success(Object result, String msg) {//google
                doGooglePay(activity, payCreateOrderReqBean);
                trackEvent(activity, EventConstant.EventName.select_google.name());
                if (commonDialog != null){
                    commonDialog.dismiss();
                }
            }

            @Override
            public void fail(Object result, String msg) {//第三方
                doWebPay(activity, payCreateOrderReqBean);
                trackEvent(activity, EventConstant.EventName.select_other.name());
                if (commonDialog != null){
                    commonDialog.dismiss();
                }
            }
        });
        commonDialog.setContentView(selectPayChannelLayout);
        commonDialog.show();
    }

    private void doGooglePay(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {

        //设置Google储值的回调
        iPay.setIPayCallBack(new IPayCallBack() {
            @Override
            public void success(BasePayBean basePayBean) {
                PL.i("IPayCallBack success");
                ToastUtils.toast(activity,R.string.text_finish_pay);
                if (otherPayWebViewDialog != null && otherPayWebViewDialog.isShowing()){
                    otherPayWebViewDialog.dismiss();
                }

//                SdkEventLogger.trackinPayEvent(activity, basePayBean);

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

            @Override
            public void cancel(String msg) {
                if (iPayListener != null) {
                    iPayListener.onPayFail();
                }
            }
        });

        iPay.startPay(activity, payCreateOrderReqBean);
    }

    public void requestStoreReview(Activity activity, SFCallBack sfCallBack){

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
                                if (sfCallBack != null) {
                                    sfCallBack.success("","");
                                }
                            }
                        });

                    } else {
                        // There was some problem, log or handle the error code.
                        PL.i("requestReviewFlow There was some problem");
//                        int reviewErrorCode = task.getException().getErrorCode();
                        if (sfCallBack != null) {
                            sfCallBack.success("","");
                        }
                    }
                });
            }
        });

    }


    @Override
    public void showBindPhoneView(Activity activity, SFCallBack<BaseResponseModel> sfCallBack) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!SdkUtil.isVersion1(activity)) {
                    if (commonDialog != null){
                        commonDialog.dismiss();
                    }
                    commonDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                    AccountBindPhoneLayout accountBindPhoneLayout = new AccountBindPhoneLayout(activity);
                    accountBindPhoneLayout.setsBaseDialog(commonDialog);
                    accountBindPhoneLayout.setSFCallBack(sfCallBack);
                    commonDialog.setContentView(accountBindPhoneLayout);
                    commonDialog.show();
                }
            }
        });

    }

    @Override
    public void showUpgradeAccountView(Activity activity, SFCallBack sfCallBack) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!SdkUtil.isVersion1(activity)) {
                    if (commonDialog != null){
                        commonDialog.dismiss();
                    }
                    commonDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                    ThirdPlatBindAccountLayoutV2 platBindAccountLayoutV2 = new ThirdPlatBindAccountLayoutV2(activity);
                    platBindAccountLayoutV2.setsBaseDialog(commonDialog);
                    platBindAccountLayoutV2.setSFCallBack(sfCallBack);
                    commonDialog.setContentView(platBindAccountLayoutV2);
                    commonDialog.show();
                }
            }
        });

    }

    @Override
    public void showSocialView(Activity activity) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("showSocialView");
                if (sWebViewDialog != null){
                    sWebViewDialog.dismiss();
                }
                String webUrl = activity.getString(R.string.mw_sdk_social_url);
                if (SStringUtil.isEmpty(webUrl)){
                    ToastUtils.toast(activity, "web url is empty");
                    PL.i("showSocialView webUrl is empty");
                    return;
                }
                webUrl = String.format(webUrl, ResConfig.getGameCode(activity));
                SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(activity);
                sGameBaseRequestBean.setCompleteUrl(webUrl);
                webUrl = sGameBaseRequestBean.createPreRequestUrl();

                View bannerSwebView = activity.getLayoutInflater().inflate(R.layout.mw_social_banner, null);
                SWebViewLayout sWebViewLayout = bannerSwebView.findViewById(R.id.svl_social_webview);
                sWebViewLayout.getsWebView().setWebViewClient(new BaseWebViewClient(activity){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
                sWebViewLayout.getTitleHeaderView().setVisibility(View.GONE);
                bannerSwebView.findViewById(R.id.iv_social_close).setOnClickListener(v -> {
                    if (sWebViewDialog != null) {
                        sWebViewDialog.dismiss();
                    }
                });

                sWebViewDialog = new SWebViewDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen, bannerSwebView, sWebViewLayout.getsWebView(), null);
                sWebViewDialog.setWebUrl(webUrl);
                sWebViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                sWebViewDialog.setsWebDialogCallback(new SWebViewDialog.SWebDialogCallback() {
                    @Override
                    public void createFinish(SWebViewDialog sWebViewDialog, SWebView sWebView) {
                    }
                });
                sWebViewDialog.show();

            }
        });

    }

    @Override
    public void showActView(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (commonDialog != null){
                    commonDialog.dismiss();
                }

                ConfigBean configBean = SdkUtil.getSdkCfg(activity);
                if (configBean != null) {
                    ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(activity);
                    //versionData.setShowMarket(true);
                    if (versionData != null && versionData.isShowMarket()) {
                        Request.requestActData(activity, new SFCallBack<ActDataModel>() {
                            @Override
                            public void success(ActDataModel result, String msg) {

                                if (result != null && result.isRequestSuccess() && result.getData() != null && !result.getData().isEmpty()){
                                    commonDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                                    ActExpoView mActExpoView = new ActExpoView(activity, result.getData(), commonDialog);
                                    mActExpoView.setsBaseDialog(commonDialog);
                                    commonDialog.setContentView(mActExpoView);
                                    commonDialog.show();
                                }else {
                                    ToastUtils.toast(activity, "" + result.getMessage());
                                }

                            }

                            @Override
                            public void fail(ActDataModel result, String msg) {
                                ToastUtils.toast(activity, "This feature is not turned on");
                            }
                        });
                        return;
                    }
                }
                ToastUtils.toast(activity,"This feature is not turned on");

            }
        });

    }

    @Override
    public boolean isShowAct(Activity activity) {
        return isShowAct_M;
    }

    private void showActViewSwitchRequest(Activity activity) {

        if (isShowAct_M){
            return;
        }
        ConfigBean configBean = SdkUtil.getSdkCfg(activity);
        if (configBean != null) {
            ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(activity);
            //versionData.setShowMarket(true);
            if (versionData != null && versionData.isShowMarket()) {
                Request.requestMarketSwitch(activity, new SFCallBack<ToggleResult>() {
                    @Override
                    public void success(ToggleResult result, String msg) {

                        if (result != null && result.isRequestSuccess() && result.getData() != null){
                            isShowAct_M = true;
                        }

                    }

                    @Override
                    public void fail(ToggleResult result, String msg) {
                    }
                });
            }
        }

    }

    @Override
    public void requestVfCode(Activity activity, String areaCode, String telephone, SFCallBack<BaseResponseModel> sfCallBack) {
        PL.i("requestVfCode areaCode=" + areaCode + " telephone=" + telephone);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseResponseModel errorModel = new BaseResponseModel();
                errorModel.setCode("1001");
                if (SStringUtil.isEmpty(areaCode)) {
                    ToastUtils.toast(activity,R.string.text_area_code_not_empty);
                    if (sfCallBack != null){
                        errorModel.setMessage(activity.getString(R.string.text_area_code_not_empty));
                        sfCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                    }
                    return;
                }
                if (SStringUtil.isEmpty(telephone)) {
                    ToastUtils.toast(activity,R.string.text_phone_not_empty);
                    if (sfCallBack != null){
                        errorModel.setMessage(activity.getString(R.string.text_phone_not_empty));
                        sfCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                    }
                    return;
                }
                PhoneInfo phoneInfo = SdkUtil.getPhoneInfoByAreaCode(activity,areaCode);
                if (phoneInfo != null){
                    if (!telephone.matches(phoneInfo.getPattern())){
                        ToastUtils.toast(activity,R.string.text_phone_not_match);
                        if (sfCallBack != null){
                            errorModel.setMessage(activity.getString(R.string.text_phone_not_match));
                            sfCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                        }
                        return;
                    }
                }
                Request.sendVfCode(activity, false, areaCode, telephone, sfCallBack);
            }
        });
    }

    @Override
    public void requestBindPhone(Activity activity, String areaCode, String telephone,String vfCode, SFCallBack<SLoginResponse> sfCallBack) {
        PL.i("requestBindPhone areaCode=" + areaCode + " telephone=" + telephone + " vfCode=" + vfCode);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (SStringUtil.isEmpty(areaCode)) {
                    ToastUtils.toast(activity,R.string.text_area_code_not_empty);
                    return;
                }
                if (SStringUtil.isEmpty(telephone)) {
                    ToastUtils.toast(activity,R.string.text_phone_not_empty);
                    return;
                }
                if (SStringUtil.isEmpty(vfCode)) {
                    ToastUtils.toast(activity,R.string.py_vfcode_empty);
                    return;
                }
                PhoneInfo phoneInfo = SdkUtil.getPhoneInfoByAreaCode(activity,areaCode);
                if (phoneInfo != null){
                    if (!telephone.matches(phoneInfo.getPattern())){
                        ToastUtils.toast(activity,R.string.text_phone_not_match);
                        return;
                    }
                }

                Request.bindPhone(activity, false, areaCode, telephone, vfCode, sfCallBack);
            }
        });
    }

    @Override
    public void requestUpgradeAccount(Activity activity, String account, String pwd, SFCallBack<SLoginResponse> sfCallBack ) {

        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(activity, R.string.py_account_empty);
            return;
        }
        final String account_temp = account.trim();
        if (!SdkUtil.checkAccount(account_temp)) {
            ToastUtils.toast(activity,R.string.text_account_format);
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.toast(activity, R.string.py_password_empty);
            return;
        }
        final String pwd_temp = pwd.trim();
        if (!SdkUtil.checkPassword(pwd_temp)) {
            ToastUtils.toast(activity,R.string.text_pwd_format);
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Request.bindAcountInGame(activity,false,SdkUtil.getPreviousLoginType(activity), account_temp, pwd_temp, sfCallBack);
            }
        });
    }

    private void doWebPay(Activity activity, PayCreateOrderReqBean bean) {

        String payThirdUrl = ResConfig.getPayPreferredUrl(activity) + activity.getResources().getString(R.string.api_pay_web_payment);//"api/web/payment.page";
        bean.setCompleteUrl(payThirdUrl);

        String webUrl = bean.createPreRequestUrl();

        Intent intent = MWWebPayActivity.create(activity,"",webUrl,bean.getCpOrderId(),bean.getProductId(),bean.getExtra());
        activity.startActivityForResult(intent, RequestCode.RequestCode_Web_Pay);

     /*   otherPayWebViewDialog = new SWebViewDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
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
        otherPayWebViewDialog.show();*/
    }


    @Override
    public void checkGooglePlayServicesAvailable(Activity activity) {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
        if (code == ConnectionResult.SUCCESS){
            PL.i("支持的Google服务");
            ToastUtils.toast(activity,"支持的Google服务");
        }else {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(activity);
        }
    }

    //打开fb相关的url，优先通过fb app打开
    public void openFbUrl(Activity activity, String url){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse(url);
                Intent fb_intent = new Intent(Intent.ACTION_VIEW,uri);
                fb_intent.setPackage("com.facebook.katana");
                fb_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Intent other_intent = new Intent(Intent.ACTION_VIEW,uri);
                other_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    if (fb_intent.resolveActivity(activity.getPackageManager()) != null){
                        activity.startActivity(fb_intent);
                    }else {
                        activity.startActivity(other_intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void showFloatView(Activity activity, FloatCallback floatCallback) {

        String floatCfgData = SdkUtil.getFloatCfgData(activity);
        String menuResData = SdkUtil.getFloatSwitchData(activity);

        if (SStringUtil.isEmpty(floatCfgData) || SStringUtil.isEmpty(menuResData)){
            PL.i("floatCfgData=" + floatCfgData + "  menuResData=" + menuResData);
            ToastUtils.toast(activity,"float data error");
            return;
        }

        FloatConfigData xFloatConfigData = new Gson().fromJson(floatCfgData, FloatConfigData.class);

        if (!xFloatConfigData.isButtonSwitch()){
            PL.i("float not open");
            return;
        }
        FloatSwitchRes xFloatSwitchRes = new Gson().fromJson(menuResData, FloatSwitchRes.class);
        if (xFloatSwitchRes == null || xFloatSwitchRes.getData() == null){
            PL.i("FloatSwitchRes null error");
            return;
        }


        FloatingManager.getInstance().initFloatingView(activity,xFloatConfigData.getButtonIcon(), new FloatButtionClickCallback() {
            @Override
            public void show(String msg) {

                if (floatViewDialog != null){
                    floatViewDialog.show();
                    return;
                }
                floatViewDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                ArrayList<MenuData> arrayList = new ArrayList<>();
                FloatContentView mActExpoView = new FloatContentView(activity, arrayList, floatViewDialog, floatCallback);
                mActExpoView.setsBaseDialog(floatViewDialog);
                floatViewDialog.setContentView(mActExpoView);
                floatViewDialog.show();

            }
        });

    }
}

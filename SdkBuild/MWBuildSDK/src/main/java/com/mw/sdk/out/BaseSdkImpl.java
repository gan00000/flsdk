package com.mw.sdk.out;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.core.base.BaseWebViewClient;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.AppUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.SignatureUtil;
import com.core.base.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mw.base.bean.SPayType;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.MWBaseWebActivity;
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
import com.mw.sdk.bean.res.FloatMenuResData;
import com.mw.sdk.bean.res.MenuData;
import com.mw.sdk.bean.res.ToggleResult;
import com.mw.sdk.callback.FloatButtionClickCallback;
import com.mw.sdk.callback.FloatCallback;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.constant.RequestCode;
import com.mw.sdk.constant.ResultCode;
import com.mw.sdk.log.LogViewManager;
import com.mw.sdk.login.DialogLoginImpl;
import com.mw.sdk.login.ILogin;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.v2.AccountBindPhoneLayout;
import com.mw.sdk.login.widget.v2.SelectPayChannelLayout;
import com.mw.sdk.login.widget.v2.ThirdPlatBindAccountLayoutV2;
import com.mw.sdk.out.bean.EventPropertie;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.IPayFactory;
import com.mw.sdk.pay.gp.GooglePayImpl;
import com.mw.sdk.utils.DataManager;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.utils.ShareUtil;
import com.mw.sdk.widget.SBaseDialog;
import com.mw.sdk.widget.SWebView;
import com.mw.sdk.widget.SWebViewDialog;
import com.mw.sdk.widget.SWebViewLayout;
import com.thirdlib.adjust.AdjustHelper;
import com.thirdlib.af.AFHelper;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleProxy;
import com.thirdlib.huawei.HuaweiPayImpl;
import com.thirdlib.irCafebazaar.BazaarPayActivity;
import com.thirdlib.td.TDAnalyticsHelper;
import com.thirdlib.tiktok.TTSdkHelper;
import com.thirdlib.vk.RustoreManager;
import com.thirdlib.vk.VKPurchaseManger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


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
//    protected IPay iPay_Ru;
//    protected IPay iPay_GG;
//    protected IPay iPayOneStore;
    private Map<String, IPay> iPayMap = new HashMap<>();
    protected IPayListener iPayListener;
    protected Activity activity;

    protected HuaweiPayImpl huaweiPay;

    protected DataManager dataManager;

    private boolean isShowAct_M;

//    private ISdkCallBack switchAccountCallBack;

    private long regRoleInfoTimestamp;

    private SharedPreferences googleDeepLinkPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener deepLinkListener;

    public BaseSdkImpl() {
//        iLogin = ObjFactory.create(DialogLoginImpl.class);
        PL.i("BaseSdkImpl 构造函数");
    }

    @Override
    public void applicationOnCreate(Application application) {

        PL.initLog(application.getApplicationContext());

        PL.i("BaseSdkImpl applicationOnCreate");

        Observable.just("" + System.currentTimeMillis())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Throwable {
                        PL.i("BaseSdkImpl applicationOnCreate async task");
                        //获取Google 广告ID
                        SdkEventLogger.registerGoogleAdId(application.getApplicationContext());
                        return "";
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                               @Override
                               public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                               }

                               @Override
                               public void onNext(@io.reactivex.rxjava3.annotations.NonNull String s) {

                                   PL.i("BaseSdkImpl applicationOnCreate async onNext");
                                   TDAnalyticsHelper.init(application.getApplicationContext());
                               }

                               @Override
                               public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                               }

                               @Override
                               public void onComplete() {

                               }
                           });

        //重置登录数据
        SdkUtil.resetSdkLoginData(application.getApplicationContext());

        AFHelper.applicationOnCreate(application);
        AdjustHelper.init(application);
        TTSdkHelper.init(application.getApplicationContext());
        SPUtil.saveBoolean(application.getApplicationContext(), SdkUtil.SDK_SP_FILE,"sdk_applicationOnCreate_call", true);

    }

    //    @Deprecated
//    @Override
//    public void initSDK(final Activity activity) {
//        initSDK(activity, SGameLanguage.zh_TW);
//    }

//    @Override
    public void initSDK(final Activity activity) {

        PL.i("sdk initSDK");

//        Localization.gameLanguage(activity, gameLanguage);
        //清除上一次登录成功的返回值
        //GamaUtil.saveSdkLoginData(activity, "");

        //平台安装上报
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PL.i("sdk initSDK postDelayed");
                //广告
                SdkEventLogger.reportInstallActivation(activity.getApplicationContext());
                SdkEventLogger.activateApp(activity);

            }
        },500);//等广告id生成好


        //时间打点开始
//                LogTimer.getInstance().start(activity);

//                setGameLanguage(activity,SGameLanguage.zh_TW);

//                ConfigRequest.requestBaseCfg(activity.getApplicationContext());//下载配置文件
//                ConfigRequest.requestTermsCfg(activity.getApplicationContext());//下载服务条款
        // 1.初始化fb sdk
//                SFacebookProxy.initFbSdk(activity.getApplicationContext());
        sFacebookProxy = SFacebookProxy.newObj(activity.getApplicationContext());
        isInitSdk = true;

        boolean isCall = SPUtil.getBoolean(activity.getApplicationContext(), SdkUtil.SDK_SP_FILE,"sdk_applicationOnCreate_call");
        if (!isCall){
            PL.e("sdk IMWSDK.applicationOnCreate(Application application) not call, plase call IMWSDK.applicationOnCreate(Application application) in app Application");
            ToastUtils.toastL(activity, "Error: Plase call IMWSDK.applicationOnCreate(Application application) in app Application first");
        }
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

        PL.i("IMWSDK registerRoleInfo roleId:" + roleId + ",roleName:" + roleName + ",roleLevel:" + roleLevel + ",vipLevel:" + vipLevel + ",severCode:" + severCode + ",serverName:" + serverName);

        if (SStringUtil.isEmpty(roleId) || SStringUtil.isEmpty(severCode)){
            return;
        }
        SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
        long curTime = System.currentTimeMillis();
        if (curTime - this.regRoleInfoTimestamp < 1000 * 60 * 15){
            PL.d("registerRoleInfo to fast");
            return;
        }
        this.regRoleInfoTimestamp = curTime;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                try {
                    TDAnalyticsHelper.setAccountId(roleId);//shushu
                    TDAnalyticsHelper.setCommonProperties(activity);
                    //trackEvent(activity, EventConstant.EventName.DetailedLevel);
                    //if (Integer.parseInt(roleLevel) >= 40){
                     //   SdkEventLogger.trackingWithEventName(activity, EventConstant.EventName.AchieveLevel_40.name(), null, EventConstant.AdType.AdTypeAllChannel, true);
                   // }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (SStringUtil.isNotEmpty(roleId) && SStringUtil.isNotEmpty(severCode) && SStringUtil.isNotEmpty(SdkUtil.getUid(activity))){

                    if (iPay != null) {
                        iPay.startQueryPurchase(activity);
                    }

                    Request.requestFloatMenus(activity.getApplicationContext(), new SFCallBack<String>() {
                        @Override
                        public void success(String result, String msg) {
                            showFloatView(activity);
                        }

                        @Override
                        public void fail(String result, String msg) {

                        }
                    });
                }
                //showActViewSwitchRequest(activity);//注释掉，先不用

                LogViewManager.getInstance().initFloatingView(activity);
            }
        });

        SPUtil.saveBoolean(activity, SdkUtil.SDK_SP_FILE,"sdk_registerRoleInfo_call", true);
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
                    iPay.queryPreRegData(activity, iSdkCallBack);
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
               // trackEvent(activity, EventConstant.EventName.DetailedLevel);
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
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        onCreate(activity);
        String channel_platform = ResConfig.getChannelPlatform(activity);
        String payChannel = activity.getResources().getString(R.string.mw_dialog_pay_add_type);
        if(ChannelPlatform.VK.getChannel_platform().equals(channel_platform)
                || payChannel.contains(ChannelPlatform.VK.getChannel_platform())) {
            VKPurchaseManger.getInstance().onCreate(activity, savedInstanceState);
        }

    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {

        String channel_platform = ResConfig.getChannelPlatform(activity);
        String payChannel = activity.getResources().getString(R.string.mw_dialog_pay_add_type);

        if(ChannelPlatform.VK.getChannel_platform().equals(channel_platform)
                || payChannel.contains(ChannelPlatform.VK.getChannel_platform())) {
            VKPurchaseManger.getInstance().onNewIntent(activity, intent);
        }
    }

    @Override
    public void onCreate(final Activity activity) {
        PL.i("sdk onCreate");
        PL.i("the aar version info:" + SdkUtil.getSdkInnerVersion(activity) + "_" + BuildConfig.JAR_VERSION);//打印版本号

        String keyhash = SignatureUtil.getHashKey(activity, activity.getPackageName());
        String sha1 = SignatureUtil.getSignatureSHA1WithColon(activity, activity.getPackageName());
        PL.i("app fb keyhash:" + keyhash);
        PL.i("app google sha1:" + sha1);
        PL.i("app sha256:" + SignatureUtil.getSignatureSHA256WithColon(activity, activity.getPackageName()));
        SdkUtil.saveSignInfo(activity, keyhash + "_" + sha1);

        String mainActivityName = ApkInfoUtil.getLaunchActivityName(activity);
        PL.i("app main类名:" + mainActivityName);

        this.activity = activity;
        this.regRoleInfoTimestamp = 0;
        dataManager = DataManager.getInstance();
        SdkUtil.saveAppOpenCount(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                onCreate_OnUi(activity);
            }
        });

    }

    protected void onCreate_OnUi(Activity activity) {
        if (!isInitSdk) {
            initSDK(activity);
        }

        //==============deep link=====================
        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(activity);//启动过不在获取
        if (sLoginResponse == null){
            //fb deeplink
            SFacebookProxy.fetchDeferredAppLinkData(activity);

            googleDeepLinkPreferences = activity.getSharedPreferences("google.analytics.deferred.deeplink.prefs", MODE_PRIVATE);
            deepLinkListener = (sharedPreferences, key) -> {
                PL.i("Deep link changed");
                if ("deeplink".equals(key)) {
                    String deeplink = sharedPreferences.getString(key, null);
                    //Double cTime = Double.longBitsToDouble(sharedPreferences.getLong("timestamp", 0));
                    PL.i("Deep link retrieved: " + deeplink);
                    if (SStringUtil.isEmpty(deeplink)){
                        PL.i("The deep link retrieval failed or empty");
                        //SdkUtil.saveDeepLink(activity, "");
                    }else {
                        SdkUtil.saveDeepLink(activity, deeplink);
                    }
                }
            };

            if (googleDeepLinkPreferences != null && deepLinkListener != null) {
                PL.i("googleDeepLinkPreferences  registerOnSharedPreferenceChangeListener");
                googleDeepLinkPreferences.registerOnSharedPreferenceChangeListener(deepLinkListener);
            }
        }
        //===========================end=======


        ConfigRequest.requestBaseCfg(activity.getApplicationContext());//加载配置
        ConfigRequest.requestAreaCodeInfo(activity.getApplicationContext());

//        Request.requestFloatConfigData(activity.getApplicationContext(),null);

        if (iLogin != null) {
            iLogin.onCreate(activity);
        }
        String channel_platform = ResConfig.getChannelPlatform(activity);
        if (!ChannelPlatform.BAZAAR.getChannel_platform().equals(channel_platform)) {
            iPay = IPayFactory.create(activity);
            if (iPay != null){
                iPayMap.put(channel_platform, iPay);
                iPay.onCreate(activity);
            }
        }

    }

    @Override
    public void onStart(Activity activity) {
        PL.i("IMWSDK onStart");
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

                if (requestCode == BazaarPayActivity.Request_Code_BazaarPay && resultCode == BazaarPayActivity.Result_Code_BazaarPay){
                    int extra_code = data.getIntExtra(BazaarPayActivity.K_Extra_Code, 1001);
                    BasePayBean payBean = (BasePayBean)data.getSerializableExtra(BazaarPayActivity.K_Extra_Data);
                    if (extra_code==1000 && payBean != null){
                        if (iPayListener != null) {
                            iPayListener.onPaySuccess(payBean.getProductId(),payBean.getCpOrderId());
                        }
                    }else {
                        if (iPayListener != null) {
                            iPayListener.onPayFail();
                        }
                    }
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
                if (googleDeepLinkPreferences != null && deepLinkListener != null){
                    googleDeepLinkPreferences.unregisterOnSharedPreferenceChangeListener(deepLinkListener);
                    deepLinkListener = null;
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
                iPayMap.clear();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IMWSDK onRequestPermissionsResult");
                if (iLogin != null){
                    iLogin.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
                }
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
        this.trackEvent(activity, eventName.name(), null, 0);
    }

    @Override
    public void trackEvent(Activity activity, String eventName) {
        /*Log.i(TAG, "trackEvent eventName...");
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
        });*/

        this.trackEvent(activity, eventName, null, 0);
    }

    @Override
    public void trackEvent(Activity activity, String eventName, JSONObject propertieJsonObj, int m) {
        Log.i(TAG, "trackEvent name = " + eventName);
        if (SStringUtil.isEmpty(eventName)){
            Log.i(TAG, "trackEvent eventName is null");
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SdkEventLogger.sendEventToSever(activity, eventName);
                if (propertieJsonObj != null){
                    SdkEventLogger.trackingWithEventName(activity, eventName, JsonUtil.jsonObjectToMap(propertieJsonObj), EventConstant.AdType.AdTypeAllChannel);
                }else {
                    SdkEventLogger.trackingWithEventName(activity, eventName, null, EventConstant.AdType.AdTypeAllChannel);
                }
                TDAnalyticsHelper.trackEvent(eventName, propertieJsonObj, 0);
            }
        });
    }

//    public void trackEvent(Activity activity, String eventName, EventPropertie eventPropertie) {
//        if (eventPropertie == null) {
//            trackEvent(activity, eventName, null, 0);
//        }else {
//            trackEvent(activity, eventName, eventPropertie.objToJsonObj(), 0);
//        }
//    }

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
        this.regRoleInfoTimestamp = 0;
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

                    resetFiled(activity);
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
        this.regRoleInfoTimestamp = 0;
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
    public void exitGame(Activity activity, ISdkCallBack iSdkCallBack) {
        this.regRoleInfoTimestamp = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (dataManager == null){
                    dataManager = DataManager.getInstance();
                }
                dataManager.setLogin(false);

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

                boolean isCall = SPUtil.getBoolean(activity.getApplicationContext(), SdkUtil.SDK_SP_FILE,"sdk_registerRoleInfo_call");
                if (!isCall){
                    PL.e("sdk IMWSDK.registerRoleInfo() not call, plase call IMWSDK.registerRoleInfo() ");
                    ToastUtils.toastL(activity, "Error: Plase call IMWSDK.registerRoleInfo() when get role info");
                }

                if (SStringUtil.isEmpty(roleId) || SStringUtil.isEmpty(severCode)){
                    ToastUtils.toast(activity,"roleId and severCode must not empty");
                    return;
                }
                SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
                startPay(activity, payType, cpOrderId, productId, extra, listener);
                trackEvent(activity, EventConstant.EventName.Initiate_Checkout);
                //trackEvent(activity, EventConstant.EventName.DetailedLevel);
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

        String channel_platform = ResConfig.getChannelPlatform(activity);

        if(ChannelPlatform.MEOW.getChannel_platform().equals(channel_platform)
                || ChannelPlatform.APKPURE.getChannel_platform().equals(channel_platform)) {
            doWebPay(activity, payCreateOrderReqBean);
        } else if(ChannelPlatform.HUAWEI.getChannel_platform().equals(channel_platform)) {
            doHuaweiPay(activity, payCreateOrderReqBean);
        } else if(ChannelPlatform.QOOAPP.getChannel_platform().equals(channel_platform)) {
            doQooAppPay(activity, payCreateOrderReqBean);
        } else if(ChannelPlatform.LUNQI.getChannel_platform().equals(channel_platform)) {
//            doLunqiPay(activity, payCreateOrderReqBean);
            doWebPay(activity, payCreateOrderReqBean);
        }else if (ChannelPlatform.GOOGLE.getChannel_platform().equals(channel_platform)
                || ChannelPlatform.VK.getChannel_platform().equals(channel_platform)
                || ChannelPlatform.ONESTORE.getChannel_platform().equals(channel_platform)
                || ChannelPlatform.SAMSUNG.getChannel_platform().equals(channel_platform)
                || ChannelPlatform.Xiaomi.getChannel_platform().equals(channel_platform)
                || ChannelPlatform.NOWGG.getChannel_platform().equals(channel_platform)){
            checkGoogleOrWebPay(activity, payCreateOrderReqBean);
        }else if (ChannelPlatform.BAZAAR.getChannel_platform().equals(channel_platform)){

            Intent intent = new Intent(activity, BazaarPayActivity.class);
            intent.putExtra(BazaarPayActivity.K_Extra_Data, payCreateOrderReqBean);
            activity.startActivityForResult(intent, BazaarPayActivity.Request_Code_BazaarPay);

        }else{
            doWebPay(activity, payCreateOrderReqBean);
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

   /* public void showTogglePayDialog(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {

        if (commonDialog != null){
            commonDialog.dismiss();
        }
        commonDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        SelectPayChannelLayout selectPayChannelLayout = new SelectPayChannelLayout(activity);
        selectPayChannelLayout.setsBaseDialog(commonDialog);
        selectPayChannelLayout.setSfCallBack(new SFCallBack<Integer>() {
            @Override
            public void success(Integer result, String msg) {//google
                doGooglePay(activity, payCreateOrderReqBean);
                trackEvent(activity, EventConstant.EventName.select_google.name());
                if (commonDialog != null){
                    commonDialog.dismiss();
                }
            }

            @Override
            public void fail(Integer result, String msg) {//第三方
                doWebPay(activity, payCreateOrderReqBean);
                trackEvent(activity, EventConstant.EventName.select_other.name());
                if (commonDialog != null){
                    commonDialog.dismiss();
                }
            }
        });
        commonDialog.setContentView(selectPayChannelLayout);
        commonDialog.show();
    }*/

    public void showTogglePayDialog(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {

        PL.i("showTogglePayDialog...");
        if (commonDialog != null){
            commonDialog.dismiss();
        }
        commonDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        SelectPayChannelLayout selectPayChannelLayout = new SelectPayChannelLayout(activity);
        selectPayChannelLayout.setsBaseDialog(commonDialog);
        selectPayChannelLayout.setSfCallBack(new SFCallBack<ChannelPlatform>() {
            @Override
            public void success(ChannelPlatform result, String msg) {

                if(iPayMap.containsKey(result.getChannel_platform()) && iPayMap.get(result.getChannel_platform()) != null){
                    iPay = iPayMap.get(result.getChannel_platform());
                    PL.d("iPay already create...");
                }else {
                    IPay mxPay = IPayFactory.create(activity, result);
                    if (mxPay != null){
                        iPayMap.put(result.getChannel_platform(), mxPay);
                        iPay = mxPay;
                        iPay.onCreate(activity);
                    }
                }

                doGooglePay(activity, payCreateOrderReqBean);
                if (result == ChannelPlatform.GOOGLE){
                    trackEvent(activity, EventConstant.EventName.select_google.name());
                }
                if (commonDialog != null){
                    commonDialog.dismiss();
                }
            }

            @Override
            public void fail(ChannelPlatform result, String msg) {//第三方
                PL.i("setSfCallBack result = " + result);

                if (result == ChannelPlatform.MEOW){//第三方
                    doWebPay(activity, payCreateOrderReqBean);
                }

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

                EventPropertie eventPropertie = new EventPropertie();
                if (basePayBean != null){//eventName为空才是正常的储值上报
                    eventPropertie.setOrder_id(basePayBean.getOrderId());
                    //eventPropertie.setPayment_name(basePayBean.getProductId());
                    eventPropertie.setPay_amount(basePayBean.getUsdPrice());
                    eventPropertie.setProduct_id(basePayBean.getProductId());
                    eventPropertie.setPay_method("google");
                    eventPropertie.setCurrency_type("USD");
                }
                TDAnalyticsHelper.trackEvent("pay_fail",eventPropertie);

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

        PL.i("requestStoreReview...");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String channel_platform = ResConfig.getChannelPlatform(activity);

                if(ChannelPlatform.GOOGLE.getChannel_platform().equals(channel_platform)) {
                    SGoogleProxy.requestStoreReview(activity, sfCallBack);
                } else if (ChannelPlatform.SAMSUNG.getChannel_platform().equals(channel_platform)) {
                }  else if (ChannelPlatform.VK.getChannel_platform().equals(channel_platform)) {
                    RustoreManager.launchReviewFlow(activity, sfCallBack);
                }else if (ChannelPlatform.NOWGG.getChannel_platform().equals(channel_platform)) {
                }else {
                }
            }
        });

    }


    @Override
    public void showBindPhoneView(Activity activity, SFCallBack<BaseResponseModel> sfCallBack) {
        PL.i("IMWSDK showBindPhoneView");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

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
        });

    }

    @Override
    public void showUpgradeAccountView(Activity activity, SFCallBack sfCallBack) {
        PL.i("IMWSDK showUpgradeAccountView");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

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

    //@Override
    public void showFloatView(Activity activity) {

//        String floatCfgData = SdkUtil.getFloatCfgData(activity);
        String menuResData = SdkUtil.getFloatMenuResData(activity);

        if (SStringUtil.isEmpty(menuResData)){
            PL.i("float menuResData=" + menuResData);
            //ToastUtils.toast(activity,"float data error");
            return;
        }

//        FloatConfigData xFloatConfigData = new Gson().fromJson(floatCfgData, FloatConfigData.class);

        FloatMenuResData xFloatMenuResData = SdkUtil.getFloatMenuResDataObj(activity);
        if (xFloatMenuResData == null || xFloatMenuResData.getData() == null || !xFloatMenuResData.isRequestSuccess() || xFloatMenuResData.getData().getMenuList() == null){
            PL.i("FloatMenuResData null error");
            return;
        }
        if (!xFloatMenuResData.getData().isButtonSwitch()){
            PL.i("float not open");
            return;
        }


        FloatingManager.getInstance().initFloatingView(activity,xFloatMenuResData.getData().getButtonIcon(), new FloatButtionClickCallback() {
            @Override
            public void show(String msg) {

                if (floatViewDialog != null){
                    floatViewDialog.show();
                    return;
                }
                floatViewDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                ArrayList<MenuData> arrayList = new ArrayList<>();
                FloatContentView mActExpoView = new FloatContentView(activity, arrayList, floatViewDialog, new FloatCallback() {
                    @Override
                    public void switchAccount(String msg) {
//                        if (switchAccountCallBack != null){
//                            switchAccountCallBack.success();
//                        }
                    }
                });
                mActExpoView.setsBaseDialog(floatViewDialog);
                floatViewDialog.setContentView(mActExpoView);
                floatViewDialog.show();

            }
        });

    }

    private void resetFiled(Activity activity) {
        try {
            if(this.floatViewDialog != null){
                this.floatViewDialog.dismiss();
                this.floatViewDialog = null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openUrlByBrowser(Activity activity, String url) {
        if (SStringUtil.isEmpty(url)){
            ToastUtils.toast(activity, "url empty error");
            return;
        }
        if (url.startsWith("https://www.facebook.com/")){
            openFbUrl(activity, url);
            return;
        }
        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(activity);
        sGameBaseRequestBean.setCompleteUrl(url);
        AppUtil.openInOsWebApp(activity,sGameBaseRequestBean.createPreRequestUrl());
    }

    public void openUrlBySdkWebview(Activity activity, String url) {
        if (SStringUtil.isEmpty(url)){
            ToastUtils.toast(activity, "url empty error");
            return;
        }
        Intent csIntent = MWBaseWebActivity.create(activity,"", url);
        activity.startActivity(csIntent);
    }

    @Override
    public void openSdkGame(Activity activity, ISdkCallBack iSdkCallBack) {

        PL.i("openSdkGame...");

        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(activity);
        if (sLoginResponse != null && sLoginResponse.getData() != null && sLoginResponse.getData().getIsTest()){
            showSdkGame(activity, iSdkCallBack);//测试用户直接显示
            return;
        }

        int mCount = SdkUtil.getAppOpenCount(activity);
        PL.i("openSdkGame getAppOpenCount=" + mCount);
        if (mCount > 2){
            if (iSdkCallBack != null){
                iSdkCallBack.success();
            }
            return;
        }

        String deferredAppLinkDataStr = SdkUtil.getDeepLink(activity);
        PL.i("openSdkGame deferredAppLinkDataStr=" + deferredAppLinkDataStr);
        if (SStringUtil.isNotEmpty(deferredAppLinkDataStr)){
            showSdkGame(activity, iSdkCallBack);
        }else {
            if (iSdkCallBack != null){
                iSdkCallBack.success();
            }
        }

    }

    private static void showSdkGame(Activity activity, ISdkCallBack iSdkCallBack) {

        String miniGameUrl = activity.getString(R.string.mw_sdk_mini_game_url);
        if (SStringUtil.isEmpty(miniGameUrl)){
            if (iSdkCallBack != null){
                iSdkCallBack.success();
            }
            return;
        }

        miniGameUrl = String.format(miniGameUrl, ResConfig.getGameCode(activity));
        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(activity);
        sGameBaseRequestBean.setCompleteUrl(miniGameUrl);

//        Intent csIntent = MWBaseWebActivity.create(activity,"", sGameBaseRequestBean.createPreRequestUrl());
//        activity.startActivity(csIntent);

        SWebViewLayout sWebViewLayout = new SWebViewLayout(activity);
        sWebViewLayout.getTitleHeaderView().setVisibility(View.GONE);

        SWebViewDialog webViewDialog = new SWebViewDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen, sWebViewLayout, sWebViewLayout.getsWebView(), sWebViewLayout.getBackImageView());

        webViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (iSdkCallBack != null){
                    iSdkCallBack.success();
                }
            }
        });

        webViewDialog.setWebUrl(sGameBaseRequestBean.createPreRequestUrl());
        webViewDialog.show();

//        openUrlByBrowser(activity, "http://test-game.hzwxbz999.cn/unitytest0730/index.html");
    }

    //    @Override
//    public void setSwitchAccountListener(Activity activity, ISdkCallBack sdkCallBack) {
//        this.switchAccountCallBack = sdkCallBack;
//    }

//    @Override
//    public ISdkCallBack getSwitchAccountCallback() {
//        return this.switchAccountCallBack;
//    }


    @Override
    public String getSdkLanguage(Context context) {
        return SdkUtil.getSdkLocaleLanguage(context);
    }

}

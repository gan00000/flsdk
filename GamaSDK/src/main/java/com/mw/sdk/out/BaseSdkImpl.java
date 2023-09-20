package com.mw.sdk.out;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.SignatureUtil;
import com.core.base.utils.ToastUtils;
import com.mw.base.utils.SdkVersionUtil;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.v2.SelectPayChannelLayout;
import com.mw.sdk.pay.gp.bean.res.TogglePayRes;
import com.mw.sdk.utils.DataManager;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.IPayFactory;
import com.mw.sdk.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.mw.sdk.pay.gp.bean.res.BasePayBean;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.mw.base.bean.PhoneInfo;
import com.mw.base.bean.SGameBaseRequestBean;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.bean.SPayType;
import com.mw.base.cfg.ConfigBean;
import com.mw.base.cfg.ConfigRequest;
import com.mw.base.cfg.ResConfig;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.R;
import com.mw.sdk.SBaseDialog;
import com.mw.sdk.constant.ResultCode;
import com.mw.sdk.MWBaseWebActivity;
import com.mw.sdk.pay.MWWebPayActivity;
import com.mw.sdk.SWebViewDialog;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.api.Request;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.constant.RequestCode;
import com.mw.sdk.login.DialogLoginImpl;
import com.mw.sdk.login.ILogin;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.widget.v2.AccountBindPhoneLayout;
import com.mw.sdk.login.widget.v2.ThirdPlatBindAccountLayoutV2;
import com.mw.sdk.social.share.ShareUtil;
import com.mw.sdk.version.BaseSdkVersion;
import com.thirdlib.adjust.AdjustHelper;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.huawei.HuaweiPayImpl;

import java.util.Map;


public class BaseSdkImpl implements IMWSDK {

    private static final String TAG = BaseSdkImpl.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 401;
    protected ILogin iLogin;

    private long firstClickTime;

    private static boolean isInitSdk = false;

    protected SFacebookProxy sFacebookProxy;

    protected SWebViewDialog csSWebViewDialog;
    protected SWebViewDialog otherPayWebViewDialog;

    protected SBaseDialog commonDialog;

    protected IPay iPay;
    protected IPay iPayOneStore;
    protected IPayListener iPayListener;
    protected Activity activity;

    protected HuaweiPayImpl huaweiPay;

    protected DataManager dataManager;

    private ReviewInfo reviewInfo;

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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IMWSDK registerRoleInfo");
                PL.i("roleId:" + roleId + ",roleName:" + roleName + ",roleLevel:" + roleLevel + ",vipLevel:" + vipLevel + ",severCode:" + severCode + ",serverName:" + serverName);
                SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
                if (iPay != null){
                    iPay.startQueryPurchase(activity.getApplicationContext());
                }
            }
        });
    }

    public void checkPreRegData(final Activity activity, ISdkCallBack iSdkCallBack) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IMWSDK checkPreRegData");
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
                if (csSWebViewDialog != null){
                    csSWebViewDialog.onActivityResult(activity, requestCode, resultCode, data);
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
    public void pay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra, String roleId,String roleName,String roleLevel, String vipLevel, String severCode,String serverName, IPayListener listener) {
        PL.i("sdk pay payType:" + payType.toString() + " ,cpOrderId:" + cpOrderId + ",productId:" + productId + ",extra:" + extra);
        if ((System.currentTimeMillis() - firstClickTime) < 1000) {//防止连续点击
            PL.i("点击过快，无效");
            return;
        }
        //iPayListener = listener;
        firstClickTime = System.currentTimeMillis();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SdkUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
                startPay(activity, payType, cpOrderId, productId, extra, listener);
                trackEvent(activity, EventConstant.EventName.Initiate_Checkout);
            }
        });
    }

    protected String productId;
    protected String cpOrderId;
    protected String extra;
    protected void startPay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra, IPayListener listener) {
        this.iPayListener = listener;
        this.activity = activity;
        this.productId = productId;
        this.cpOrderId = cpOrderId;
        this.extra = extra;

        GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean = new GooglePayCreateOrderIdReqBean(activity);
        googlePayCreateOrderIdReqBean.setCpOrderId(cpOrderId);
        googlePayCreateOrderIdReqBean.setProductId(productId);
        googlePayCreateOrderIdReqBean.setExtra(extra);

        String channel_platform = activity.getResources().getString(R.string.channel_platform);

        if(payType == SPayType.WEB || ChannelPlatform.MEOW.getChannel_platform().equals(channel_platform)) {
            doWebPay(activity,googlePayCreateOrderIdReqBean);
        } else if(payType == SPayType.HUAWEI || ChannelPlatform.HUAWEI.getChannel_platform().equals(channel_platform)) {
            doHuaweiPay(activity,googlePayCreateOrderIdReqBean);
        } else if(payType == SPayType.QooApp || ChannelPlatform.QOOAPP.getChannel_platform().equals(channel_platform)) {
            doQooAppPay(activity,googlePayCreateOrderIdReqBean);
        } else {//默认Google储值
            checkGoogleOrWebPay(activity, googlePayCreateOrderIdReqBean);
        }
    }
    protected void doQooAppPay(Activity activity, GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean) {

    }
    private void doHuaweiPay(Activity activity, GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean) {

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
        huaweiPay.startPay(activity, googlePayCreateOrderIdReqBean);
    }

    private void checkGoogleOrWebPay(Activity activity, GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean) {

        ConfigBean configBean = SdkUtil.getSdkCfg(activity);
        if (configBean != null) {
            ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(activity);
//            versionData.setTogglePay(true);//test
            if (versionData != null && versionData.isTogglePay()){//检查是否需要切换支付，总开关

                GooglePayCreateOrderIdReqBean checkPayTypeReqBean = new GooglePayCreateOrderIdReqBean(activity);
                checkPayTypeReqBean.setCpOrderId(googlePayCreateOrderIdReqBean.getCpOrderId());
                checkPayTypeReqBean.setProductId(googlePayCreateOrderIdReqBean.getProductId());
                checkPayTypeReqBean.setExtra(googlePayCreateOrderIdReqBean.getExtra());

                Request.togglePayRequest(activity, checkPayTypeReqBean, new SFCallBack<TogglePayRes>() {
                    @Override
                    public void success(TogglePayRes result, String msg) {

//                        result.getData().setTogglePay(true);
//                        result.getData().setHideSelectChannel(true);
                        if (result != null && result.isRequestSuccess() && result.getData() != null && result.getData().isTogglePay()){

                            if (result.getData().isHideSelectChannel()) {//是否显示询问用户
                                doWebPay(activity,googlePayCreateOrderIdReqBean);
                            }else {//默认弹出显示询问用户

                                if (commonDialog != null){
                                    commonDialog.dismiss();
                                }
                                commonDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                                SelectPayChannelLayout selectPayChannelLayout = new SelectPayChannelLayout(activity);
                                selectPayChannelLayout.setsBaseDialog(commonDialog);
                                selectPayChannelLayout.setSfCallBack(new SFCallBack() {
                                    @Override
                                    public void success(Object result, String msg) {//google
                                        doGooglePay(activity, googlePayCreateOrderIdReqBean);
                                        if (commonDialog != null){
                                            commonDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void fail(Object result, String msg) {//第三方
                                        doWebPay(activity,googlePayCreateOrderIdReqBean);
                                        if (commonDialog != null){
                                            commonDialog.dismiss();
                                        }
                                    }
                                });
                                commonDialog.setContentView(selectPayChannelLayout);
                                commonDialog.show();
                            }

                        }else {
                            doGooglePay(activity, googlePayCreateOrderIdReqBean);
                        }
                    }

                    @Override
                    public void fail(TogglePayRes result, String msg) {
                        doGooglePay(activity, googlePayCreateOrderIdReqBean);
                    }
                });
                return;
            }
        }
        doGooglePay(activity, googlePayCreateOrderIdReqBean);
    }
    private void doGooglePay(Activity activity, GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean) {

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

        iPay.startPay(activity, googlePayCreateOrderIdReqBean);
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

    private void doWebPay(Activity activity, GooglePayCreateOrderIdReqBean bean) {

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

 /*   @SuppressLint("JavascriptInterface")
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

    *//**
     * 充值通知，提供给网页使用
     * @param success  是否成功
     * @param productId  商品id
     *//*
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
    }*/

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
}

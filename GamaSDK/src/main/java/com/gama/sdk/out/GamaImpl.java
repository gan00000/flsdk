package com.gama.sdk.out;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.core.base.ObjFactory;
import com.core.base.utils.AppUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.SignatureUtil;
import com.gama.base.bean.SGameBaseRequestBean;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SLoginType;
import com.gama.base.bean.SPayType;
import com.gama.base.cfg.ConfigRequest;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.Localization;
import com.gama.base.utils.SLog;
import com.gama.data.login.ILoginCallBack;
import com.gama.pay.gp.GooglePayActivity2;
import com.gama.pay.gp.GooglePayHelper;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.gp.bean.req.WebPayReqBean;
import com.gama.pay.gp.util.PayHelper;
import com.gama.sdk.BuildConfig;
import com.gama.sdk.R;
import com.gama.sdk.SWebViewDialog;
import com.gama.sdk.ads.GamaAdsConstant;
import com.gama.sdk.ads.StarEventLogger;
import com.gama.sdk.callback.IPayListener;
import com.gama.sdk.login.DialogLoginImpl;
import com.gama.sdk.login.ILogin;
import com.gama.sdk.login.widget.v2.age.IGamaAgePresenter;
import com.gama.sdk.login.widget.v2.age.callback.GamaAgeCallback;
import com.gama.sdk.login.widget.v2.age.impl.GamaAgeImpl;
import com.gama.sdk.social.bean.UserInfo;
import com.gama.sdk.social.callback.FetchFriendsCallback;
import com.gama.sdk.social.callback.InviteFriendsCallback;
import com.gama.sdk.social.callback.UserProfileCallback;
import com.gama.sdk.social.share.GamaShare;
import com.gama.sdk.utils.LogTimer;
import com.gama.sdk.webpage.GamaWebPageHelper;
import com.gama.thirdlib.facebook.FaceBookUser;
import com.gama.thirdlib.facebook.FriendProfile;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGooglePlayGameServices;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GamaImpl implements IGama {
    private static final String TAG = GamaImpl.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 401;
    private ILogin iLogin;

    private long firstClickTime;

    private static boolean isInitSdk = false;

    private SFacebookProxy sFacebookProxy;
    private SGooglePlayGameServices sGooglePlayGameServices;
    private GamaShare gamaShare;

    private SWebViewDialog otherPayWebViewDialog;

    private IPayListener iPayListener;

    public GamaImpl() {
        iLogin = ObjFactory.create(DialogLoginImpl.class);
    }

    @Deprecated
    @Override
    public void initSDK(final Activity activity) {
        initSDK(activity, SGameLanguage.zh_TW);
    }

    @Override
    public void initSDK(final Activity activity, final SGameLanguage gameLanguage) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama initSDK");
                Localization.gameLanguage(activity, gameLanguage);
                //清除上一次登录成功的返回值
                GamaUtil.saveSdkLoginData(activity, "");

                //获取Google 广告ID
                StarEventLogger.registerGoogleAdId(activity);
                //Gama平台安装上报
                StarEventLogger.reportInstallActivation(activity.getApplicationContext());
//                try {
//                    Fresco.initialize(activity.getApplicationContext());//初始化fb Fresco库
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                //广告
                StarEventLogger.activateApp(activity);

                //时间打点开始
                LogTimer.getInstance().start(activity);

//                        setGameLanguage(activity,SGameLanguage.zh_TW);

                ConfigRequest.requestBaseCfg(activity.getApplicationContext());//下载配置文件
//                ConfigRequest.requestTermsCfg(activity.getApplicationContext());//下载服务条款
                // 1.初始化fb sdk
//                SFacebookProxy.initFbSdk(activity.getApplicationContext());
                sFacebookProxy = new SFacebookProxy(activity.getApplicationContext());
                isInitSdk = true;
                //进行启动的查单
                SLog.logD(TAG, "Start launch query.");
                GooglePayHelper.getInstance().queryConsumablePurchase(activity);

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
                GamaUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息

                HashMap<String, Object> map = new HashMap<>();
                map.put(GamaAdsConstant.GAMA_EVENT_ROLEID, roleId);
                map.put(GamaAdsConstant.GAMA_EVENT_ROLENAME, roleName);
                map.put(GamaAdsConstant.GAMA_EVENT_ROLE_LEVEL, roleLevel);
                map.put(GamaAdsConstant.GAMA_EVENT_ROLE_VIP_LEVEL, vipLevel);
                map.put(GamaAdsConstant.GAMA_EVENT_SERVERCODE, severCode);
                map.put(GamaAdsConstant.GAMA_EVENT_SERVERNAME, serverName);
                StarEventLogger.trackingRoleInfo(activity, map);
            }
        });
    }

    @Override
    public void login(final Activity activity, final ILoginCallBack iLoginCallBack) {
        PL.i("IGama login");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("fb keyhash:" + SignatureUtil.getHashKey(activity, activity.getPackageName()));
                PL.i("google sha1:" + SignatureUtil.getSignatureSHA1WithColon(activity, activity.getPackageName()));
                if (iLogin != null) {
                    //清除上一次登录成功的返回值
                    GamaUtil.saveSdkLoginData(activity, "");

                    iLogin.initFacebookPro(activity, sFacebookProxy);
                    iLogin.startLogin(activity, iLoginCallBack);
                }
            }
        });


    }

    @Override
    public void pay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra, final IPayListener listener) {
        PL.i("IGama pay payType:" + payType.toString() + " ,cpOrderId:" + cpOrderId + ",productId:" + productId + ",extra:" + extra + ", IPayListener: " + listener);
        if ((System.currentTimeMillis() - firstClickTime) < 1000) {//防止连续点击
            PL.i("点击过快，无效");
            return;
        }
        iPayListener = listener;
        firstClickTime = System.currentTimeMillis();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (GamaUtil.needShowAgePage(activity)) {
                    final IGamaAgePresenter presenter = new GamaAgeImpl();
                    ((GamaAgeImpl) presenter).setAgeCallback(new GamaAgeCallback() {
                        @Override
                        public void onSuccess() {
                            PL.i("上报年龄成功");
                            presenter.requestAgeLimit(activity);
                        }

                        @Override
                        public void onFailure() {
                            if(listener != null) {
                                listener.onPayFinish(null);
                            }
                        }

                        @Override
                        public void canBuy() {
                            PL.i("未达到年龄购买限制");
                            startPay(activity, payType, cpOrderId, productId, extra);
                        }
                    });
                    presenter.goAgeStyleThree(activity);
                } else if (GamaUtil.needRequestAgeLimit(activity)) {
                    IGamaAgePresenter presenter = new GamaAgeImpl();
                    ((GamaAgeImpl) presenter).setAgeCallback(new GamaAgeCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure() {
                            if(listener != null) {
                                listener.onPayFinish(null);
                            }
                        }

                        @Override
                        public void canBuy() {
                            PL.i("未达到年龄购买限制");
                            startPay(activity, payType, cpOrderId, productId, extra);
                        }
                    });
                    presenter.requestAgeLimit(activity);
                } else {
                    startPay(activity, payType, cpOrderId, productId, extra);
                }
            }
        });
    }

    @Override
    public void openWebview(final Activity activity) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SGameBaseRequestBean webviewReqeustBean = new SGameBaseRequestBean(activity);

                //设置签名
//        appkey+gameCode+userId+roleId+timestamp
                webviewReqeustBean.setSignature(SStringUtil.toMd5(webviewReqeustBean.getAppKey() + webviewReqeustBean.getGameCode()
                        + webviewReqeustBean.getUserId() + webviewReqeustBean.getRoleId() + webviewReqeustBean.getTimestamp()));

                webviewReqeustBean.setRequestUrl(ResConfig.getActivityPreferredUrl(activity));//活动域名
                webviewReqeustBean.setRequestSpaUrl(ResConfig.getActivitySpareUrl(activity));
                webviewReqeustBean.setRequestMethod(activity.getResources().getString(R.string.gama_act_dynamic_method));

                SWebViewDialog sWebViewDialog = new SWebViewDialog(activity, R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);

                sWebViewDialog.setWebUrl(webviewReqeustBean.createPreRequestUrl());

                sWebViewDialog.show();
            }
        });
    }

    private void startPay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra) {
        if (payType == SPayType.OTHERS) {//第三方储值

            othersPay(activity, cpOrderId, extra);

        } else {//默认Google储值

            if (GamaUtil.getSdkCfg(activity) != null && GamaUtil.getSdkCfg(activity).openOthersPay(activity)) {//假若Google包侵权被下架，此配置可以启动三方储值
                PL.i("转第三方储值");
                othersPay(activity, cpOrderId, extra);

            } else {

                googlePay(activity, cpOrderId, productId, extra);
            }

        }
    }

    private void googlePay(Activity activity, String cpOrderId, String productId, String extra) {
        GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean = new GooglePayCreateOrderIdReqBean(activity);
        googlePayCreateOrderIdReqBean.setCpOrderId(cpOrderId);
        googlePayCreateOrderIdReqBean.setProductId(productId);
//        googlePayCreateOrderIdReqBean.setRoleLevel(roleLevel);
        googlePayCreateOrderIdReqBean.setExtra(extra);

        Intent i = new Intent(activity, GooglePayActivity2.class);
        i.putExtra(GooglePayActivity2.GooglePayReqBean_Extra_Key, googlePayCreateOrderIdReqBean);
        activity.startActivityForResult(i, GooglePayActivity2.GooglePayReqeustCode);
    }

    private void othersPay(Activity activity, String cpOrderId, String extra) {
        WebPayReqBean webPayReqBean = PayHelper.buildWebPayBean(activity, cpOrderId, extra);

        String payThirdUrl = null;
        if (GamaUtil.getSdkCfg(activity) != null) {

            payThirdUrl = GamaUtil.getSdkCfg(activity).getS_Third_PayUrl();
        }
        if (TextUtils.isEmpty(payThirdUrl)) {
            payThirdUrl = ResConfig.getPayPreferredUrl(activity) + ResConfig.getPayThirdMethod(activity);
        }
        webPayReqBean.setCompleteUrl(payThirdUrl);

        String webUrl = webPayReqBean.createPreRequestUrl();

        otherPayWebViewDialog = new SWebViewDialog(activity, R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        otherPayWebViewDialog.setWebUrl(webUrl);
        otherPayWebViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (iPayListener != null) {
                    PL.i(TAG, "OtherPay支付回调");
                    iPayListener.onPayFinish(new Bundle());
                } else {
                    PL.i(TAG, "OtherPay支付回调为空");
                }
            }
        });
        otherPayWebViewDialog.show();
    }

    @Override
    public void onCreate(final Activity activity) {
        PL.i("IGama onCreate");
        PL.i("the jar version:" + BuildConfig.JAR_VERSION);//打印版本号
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

                if (!isInitSdk) {
                    initSDK(activity, SGameLanguage.zh_TW);
                }
                if (iLogin != null) {
                    iLogin.onCreate(activity);
                }
                sGooglePlayGameServices = new SGooglePlayGameServices(activity);

                //permission授权
                //        PermissionUtil.requestPermissions_STORAGE(activity,PERMISSION_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onResume(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama onResume");
//        AppUtil.hideActivityBottomBar(activity);
                if (iLogin != null) {
                    iLogin.onResume(activity);
                }
                GooglePayHelper.getInstance().setForeground(true);
                GamaWebPageHelper.onResume(activity);
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
                if (otherPayWebViewDialog != null) {
                    otherPayWebViewDialog.onActivityResult(activity, requestCode, resultCode, data);
                }
                if (requestCode == GooglePayActivity2.GooglePayReqeustCode && resultCode == GooglePayActivity2.GooglePayResultCode) {
                    Bundle bundle = null;
                    if (data != null) {
                        bundle = data.getExtras();
                    }
                    if (bundle == null) {
                        bundle = new Bundle();
                    } else {
                        StarEventLogger.trackinPayEvent(activity, bundle);
                    }

                    if (iPayListener != null) { //支付刷新的回调
                        PL.i(TAG, "GooglePay支付回调");
                        iPayListener.onPayFinish(bundle);
                    } else {
                        PL.i(TAG, "GooglePay支付回调为空");
                    }
                    return;
                }

                if (sGooglePlayGameServices != null) {
                    sGooglePlayGameServices.handleActivityResult(activity, requestCode, resultCode, data);
                }

                GamaShare.onActivityResult(activity, requestCode, resultCode, data);
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
                GooglePayHelper.getInstance().setForeground(false);
            }
        });
    }

    @Override
    public void onDestroy(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama onDestroy");
                if (iLogin != null) {
                    iLogin.onDestroy(activity);
                }
                if (sFacebookProxy != null) {
                    sFacebookProxy.onDestroy(activity);
                }
                //时间打点结束
                LogTimer.getInstance().cancel();
                //退出游戏时停止定时查单
                GooglePayHelper.getInstance().stopQueryTask();
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

    @Deprecated
    @Override
    public void openWebPage(final Activity activity, final GamaOpenWebType type, final String url) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                openWebPage(activity, type, url, null);
            }
        });
    }

    @Override
    public void openWebPage(final Activity activity, final GamaOpenWebType type, final String url, final ISdkCallBack callBack) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GamaWebPageHelper.openWebPage(activity, type, url, callBack);
            }
        });
    }

    @Override
    public void openPlatform(final Activity activity) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                PL.i("IGama openPlatform");
//                if (GamaUtil.isLogin(activity)){
//                    activity.startActivity(new Intent(activity, PlatMainActivity.class));
//                }else {
//                    ToastUtils.toast(activity,"please login game first");
//                }
//            }
//        });

    }

    @Override
    public void gamaGetUserProfile(final Activity activity, final UserProfileCallback callBack) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String loginType = GamaUtil.getPreviousLoginType(activity);
                if (SLoginType.LOGIN_TYPE_FB.equals(loginType)) {
                    if (sFacebookProxy != null) {
                        sFacebookProxy.getMyProfile(activity, new SFacebookProxy.FbLoginCallBack() {
                            @Override
                            public void onCancel() {
                                if (callBack != null) {
                                    callBack.onCancel();
                                }
                            }

                            @Override
                            public void onError(String message) {
                                sFacebookProxy.fbLogin(activity, new SFacebookProxy.FbLoginCallBack() {
                                    @Override
                                    public void onCancel() {
                                        if (callBack != null) {
                                            callBack.onCancel();
                                        }
                                    }

                                    @Override
                                    public void onError(String message) {
                                        if (callBack != null) {
                                            callBack.onError(message);
                                        }
                                    }

                                    @Override
                                    public void onSuccess(FaceBookUser user) {
                                        sFacebookProxy.getMyProfile(activity, new SFacebookProxy.FbLoginCallBack() {
                                            @Override
                                            public void onCancel() {
                                                if (callBack != null) {
                                                    callBack.onCancel();
                                                }
                                            }

                                            @Override
                                            public void onError(String message) {
                                                if (callBack != null) {
                                                    callBack.onError(message);
                                                }
                                            }

                                            @Override
                                            public void onSuccess(FaceBookUser user) {
                                                UserInfo userInfo = new UserInfo();
                                                userInfo.setName(user.getName());
                                                userInfo.setGender(user.getGender());
                                                userInfo.setBirthday(user.getBirthday());
                                                userInfo.setUserThirdId(user.getUserFbId());
                                                userInfo.setPictureUri(user.getPictureUri());
                                                userInfo.setAccessTokenString(user.getAccessTokenString());
                                                userInfo.setTokenForBusiness(user.getTokenForBusiness());
                                                if (callBack != null) {
                                                    callBack.onSuccess(userInfo);
                                                }
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(FaceBookUser user) {
                                UserInfo userInfo = new UserInfo();
                                userInfo.setName(user.getName());
                                userInfo.setGender(user.getGender());
                                userInfo.setBirthday(user.getBirthday());
                                userInfo.setUserThirdId(user.getUserFbId());
                                userInfo.setPictureUri(user.getPictureUri());
                                userInfo.setAccessTokenString(user.getAccessTokenString());
                                userInfo.setTokenForBusiness(user.getTokenForBusiness());
                                if (callBack != null) {
                                    callBack.onSuccess(userInfo);
                                }
                            }
                        });
                    }
                } else {
                    if (callBack != null) {
                        callBack.onSuccess(new UserInfo());
                    }
                }
            }
        });
    }

    @Override
    public void gamaFetchFriends(final Activity activity, final GamaThirdPartyType type, final Bundle bundle, final String paging, final int limit, final FetchFriendsCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "type : " + type.name() + "  paging : " + paging + "  limit : " + limit + "  bundle : " + bundle);
                switch (type) {
                    case FACEBOOK:
                        if (sFacebookProxy != null) {
                            Bundle newBundle;
                            if (bundle == null) {
                                newBundle = new Bundle();
                                newBundle.putString("fields", "friends.limit(" + limit + "){name,picture.width(300)}");
                            } else {
                                newBundle = bundle;
                            }
                            sFacebookProxy.requestMyFriends(activity, newBundle, paging, new SFacebookProxy.RequestFriendsCallBack() {
                                @Override
                                public void onError() {
                                    if (callback != null) {
                                        callback.onError();
                                    }
                                }

                                @Override
                                public void onSuccess(JSONObject graphObject, List<FriendProfile> friendProfiles, String next, String previous, int count) {
                                    if (callback != null) {
                                        callback.onSuccess(graphObject, friendProfiles, next, previous, count);
                                    }
                                }
                            });
                        }
                }
            }
        });
    }

    @Override
    public void gamaShare(final Activity activity, final GamaThirdPartyType type, final String message, final String shareLinkUrl, final String picPath, final ISdkCallBack iSdkCallBack) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "type : " + type.name() + "  message : " + message + "  shareLinkUrl : " + shareLinkUrl + "  picPath : " + picPath);
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

                                if (SStringUtil.isNotEmpty(GamaUtil.getServerCode(activity)) && SStringUtil.isNotEmpty(GamaUtil.getRoleId(activity))) {
                                    try {
                                        if (newShareLinkUrl.contains("?")) {//userId+||S||+serverCode+||S||+roleId
                                            newShareLinkUrl = newShareLinkUrl + "&campaign=" + URLEncoder.encode(GamaUtil.getUid(activity) + "||S||" + GamaUtil.getServerCode(activity) + "||S||" + GamaUtil.getRoleId(activity), "UTF-8");
                                        } else {
                                            newShareLinkUrl = newShareLinkUrl + "?campaign=" + URLEncoder.encode(GamaUtil.getUid(activity) + "||S||" + GamaUtil.getServerCode(activity) + "||S||" + GamaUtil.getRoleId(activity), "UTF-8");
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                                sFacebookProxy.fbShare(activity, fbShareCallBack, "", "", shareLinkUrl, "");
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
                        GamaShare.share(activity, type, message, shareLinkUrl, picPath, iSdkCallBack);
                        break;
                }
            }
        });
    }

//    @Override
//    public void gamaSentMessageToSpecifiedFriends(Activity activity, GamaThirdPartyType type, Uri picUri, final ISdkCallBack iSdkCallBack) {
//        switch (type) {
//            case FACEBOOK:
//                if(sFacebookProxy != null) {
//                    sFacebookProxy.shareToMessenger(activity, picUri, new SFacebookProxy.FbShareCallBack() {
//                        @Override
//                        public void onCancel() {
//                            if (iSdkCallBack != null){
//                                iSdkCallBack.failure();
//                            }
//                        }
//
//                        @Override
//                        public void onError(String message) {
//                            if (iSdkCallBack != null){
//                                iSdkCallBack.failure();
//                            }
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            if (iSdkCallBack != null){
//                                iSdkCallBack.success();
//                            }
//                        }
//                    });
//                } else {
//                    if (iSdkCallBack != null){
//                        iSdkCallBack.failure();
//                    }
//                }
//        }
//    }

    @Override
    public void gamaInviteFriends(final Activity activity, final GamaThirdPartyType type, final List<FriendProfile> invitingList,
                                  final String message, final String title, final InviteFriendsCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "type : " + type.name() + "  message : " + message + "  title : " + title + "  invitingList : " + invitingList);
                switch (type) {
                    case FACEBOOK:
                        if (sFacebookProxy != null) {
                            sFacebookProxy.inviteFriends(activity, invitingList, title, message, new SFacebookProxy.FbInviteFriendsCallBack() {
                                @Override
                                public void onCancel() {
                                    if (callback != null) {
                                        callback.failure();
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    if (callback != null) {
                                        callback.failure();
                                    }
                                }

                                @Override
                                public void onSuccess(String requestId, List<String> requestRecipients) {
                                    if (callback != null) {
                                        callback.success(requestId, requestRecipients);
                                    }
                                }
                            });
                        } else {
                            if (callback != null) {
                                callback.failure();
                            }
                        }
                }
            }
        });
    }

    @Override
    public boolean gamaShouldShareWithType(Activity activity, GamaThirdPartyType type) {
        Log.i(TAG, "type : " + type.name());
        return GamaShare.shouldShareWithType(activity, type);
    }

    @Override
    public void gamaTrack(final Activity activity, final String eventName, final Map<String, Object> map, final Set<GamaAdsConstant.GamaEventReportChannel> channelSet) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StarEventLogger.trackingWithEventName(activity, eventName, map, channelSet);
            }
        });
    }

}

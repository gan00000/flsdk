package com.mw.sdk.out;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.core.base.ObjFactory;
import com.core.base.utils.AppUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SignatureUtil;
import com.core.base.utils.ToastUtils;
import com.gama.pay.IPay;
import com.gama.pay.IPayCallBack;
import com.gama.pay.IPayFactory;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.gp.bean.res.BasePayBean;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.bean.SPayType;
import com.mw.base.cfg.ConfigRequest;
import com.mw.base.utils.Localization;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.R;
import com.mw.sdk.SWebViewDialog;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.login.DialogLoginImpl;
import com.mw.sdk.login.ILogin;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.social.share.GamaShare;
import com.thirdlib.facebook.SFacebookProxy;

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

    private IPay iPay;
    protected IPayListener iPayListener;

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

            }
        });
    }

    @Override
    public void login(final Activity activity, final ILoginCallBack iLoginCallBack) {
        PL.i("sdk login");
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
    public void pay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra, final IPayListener listener) {
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
                startPay(activity, payType, cpOrderId, productId, extra, listener);
            }
        });
    }

    @Override
    public void openCs(Activity activity) {
//        openWebPage(activity,GamaOpenWebType.CUSTOM_URL,activity.getString(R.string.emm_service_url));
    }

    protected void startPay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra, IPayListener listener) {

        if (payType == SPayType.GOOGLE) {
//            googlePay(activity, cpOrderId, productId, extra);
            GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean = new GooglePayCreateOrderIdReqBean(activity);
            googlePayCreateOrderIdReqBean.setCpOrderId(cpOrderId);
            googlePayCreateOrderIdReqBean.setProductId(productId);
            googlePayCreateOrderIdReqBean.setExtra(extra);
//
//        Intent i = new Intent(activity, GooglePayActivity2.class);
//        i.putExtra(GooglePayActivity2.GooglePayReqBean_Extra_Key, googlePayCreateOrderIdReqBean);
//        activity.startActivityForResult(i, GooglePayActivity2.GooglePayReqeustCode);

            //设置Google储值的回调
            iPay.setIPayCallBack(new IPayCallBack() {
                @Override
                public void success(BasePayBean basePayBean) {
                    SdkEventLogger.trackinPayEvent(activity, basePayBean);
                    ToastUtils.toast(activity,R.string.text_finish_pay);
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

            iPay.startPay(activity,googlePayCreateOrderIdReqBean);

        } else if(payType == SPayType.WEB) {
//            a(activity, cpOrderId, extra);
        } else {//默认Google储值
            PL.i("不支持當前類型： " + payType.name());
        }
    }


    @Override
    public void onCreate(final Activity activity) {
        PL.i("sdk onCreate");
        PL.i("the jar version:" + BuildConfig.JAR_VERSION);//打印版本号

        PL.i("fb keyhash:" + SignatureUtil.getHashKey(activity, activity.getPackageName()));
        PL.i("google sha1:" + SignatureUtil.getSignatureSHA1WithColon(activity, activity.getPackageName()));

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

                ConfigRequest.requestBaseCfg(activity);//加载配置

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


   /* public void share(final Activity activity, final ThirdPartyType type, final String message, final String shareLinkUrl, final String picPath, final ISdkCallBack iSdkCallBack) {
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

                                if (SStringUtil.isNotEmpty(SdkUtil.getServerCode(activity)) && SStringUtil.isNotEmpty(SdkUtil.getRoleId(activity))) {
                                    try {
                                        if (newShareLinkUrl.contains("?")) {//userId+||S||+serverCode+||S||+roleId
                                            newShareLinkUrl = newShareLinkUrl + "&campaign=" + URLEncoder.encode(SdkUtil.getUid(activity) + "||S||" + SdkUtil.getServerCode(activity) + "||S||" + SdkUtil.getRoleId(activity), "UTF-8");
                                        } else {
                                            newShareLinkUrl = newShareLinkUrl + "?campaign=" + URLEncoder.encode(SdkUtil.getUid(activity) + "||S||" + SdkUtil.getServerCode(activity) + "||S||" + SdkUtil.getRoleId(activity), "UTF-8");
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

    @Override
    public boolean gamaShouldShareWithType(Activity activity, ThirdPartyType type) {
        Log.i(TAG, "type : " + type.name());
        return GamaShare.shouldShareWithType(activity, type);
    }*/

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
}

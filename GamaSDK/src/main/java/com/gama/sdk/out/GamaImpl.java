package com.gama.sdk.out;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.core.base.ObjFactory;
import com.core.base.utils.AppUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.SignatureUtil;
import com.core.base.utils.ToastUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.gama.base.bean.SGameBaseRequestBean;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.base.cfg.ConfigRequest;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.Localization;
import com.gama.data.login.ILoginCallBack;
import com.gama.pay.IPayCallBack;
import com.gama.pay.gp.GooglePayActivity2;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.gp.bean.req.WebPayReqBean;
import com.gama.pay.gp.util.PayHelper;
import com.gama.sdk.BuildConfig;
import com.gama.sdk.R;
import com.gama.sdk.SWebViewDialog;
import com.gama.sdk.ads.StarEventLogger;
import com.gama.sdk.callback.IPayListener;
import com.gama.sdk.login.DialogLoginImpl;
import com.gama.sdk.login.ILogin;
import com.gama.sdk.plat.PlatMainActivity;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGooglePlayGameServices;

import java.net.URLEncoder;


public class GamaImpl implements IGama {
    private static final String TAG = GamaImpl.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 401;
    private ILogin iLogin;

    private long firstClickTime;

    private static boolean isInitSdk = false;

    private SFacebookProxy sFacebookProxy;
    private SGooglePlayGameServices sGooglePlayGameServices;

    private SWebViewDialog otherPayWebViewDialog;

    private IPayListener iPayListener;

    public GamaImpl() {
        iLogin = ObjFactory.create(DialogLoginImpl.class);
    }

    @Override
    public void initSDK(final Activity activity) {
        PL.i("IGama initSDK");
        //清除上一次登录成功的返回值
        GamaUtil.saveSdkLoginData(activity,"");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //获取Google 广告ID
                StarEventLogger.registerGoogleAdId(activity);
                //Gama平台安装上报
                StarEventLogger.reportInstallActivation(activity.getApplicationContext());
                try {
                    Fresco.initialize(activity.getApplicationContext());//初始化fb Fresco库
                } catch (Exception e) {
                    e.printStackTrace();
                }

                setGameLanguage(activity,SGameLanguage.zh_TW);

                ConfigRequest.requestBaseCfg(activity.getApplicationContext());//下载配置文件
                ConfigRequest.requestTermsCfg(activity.getApplicationContext());//下载服务条款
                // 1.初始化fb sdk
                SFacebookProxy.initFbSdk(activity.getApplicationContext());
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
    public void registerRoleInfo(Activity activity, String roleId, String roleName, String roleLevel, String vipLevel, String severCode, String serverName) {
        PL.i("IGama registerRoleInfo");
        PL.i("roleId:" + roleId + ",roleName:" + roleName + ",roleLevel:" + roleLevel + ",vipLevel:" + vipLevel + ",severCode:" + severCode + ",serverName:" + serverName);
        GamaUtil.saveRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);//保存角色信息
    }

    @Override
    public void login(final Activity activity, final ILoginCallBack iLoginCallBack) {
        PL.i("IGama login");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("fb keyhash:" + SignatureUtil.getHashKey(activity, activity.getPackageName()));
                PL.i("google sha1:" + SignatureUtil.getSignatureSHA1WithColon(activity, activity.getPackageName()));
                if (iLogin != null){
                    //清除上一次登录成功的返回值
                    GamaUtil.saveSdkLoginData(activity,"");

                    iLogin.initFacebookPro(activity,sFacebookProxy);
                    iLogin.startLogin(activity, iLoginCallBack);
                }
            }
        });


    }

    @Override
    public void pay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra, IPayListener listener) {
        PL.i("IGama pay payType:" + payType.toString() + " ,cpOrderId:" + cpOrderId + ",productId:" + productId + ",extra:" + extra + ", IPayListener: " + listener);
        if ((System.currentTimeMillis() - firstClickTime) < 1000){//防止连续点击
            PL.i("点击过快，无效");
            return;
        }
        iPayListener = listener;
        firstClickTime = System.currentTimeMillis();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startPay(activity, payType, cpOrderId, productId, extra);
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

    @Override
    public void share(Activity activity, ISdkCallBack iSdkCallBack, String shareLinkUrl) {
        share(activity, iSdkCallBack,"","", shareLinkUrl,"");
    }

    @Override
    public void share(Activity activity, final ISdkCallBack iSdkCallBack, String title, String message, String shareLinkUrl, String sharePictureUrl) {
        if (sFacebookProxy != null){

            SFacebookProxy.FbShareCallBack fbShareCallBack = new SFacebookProxy.FbShareCallBack() {
                @Override
                public void onCancel() {
                    if (iSdkCallBack != null){
                        iSdkCallBack.failure();
                    }
                }

                @Override
                public void onError(String message) {
                    if (iSdkCallBack != null){
                        iSdkCallBack.failure();
                    }
                }

                @Override
                public void onSuccess() {
                    if (iSdkCallBack != null){
                        iSdkCallBack.success();
                    }
                }
            };
            if (SStringUtil.isNotEmpty(GamaUtil.getServerCode(activity)) && SStringUtil.isNotEmpty(GamaUtil.getRoleId(activity))) {
                if (shareLinkUrl.contains("?")){//userId+||S||+serverCode+||S||+roleId
                    shareLinkUrl = shareLinkUrl + "&campaign=" + URLEncoder.encode(GamaUtil.getUid(activity)+ "||S||" + GamaUtil.getServerCode(activity)+"||S||"+GamaUtil.getRoleId(activity));
                }else {
                    shareLinkUrl = shareLinkUrl + "?campaign=" + URLEncoder.encode(GamaUtil.getUid(activity)+ "||S||" + GamaUtil.getServerCode(activity)+"||S||"+GamaUtil.getRoleId(activity));
                }
            }
            sFacebookProxy.fbShare(activity, fbShareCallBack,title,message,shareLinkUrl,sharePictureUrl);
        }
    }



    private void startPay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra) {
        if (payType == SPayType.OTHERS){//第三方储值

            othersPay(activity, cpOrderId, extra);

        }else{//默认Google储值

            if (GamaUtil.getSdkCfg(activity) != null && GamaUtil.getSdkCfg(activity).openOthersPay(activity)){//假若Google包侵权被下架，此配置可以启动三方储值
                PL.i("转第三方储值");
                othersPay(activity, cpOrderId, extra);

            }else{

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
        activity.startActivityForResult(i,GooglePayActivity2.GooglePayReqeustCode);
    }

    private void othersPay(Activity activity, String cpOrderId, String extra) {
        WebPayReqBean webPayReqBean = PayHelper.buildWebPayBean(activity,cpOrderId,extra);

        String payThirdUrl = null;
        if (GamaUtil.getSdkCfg(activity) != null) {

            payThirdUrl = GamaUtil.getSdkCfg(activity).getS_Third_PayUrl();
        }
        if (TextUtils.isEmpty(payThirdUrl)){
            payThirdUrl = ResConfig.getPayPreferredUrl(activity) + ResConfig.getPayThirdMethod(activity);
        }
        webPayReqBean.setCompleteUrl(payThirdUrl);

        String webUrl = webPayReqBean.createPreRequestUrl();

        otherPayWebViewDialog = new SWebViewDialog(activity, R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        otherPayWebViewDialog.setWebUrl(webUrl);
        otherPayWebViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(iPayListener != null) {
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

                //广告
                StarEventLogger.activateApp(activity);

                if (!isInitSdk){
                    initSDK(activity);
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
    public void onResume(Activity activity) {
        PL.i("IGama onResume");
//        AppUtil.hideActivityBottomBar(activity);
        if (iLogin != null) {
            iLogin.onResume(activity);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        PL.i("IGama onActivityResult");
        if (iLogin != null) {
            iLogin.onActivityResult(activity, requestCode, resultCode, data);
        }
        if (sFacebookProxy != null){
            sFacebookProxy.onActivityResult(activity, requestCode, resultCode, data);
        }
        if (otherPayWebViewDialog != null){
            otherPayWebViewDialog.onActivityResult(activity, requestCode, resultCode, data);
        }
        if (requestCode == GooglePayActivity2.GooglePayReqeustCode && resultCode == GooglePayActivity2.GooglePayResultCode){
            if(iPayListener != null) { //支付刷新的回调
                PL.i(TAG, "GooglePay支付回调");
                if (data != null && data.getExtras() != null) {
                    Bundle b = data.getExtras();
                    iPayListener.onPayFinish(b);
                } else {
                    iPayListener.onPayFinish(new Bundle());
                }
            } else {
                PL.i(TAG, "GooglePay支付回调为空");
            }
            if (data != null && data.getExtras() != null){
                Bundle b = data.getExtras();
                GooglePayCreateOrderIdReqBean g = (GooglePayCreateOrderIdReqBean) data.getSerializableExtra("GooglePayCreateOrderIdReqBean");
                if (b.getInt("status") == 93 && g != null){//充值成功
                    /*try {
                        if (g.getGameCode().equals("gbmmd")) {//全球萌萌哒特殊处理
                            PL.i("google pay success,value:" + g.getPayValue());
                            Map<String,Double> id_price = new HashMap<>();
                            id_price.put("com.brmmd.3.99.month",3.99);
                            id_price.put("com.brmmd.19.99.month",19.99);
                            id_price.put("py.brmmd.1.99",1.99);
                            id_price.put("py.brmmd.4.99",4.99);
                            id_price.put("py.brmmd.9.99",9.99);
                            id_price.put("py.brmmd.29.99",29.99);
                            id_price.put("py.brmmd.49.99",49.99);
                            id_price.put("py.brmmd.99.99",99.99);
                            StarEventLogger.trackinPayEvent(activity,id_price.get(g.getProductId()));
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }*/
                    PL.i("google pay success");
                }
            }
            return;
        }

        if (sGooglePlayGameServices != null){
            sGooglePlayGameServices.handleActivityResult(activity, requestCode, resultCode, data);
        }
    }

    @Override
    public void onPause(Activity activity) {
        PL.i("IGama onPause");
        if (iLogin != null) {
            iLogin.onPause(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        PL.i("IGama onStop");
        if (iLogin != null) {
            iLogin.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        PL.i("IGama onDestroy");
        if (iLogin != null) {
            iLogin.onDestroy(activity);
        }
        if (sFacebookProxy != null){
            sFacebookProxy.onDestroy(activity);
        }
    }

    @Override
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        PL.i("IGama onRequestPermissionsResult");
    }

    @Override
    public void onWindowFocusChanged(Activity activity, boolean hasFocus) {
        PL.i("IGama onWindowFocusChanged: hasFocus -- " + hasFocus);
        if (hasFocus) {
            AppUtil.hideActivityBottomBar(activity);
        }
    }

    @Override
    public void openWebPage(Activity activity, String url) {
        SWebViewDialog sWebViewDialog = new SWebViewDialog(activity, R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);

        sWebViewDialog.setWebUrl(url);

        sWebViewDialog.show();
    }

    @Override
    public void openPlatform(final Activity activity) {
        PL.i("IGama openPlatform");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (GamaUtil.isLogin(activity)){
                    activity.startActivity(new Intent(activity, PlatMainActivity.class));
                }else {
                    ToastUtils.toast(activity,"please login game first");
                }
            }
        });

    }
}

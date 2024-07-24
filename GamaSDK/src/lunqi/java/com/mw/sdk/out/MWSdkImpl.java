package com.mw.sdk.out;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.req.ThirdLoginRegRequestBean;
import com.mw.sdk.bean.res.BasePayBean;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.SdkAppLdkoginView;
import com.mw.sdk.login.p.LoginPresenterImpl;
import com.mw.sdk.pay.IPayCallBack;
import com.thirdlib.lunqi.LunqiPayImpl;
import com.xlsdk.mediator.XLSDK;
import com.xlsdk.mediator.sdk.XLGameRoleInfo;
import com.xlsdk.mediator.sdk.listener.IXLExitNotifier;
import com.xlsdk.mediator.sdk.listener.IXLInitNotifier;
import com.xlsdk.mediator.sdk.listener.IXLLoginNotifier;
import com.xlsdk.mediator.sdk.listener.IXLPayNotifier;
import com.xlsdk.mediator.sdk.listener.IXLSwitchAccountNotifier;
import com.xlsdk.mediator.sdk.user.XLUserInfo;

import org.json.JSONObject;

public class MWSdkImpl extends BaseSdkImpl {

    private static final String TAG = MWSdkImpl.class.getSimpleName();

    SdkAppLdkoginView sdkAppLdkoginView;

    PayCreateOrderReqBean createOrderIdReqBean;

    LunqiPayImpl lunqiPay;

    boolean initSuccess;

    ILoginCallBack iLoginCallBack;

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);

        initNotifier();
        XLSDK.getInstance().init(activity);

        XLSDK.getInstance().onCreate();
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        XLSDK.getInstance().onActivityResult(activity, requestCode, resultCode, data);
        super.onActivityResult(activity, requestCode, resultCode, data);
    }


//    public void onStart(){
//        XLSDK.getInstance().onStart(Activity.this);
//        super.onStart();
//    }

    @Override
    public void onPause(Activity activity) {
        XLSDK.getInstance().onPause(activity);
        super.onPause(activity);
    }


    @Override
    public void onResume(Activity activity) {
        XLSDK.getInstance().onResume(activity);
        super.onResume(activity);
    }

//    public void onNewIntent(Intent newIntent){
//        super.onNewIntent(intent);
//        XLSDK.getInstance().onNewIntent(intent);
//    }

    @Override
    public void onStop(Activity activity) {

        XLSDK.getInstance().onStop(activity);
        super.onStop(activity);
    }


    @Override
    public void onDestroy(Activity activity) {
        XLSDK.getInstance().onDestroy(activity);
        super.onDestroy(activity);
    }


//    public void onBackPressed(){
//        XLSDK.getInstance().onBackPressed(Activity.this);
//        super.onBackPressed();
//    }


//    public void onRestart(){
//        XLSDK.getInstance().onRestart(Activity.this);
//        super.onRestart();
//    }

    @Override
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        XLSDK.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
    }


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        XLSDK.getInstance().onSaveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }
//
//
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        XLSDK.getInstance().onConfigurationChanged(newConfig);
//        super.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        XLSDK.getInstance().onWindowFocusChanged(hasFocus);
//        super.onWindowFocusChanged(hasFocus);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return super.onKeyDown(keyCode,event);
//    }


    private void initNotifier() {


        XLSDK.getInstance().setIXLInitNotifier(new IXLInitNotifier() {
            @Override
            public void onFailed(String msg) {
                //初始化失败
                PL.i("init onFailed");
            }

            @Override
            public void onSuccess() {
                //初始化成功
                initSuccess = true;
                PL.i("init onSuccess");
                if (lunqiPay == null) {
                    lunqiPay = new LunqiPayImpl(activity);
                }
            }
        });


        XLSDK.getInstance().setIXLLoginNotifier(new IXLLoginNotifier() {
            @Override
            public void onLoginSuccess(XLUserInfo userInfo) {
                handleLoginSuccess(userInfo);

            }

            @Override
            public void onLoginFailed(String msg) {
                //登录失败
                PL.i("lunqi onLoginFailed msg=" + msg);
            }

            @Override
            public void onLoginCancell(String msg) {
                //登录取消
                PL.i("lunqi onLoginFailed msg=" + msg);
            }

            @Override
            public void onLogout() {
                //注销回调,收到此回调后需切换到游戏登录场景并再次调用login接口
                PL.i("lunqi onLogout");
                //login(activity, iLoginCallBack);
                if (iLoginCallBack != null){
                    iLoginCallBack.onLogout("");
                }
            }
        });

        XLSDK.getInstance().setIxlSwitchAccountNotifier(new IXLSwitchAccountNotifier() {


            @Override
            public void onLoginSuccess(XLUserInfo userInfo) {
                handleLoginSuccess(userInfo);
            }

            @Override
            public void onLoginFailed(String msg) {
                //登录失败
            }

            @Override
            public void onLoginCancell(String msg) {
                //登录取消
            }

            @Override
            public void onLogout() {
                //注销回调,收到此回调后需切换到游戏登录场景并再次调用login接口
                if (iLoginCallBack != null){
                    iLoginCallBack.onLogout("");
                }
            }
        });

        XLSDK.getInstance().setIXLPayNotifier(new IXLPayNotifier() {
            @Override
            public void onCancel(String msg) {
                //支付取消
            }

            @Override
            public void onFailed(String msg) {
                //支付失败
            }


            @Override
            public void onSuccess() {
                //支付成功
            }
        });

        XLSDK.getInstance().setIXLExitNotifier(new IXLExitNotifier() {


            @Override
            public void onFailed(String msg) {
                //退出失败,游戏处理自身退出逻辑 例如调用Finish()
            }


            @Override
            public void onSuccess() {
                //退出成功,不用处理
            }
        });


    }

    private void handleLoginSuccess(XLUserInfo userInfo) {
        //登录成功
        String ck = userInfo.getCk();
        String t = userInfo.getT();
        String sitecode = userInfo.getSitecode();
        String passport = userInfo.getPassport();//用户标识
        String gameCode = userInfo.getGameCode();

//        MD5(sitecode + t + loginkey + passport)
        String loginkey = "ZztiAfFNnLYoKFaGuH";
        String ead = SStringUtil.toMd5(sitecode + t + loginkey + passport);
        if (!ck.equals(ead)){
            //ToastUtils.toast(activity,"Account verification failed");
//            return;
            PL.i("Account verification failed");
        }

        //登录验证
        PL.i("lunqi onLoginSuccess passport = " + passport);
        if (SStringUtil.isNotEmpty(passport)) {
            LoginPresenterImpl loginPresenter = new LoginPresenterImpl();
            loginPresenter.setBaseView(sdkAppLdkoginView);

            ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
            thirdLoginRegRequestBean.setThirdPlatId(passport);
            thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_LUNQI);
            thirdLoginRegRequestBean.setThirdAccount("");

            loginPresenter.thirdPlatLogin(activity, thirdLoginRegRequestBean);
        }
    }

    @Override
    public void registerRoleInfo(Activity activity, String roleId, String roleName, String roleLevel, String vipLevel, String severCode, String serverName) {
        super.registerRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);

//        XLGameRoleInfo roleInfo = new XLGameRoleInfo();
//        roleInfo.setServerID(severCode);//服务器id
//        roleInfo.setServerName(serverName);//服务器名称
//        roleInfo.setGameRoleName(roleName);//角色名称
//        roleInfo.setGameRoleID(roleId);//角色id
//        roleInfo.setGameBalance("0");//角色用户余额.没有传"0"
//        roleInfo.setVipLevel(vipLevel); //设置当前用户vip等级，必须为数字整型字符串,请勿传"vip1"等类似字符串,没有传"0"
//        roleInfo.setGameUserLevel(roleLevel);//设置游戏角色等级,没有传"0"
//        roleInfo.setPartyName("");//设置帮派名称,没有传""
//        roleInfo.setRoleCreateTime("1473141432");//值为10位数时间戳,角色创建时间
//        roleInfo.setPartyId("0");//设置帮派id，必须为整型字符串,没有传"0"
//        roleInfo.setGameRoleGender("");//角色性别，gg或mm
//
//        roleInfo.setGameRolePower(""); //，设置角色战力，必须为整型字符串,没有传"0"
//        roleInfo.setPartyRoleId("11"); //设置角色在帮派中的id,没有传"0"
//        roleInfo.setPartyRoleName("asdas"); //设置角色在帮派中的名称,没有传""
//        roleInfo.setProfessionId("38"); //设置角色职业id，必须为整型字符串,没有传"0"
//        roleInfo.setProfession("法师"); //设置角色职业名称
//
////创角时传 XLSDK.GAME_DATA_TYPE_CREATE_ROLE
////角色登录时传 XLSDK.GAME_DATA_TYPE_ROLE_LOGIN
////角色升级时传 XLSDK.GAME_DATA_TYPE_LEVEL
//        XLSDK.getInstance().setGameRoleInfo(activity,roleInfo,XLSDK.GAME_DATA_TYPE_CREATE_ROLE);

    }

    @Override
    public void login(Activity activity, ILoginCallBack iLoginCallBack) {

        PL.i("sdk login");
        this.activity = activity;
        this.iLoginCallBack = iLoginCallBack;
        sdkAppLdkoginView = new SdkAppLdkoginView();
        sdkAppLdkoginView.setiLoginCallBack(iLoginCallBack);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XLSDK.getInstance().Login();
            }
        });

    }

    @Override
    public void switchLogin(Activity activity, ILoginCallBack iLoginCallBack) {
        this.activity = activity;
        this.iLoginCallBack = iLoginCallBack;
        XLSDK.getInstance().switchLogin();
    }

    @Override
    protected void doLunqiPay(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {
        super.doLunqiPay(activity, payCreateOrderReqBean);
        this.createOrderIdReqBean = payCreateOrderReqBean;
        if (lunqiPay != null) {

            lunqiPay.setPayCallBack(new IPayCallBack() {
                @Override
                public void success(BasePayBean basePayBean) {
                    if (iPayListener != null){
                        iPayListener.onPaySuccess(basePayBean.getProductId(), basePayBean.getCpOrderId());
                    }
                }

                @Override
                public void fail(BasePayBean basePayBean) {
                    if (iPayListener != null){
                        iPayListener.onPayFail();
                    }
                }

                @Override
                public void cancel(String msg) {
                    if (iPayListener != null){
                        iPayListener.onPayFail();
                    }
                }
            });
            lunqiPay.startPay(activity, createOrderIdReqBean);
        }

    }

    @Override
    public void shareLine(Activity activity, String content, ISdkCallBack iSdkCallBack) {
    }

    @Override
    public void shareFacebook(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
    }

    @Override
    public void share(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
    }

    @Override
    public void share(Activity activity, ThirdPartyType type, String hashTag, String message, String shareLinkUrl, String picPath, ISdkCallBack iSdkCallBack) {
    }

    @Override
    public void requestStoreReview(Activity activity, SFCallBack sfCallBack) {

    }

    @Override
    protected void onCreate_OnUi(Activity activity) {
        super.onCreate_OnUi(activity);
        iPay = null;
//        sFacebookProxy = null;
    }

    @Override
    public void trackEvent(Activity activity, String eventName, JSONObject propertieJsonObj, int m){

        Log.i(TAG, "trackEvent name = " + eventName);
        if (eventName == null){
            Log.i(TAG, "trackEvent eventName is null");
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                SdkEventLogger.sendEventToSever(activity, eventName);
//                if (propertieJsonObj != null){
//                    SdkEventLogger.trackingWithEventName(activity, eventName, JsonUtil.jsonObjectToMap(propertieJsonObj), EventConstant.AdType.AdTypeAllChannel);
//                }else {
//                    SdkEventLogger.trackingWithEventName(activity, eventName, null, EventConstant.AdType.AdTypeAllChannel);
//                }
//                TDAnalyticsHelper.trackEvent(eventName, propertieJsonObj, 0);

                XLSDK.getInstance().callFunctionWithParams(activity, 0, eventName);
            }
        });
    }

    @Override
    public void openCs(Activity activity) {
        super.openCs(activity);

        XLSDK.getInstance().callFunctionWithParams(activity,2, "");
    }
}

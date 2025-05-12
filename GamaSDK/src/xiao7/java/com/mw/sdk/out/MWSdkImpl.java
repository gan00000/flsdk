package com.mw.sdk.out;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.R;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.req.ThirdLoginRegRequestBean;
import com.mw.sdk.bean.res.BasePayBean;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.SdkAppLdkoginView;
import com.mw.sdk.login.p.LoginPresenterImpl;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.utils.SdkUtil;
import com.smwl.smsdk.abstrat.SMInitListener;
import com.smwl.smsdk.abstrat.SMLoginListener;
import com.smwl.smsdk.app.SMPlatformManager;
import com.smwl.smsdk.bean.RoleInfo;
import com.smwl.smsdk.bean.SMUserInfo;
import com.thirdlib.xiao7.Xiao7PayImpl;

import org.json.JSONObject;

public class MWSdkImpl extends BaseSdkImpl {

    private static final String TAG = MWSdkImpl.class.getSimpleName();

    SdkAppLdkoginView sdkAppLdkoginView;

    PayCreateOrderReqBean createOrderIdReqBean;

    Xiao7PayImpl payImplObj;

    boolean initSuccess;

    ILoginCallBack iLoginCallBack;

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);

        sdkInit(activity);

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
    }


//    public void onStart(){
//        XLSDK.getInstance().onStart(Activity.this);
//        super.onStart();
//    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
    }


    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
    }

//    public void onNewIntent(Intent newIntent){
//        super.onNewIntent(intent);
//        XLSDK.getInstance().onNewIntent(intent);
//    }

    @Override
    public void onStop(Activity activity) {
        super.onStop(activity);
    }


    @Override
    public void onDestroy(Activity activity) {
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


    private void sdkInit(Activity mActivity) {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 程序的入口执行初始化操作：一般放在游戏入口的activity里面用主线程调用
                // 需要传入三个参数：游戏activity、appkey、SMInitListener
                SdkUtil.saveX7Guid(mActivity,"");
                SMPlatformManager.getInstance().init(mActivity,
                        mActivity.getString(R.string.sdk_xiao7_appkey), new SMInitListener() {
                            @Override
                            public void onSuccess() {
                                Log.i("X7SDKDemo", "游戏初始化成功");
                            }

                            @Override
                            public void onFail(String reason) {
                                Log.i("X7SDKDemo", "游戏初始化失败：" + reason);
                                ToastUtils.toast(mActivity, "error=>" + reason);
                            }
                        });
            }
        });

    }

    private void handleLoginSuccess(String token) {
        //登录成功
        PL.i("handleLoginSuccess token=" + token);
        if (SStringUtil.isEmpty(token)){
            return;
        }
        LoginPresenterImpl loginPresenter = new LoginPresenterImpl();
        loginPresenter.setBaseView(sdkAppLdkoginView);

        ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
        thirdLoginRegRequestBean.setThirdPlatId("");
        thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_XIAO7);
        thirdLoginRegRequestBean.setThirdAccount("");
        thirdLoginRegRequestBean.setThirdAccessToken(token);

        loginPresenter.thirdPlatLogin(activity, thirdLoginRegRequestBean);
    }

    @Override
    public void registerRoleInfo(Activity activity, String roleId, String roleName, String roleLevel, String vipLevel, String severCode, String serverName) {
        super.registerRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // RoleInfo的九个参数都不允许为空
                RoleInfo roleInfo = new RoleInfo();
                roleInfo.setGame_area(serverName);
                roleInfo.setGame_area_id(severCode);
                roleInfo.setGame_guid(SdkUtil.getX7Guid(activity));
                roleInfo.setGame_role_id(roleId);
                roleInfo.setGame_role_name(roleName);
                roleInfo.setRoleLevel(roleLevel);
                roleInfo.setRoleCE("-1");
                roleInfo.setRoleStage("-1");
                roleInfo.setRoleRechargeAmount("-1");
                SMPlatformManager.getInstance().smAfterChooseRoleSendInfo(activity, roleInfo);

            }
        });

    }

    @Override
    public void login(Activity activity, ILoginCallBack iLoginCallBack) {

        PL.i("sdk login");
        this.activity = activity;
        this.iLoginCallBack = iLoginCallBack;
        sdkAppLdkoginView = new SdkAppLdkoginView();
        sdkAppLdkoginView.setiLoginCallBack(iLoginCallBack);
        SdkUtil.saveX7Guid(activity,"");
        // 登录接口代码实例
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SMPlatformManager.getInstance().login(activity, new SMLoginListener() {
                    @Override
                    public void onLoginSuccess(SMUserInfo loginInfo) {
                        Log.i("X7SDKDemo", "登录成功");
                        // 客户端登录成功后，会通过如下方式得到token值，游戏客户端传token给自己服务端，
                        // 服务端做登录验证，登录成功后，会返回给游戏guid，每一个小7通行证可以拥有至多
                        // 10个子账号（guid），guid对应了游戏中的游戏账号
                        String tokenkey = loginInfo.getTokenkey();
//                        Intent intent = new Intent(FirstActivity.this, DemoMainActivity.class);
//                        startActivity(intent);

                        handleLoginSuccess(tokenkey);
                    }

                    @Override
                    public void onLoginFailed(String reason) {
                        Log.i("X7SDKDemo", "登录失败：" + reason);
                        ToastUtils.toast(activity, reason);
                    }

                    @Override
                    public void onLoginCancell(String reason) {
                        Log.i("X7SDKDemo", "登录取消：" + reason);
                    }

                    @Override
                    public void onLogoutSuccess(boolean isSwitch) {
                        // 登出成功，isSwitch 为false代表是游戏方调用logout()导致的登出，
                        // 为true代表是SDK内部切换账号/小号导致的登出
                        Log.i("X7SDKDemo", "登出成功，是否切换账号/小号：" + isSwitch);
                        // 在Demo中发送广播，模拟关闭游戏的主界面
//                        // isSwitch为true，需调用登录接口
//                        if (isSwitch) {
//                            toLogin();
//                        }

                        // SDK内部退出登录，需要游戏方处理返回到游戏登录界面
                        if (iLoginCallBack != null){
                            iLoginCallBack.onLogout(String.valueOf(isSwitch));
                        }

                    }
                });
            }
        });
    }

    @Override
    public void switchLogin(Activity activity, ILoginCallBack iLoginCallBack) {
        this.activity = activity;
        this.iLoginCallBack = iLoginCallBack;
        SMPlatformManager.getInstance().logout();
        login(activity, iLoginCallBack);
    }

    @Override
    protected void x7Pay(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {
        super.doLunqiPay(activity, payCreateOrderReqBean);
        this.createOrderIdReqBean = payCreateOrderReqBean;

        if (payImplObj == null) {
            payImplObj = new Xiao7PayImpl(activity);
        }

        payImplObj.setPayCallBack(new IPayCallBack() {
            @Override
            public void success(BasePayBean basePayBean) {
                if (iPayListener != null) {
                    iPayListener.onPaySuccess(basePayBean.getProductId(), basePayBean.getCpOrderId());
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
        payImplObj.startPay(activity, createOrderIdReqBean);

    }

//    @Override
//    public void shareLine(Activity activity, String content, ISdkCallBack iSdkCallBack) {
//    }
//
//    @Override
//    public void shareFacebook(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
//    }
//
//    @Override
//    public void share(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
//    }

    @Override
    public void share(Activity activity, ThirdPartyType type, String hashTag, String message, String shareLinkUrl, String picPath, ISdkCallBack iSdkCallBack) {
//        AppUtil.openInOsWebApp(activity, " https://www.facebook.com/ldgameofficial");
    }

    @Override
    public void requestStoreReview(Activity activity, SFCallBack sfCallBack) {

        if (sfCallBack != null){
            sfCallBack.success(null,"");
        }

    }

    @Override
    public void showUpgradeAccountView(Activity activity, SFCallBack sfCallBack) {
        if (sfCallBack != null){
            sfCallBack.success(null,"");
        }
    }

    @Override
    protected void onCreate_OnUi(Activity activity) {
        super.onCreate_OnUi(activity);
        iPay = null;
//        sFacebookProxy = null;
    }

    @Override
    public void trackEvent(Activity activity, String eventName, JSONObject propertieJsonObj, int m){

//        Log.i(TAG, "trackEvent name = " + eventName);
//        if (eventName == null){
//            Log.i(TAG, "trackEvent eventName is null");
//            return;
//        }
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
    }

}

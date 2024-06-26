package com.mw.sdk.out;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.ld.sdk.LDSdkManager;
import com.ld.sdk.core.bean.GameRoleInfo;
import com.ld.sdk.internal.LDCallback;
import com.ld.sdk.internal.LDException;
import com.ld.sdk.internal.LDLoginCallback;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.req.ThirdLoginRegRequestBean;
import com.mw.sdk.bean.res.BasePayBean;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.SdkAppLdkoginView;
import com.mw.sdk.login.p.LoginPresenterImpl;
import com.mw.sdk.pay.IPayCallBack;
import com.thirdlib.ld.LDPayImpl;

import org.json.JSONObject;

public class MWSdkImpl extends BaseSdkImpl {

    private static final String TAG = MWSdkImpl.class.getSimpleName();

    SdkAppLdkoginView sdkAppLdkoginView;

    PayCreateOrderReqBean createOrderIdReqBean;

    LDPayImpl payImplObj;

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
        LDSdkManager.getInstance().onPause(activity);
        super.onPause(activity);
    }


    @Override
    public void onResume(Activity activity) {
        LDSdkManager.getInstance().onResume(activity);
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
        LDSdkManager.getInstance().unInit(activity);
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


    private void sdkInit(Activity context) {

        LDSdkManager.getInstance().init(context, new LDCallback<Boolean>() {

            @Override
            public void done(Boolean status, @Nullable LDException e) {
                if (e == null) {//使用e来判断成功还是失败
                    //成功：此时status=true
                    PL.i("LDSdkManager init success");
                } else {
                    //失败:此时status=false，失败原因：e.toString()
                    PL.i("LDSdkManager init fail");
                }
            }
        });


    }

    private void handleLoginSuccess(String userId, String token) {
        //登录成功
        PL.i("handleLoginSuccess userId=" + userId + ",token=" + token);
        if (SStringUtil.isEmpty(userId) || SStringUtil.isEmpty(token)){
            return;
        }
        LoginPresenterImpl loginPresenter = new LoginPresenterImpl();
        loginPresenter.setBaseView(sdkAppLdkoginView);

        ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
        thirdLoginRegRequestBean.setThirdPlatId(userId);
        thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_LD);
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
                GameRoleInfo ldGameInfo = new GameRoleInfo();
                ldGameInfo.serverId = severCode;      // 服务器id
                ldGameInfo.serverName = serverName; // 服务器名字
                ldGameInfo.roleId = roleId;     // 角色id
                ldGameInfo.roleName = roleName;     // 角色名字
                ldGameInfo.roleType = "";     // 角色类型，例如：战士，魔法师，弓箭手
                ldGameInfo.level = roleLevel;         // 等级，如果没有，可传固定字符串"0"
                ldGameInfo.money = "0"; // 游戏的金币数，如果没有，可传固定字符串"0"
                ldGameInfo.partyName = "0";    // 公会，如果没有，可传固定字符串"0"
                ldGameInfo.powerNum = 0;     // 角色战斗力，如果没有，可传固定字符串"0"
                try {
                    ldGameInfo.vipLevel = Integer.parseInt(vipLevel);         // vip等级，如果没有，可传固定字符串"0"
                } catch (NumberFormatException e) {
                    ldGameInfo.vipLevel = 0;
                }
                LDSdkManager.getInstance().enterGame(activity, ldGameInfo, new LDCallback<Boolean>() {
                    @Override
                    public void done(Boolean t, @Nullable LDException e) {
                        if (e == null) {//使用e来判断成功还是失败
                            //成功
                        } else {
                            //失败原因：e.toString()
                        }
                    }
                });
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LDSdkManager.getInstance().showLoginView(activity, new LDLoginCallback() {
                    @Override
                    public void onSuccess(String cpUserId, String cpToken) {
                        //登录成功
//                        cpUserId	String	LD用户id，用于最下方的第5步：登录验证
//                        cpToken	String	用于最下方的第5步：登录验证

                        handleLoginSuccess(cpUserId, cpToken);

                    }

                    @Override
                    public void onError(String error) {
                        //登录失败：其中error代表失败原因
                    }

                    @Override
                    public void onLogout() {
                        // SDK内部退出登录，需要游戏方处理返回到游戏登录界面
                    }
                });

            }
        });

    }

    @Override
    public void switchLogin(Activity activity, ILoginCallBack iLoginCallBack) {
        this.activity = activity;
        this.iLoginCallBack = iLoginCallBack;
        login(activity, iLoginCallBack);
    }

    @Override
    protected void LDPay(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean) {
        super.doLunqiPay(activity, payCreateOrderReqBean);
        this.createOrderIdReqBean = payCreateOrderReqBean;
        if (payImplObj != null) {

            payImplObj.setPayCallBack(new IPayCallBack() {
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
            payImplObj.startPay(activity, createOrderIdReqBean);
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
            }
        });
    }
}

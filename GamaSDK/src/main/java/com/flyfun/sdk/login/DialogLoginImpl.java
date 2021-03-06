package com.flyfun.sdk.login;

import android.app.Activity;
import android.content.Intent;

import com.facebook.internal.CallbackManagerImpl;
import com.flyfun.data.login.ILoginCallBack;
import com.flyfun.sdk.callback.GamaCommonViewCallback;
import com.flyfun.sdk.login.widget.v2.StartTermsLayoutV2;
import com.flyfun.sdk.utils.DialogUtil;
import com.flyfun.thirdlib.facebook.SFacebookProxy;
import com.flyfun.thirdlib.google.SGoogleSignIn;
import com.flyfun.thirdlib.twitter.GamaTwitterLogin;
import com.flyfun.base.utils.GamaUtil;

/**
 * Created by gan on 2017/4/12.
 */

public class DialogLoginImpl implements ILogin {

    private SFacebookProxy sFacebookProxy;

    private SGoogleSignIn sGoogleSignIn;

    private GamaTwitterLogin twitterLogin;


    @Override
    public void onCreate(Activity activity) {

        sGoogleSignIn = new SGoogleSignIn(activity, DialogUtil.createLoadingDialog(activity, "Loading..."));

        if(GamaUtil.isJapan(activity)) {
            twitterLogin = new GamaTwitterLogin(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (sFacebookProxy != null && requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            sFacebookProxy.onActivityResult(activity, requestCode, resultCode, data);
        }
        if (sGoogleSignIn != null){
            sGoogleSignIn.handleActivityResult(activity,requestCode,resultCode,data);
        }
        if(twitterLogin != null) {
            twitterLogin.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPause(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }

    @Override
    public void startLogin(final Activity activity, final ILoginCallBack iLoginCallBack) {

        if(GamaUtil.isJapan(activity) && twitterLogin == null) {
            twitterLogin = new GamaTwitterLogin(activity);
        }
        boolean isTermRead = GamaUtil.getStartTermRead(activity);

        if(!isTermRead) {
            GamaCommonViewCallback commonViewCallback = new GamaCommonViewCallback() {
                @Override
                public void onSuccess() {
                    goDialogView(activity, iLoginCallBack);
                }

                @Override
                public void onFailure() {
                    if(iLoginCallBack != null) { //回调退出登录界面的状态
                        iLoginCallBack.onLogin(null);
                    }
                }
            };
           /* if (GamaUtil.isNorthAmarican(activity)) {
                StartTermsLayoutV2En termsLayoutV2 = new StartTermsLayoutV2En(activity,
                        com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen, commonViewCallback);
                termsLayoutV2.show();
            } else {
                StartTermsLayoutV2 termsLayoutV2 = new StartTermsLayoutV2(activity,
                        com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen, commonViewCallback);
                termsLayoutV2.show();
            }*/

            StartTermsLayoutV2 termsLayoutV2 = new StartTermsLayoutV2(activity,
                    com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen, commonViewCallback);
            termsLayoutV2.show();
        } else {
            goDialogView(activity, iLoginCallBack);
        }
    }

    private void goDialogView(Activity activity, ILoginCallBack iLoginCallBack) {
       /* if (GamaUtil.isNorthAmarican(activity)) {
            SLoginDialogV2En sLoginDialog = new SLoginDialogV2En(activity, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
            sLoginDialog.setSFacebookProxy(sFacebookProxy);
            sLoginDialog.setSGoogleSignIn(sGoogleSignIn);
            if (twitterLogin != null) {
                sLoginDialog.setTwitterLogin(twitterLogin);
            }
            sLoginDialog.setLoginCallBack(iLoginCallBack);
            sLoginDialog.show();
        } else {
            SLoginDialogV2 sLoginDialog = new SLoginDialogV2(activity, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
            sLoginDialog.setSFacebookProxy(sFacebookProxy);
            sLoginDialog.setSGoogleSignIn(sGoogleSignIn);
            if (twitterLogin != null) {
                sLoginDialog.setTwitterLogin(twitterLogin);
            }
            sLoginDialog.setLoginCallBack(iLoginCallBack);
            sLoginDialog.show();
        }*/

        SLoginDialogV2 sLoginDialog = new SLoginDialogV2(activity, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        sLoginDialog.setSFacebookProxy(sFacebookProxy);
        sLoginDialog.setSGoogleSignIn(sGoogleSignIn);
        if (twitterLogin != null) {
            sLoginDialog.setTwitterLogin(twitterLogin);
        }
        sLoginDialog.setLoginCallBack(iLoginCallBack);
        sLoginDialog.show();
    }

    @Override
    public void initFacebookPro(Activity activity, SFacebookProxy sFacebookProxy) {
        this.sFacebookProxy = sFacebookProxy;
    }
}

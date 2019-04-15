package com.gama.sdk.login;

import android.app.Activity;
import android.content.Intent;

import com.facebook.internal.CallbackManagerImpl;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.Localization;
import com.gama.data.login.ILoginCallBack;
import com.gama.sdk.callback.GamaCommonViewCallback;
import com.gama.sdk.login.widget.v2.StartTermsLayoutV2;
import com.gama.sdk.utils.DialogUtil;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGoogleSignIn;
import com.gama.thirdlib.twitter.GamaTwitterLogin;
import com.twitter.sdk.android.core.Twitter;

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

        if(SGameLanguage.ja_JP == Localization.getSGameLanguage(activity)) {
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

        if(SGameLanguage.ja_JP == Localization.getSGameLanguage(activity) && twitterLogin == null) {
            twitterLogin = new GamaTwitterLogin(activity);
        }
        boolean isTermRead = GamaUtil.getStartTermRead(activity);

        if(!isTermRead) {
            StartTermsLayoutV2 termsLayoutV2 = new StartTermsLayoutV2(activity, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen, new GamaCommonViewCallback() {
                @Override
                public void onSuccess() {
                    SLoginDialogV2 sLoginDialog = new SLoginDialogV2(activity, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                    sLoginDialog.setSFacebookProxy(sFacebookProxy);
                    sLoginDialog.setSGoogleSignIn(sGoogleSignIn);
                    if(twitterLogin != null) {
                        sLoginDialog.setTwitterLogin(twitterLogin);
                    }
                    sLoginDialog.setLoginCallBack(iLoginCallBack);
                    sLoginDialog.show();
                }

                @Override
                public void onFailure() {
                    if(iLoginCallBack != null) { //回调退出登录界面的状态
                        iLoginCallBack.onLogin(null);
                    }
                }
            });
            termsLayoutV2.show();
        } else {
            SLoginDialogV2 sLoginDialog = new SLoginDialogV2(activity, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
            sLoginDialog.setSFacebookProxy(sFacebookProxy);
            sLoginDialog.setSGoogleSignIn(sGoogleSignIn);
            if(twitterLogin != null) {
                sLoginDialog.setTwitterLogin(twitterLogin);
            }
            sLoginDialog.setLoginCallBack(iLoginCallBack);
            sLoginDialog.show();
        }
    }

    @Override
    public void initFacebookPro(Activity activity, SFacebookProxy sFacebookProxy) {
        this.sFacebookProxy = sFacebookProxy;
    }
}

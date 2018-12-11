package com.gama.sdk.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.gama.base.utils.GamaUtil;
import com.gama.data.login.ILoginCallBack;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.v2.StartTermsLayoutV2;
import com.gama.sdk.utils.DialogUtil;
import com.gama.thirdlib.facebook.SFacebookProxy;
import com.gama.thirdlib.google.SGoogleSignIn;

/**
 * Created by gan on 2017/4/12.
 */

public class DialogLoginImpl implements ILogin {

    private SFacebookProxy sFacebookProxy;

    private SGoogleSignIn sGoogleSignIn;


    @Override
    public void onCreate(Activity activity) {

        sGoogleSignIn = new SGoogleSignIn(activity, DialogUtil.createLoadingDialog(activity, "Loading..."));

    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
//        if (sFacebookProxy != null && requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
//            sFacebookProxy.onActivityResult(activity, requestCode, resultCode, data);
//        }
        if (sGoogleSignIn != null){
            sGoogleSignIn.handleActivityResult(activity,requestCode,resultCode,data);
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

        boolean isTermRead = GamaUtil.getStartTermRead(activity);

        if(!isTermRead) {
            StartTermsLayoutV2 termsLayoutV2 = new StartTermsLayoutV2(activity, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen, new StartTermsLayoutV2.ViewListener() {
                @Override
                public void onSuccess() {
                    SLoginDialogV2 sLoginDialog = new SLoginDialogV2(activity, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
                    sLoginDialog.setSFacebookProxy(sFacebookProxy);
                    sLoginDialog.setSGoogleSignIn(sGoogleSignIn);
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
            sLoginDialog.setLoginCallBack(iLoginCallBack);
            sLoginDialog.show();
        }
    }

    @Override
    public void initFacebookPro(Activity activity, SFacebookProxy sFacebookProxy) {
        this.sFacebookProxy = sFacebookProxy;
    }
}

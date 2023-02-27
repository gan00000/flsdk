package com.mw.sdk.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.core.base.utils.SStringUtil;
import com.facebook.internal.CallbackManagerImpl;
import com.mw.base.cfg.ConfigBean;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.SWebViewDialog;
import com.mw.sdk.login.widget.v2.NoticeView;
import com.mw.sdk.utils.DialogUtil;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleSignIn;
import com.thirdlib.line.SLineSignIn;

/**
 * Created by gan on 2017/4/12.
 */

public class DialogLoginImpl implements ILogin {

    private SLoginDialogV2 sLoginDialog;
    private SFacebookProxy sFacebookProxy;

    private SGoogleSignIn sGoogleSignIn;
    private SLineSignIn sLineSignIn;

//    private GamaTwitterLogin twitterLogin;


    public SGoogleSignIn getGoogleSignIn() {
        return sGoogleSignIn;
    }

    @Override
    public void onCreate(Activity activity) {

        sGoogleSignIn = new SGoogleSignIn(activity, DialogUtil.createLoadingDialog(activity, "Loading..."));
        sLineSignIn = new SLineSignIn(activity, DialogUtil.createLoadingDialog(activity, "Loading..."));
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
        if (sLineSignIn != null){
            sLineSignIn.handleActivityResult(activity,requestCode,resultCode,data);
        }
//        if(twitterLogin != null) {
//            twitterLogin.onActivityResult(requestCode, resultCode, data);
//        }
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

        if (!SdkUtil.isVersion1(activity)) {
            ConfigBean configBean = SdkUtil.getSdkCfg(activity);
            if (configBean != null) {
                ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(activity);
                //test
//                versionData.setShowNotice(true);
                if (versionData != null && versionData.isShowNotice()) { //显示dialog web公告
                    NoticeView noticeView = new NoticeView(activity);
                    SWebViewDialog webViewDialog = new SWebViewDialog(activity, com.mw.sdk.R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen,noticeView,noticeView.getSWebView(),null);
                    if (configBean.getUrl() != null) {
                        webViewDialog.setWebUrl(configBean.getUrl().getNoticeUrl());
                    }
                    noticeView.setsBaseDialog(webViewDialog);
                    webViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            goDialogView(activity, iLoginCallBack);
                        }
                    });
                    webViewDialog.show();
                    return;
                }
            }
        }
        goDialogView(activity, iLoginCallBack);
    }

    private void goDialogView(Activity activity, ILoginCallBack iLoginCallBack) {

        if (sLoginDialog != null){
            sLoginDialog.dismiss();
        }
        sLoginDialog = new SLoginDialogV2(activity, com.mw.sdk.R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        sLoginDialog.setSFacebookProxy(sFacebookProxy);
        sLoginDialog.setSGoogleSignIn(sGoogleSignIn);
        sLoginDialog.setsLineSignIn(sLineSignIn);
//        if (twitterLogin != null) {
//            sLoginDialog.setTwitterLogin(twitterLogin);
//        }
        sLoginDialog.setLoginCallBack(iLoginCallBack);
        sLoginDialog.show();
    }

    @Override
    public void initFacebookPro(Activity activity, SFacebookProxy sFacebookProxy) {
        this.sFacebookProxy = sFacebookProxy;
    }
}

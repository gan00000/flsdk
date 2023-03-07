package com.mw.sdk.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.facebook.internal.CallbackManagerImpl;
import com.mw.base.bean.SLoginType;
import com.mw.base.cfg.ConfigBean;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.SWebViewDialog;
import com.mw.sdk.login.widget.v2.NoticeView;
import com.mw.sdk.utils.DialogUtil;
import com.thirdlib.facebook.SFacebookProxy;
import com.thirdlib.google.SGoogleSignIn;
import com.thirdlib.huawei.HuaweiSignIn;
import com.thirdlib.line.SLineSignIn;

/**
 * Created by gan on 2017/4/12.
 */

public class DialogLoginImpl implements ILogin {

    private SLoginDialogV2 sLoginDialog;
    private SFacebookProxy sFacebookProxy;

    private SGoogleSignIn sGoogleSignIn;
    private SLineSignIn sLineSignIn;
    private HuaweiSignIn huaweiSignIn;


    @Override
    public void onCreate(Activity activity) {

        sGoogleSignIn = new SGoogleSignIn(activity, DialogUtil.createLoadingDialog(activity, "Loading..."));
        sLineSignIn = new SLineSignIn(activity, DialogUtil.createLoadingDialog(activity, "Loading..."));
        huaweiSignIn = new HuaweiSignIn(activity, DialogUtil.createLoadingDialog(activity, "Loading..."));
    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (sFacebookProxy != null) {
            sFacebookProxy.onActivityResult(activity, requestCode, resultCode, data);
        }
        if (sGoogleSignIn != null){
            sGoogleSignIn.handleActivityResult(activity,requestCode,resultCode,data);
        }
        if (sLineSignIn != null){
            sLineSignIn.handleActivityResult(activity,requestCode,resultCode,data);
        }
        if(huaweiSignIn != null) {
            huaweiSignIn.handleActivityResult(activity, requestCode, resultCode, data);
        }
        PL.i("DialogLoginImpl onActivityResult");
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

    @Override
    public void signOut(Activity activity) {

//        String previousLoginType = SdkUtil.getPreviousLoginType(activity);
//        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, previousLoginType)) {//google
//        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_LINE, previousLoginType)) {//line
//        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, previousLoginType)) {//fb
//        }else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_HUAWEI, previousLoginType)) {
//        }

        if (sFacebookProxy != null) {
            sFacebookProxy.fbLogout(activity);
        }

        if (sGoogleSignIn != null) {
            sGoogleSignIn.signOut();
        }

        if(huaweiSignIn != null) {
            huaweiSignIn.signOut(activity);
        }

    }

    private void goDialogView(Activity activity, ILoginCallBack iLoginCallBack) {

        try {
            if (sLoginDialog != null){
                sLoginDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sLoginDialog = new SLoginDialogV2(activity, com.mw.sdk.R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        sLoginDialog.setSFacebookProxy(sFacebookProxy);
        sLoginDialog.setSGoogleSignIn(sGoogleSignIn);
        sLoginDialog.setsLineSignIn(sLineSignIn);
        sLoginDialog.setHuaweiSignIn(huaweiSignIn);
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

package com.mw.sdk.out;

import android.app.Activity;

import com.core.base.callback.SFCallBack;

public class MWSdkImpl extends BaseSdkImpl {

    @Override
    public void share(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
        if (iSdkCallBack != null){
            iSdkCallBack.success();
        }
    }

    @Override
    public void shareFacebook(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
        if (iSdkCallBack != null){
            iSdkCallBack.success();
        }
    }

    @Override
    public void shareLine(Activity activity, String content, ISdkCallBack iSdkCallBack) {
        if (iSdkCallBack != null){
            iSdkCallBack.success();
        }
    }

    @Override
    public void requestStoreReview(Activity activity, SFCallBack sfCallBack) {
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
}

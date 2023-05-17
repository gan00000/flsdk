package com.mw.sdk.out;

import android.app.Activity;

import com.core.base.callback.SFCallBack;

public class MWSdkImpl extends BaseSdkImpl {

    @Override
    public void share(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {

    }

    @Override
    public void shareFacebook(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {

    }

    @Override
    public void shareLine(Activity activity, String content, ISdkCallBack iSdkCallBack) {

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
}

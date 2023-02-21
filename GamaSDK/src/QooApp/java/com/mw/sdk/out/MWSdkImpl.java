package com.mw.sdk.out;

import android.app.Activity;

import com.core.base.utils.PL;
import com.mw.sdk.login.ILoginCallBack;
import com.qooapp.opensdk.QooAppOpenSDK;
import com.qooapp.opensdk.common.QooAppCallback;

public class MWSdkImpl extends BaseSdkImpl {

    private static final String TAG = MWSdkImpl.class.getSimpleName();

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);

        // you can use this way to init QooAppOpenSDK.
        // you must provide params in AndroidManifest.xml
        QooAppOpenSDK.initialize(new QooAppCallback() {
            @Override
            public void onSuccess(String response) {
                PL.i("QooAppOpenSDK.initialize onSuccess:" + response);
            }

            @Override
            public void onError(String error) {
                PL.i("QooAppOpenSDK.initialize onError:" + error);
            }
        }, activity);

    }

    @Override
    public void login(Activity activity, ILoginCallBack iLoginCallBack) {

        PL.i("sdk login");
        this.activity = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qooAppLogin(activity);
            }
        });

    }

    private void qooAppLogin(Activity activity){

        QooAppOpenSDK.getInstance().login(new QooAppCallback() {
            @Override
            public void onSuccess(String response) {
                PL.i("QooAppOpenSDK.login onSuccess:" + response);
            }

            @Override
            public void onError(String error) {
                PL.i("QooAppOpenSDK.login onError:" + error);
            }
        }, activity);
    }
}

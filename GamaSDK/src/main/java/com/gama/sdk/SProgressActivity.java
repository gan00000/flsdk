package com.gama.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.gama.base.cfg.ResConfig;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.data.login.execute2.ThirdAccountToBindRequestTaskV2;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.utils.DialogUtil;
import com.gama.thirdlib.google.SGoogleSignIn;

public class SProgressActivity extends SBaseSdkActivity {

    private static final String TAG = SProgressActivity.class.getSimpleName();

    private SGoogleSignIn sGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        sGoogleSignIn = new SGoogleSignIn(this, DialogUtil.createLoadingDialog(this, "Loading..."));
        final String googleClientId = ResConfig.getGoogleClientId(this);
        sGoogleSignIn.setClientId(googleClientId);
        sGoogleSignIn.startSignIn(new SGoogleSignIn.GoogleSignInCallBack() {
            @Override
            public void success(String id, String mFullName, String mEmail, String idTokenString) {
                ThirdAccountToBindRequestTaskV2 task = new ThirdAccountToBindRequestTaskV2(SProgressActivity.this,
                        id,
                        idTokenString,
                        googleClientId,
                        GSRequestMethod.GSRequestType.GAMESWORD);
                task.setLoadDialog(DialogUtil.createLoadingDialog(SProgressActivity.this, "Loading..."));
                task.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
                    @Override
                    public void success(SLoginResponse response, String rawResult) {
                        if (response != null) {
                            if ("1000".equals(response.getCode())) {
                                setResultSuccess();
                            } else {
                                if (!TextUtils.isEmpty(response.getMessage())) {
                                    ToastUtils.toast(SProgressActivity.this, response.getMessage());
                                }
                                setResultFailed();
                            }
                        }
                    }

                    @Override
                    public void timeout(String code) {
                        setResultFailed();
                    }

                    @Override
                    public void noData() {
                        setResultFailed();
                    }

                    @Override
                    public void cancel() {
                        setResultFailed();
                    }
                });
                task.excute(SLoginResponse.class);
            }

            @Override
            public void failure() {
                PL.i(TAG, "google sign in failure");
                setResultFailed();
            }
        });

    }

    private void setResultSuccess() {
        this.setResult(RESULT_OK);
        finish();
    }

    private void setResultFailed() {
        this.setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (sGoogleSignIn != null){
            sGoogleSignIn.handleActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sGoogleSignIn != null){
            sGoogleSignIn = null;
        }
    }
}

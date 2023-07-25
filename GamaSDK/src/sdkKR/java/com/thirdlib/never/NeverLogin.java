package com.thirdlib.never;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.R;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;
import com.thirdlib.IThirdHelper;
import com.thirdlib.ThirdCallBack;

public class NeverLogin implements IThirdHelper {

    private static final String TAG = NeverLogin.class.getSimpleName();

    private Activity mActivity;

    NaverIdLoginSDK naverIdLoginSDK;

    public NeverLogin() {

    }

    @Override
    public void init(Context context) {
        String clientId = context.getResources().getString(R.string.naver_client_id);//"rax8xuw2CP1HFvQsdIJC";
        String clientSecret = context.getResources().getString(R.string.naver_client_secret);//"2hzM7vADCF";
        String clientName = ApkInfoUtil.getApplicationName(context);

        NaverIdLoginSDK.INSTANCE.initialize(context, clientId, clientSecret, clientName);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (requestCode == getTwitterAuthClient().getRequestCode()) {
            getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
        }*/
    }

    @Override
    public void startLogin(Activity activity, ThirdCallBack thirdCallBack) {

        if(activity == null || activity.isFinishing()) {
            Log.e(TAG, "activity in illegal state");
            return;
        }
        this.mActivity = activity;
        PL.d("naver start authenticate");
        NaverIdLoginSDK.INSTANCE.authenticate(activity, new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                PL.d("NaverIdLoginSDK authenticate onSuccess");
                String tvAccessToken = NaverIdLoginSDK.INSTANCE.getAccessToken();
                String tvRefreshToken = NaverIdLoginSDK.INSTANCE.getRefreshToken();
                long tvExpires = NaverIdLoginSDK.INSTANCE.getExpiresAt();
                String tvType = NaverIdLoginSDK.INSTANCE.getTokenType();
                String tvState = NaverIdLoginSDK.INSTANCE.getState().toString();

                PL.d("tvAccessToken=%s,tvState=%s", tvAccessToken, tvState);

                new NidOAuthLogin().callProfileApi(new NidProfileCallback<NidProfileResponse>() {
                    @Override
                    public void onSuccess(NidProfileResponse nidProfileResponse) {
                        PL.d("NidOAuthLogin callProfileApi onSuccess");
                        String uid = nidProfileResponse.getProfile().getId();
                        String name = nidProfileResponse.getProfile().getName();
                        String nickname = nidProfileResponse.getProfile().getNickname();
                        String email = nidProfileResponse.getProfile().getEmail();

                        PL.d("uid=%s,name=%s,email=%s,nickname=%s", uid, name, email, nickname);

                        if (thirdCallBack != null){
                            thirdCallBack.success(uid, nickname, "",tvAccessToken);
                        }
                    }

                    @Override
                    public void onFailure(int i, @NonNull String s) {
                        PL.e("NidOAuthLogin callProfileApi onFailure:" + s);
                        if (thirdCallBack != null){
                            thirdCallBack.failure(s);
                        }
                    }

                    @Override
                    public void onError(int i, @NonNull String s) {
                        PL.e("NidOAuthLogin callProfileApi error:" + s);
                        if (thirdCallBack != null){
                            thirdCallBack.failure(s);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int i, @NonNull String s) {
                PL.e("NaverIdLoginSDK authenticate onFailure:" + s);
                String errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode().getCode();
                String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                ToastUtils.toast(activity,"errorCode:" + errorCode + "  errorDesc:" + errorDescription);
                if (thirdCallBack != null){
                    thirdCallBack.failure(s);
                }
            }

            @Override
            public void onError(int i, @NonNull String s) {
                PL.e("NaverIdLoginSDK authenticate error:" + s);
                if (thirdCallBack != null){
                    thirdCallBack.failure(s);
                }
            }
        });
    }

}

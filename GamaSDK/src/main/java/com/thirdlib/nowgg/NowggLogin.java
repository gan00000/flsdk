package com.thirdlib.nowgg;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.ResultReceiver;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.google.gson.Gson;
import com.mw.sdk.R;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NowggLogin {

    private static final int GET_ACCOUNTS_REQUEST_CODE = 2001;
    private Activity activity;
    private NowggSignInCallBack nowggSignInCallBack;

    public static final String ID_TOKEN = "id_token"; // 不要修改
    public static String CLIENT_ID = ""; // 替换为您的client id
    public static final String ACCOUNT_TYPE = "now.gg"; // 不要修改
    public static final String HOST_URL = "hostUrl"; //不要修改

    public void startSign(Activity activity, NowggSignInCallBack nowggSignInCallBack){

        this.activity = activity;
        this.nowggSignInCallBack = nowggSignInCallBack;

        CLIENT_ID = activity.getString(R.string.mw_nowgg_client_id);
        if (SStringUtil.isEmpty(CLIENT_ID)){
            ToastUtils.toast(activity,"请配置mw_nowgg_client_id");
            return;
        }
        PL.i("nowgg CLIENT_ID=" + CLIENT_ID);

        if (activity.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            PL.i("请求授权GET_ACCOUNTS");
            activity.requestPermissions(new String[] {
                            Manifest.permission.GET_ACCOUNTS
                    },
                    GET_ACCOUNTS_REQUEST_CODE);
        } else {
            // 已有 GET_ACCOUNTS 权限，可以继续登录
            PL.i( "onCreate: Already has permission, proceed with signin");
            signIn();
        }

    }

    public void onRequestPermissionsResult(Activity activity, int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == GET_ACCOUNTS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 已授权，可以继续登录
                signIn();
            }
        }else {
            ToastUtils.toast(activity,"Please grant GET_ACCOUNTS permission first");
        }
    }

    private void signIn() {
        Account account = getNowggAccount();
        if (account != null) {
            Bundle bundle = new Bundle();
            bundle.putString("client_id", CLIENT_ID);
            String authTokenType = ID_TOKEN;
            AccountManager.get(activity.getApplicationContext()).
                    getAuthToken(account, authTokenType, bundle, activity, new OnTokenAcquired(), null);
        } else {
            addNowggAccount();
        }
    }

    private Account getNowggAccount() {
        Account[] accounts = AccountManager.get(activity.getApplicationContext()).getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length != 0) {
            PL.i("getNowggAccount: account found");
            // 在一个系统中，目前仅允许添加一个now.gg账户
            return accounts[0];
        }
        return null;
    }

    private void addNowggAccount() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("gg.now.accounts", "gg.now.accounts.AuthenticatorActivity"));
            intent.setAction("IAP_ADD_ACCOUNT");
            intent.putExtra("resultReceiver", parcelResultReceiver(resultReceiver));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.toast(activity, e.getMessage());
        }
    }

    ResultReceiver parcelResultReceiver(ResultReceiver actualReceiver) {
        Parcel parcel = Parcel.obtain();
        actualReceiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ResultReceiver receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return receiverForSending;
    }

    ResultReceiver resultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            PL.i("onReceiveResult() called with: resultCode = [" + resultCode + "], resultData = [" + resultData.toString() + "]");
            // onReceiveResult
            if (resultCode == 0) { // 登录成功
                signIn();
            } else {
                // 登录失败
                PL.i( "onReceiveResult: sign in failed");
            }
        }
    };

    private class OnTokenAcquired implements AccountManagerCallback {

        @Override
        public void run(AccountManagerFuture result) {
            try {
                Bundle bundle = (Bundle) result.getResult();
                boolean success = bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
                if (success) {
                    final String token = bundle.getString(ID_TOKEN);
                    // 获取now.gg服务器URL，用其获得用户详情和验证令牌

                    final String hostUrl = bundle.getString(HOST_URL);
                    verifyToken(token, hostUrl);
                }
                else {
                    // 获取令牌失败
                    // 错误时，开发者可以展示错误或者采用其它登录策略。
                    PL.i( "run: get token failed " + bundle);
                    if (nowggSignInCallBack != null){
                        nowggSignInCallBack.failure("get token failed");
                    }
                }
            } catch (AuthenticatorException | IOException | OperationCanceledException e) {
                e.printStackTrace();
            }
        }
    }

    private void verifyToken(String idToken, String hostUrl) {
        BackendApiCallService backendApiCallService = getBackendRetrofit(hostUrl).create(BackendApiCallService.class);

        Call< TokenVerifyResponse > tokenVerifyResponseCall = backendApiCallService.verifyIdToken(new TokenVerifyRequest(ID_TOKEN, idToken, CLIENT_ID));

        tokenVerifyResponseCall.enqueue(new Callback< TokenVerifyResponse >() {
            @Override
            public void onResponse(Call < TokenVerifyResponse > call, Response< TokenVerifyResponse > response) {
                if (response.isSuccessful()) {
                    PL.i( "nowgg login onResponse: " + response.body().toString());
                    UserDataVerified userDataVerified = response.body().getUserDataVerified();
                    if (userDataVerified != null){
                        if (nowggSignInCallBack != null){
                            nowggSignInCallBack.success(userDataVerified.userId, userDataVerified.name, userDataVerified.email, userDataVerified.tokenId);
                        }
                    }else {
                        if (nowggSignInCallBack != null){
                            nowggSignInCallBack.failure("userDataVerified is null");
                        }
                    }


                } else {
                    Gson gson = new Gson();
                    TokenVerifyResponse error = gson.fromJson(response.errorBody().charStream(), TokenVerifyResponse.class);

                    if (!error.isSuccess() && ("EXPIRED_TOKEN".equals(error.getCode()) || "INVALID_TOKEN".equals(error.getCode()))) {
                        // retry the verify token call, after getting a new TOKEN
                    }
                    if (nowggSignInCallBack != null){
                        nowggSignInCallBack.failure(error.getCode() + "");
                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                PL.i( "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                if (nowggSignInCallBack != null){
                    nowggSignInCallBack.failure("verifyToken token failed");
                }
            }
        });

    }


    public Retrofit getBackendRetrofit(String hostUrl) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        return new Retrofit.Builder()
                .baseUrl(hostUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }


    public interface NowggSignInCallBack{
        void success(String id, String mFullName, String mEmail, String idTokenString);
        void failure(String msg);
    }
}

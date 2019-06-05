package com.gama.pay.onestore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.gama.pay.IOneStorePay;
import com.gama.pay.IPayCallBack;
import com.gama.pay.onestore.bean.req.PayReqBean;
import com.gamamobi.onestore.api.IapResult;
import com.gamamobi.onestore.api.PurchaseClient;

/**
 * Created by gan on 2017/2/23.
 */

public class OneStoreImpl implements IOneStorePay {
    private static final String TAG = OneStoreImpl.class.getSimpleName();
    private Activity mActivity;
    private PurchaseClient client;
    private IPayCallBack iPayCallBack;
    private static final int IAP_API_VERSION = 5;
    private ProgressDialog progressDialog;
    private static final int LOGIN_REQUEST_CODE = 1007;

    public OneStoreImpl(Activity activity) {
        this.mActivity = activity;
        client = new PurchaseClient(mActivity);
    }

    @Override
    public void connectOneService() {
//        showProgress(mActivity);
        client.connect(mServiceConnectionListener);
    }

    @Override
    public void checkIsBillingSupport() {
        showProgress(mActivity);
        client.isBillingSupportedAsync(IAP_API_VERSION, mBillingSupportedListener);
    }

    @Override
    public void queryPurchases() {

    }

    @Override
    public void launchPurchaseFlowAsync() {

    }

    @Override
    public void consumeAsync() {

    }

    @Override
    public void startPay(Activity activity, PayReqBean payReqBean) {

    }

    @Override
    public void setIPayCallBack(IPayCallBack iPayCallBack) {
        this.iPayCallBack = iPayCallBack;
    }

    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

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

    /**
     * one服务连接回调
     */
    PurchaseClient.ServiceConnectionListener mServiceConnectionListener = new PurchaseClient.ServiceConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(TAG, "Service connected");
            hideProgress();
            checkIsBillingSupport();
        }

        @Override
        public void onDisconnected() {
            Log.d(TAG, "Service disconnected");
            hideProgress();
        }

        @Override
        public void onErrorNeedUpdateException() {
            Log.e(TAG, "connect onError, 我需要更新我的OneStore服务应用程序");
            hideProgress();
            updateOrInstallOneStoreService();
        }
    };

    private void updateOrInstallOneStoreService() {
        PurchaseClient.launchUpdateOrInstallFlow(mActivity);
    }

    /**
     * 查询是否支持one支付
     */
    PurchaseClient.BillingSupportedListener mBillingSupportedListener = new PurchaseClient.BillingSupportedListener() {

        @Override
        public void onSuccess() {
            Log.d(TAG, "isBillingSupportedAsync onSuccess");
            hideProgress();

            // isBillingSupportedAsync 호출 성공시에,
            // 구매내역정보를 호출을 진행하여 관리형상품 및 월정액상품 구매내역에 대해서 받아옵니다.
            createOrder();
        }

        @Override
        public void onError(IapResult result) {
            Log.e(TAG, "isBillingSupportedAsync onError, " + result.toString());
            hideProgress();

            // RESULT_NEED_LOGIN 에러시에 개발사의 애플리키에션 life cycle에 맞춰 명시적으로 원스토어 로그인을 호출합니다.
            if (IapResult.RESULT_NEED_LOGIN == result) {
                loadLoginFlow();
            }
        }

        @Override
        public void onErrorRemoteException() {
            Log.e(TAG, "isBillingSupportedAsync onErrorRemoteException, 无法连接一个商店服务");
            hideProgress();
//            alert("无法连接一个商店服务");
        }

        @Override
        public void onErrorSecurityException() {
            Log.e(TAG, "isBillingSupportedAsync onErrorSecurityException, 异常应用请求付款");
            hideProgress();
//            alert("异常应用请求付款");
        }

        @Override
        public void onErrorNeedUpdateException() {
            Log.e(TAG, "isBillingSupportedAsync onErrorNeedUpdateException, 我需要更新我的OneStore服务应用程序");
            hideProgress();
            updateOrInstallOneStoreService();
        }
    };

    private void createOrder() {

    }

    public void showProgress(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    return;
                } else {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage("Server connection...");
                    progressDialog.show();
                }
            }
        });
    }

    public void hideProgress() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void loadLoginFlow() {
        Log.d(TAG, "loadLoginFlow()");

        if (client == null) {
            Log.d(TAG, "PurchaseClient is not initialized");
            return;
        }

        client.launchLoginFlowAsync(IAP_API_VERSION, mActivity, LOGIN_REQUEST_CODE, mLoginFlowListener);

//        alertDecision("我们无法验证您的OneStore帐户信息。 登录？", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (mPurchaseClient.launchLoginFlowAsync(IAP_API_VERSION, LuckyActivity.this, LOGIN_REQUEST_CODE, mLoginFlowListener) == false) {
//                    // listener is null
//                }
//            }
//        });
    }

    /**
     * one登录回调
     */
    PurchaseClient.LoginFlowListener mLoginFlowListener = new PurchaseClient.LoginFlowListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "launchLoginFlowAsync onSuccess");
            hideProgress();
            // 开发人员应指定以下方案以成功登录。
        }

        @Override
        public void onError(IapResult result) {
            Log.e(TAG, "launchLoginFlowAsync onError, " + result.toString());
            hideProgress();
//            alert(result.getDescription());
        }

        @Override
        public void onErrorRemoteException() {
            Log.e(TAG, "launchLoginFlowAsync onError, 无法连接一个商店服务");

            hideProgress();
//            alert("无法连接一个商店服务");
        }

        @Override
        public void onErrorSecurityException() {
            Log.e(TAG, "launchLoginFlowAsync onError, 异常应用请求付款");

            hideProgress();
//            alert("异常应用请求付款");
        }

        @Override
        public void onErrorNeedUpdateException() {
            Log.e(TAG, "launchLoginFlowAsync onError, 我需要更新我的OneStore服务应用程序");

            hideProgress();
            updateOrInstallOneStoreService();
        }

    };
}

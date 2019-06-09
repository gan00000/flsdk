package com.gamamobi.onestore.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.gama.base.cfg.ResConfig;
import com.gama.onestore.plug.BuildConfig;
import com.gamamobi.onestore.IOneStorePay;
import com.gamamobi.onestore.IPayCallBack;
import com.gamamobi.onestore.api.IapEnum;
import com.gamamobi.onestore.api.IapResult;
import com.gamamobi.onestore.api.PurchaseClient;
import com.gamamobi.onestore.api.PurchaseData;
import com.gamamobi.onestore.pay.bean.req.OneStoreCreateOrderIdReqBean;
import com.gamamobi.onestore.pay.bean.req.OneStoreExchangeReqBean;
import com.gamamobi.onestore.pay.bean.req.PayReqBean;
import com.gamamobi.onestore.pay.bean.res.OneStoreCreateOrderIdRes;
import com.gamamobi.onestore.pay.bean.res.OneStoreExchangeRes;
import com.gamamobi.onestore.pay.constants.OneStoreDomainSite;
import com.gamamobi.onestore.pay.task.OneStoreCreateOrderReqTask;
import com.gamamobi.onestore.pay.task.OneStoreExchangeReqTask;

import java.util.List;

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
    private static final int LOGIN_REQUEST_CODE = 2006;
    private static final int PURCHASE_REQUEST_CODE = 2007;
    private OneStoreCreateOrderIdReqBean createOrderIdReqBean;
    private boolean isPaying = false;

    public OneStoreImpl(Activity activity) {
        this.mActivity = activity;
        client = new PurchaseClient(activity);
    }

    private void connectOneService() {
//        showProgress(mActivity);
        client.connect(mServiceConnectionListener);
    }

    private void checkIsBillingSupport() {
        showProgress(mActivity);
        client.isBillingSupportedAsync(IAP_API_VERSION, mBillingSupportedListener);
    }

    private void queryPurchases() {
        client.queryPurchasesAsync(IAP_API_VERSION, IapEnum.ProductType.IN_APP.getType(), mQueryPurchaseListener);
    }

    private void launchPurchaseFlowAsync(OneStoreCreateOrderIdReqBean bean, OneStoreCreateOrderIdRes response) {
        showProgress(mActivity);
        client.launchPurchaseFlowAsync(IAP_API_VERSION, mActivity, PURCHASE_REQUEST_CODE, bean.getProductId(), "",
                IapEnum.ProductType.IN_APP.getType(), response.getDeveloperPayload(), "", false, mPurchaseFlowListener);
    }

    private void consumeAsync() {

    }

    @Override
    public void startPay(Activity activity, PayReqBean payReqBean) {
        this.createOrderIdReqBean = null;

        PL.i("the jar version:" + BuildConfig.JAR_VERSION);//打印版本号

        if (activity == null) {
            PL.w("activity is null");
            return;
        }
        this.mActivity = activity;
        if (payReqBean == null) {
            PL.w("payReqBean is null");
            return;
        }

        if (isPaying) {
            PL.w("google is paying...");
            return;
        }
        PL.w("google set paying...");
        isPaying = true;

        //由GooglePayActivity2传入
        this.createOrderIdReqBean = (OneStoreCreateOrderIdReqBean) payReqBean;
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(ResConfig.getPayPreferredUrl(activity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(ResConfig.getPaySpareUrl(activity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(OneStoreDomainSite.ONESTORE_ORDER_CREATE);

        connectOneService();
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
        Log.e(TAG, "onActivityResult requestCode " + requestCode);
        Log.e(TAG, "onActivityResult resultCode " + resultCode);

        switch (requestCode) {
            case LOGIN_REQUEST_CODE:

                /*
                 * 调用launchLoginFlowAsync API时，将通过handleLoginData解析收到的intent数据。
                 * 解析的结果通过在launchLoginFlowAsync调用中传递的Login Flow Listener传递。
                 */

                if (resultCode == Activity.RESULT_OK) {
                    if (!client.handleLoginData(data)) {
                        Log.e(TAG, "onActivityResult handleLoginData false ");
                        // listener is null
                    }
                } else {
                    Log.e(TAG, "onActivityResult user canceled");
                    callbackFail(null);
                    // user canceled , do nothing..
                }
                break;

            case PURCHASE_REQUEST_CODE:

                /*
                 * launchPurchaseFlowAsync API调用handlePurchaseData来解析接收到的intent数据。
                 * 解析的结果通过 PurchaseFlowListener 传递，该调用在调用launchPurchaseFlowAsync时传递。
                 */

                if (resultCode == Activity.RESULT_OK) {
                    if (!client.handlePurchaseData(data)) {
                        Log.e(TAG, "onActivityResult handlePurchaseData false ");
                        // listener is null 的时候会走进来，不然就会回调到 PurchaseFlowListener 去
                    }
                } else {
                    Log.e(TAG, "onActivityResult user canceled");
                    callbackFail(null);
                    // user canceled , do nothing..
                }
                break;
            default:
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
        if (client != null) {
            PL.i(TAG, "terminate purchase client");
            client.terminate();
        } else {
            PL.i(TAG, "purchase client already release");
        }
    }

    /**
     * one服务连接回调
     */
    private PurchaseClient.ServiceConnectionListener mServiceConnectionListener = new PurchaseClient.ServiceConnectionListener() {
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
            callbackFail(null);
        }

        @Override
        public void onErrorNeedUpdateException() {
            Log.e(TAG, "connect onError, 我需要更新我的OneStore服务应用程序");
            hideProgress();
            updateOrInstallOneStoreService();
            callbackFail(null);
        }
    };

    private void updateOrInstallOneStoreService() {
        PurchaseClient.launchUpdateOrInstallFlow(mActivity);
    }

    /**
     * 查询是否支持one支付
     */
    private PurchaseClient.BillingSupportedListener mBillingSupportedListener = new PurchaseClient.BillingSupportedListener() {

        @Override
        public void onSuccess() {
            Log.d(TAG, "isBillingSupportedAsync onSuccess");
            hideProgress();

            queryPurchases();
        }

        @Override
        public void onError(IapResult result) {
            Log.e(TAG, "isBillingSupportedAsync onError, " + result.toString());
            hideProgress();

            // RESULT_NEED_LOGIN 에러시에 개발사의 애플리키에션 life cycle에 맞춰 명시적으로 원스토어 로그인을 호출합니다.
            if (IapResult.RESULT_NEED_LOGIN == result) {
                loadLoginFlow();
            } else {
                callbackFail(null);
            }
        }

        @Override
        public void onErrorRemoteException() {
            Log.e(TAG, "isBillingSupportedAsync onErrorRemoteException, 无法连接一个商店服务");
            hideProgress();
//            alert("无法连接一个商店服务");
            callbackFail(null);
        }

        @Override
        public void onErrorSecurityException() {
            Log.e(TAG, "isBillingSupportedAsync onErrorSecurityException, 异常应用请求付款");
            hideProgress();
//            alert("异常应用请求付款");
            callbackFail(null);
        }

        @Override
        public void onErrorNeedUpdateException() {
            Log.e(TAG, "isBillingSupportedAsync onErrorNeedUpdateException, 我需要更新我的OneStore服务应用程序");
            hideProgress();
            updateOrInstallOneStoreService();
            callbackFail(null);
        }
    };

    private void createOrder(final OneStoreCreateOrderIdReqBean bean) {
        OneStoreCreateOrderReqTask oneStoreCreateOrderReqTask = new OneStoreCreateOrderReqTask(bean);
        oneStoreCreateOrderReqTask.setReqCallBack(new ISReqCallBack<OneStoreCreateOrderIdRes>() {
            @Override
            public void success(OneStoreCreateOrderIdRes response, String rawResult) {
                if (response != null && response.isRequestSuccess() && !TextUtils.isEmpty(response.getOrderId())
                        && !TextUtils.isEmpty(response.getDeveloperPayload())) {
                    Log.i("gama one", "create result = " + rawResult);
                    launchPurchaseFlowAsync(bean, response);
                } else {
                    if (response != null && !TextUtils.isEmpty(response.getMessage())) {
                        ToastUtils.toast(mActivity, response.getMessage());
                    }
                    callbackFail(null);
                }

            }

            @Override
            public void timeout(String code) {
                callbackFail(null);
            }

            @Override
            public void noData() {
                callbackFail(null);
            }
        });
        oneStoreCreateOrderReqTask.excute(OneStoreCreateOrderIdRes.class);
    }

    private void showProgress(final Activity activity) {
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

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
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
    private PurchaseClient.LoginFlowListener mLoginFlowListener = new PurchaseClient.LoginFlowListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "launchLoginFlowAsync onSuccess");
            hideProgress();
            // 登入成功后继续下一步支付流程--查丢单
            queryPurchases();
        }

        @Override
        public void onError(IapResult result) {
            Log.e(TAG, "launchLoginFlowAsync onError, " + result.toString());
            hideProgress();
//            alert(result.getDescription());
            callbackFail(null);
        }

        @Override
        public void onErrorRemoteException() {
            Log.e(TAG, "launchLoginFlowAsync onError, 无法连接一个商店服务");

            hideProgress();
//            alert("无法连接一个商店服务");
            callbackFail(null);
        }

        @Override
        public void onErrorSecurityException() {
            Log.e(TAG, "launchLoginFlowAsync onError, 异常应用请求付款");

            hideProgress();
//            alert("异常应用请求付款");
            callbackFail(null);
        }

        @Override
        public void onErrorNeedUpdateException() {
            Log.e(TAG, "launchLoginFlowAsync onError, 我需要更新我的OneStore服务应用程序");

            hideProgress();
            updateOrInstallOneStoreService();
            callbackFail(null);
        }

    };

    /**
     * 查询购买的回调
     */
    private PurchaseClient.QueryPurchaseListener mQueryPurchaseListener = new PurchaseClient.QueryPurchaseListener() {
        @Override
        public void onSuccess(List<PurchaseData> purchaseDataList, String productType) {
            Log.d(TAG, "queryPurchasesAsync onSuccess, " + purchaseDataList.toString());
            hideProgress();
            if (purchaseDataList != null && purchaseDataList.size() > 0) {
                for (PurchaseData data : purchaseDataList) {
                    sendRequest(data, true);
                }
            }
            createOrder(createOrderIdReqBean);
        }

        @Override
        public void onErrorRemoteException() {
            Log.e(TAG, "queryPurchasesAsync onError, 无法连接一个商店服务");
            hideProgress();
//            alert("无法连接一个商店服务");
            callbackFail(null);
        }

        @Override
        public void onErrorSecurityException() {
            Log.e(TAG, "queryPurchasesAsync onError, 异常应用请求付款");
            hideProgress();
//            alert("异常应用请求付款");
            callbackFail(null);
        }

        @Override
        public void onErrorNeedUpdateException() {
            Log.e(TAG, "queryPurchasesAsync onError, 我需要更新我的OneStore服务应用程序");
            hideProgress();
            updateOrInstallOneStoreService();
            callbackFail(null);
        }

        @Override
        public void onError(IapResult result) {
            Log.e(TAG, "queryPurchasesAsync onError, " + result.toString());
            hideProgress();
//            alert(result.getDescription());
            callbackFail(null);
        }
    };

    private PurchaseClient.PurchaseFlowListener mPurchaseFlowListener = new PurchaseClient.PurchaseFlowListener() {
        @Override
        public void onSuccess(PurchaseData purchaseData) {
            Log.d(TAG, "launchPurchaseFlowAsync onSuccess, " + purchaseData.toString());
            hideProgress();

            //购买完成请求发币，由服务端验证和消费
            sendRequest(purchaseData, false);
        }

        @Override
        public void onError(IapResult result) {
            Log.e(TAG, "launchPurchaseFlowAsync onError, " + result.toString());
            hideProgress();
//            alert(result.getDescription());
            callbackFail(null);
        }

        @Override
        public void onErrorRemoteException() {
            Log.e(TAG, "launchPurchaseFlowAsync onError, 无法连接一个商店服务");
            hideProgress();
//            alert("无法连接一个商店服务");
            callbackFail(null);
        }

        @Override
        public void onErrorSecurityException() {
            Log.e(TAG, "launchPurchaseFlowAsync onError, 异常应用请求付款");
            hideProgress();
//            alert("异常应用请求付款");
            callbackFail(null);
        }

        @Override
        public void onErrorNeedUpdateException() {
            Log.e(TAG, "launchPurchaseFlowAsync onError, 我需要更新我的OneStore服务应用程序");
            hideProgress();
            updateOrInstallOneStoreService();
            callbackFail(null);
        }
    };

    /**
     * 发币请求
     *
     * @param data
     * @param isCheck 是否补单请求
     */
    private void sendRequest(final PurchaseData data, final boolean isCheck) {
        OneStoreExchangeReqBean oneStoreExchangeReqBean = new OneStoreExchangeReqBean(mActivity);

        oneStoreExchangeReqBean.setOneStoreOrderId(data.getOrderId());
        oneStoreExchangeReqBean.setOneStorePackageName(data.getPackageName());
        oneStoreExchangeReqBean.setOneStoreProductId(data.getProductId());
        oneStoreExchangeReqBean.setOneStorePurchaseTime(data.getPurchaseTime());
        oneStoreExchangeReqBean.setOneStorePeveloperPayload(data.getDeveloperPayload());
        oneStoreExchangeReqBean.setOneStorePurchaseId(data.getPurchaseId());
        oneStoreExchangeReqBean.setOneStorePurchaseState(data.getPurchaseState());
        oneStoreExchangeReqBean.setOneStoreSignature(data.getSignature());
        oneStoreExchangeReqBean.setOneStoreRecurringState(data.getRecurringState());
        oneStoreExchangeReqBean.setOneStoreOriginPurchaseData(data.getPurchaseData());

        oneStoreExchangeReqBean.setRequestUrl(ResConfig.getPayPreferredUrl(mActivity));
        oneStoreExchangeReqBean.setRequestSpaUrl(ResConfig.getPaySpareUrl(mActivity));
        oneStoreExchangeReqBean.setRequestMethod(OneStoreDomainSite.ONESTORE_SEND);

        OneStoreExchangeReqTask oneStoreExchangeReqTask = new OneStoreExchangeReqTask(mActivity, oneStoreExchangeReqBean);
        oneStoreExchangeReqTask.setReqCallBack(new ISReqCallBack<OneStoreExchangeRes>() {
            @Override
            public void success(OneStoreExchangeRes response, String rawResult) {
                PL.i(TAG, "exchange callback");
                // 消费
                if (response != null && response.isRequestSuccess()) {
                    PL.i(TAG, "one pay success, isCheck : " + isCheck);
                    if (!isCheck) {
                        callbackSuccess(data);
                    }
                } else {
                    if (!isCheck) {
                        if (response != null && !TextUtils.isEmpty(response.getMessage())) {
                            ToastUtils.toast(mActivity, response.getMessage());
                        }
                        callbackFail("error, please contact customer service");
                    }
                }
            }

            @Override
            public void timeout(String code) {
                if (!isCheck) {
                    callbackFail("connect timeout, please try again");
                }
            }

            @Override
            public void noData() {
                if (!isCheck) {
                    callbackFail("server error, please try again");
                }
            }
        });
        oneStoreExchangeReqTask.excute(OneStoreExchangeRes.class);
    }

    private void callbackSuccess(PurchaseData data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(OneStoreActivity.ONESTORE_PURCHASE_DATA, data);
        if (iPayCallBack != null) {
            iPayCallBack.success(bundle);
        }
    }

    private void callbackFail(String msg) {
        final Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(msg)) {
            AlertDialog.Builder bld = new AlertDialog.Builder(mActivity);
            bld.setMessage(msg);
            bld.setCancelable(false);
            bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (iPayCallBack != null) {
                        iPayCallBack.fail(bundle);
                    }
                }
            });
            bld.create().show();
        }

    }

}

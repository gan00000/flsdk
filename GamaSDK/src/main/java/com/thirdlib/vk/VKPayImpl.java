package com.thirdlib.vk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.api.PayApi;
import com.mw.sdk.api.task.LoadingDialog;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.req.PayExchangeReqBean;
import com.mw.sdk.bean.req.PayReqBean;
import com.mw.sdk.bean.res.BasePayBean;
import com.mw.sdk.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.bean.res.GPExchangeRes;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.SdkUtil;

import java.util.List;

import ru.rustore.sdk.core.util.RuStoreUtils;
import ru.rustore.sdk.pay.UserInteractor;
import ru.rustore.sdk.pay.model.ProductPurchaseResult;
import ru.rustore.sdk.pay.model.Purchase;
import ru.rustore.sdk.pay.model.PurchaseStatus;

/**
 * Created by gan on 2017/2/23.
 */

public class VKPayImpl implements IPay, VKPurchaseManger.PurchaseCallback {

    public static final String TAG_USER_CANCEL = "TAG_PAY_USER_CANCEL";
//    private VKPurchaseManger purchaseManager;

    private LoadingDialog loadingDialog;

    private Double skuAmount;


    /**
     * 创单的请求参数Bean
     */
    private PayCreateOrderReqBean createOrderIdReqBean;

    private Activity mActivity;
    private Context mContext;

    private IPayCallBack iPayCallBack;
    /**
     * 防止连续快速点击储值出现未知异常
     */
    private boolean isPaying = false;

    private void callbackSuccess(Purchase purchase, GPExchangeRes gpExchangeRes) {

        PL.i("rustore pay onConsumeResponse callbackSuccess");
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    BasePayBean payBean = new BasePayBean();
                    if (purchase != null) {
                        try {
//                            JSONObject devPayload = new JSONObject(purchase.getDeveloperPayload());
//                            String userId = devPayload.optString("userId","");
//                            String roleId = devPayload.optString("roleId","");
//                            String orderId = devPayload.optString("orderId","");
//                            payBean.setOrderId(orderId);
//                            payBean.setTransactionId(purchase.getOrderId());
//                            payBean.setPackageName(purchase.getPackageName());
                            payBean.setUsdPrice(skuAmount);
                            if (createOrderIdReqBean != null) {
                                payBean.setProductId(createOrderIdReqBean.getProductId());
                            }
//                    payBean.setmItemType(purchase.getItemType());
//                            payBean.setOriginPurchaseData(purchase.getOriginalJson());
//                            payBean.setPurchaseState(purchase.getPurchaseState());
//                            payBean.setPurchaseTime(purchase.getPurchaseTime());
//                            payBean.setSignature(purchase.getSignature());
//                            payBean.setDeveloperPayload(purchase.getDeveloperPayload());
//                            payBean.setmToken(purchase.getPurchaseToken());
//                            if (skuDetails != null && skuDetails.getOneTimePurchaseOfferDetails() != null) {
//                                double price = skuDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros() / 1000000.00;
//                                payBean.setPrice(price);
//                                payBean.setCurrency(skuDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode());
//                            }

                            if (gpExchangeRes != null && gpExchangeRes.getData() != null) {
                                payBean.setServerTimestamp(gpExchangeRes.getData().getTimestamp());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    dimissDialog();

                    if (iPayCallBack != null) {
                        iPayCallBack.success(payBean);
                    }
                }
            });
        }


    }

    private void callbackFail(String message) {
        isPaying = false;
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    dimissDialog();

                    if (!TextUtils.isEmpty(message) && TAG_USER_CANCEL.equals(message)){
                        PL.i(TAG_USER_CANCEL);
                        if (iPayCallBack != null) {//用户取消
                            iPayCallBack.cancel("");
                        }
                        return;
                    }

                    if (!TextUtils.isEmpty(message)) {//提示错误信息

                        loadingDialog.alert(message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                if (iPayCallBack != null) {
                                    iPayCallBack.fail(null);
                                }
                            }
                        });

                    } else {

                        if (iPayCallBack != null) {
                            iPayCallBack.fail(null);
                        }
                    }

                }
            });
        }

    }

    /**
     * 设置Google储值的回调
     */
    public void setIPayCallBack(IPayCallBack iPayCallBack) {
        this.iPayCallBack = iPayCallBack;
    }

    @Override
    public void startPay(Activity activity, PayReqBean payReqBean) {

//        this.createOrderIdReqBean = null;

        PL.i("the aar version info:" + SdkUtil.getSdkInnerVersion(activity) + "_" + BuildConfig.JAR_VERSION);//打印版本号

        if (activity == null) {
            PL.w("activity is null");
            return;
        }

        if (payReqBean == null) {
            PL.w("payReqBean is null");
            return;
        }

        if (isPaying) {
            PL.w("google is paying...");
            return;
        }

        //创建Loading窗
        if(loadingDialog == null ||  this.mActivity != activity){
            dimissDialog();
            loadingDialog = new LoadingDialog(activity);
        }

        PL.w("vk set paying...");
        isPaying = true;

        this.mActivity = activity;
        mContext = activity.getApplicationContext();

//        if(activity != mActivity && purchaseManager != null){
//            purchaseManager.destroy();
//            purchaseManager = null;
//        }

        this.createOrderIdReqBean = (PayCreateOrderReqBean) payReqBean;
        this.createOrderIdReqBean.setMode(ChannelPlatform.VK.getChannel_platform());
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(ApiRequestMethod.API_ORDER_CREATE);

        if (this.createOrderIdReqBean.isInitOk()) {
            //开始储值,先查询有没有未消耗的商品
            loadingDialog.showProgressDialog();
            boolean isRuStoreInstalled = RuStoreUtils.INSTANCE.isRuStoreInstalled(activity);
            PL.d("vk isRuStoreInstalled:" + isRuStoreInstalled);
          /*  VKPurchaseManger.getBillingClient(activity).getUserInfo().getAuthorizationStatus()
                    .addOnSuccessListener (status -> {
                        PL.d("vk getAuthorizationStatus onSuccess status:" + status.getAuthorized());
                        // Result processing
                        if (status != null && status.getAuthorized()){//已经登录授权，先查询补发再买
                            //getPurchases()
                            VKPurchaseManger.getInstance().queryPurchasesAsyncInPaying(activity);
                        }else {
                            //直接买
                            onePayInActivity(mActivity);
                        }
                    })
                    .addOnFailureListener(throwable -> {
                        // Error processing
                        PL.d("vk getAuthorizationStatus onFailure");
                        onePayInActivity(mActivity);
                    });*/

            UserInteractor userInteractor = VKPurchaseManger.getBillingClient(activity).getUserInteractor();
            userInteractor.getUserAuthorizationStatus()
                    .addOnSuccessListener(status -> {
                        PL.d("vk getAuthorizationStatus onSuccess status:" + status.name());
                        switch (status) {
                            case AUTHORIZED:
                                // Logic when the user is authorized in RuStore
                            {
                                //getPurchases()
                                VKPurchaseManger.getInstance().queryPurchasesAsyncInPaying(activity);
                            }
                                break;
                            case UNAUTHORIZED:
                                // Logic when the user is NOT authorized in RuStore
                            {
                                //直接买
                                onePayInActivity(mActivity);
                            }
                                break;
                        }
                    })
                    .addOnFailureListener(throwable -> {
                        // Error handling
                        PL.d("vk getAuthorizationStatus onFailure");
                        onePayInActivity(mActivity);
                    });

        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
        //isPaying = false;
        PL.w("vk set not paying");
    }

    private void dimissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismissProgressDialog();
        }
        isPaying = false;
    }

    @Override
    public void startQueryPurchase(Activity activity) {

        if (activity == null){
            return;
        }
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        this.mContext = activity;
        //VKPurchaseManger.getInstance().queryPurchasesAsync(mContext);//VK启动游戏不检查

    }

    public void queryPreRegData(final Activity activity, ISdkCallBack iSdkCallBack) {

    }

    @Override
    public void onCreate(Activity activity) {
        PL.i( "onCreate");
        mContext = activity.getApplicationContext();
//        if (purchaseManager == null){
//            purchaseManager = new PurchaseManager(activity.getApplicationContext(), this);
//        }
        VKPurchaseManger.getInstance().purchaseCallback = this;
        VKPurchaseManger.getInstance().initBillingClient(activity);

    }

    @Override
    public void onStart(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {
        PL.i( "onResume");
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onPause(Activity activity) {
        PL.i( "onPause");
    }

    @Override
    public void onStop(Activity activity) {
        PL.i( "onStop");
    }

    @Override
    public void onDestroy(Activity activity) {
        PL.i( "onDestroy");
        dimissDialog();
//        if (purchaseManager != null) {
//            purchaseManager.destroy();
//            purchaseManager = null;
//        }
    }


    /**
     * 查询消耗后开始购买
     */
    private void onePayInActivity(Activity activity) {

//        loadingDialog.showProgressDialog();

//        List productList = new ArrayList<>();
//        productList.add(this.createOrderIdReqBean.getProductId());

        PayApi.requestCreateOrder(activity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
            @Override
            public void success(GPCreateOrderIdRes createOrderIdRes, String msg1) {
                PL.i("requestCreateOrder finish success");

                if (createOrderIdRes == null || createOrderIdRes.getPayData() == null || SStringUtil.isEmpty(createOrderIdRes.getPayData().getOrderId())){
                    callbackFail("create orderId error");
                }else {

                    //5.开始购买
                   /* JSONObject devPayload = new JSONObject();
                    try {
                        skuAmount = createOrderIdRes.getPayData().getAmount();
                        devPayload.put("userId", createOrderIdReqBean.getUserId());
                        devPayload.put("roleId", createOrderIdReqBean.getRoleId());
                        devPayload.put("orderId", createOrderIdRes.getPayData().getOrderId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackFail("devPayload error");
                        return;
                    }*/

                    String devPayloadStr = createOrderIdReqBean.getRoleId();//URLEncoder.encode(devPayload.toString());
                    VKPurchaseManger.getInstance().purchaseProduct(activity, createOrderIdReqBean.getProductId(),createOrderIdRes.getPayData().getOrderId(), devPayloadStr, createOrderIdReqBean.getUserId());

                }

                dimissDialog();
            }

            @Override
            public void fail(GPCreateOrderIdRes createOrderIdRes, String msg) {
                PL.i("requestCreateOrder finish fail");
                //创建订单失败
                if (createOrderIdRes != null && SStringUtil.isNotEmpty(createOrderIdRes.getMessage())) {
                    callbackFail(createOrderIdRes.getMessage());
                }else{
                    callbackFail("error");
                }
            }
        });



    }


//    private void checkNotConfirmedPurchases(Activity activity){
//        VKPurchaseManger.getBillingClient(activity).getUserInfo().getAuthorizationStatus()
//            .addOnSuccessListener (status -> {
//                PL.d("vk getAuthorizationStatus onSuccess status:" + status.getAuthorized());
//                // Result processing
//                if (status != null && status.getAuthorized()){//已经登录授权，先查询补发再买
//                    //getPurchases()
//                    VKPurchaseManger.getInstance().queryPurchasesAsync(activity);
//                }
//            })
//            .addOnFailureListener(throwable -> {
//                // Error processing
//                PL.d("vk getAuthorizationStatus onFailure");
//            });
//    }
    //=================================================================================================
    //=================================================================================================
    //=================================================================================================

    @Override
    public void onPurchaseClientSetupFinished() {

    }

    @Override
    public void onConsumeFinished() {

    }

    @Override
    public void onPurchaseSucceed(ProductPurchaseResult paymentResult) {
        PL.i("onPurchaseSucceed");
        if (paymentResult == null){
            callbackFail("ProductPurchaseResult is null");
            return;
        }
        String purchaseId = paymentResult.getPurchaseId().getValue();
        String orderId = "";
        if (paymentResult.getOrderId() != null) {
            orderId = paymentResult.getOrderId().getValue();
        }
        String productId = paymentResult.getProductId().getValue();
        String invoiceId = paymentResult.getInvoiceId().getValue();
        String subscriptionToken = "";
        boolean sandbox = paymentResult.getSandbox();

        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
//        exchangeReqBean.setDataSignature(subscriptionToken);
//        exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
//        exchangeReqBean.setGoogleOrderId(invoiceId);
//        exchangeReqBean.setThirdPurchaseToken(subscriptionToken);
        exchangeReqBean.setReissue("no");

        exchangeReqBean.setPurchaseId(purchaseId);
        exchangeReqBean.setProductId(productId);
        exchangeReqBean.setInvoiceId(invoiceId);
        exchangeReqBean.setOrderId(orderId);//mw平台订单号
        exchangeReqBean.setSandbox(sandbox);
        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_VK);

        /*try {
            JSONObject payLoadJsonObject = new JSONObject(purchaseData.getDeveloperPayload());
            String userId = payLoadJsonObject.optString("userId");
            String orderId = payLoadJsonObject.optString("orderId");
            String roleId = payLoadJsonObject.optString("roleId");
            exchangeReqBean.setUserId(userId);
            exchangeReqBean.setOrderId(orderId);
            exchangeReqBean.setRoleId(roleId);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        PayApi.requestCommonPaySendStone(mActivity, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
            @Override
            public void success(GPExchangeRes result, String msg) {
                PL.i("startQueryPurchase requestSendStone success => " + msg);
                //3.消费
                //VKPurchaseManger.getInstance().confirmPurchase(mActivity, purchaseId);
                callbackSuccess(null, result);
            }

            @Override
            public void fail(GPExchangeRes result, String msg) {
                PL.i("startQueryPurchase requestSendStone fail => " + msg);
                if (result != null && SStringUtil.isNotEmpty(result.getMessage())){
                    callbackFail(result.getMessage());
                }else {
                    callbackFail("error");
                }
            }
        });

        //3.无论发币成功是否都消费，未确认rustore会5天后自动退款
        VKPurchaseManger.getInstance().confirmPurchase(mActivity, purchaseId);
    }

    @Override
    public void onQueryPurchaseSucceed(List<Purchase> purchases) {
        //改为一次性后下面的将不再走

        PL.i("onQueryPurchaseSucceed");

        if (purchases == null || purchases.isEmpty()){
            PL.i("onQueryPurchaseSucceed empty");
            return;
        }
        for (Purchase purchase : purchases) {

            String purchaseId = purchase.getPurchaseId().getValue();
            if (SStringUtil.isNotEmpty(purchaseId)) {

                if (purchase.getStatus() != null) {
                    //失败
//                    if (purchase.getStatus() == PurchaseStatus.CREATED || purchase.getPurchaseState() == PurchaseState.INVOICE_CREATED) {
//                        VKPurchaseManger.getInstance().deletePurchase(mContext, purchaseId);
//
//                    } else
                    if (purchase.getStatus() == PurchaseStatus.PAID) {//成功
                        //confirmPurchase(context, purchaseId);

                        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
//                        exchangeReqBean.setDataSignature(purchase.getSubscriptionToken());
//                        exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
//                        exchangeReqBean.setThirdPurchaseToken(purchase.getSubscriptionToken());
                        exchangeReqBean.setReissue("yes");

                        exchangeReqBean.setPurchaseId(purchaseId);
                        exchangeReqBean.setProductId(purchase.getProductId().getValue());
                        exchangeReqBean.setInvoiceId(purchase.getInvoiceId().getValue());
                        //unique order id generated by the app (optional). If you specify this parameter in your system, you will receive it via our API. Otherwise, it will be generated automatically.
                        //purchase.getOrderId();
                        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_VK);
                        try {

                            if (purchase.getDeveloperPayload() != null) {
                                String payLoadStr = purchase.getDeveloperPayload().getValue();
//                                payLoadStr = URLDecoder.decode(payLoadStr);
//                                JSONObject payLoadJsonObject = new JSONObject(payLoadStr);
//                                String userId = payLoadJsonObject.optString("userId");
//                                String orderId = payLoadJsonObject.optString("orderId");
//                                String roleId = payLoadJsonObject.optString("roleId");
//                                exchangeReqBean.setUserId(userId);
                                exchangeReqBean.setOrderId(payLoadStr);
//                                exchangeReqBean.setRoleId(roleId);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            //exchangeReqBean.setOrderId(purchase.getOrderId().getValue());
                        }

                        PayApi.requestCommonPaySendStone(mActivity, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
                            @Override
                            public void success(GPExchangeRes result, String msg) {
                                PL.i("startQueryPurchase requestSendStone success => " + msg);
                            }

                            @Override
                            public void fail(GPExchangeRes result, String msg) {
                                PL.i("startQueryPurchase requestSendStone fail => " + msg);
                            }
                        });

                        //同时消耗掉
                        VKPurchaseManger.getInstance().confirmPurchase(mActivity, purchaseId);

                    }
                }

            }

        }

    }

    @Override
    public void onPayingQueryPurchaseSucceed(List<Purchase> purchases) {

        if (purchases == null || purchases.isEmpty()){
            PL.i("onPayingQueryPurchaseSucceed empty");
            onePayInActivity(mActivity);
            return;
        }
        //改为一次性后下面的将不再走
        for (Purchase purchase : purchases) {

            String purchaseId = purchase.getPurchaseId().getValue();
            if (SStringUtil.isNotEmpty(purchaseId)) {

                if (purchase.getStatus() != null) {
                    //失败
//                    if (purchase.getPurchaseState() == PurchaseState.CREATED || purchase.getPurchaseState() == PurchaseState.INVOICE_CREATED) {
//                        VKPurchaseManger.getInstance().deletePurchase(mContext, purchaseId);
//
//                    } else
                    if (purchase.getStatus() == PurchaseStatus.PAID) {//成功
                        //confirmPurchase(context, purchaseId);

                        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
//                        exchangeReqBean.setDataSignature(purchase.getSubscriptionToken());
//                        exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
//                        exchangeReqBean.setThirdPurchaseToken(purchase.getSubscriptionToken());
                        exchangeReqBean.setReissue("yes");

                        exchangeReqBean.setPurchaseId(purchaseId);
                        exchangeReqBean.setProductId(purchase.getProductId().getValue());
                        exchangeReqBean.setInvoiceId(purchase.getInvoiceId().getValue());
                        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_VK);
                        try {
                            if (purchase.getDeveloperPayload() != null) {
                                String payLoadStr = purchase.getDeveloperPayload().getValue();
//                                payLoadStr = URLDecoder.decode(payLoadStr);
//                                JSONObject payLoadJsonObject = new JSONObject(payLoadStr);
//                                String userId = payLoadJsonObject.optString("userId");
//                                String orderId = payLoadJsonObject.optString("orderId");
//                                String roleId = payLoadJsonObject.optString("roleId");
//                                exchangeReqBean.setUserId(userId);
                                exchangeReqBean.setOrderId(payLoadStr);
//                                exchangeReqBean.setRoleId(roleId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //exchangeReqBean.setOrderId(purchase.getOrderId().getValue());
                        }

                        PayApi.requestCommonPaySendStone(mActivity, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
                            @Override
                            public void success(GPExchangeRes result, String msg) {
                                PL.i("startQueryPurchase requestSendStone success => " + msg);
                            }

                            @Override
                            public void fail(GPExchangeRes result, String msg) {
                                PL.i("startQueryPurchase requestSendStone fail => " + msg);
                            }
                        });

                        //同时消耗掉
                        VKPurchaseManger.getInstance().confirmPurchase(mActivity, purchaseId);

                    }
                }

            }

        }
        onePayInActivity(mActivity);
    }

    @Override
    public void onPurchaseFailed(ProductPurchaseResult paymentResult) {

    }

    @Override
    public void onError(String message) {
        PL.i("onError:" + message);
        if (mActivity != null && this.createOrderIdReqBean != null){//此时用户正在购买
            callbackFail("" + message);
        }
    }

    //=================================================================================================

}

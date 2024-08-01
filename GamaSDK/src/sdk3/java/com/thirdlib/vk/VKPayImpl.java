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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.rustore.sdk.billingclient.model.purchase.PaymentResult;
import ru.rustore.sdk.billingclient.model.purchase.Purchase;
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState;

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

        PL.i("onestore pay onConsumeResponse callbackSuccess");
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
        PL.w("google set paying...");
        isPaying = true;

        //创建Loading窗
        if(loadingDialog == null ||  this.mActivity != activity){
            dimissDialog();
            loadingDialog = new LoadingDialog(activity);
        }
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
//            onePayInActivity(activity);
            loadingDialog.showProgressDialog();
            VKPurchaseManger.getInstance().queryPurchasesAsyncInPaying(activity);

        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
        //isPaying = false;
        PL.w("google set not paying");
    }

    private void dimissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismissProgressDialog();
        }
        isPaying = false;
    }

    @Override
    public void startQueryPurchase(Context mContext) {

        if (mContext == null){
            return;
        }
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        this.mContext = mContext.getApplicationContext();
        //VKPurchaseManger.getInstance().queryPurchasesAsync(mContext);//VK启动游戏不检查

    }

    public void queryPreRegData(final Context mContext, ISdkCallBack iSdkCallBack) {

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
                    JSONObject devPayload = new JSONObject();
                    try {
                        skuAmount = createOrderIdRes.getPayData().getAmount();
                        devPayload.put("userId", createOrderIdReqBean.getUserId());
                        devPayload.put("roleId", createOrderIdReqBean.getRoleId());
                        devPayload.put("orderId", createOrderIdRes.getPayData().getOrderId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackFail("devPayload error");
                        return;
                    }

                    VKPurchaseManger.getInstance().purchaseProduct(activity, createOrderIdReqBean.getProductId(),createOrderIdRes.getPayData().getOrderId(),devPayload.toString());

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
    public void onPurchaseSucceed(PaymentResult paymentResult) {
        PL.i("onPurchaseSucceed");
        String purchaseId = ((PaymentResult.Success) paymentResult).getPurchaseId();
        String orderId = ((PaymentResult.Success) paymentResult).getOrderId();
        String productId = ((PaymentResult.Success) paymentResult).getProductId();
        String invoiceId = ((PaymentResult.Success) paymentResult).getInvoiceId();
        String subscriptionToken = ((PaymentResult.Success) paymentResult).getSubscriptionToken();
        boolean sandbox = ((PaymentResult.Success) paymentResult).getSandbox();

        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
//        exchangeReqBean.setDataSignature(subscriptionToken);
//        exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
//        exchangeReqBean.setGoogleOrderId(invoiceId);
        exchangeReqBean.setThirdPurchaseToken(subscriptionToken);
        exchangeReqBean.setReissue("no");

        exchangeReqBean.setPurchaseId(purchaseId);
        exchangeReqBean.setProductId(productId);
        exchangeReqBean.setInvoiceId(invoiceId);
        exchangeReqBean.setOrderId(orderId);//mw平台订单号
        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_ONESTORE);

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
                VKPurchaseManger.getInstance().confirmPurchase(mActivity, purchaseId);
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

    }

    @Override
    public void onQueryPurchaseSucceed(List<Purchase> purchases) {

        PL.i("onQueryPurchaseSucceed");

        if (purchases == null || purchases.isEmpty()){
            PL.i("onQueryPurchaseSucceed empty");
            return;
        }
        for (Purchase purchase : purchases) {

            String purchaseId = purchase.getPurchaseId();
            if (SStringUtil.isNotEmpty(purchaseId)) {

                if (purchase.getPurchaseState() != null) {
                    //失败
                    if (purchase.getPurchaseState() == PurchaseState.CREATED || purchase.getPurchaseState() == PurchaseState.INVOICE_CREATED) {
                        VKPurchaseManger.getInstance().deletePurchase(mContext, purchaseId);

                    } else if (purchase.getPurchaseState() == PurchaseState.PAID) {//成功
                        //confirmPurchase(context, purchaseId);

                        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
//                        exchangeReqBean.setDataSignature(purchase.getSubscriptionToken());
//                        exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
                        exchangeReqBean.setThirdPurchaseToken(purchase.getSubscriptionToken());
                        exchangeReqBean.setReissue("yes");

                        exchangeReqBean.setPurchaseId(purchaseId);
                        exchangeReqBean.setProductId(purchase.getProductId());
                        exchangeReqBean.setInvoiceId(purchase.getInvoiceId());
                        //unique order id generated by the app (optional). If you specify this parameter in your system, you will receive it via our API. Otherwise, it will be generated automatically.
                        //purchase.getOrderId();
                        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_ONESTORE);
                        try {
                            JSONObject payLoadJsonObject = new JSONObject(purchase.getDeveloperPayload());
                            String userId = payLoadJsonObject.optString("userId");
                            String orderId = payLoadJsonObject.optString("orderId");
                            String roleId = payLoadJsonObject.optString("roleId");
                            exchangeReqBean.setUserId(userId);
                            exchangeReqBean.setOrderId(orderId);
                            exchangeReqBean.setRoleId(roleId);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
            PL.i("onQueryPurchaseSucceed empty");
            onePayInActivity(mActivity);
            return;
        }
        for (Purchase purchase : purchases) {

            String purchaseId = purchase.getPurchaseId();
            if (SStringUtil.isNotEmpty(purchaseId)) {

                if (purchase.getPurchaseState() != null) {
                    //失败
                    if (purchase.getPurchaseState() == PurchaseState.CREATED || purchase.getPurchaseState() == PurchaseState.INVOICE_CREATED) {
                        VKPurchaseManger.getInstance().deletePurchase(mContext, purchaseId);

                    } else if (purchase.getPurchaseState() == PurchaseState.PAID) {//成功
                        //confirmPurchase(context, purchaseId);

                        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
//                        exchangeReqBean.setDataSignature(purchase.getSubscriptionToken());
//                        exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
                        exchangeReqBean.setThirdPurchaseToken(purchase.getSubscriptionToken());
                        exchangeReqBean.setReissue("yes");

                        exchangeReqBean.setPurchaseId(purchaseId);
                        exchangeReqBean.setProductId(purchase.getProductId());
                        exchangeReqBean.setInvoiceId(purchase.getInvoiceId());
                        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_ONESTORE);
                        try {
                            JSONObject payLoadJsonObject = new JSONObject(purchase.getDeveloperPayload());
                            String userId = payLoadJsonObject.optString("userId");
                            String orderId = payLoadJsonObject.optString("orderId");
                            String roleId = payLoadJsonObject.optString("roleId");
                            exchangeReqBean.setUserId(userId);
                            exchangeReqBean.setOrderId(orderId);
                            exchangeReqBean.setRoleId(roleId);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
    public void onPurchaseFailed(PaymentResult paymentResult) {

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

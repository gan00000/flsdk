package com.mw.sdk.pay.nowgg;

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
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.nowgg.billing.BillingManager;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.SdkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gg.now.billingclient.api.BillingClient;
import gg.now.billingclient.api.BillingResult;
import gg.now.billingclient.api.ConsumeResponseListener;
import gg.now.billingclient.api.Purchase;
import gg.now.billingclient.api.SkuDetails;
import gg.now.billingclient.api.SkuDetailsResponseListener;

/**
 * Created by gan on 2017/2/23.
 */

public class NowggPayImpl implements IPay, BillingManager.BillingUpdatesListener {

    private LoadingDialog loadingDialog;

    private BillingManager purchaseManager;
    /**
     * 当次的商品详情
     */
    //private ProductDetail productDetail = null;
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

        PL.i("nowgg pay onConsumeResponse callbackSuccess");
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    BasePayBean payBean = new BasePayBean();
                    if (purchase != null) {
                        try {
                            JSONObject devPayload = new JSONObject(purchase.getDeveloperPayload());
//                            String userId = devPayload.optString("userId","");
//                            String roleId = devPayload.optString("roleId","");
                            String orderId = devPayload.optString("orderId","");
                            payBean.setOrderId(orderId);
                            payBean.setTransactionId(purchase.getOrderId());
                            payBean.setPackageName(purchase.getPackageName());
                            payBean.setUsdPrice(skuAmount);
                            if (createOrderIdReqBean != null) {
                                payBean.setProductId(createOrderIdReqBean.getProductId());
                                payBean.setCpOrderId(createOrderIdReqBean.getCpOrderId());
                            }
//                    payBean.setmItemType(purchase.getItemType());
                            payBean.setOriginPurchaseData(purchase.getOriginalJson());
//                            payBean.setPurchaseState(purchase.getPurchaseState());
                            payBean.setPurchaseTime(purchase.getPurchaseTime());
                            payBean.setSignature(purchase.getSignature());
                            payBean.setDeveloperPayload(purchase.getDeveloperPayload());
                            payBean.setmToken(purchase.getPurchaseToken());
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

                        if (loadingDialog != null) {
                            loadingDialog.alert(message, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                    if (iPayCallBack != null) {
                                        iPayCallBack.fail(null);
                                    }
                                }
                            });
                        }

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


        PL.w("google set paying...");
        isPaying = true;

        this.mActivity = activity;
        mContext = activity.getApplicationContext();

//        if(activity != mActivity && purchaseManager != null){
//            purchaseManager.destroy();
//            purchaseManager = null;
//        }

        if (purchaseManager == null){
            purchaseManager = new BillingManager(activity, this);
        }

        this.createOrderIdReqBean = (PayCreateOrderReqBean) payReqBean;
        this.createOrderIdReqBean.setMode("nowgg");
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
            purchaseManager.queryPurchasesAsync(true);

        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
        //isPaying = false;
       // PL.w("google set not paying");
    }

    private void dimissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismissProgressDialog();
        }
        isPaying = false;
    }

    @Override
    public void startQueryPurchase(Activity activity) {

        if (purchaseManager == null || activity == null){
            return;
        }
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        this.mContext = activity;
        purchaseManager.queryPurchasesAsync(false);

    }

    public void queryPreRegData(final Activity activity, ISdkCallBack iSdkCallBack) {

    }

    @Override
    public void onCreate(Activity activity) {
        PL.i( "onCreate");
        mContext = activity.getApplicationContext();
        this.mActivity = activity;
        if (purchaseManager == null){
            purchaseManager = new BillingManager(activity, this);
        }

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
        if (purchaseManager != null) {
            purchaseManager.destroy();
            purchaseManager = null;
        }
    }


    /**
     * 查询消耗后开始购买
     */
    private void onePayInActivity(Activity activity) {

//        loadingDialog.showProgressDialog();

        List productList = new ArrayList<>();
        productList.add(this.createOrderIdReqBean.getProductId());

        purchaseManager.querySkuDetailsAsync(productList, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int i, List<SkuDetails> list) {

                PayApi.requestCreateOrder(activity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
                    @Override
                    public void success(GPCreateOrderIdRes createOrderIdRes, String msg1) {
                        PL.i("requestCreateOrder finish success");
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

                        purchaseManager.initiatePurchaseFlow(createOrderIdReqBean.getProductId(), BillingClient.SkuType.INAPP, devPayload.toString());

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
        });

    }

    //=================================================================================================
    //=================================================================================================
    //=================================================================================================

    @Override
    public void onBillingClientSetupFinished() {
        PL.i("onBillingClientSetupFinished");
    }

//    @Override
//    public void onConsumeFinished(String token, int result, boolean isPaying) {
//        PL.i("startQueryPurchase onConsumeResponse => " + token);
//    }

    @Override
    public void onAcknowledgeFinished(int result) {

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        PL.i("onPurchaseSucceed");
        if (purchases == null || purchases.isEmpty()){
            PL.i("onPurchaseSucceed empty");
            //callbackFail("PurchaseData empty");
            return;
        }
        for (Purchase purchaseData: purchases) {
            //补发币

            PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
            exchangeReqBean.setDataSignature(purchaseData.getSignature());
            exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
            exchangeReqBean.setReissue("no");

            exchangeReqBean.setGoogleOrderId(purchaseData.getOrderId());
            exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_NOWGG);

            try {
                JSONObject payLoadJsonObject = new JSONObject(purchaseData.getDeveloperPayload());
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
                    //3.消费
                    purchaseManager.consumeAsync(purchaseData.getPurchaseToken(), new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(int i, String s) {

                        }
                    });

                    callbackSuccess(purchaseData, result);
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
    }

    @Override
    public void onQueryPurchaseSucceed(BillingResult billingResult, List<Purchase>  purchases) {//默默查询是使用
        PL.i("onQueryPurchaseSucceed");

        if (purchases == null || purchases.isEmpty()){
            PL.i("onQueryPurchaseSucceed empty");
            return;
        }
        for (Purchase purchaseData: purchases) {
            //补发币

            PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(this.mContext);
            exchangeReqBean.setDataSignature(purchaseData.getSignature());
            exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
            exchangeReqBean.setReissue("yes");

            exchangeReqBean.setGoogleOrderId(purchaseData.getOrderId());
            exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_NOWGG);
            try {
                JSONObject payLoadJsonObject = new JSONObject(purchaseData.getDeveloperPayload());
                String userId = payLoadJsonObject.optString("userId");
                String orderId = payLoadJsonObject.optString("orderId");
                String roleId = payLoadJsonObject.optString("roleId");
                exchangeReqBean.setUserId(userId);
                exchangeReqBean.setOrderId(orderId);
                exchangeReqBean.setRoleId(roleId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PayApi.requestCommonPaySendStone(this.mContext, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
                @Override
                public void success(GPExchangeRes result, String msg) {
                    PL.i("startQueryPurchase requestSendStone success => " + msg);
                    //3.消费
                    purchaseManager.consumeAsync(purchaseData.getPurchaseToken(), new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(int i, String s) {

                        }
                    });
                }

                @Override
                public void fail(GPExchangeRes result, String msg) {
                    PL.i("startQueryPurchase requestSendStone fail => " + msg);
                }
            });

        }
    }

    int consume_index;
    @Override
    public void onPayingQueryPurchaseSucceed(BillingResult billingResult, List<Purchase>  purchases) {

        PL.i("onPayingQueryPurchaseSucceed");
        consume_index = 0;
        if (purchases == null || purchases.isEmpty()){
            PL.i("onQueryPurchaseSucceed empty");
            onePayInActivity(mActivity);
            return;
        }
        consume_index = purchases.size();
        for (Purchase purchaseData: purchases) {//正在购买的时候消耗
            //补发币

            PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
            exchangeReqBean.setDataSignature(purchaseData.getSignature());
            exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
            exchangeReqBean.setReissue("yes");

            exchangeReqBean.setGoogleOrderId(purchaseData.getOrderId());
            exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_NOWGG);
            try {
                JSONObject payLoadJsonObject = new JSONObject(purchaseData.getDeveloperPayload());
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
            purchaseManager.consumeAsync(purchaseData.getPurchaseToken(), new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(int i, String s) {
                    consume_index = consume_index - 1;
                    if (consume_index == 0){

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //全部已经消耗结束
                                onePayInActivity(mActivity);
                            }
                        });

                    }
                }
            });

        }

    }

    @Override
    public void onPayError(String errorMsg) {
        PL.i("onPayError errorMsg=" + errorMsg);
        //purchaseManager.handleIapResultError(iapResult);//处理iapResult
        PL.i("onPayError isPaying=" + isPaying);
        if (isPaying){
            callbackFail(errorMsg);
        }
    }

}

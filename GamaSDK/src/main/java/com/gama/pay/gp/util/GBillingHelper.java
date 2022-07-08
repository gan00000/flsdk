package com.gama.pay.gp.util;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.core.base.utils.PL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 处理Google支付3.0的帮助类
 */
public class GBillingHelper implements PurchasesUpdatedListener {
    private BillingClient billingClient;
    private static GBillingHelper gBillingHelper;
    private ArrayList<BillingHelperStatusCallback> mBillingCallbackList = new ArrayList<>();
    private boolean isBillingInit = false;
    private PurchasesUpdatedListener purchasesUpdatedListener;

//    private static String TAG = GBillingHelper.class.getSimpleName();

    public static GBillingHelper getInstance() {
        if (gBillingHelper == null) {
            synchronized (GBillingHelper.class) {
                if (gBillingHelper == null) {
                    gBillingHelper = new GBillingHelper();
                }
            }
        }
        return gBillingHelper;
    }

    /**
     * 添加Callback
     * @param callback
     */
    public void setBillingHelperStatusCallback(BillingHelperStatusCallback callback) {
        if (!this.mBillingCallbackList.contains(callback)) {
            PL.i( "Add BillingCallbackList.");
            mBillingCallbackList.add(callback);
        } else {
            PL.i( "Already have BillingCallbackList.");
        }
    }

    /**
     * 移除Callback
     * @param callback
     */
    public void removeBillingHelperStatusCallback(BillingHelperStatusCallback callback) {
        if (this.mBillingCallbackList.contains(callback)) {
            this.mBillingCallbackList.remove(callback);
            PL.i( "Remove BillingCallbackList.");
        } else {
            PL.i( "No BillingCallbackList match.");
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {

        PL.i( "billing onPurchasesUpdated.");
//        billingClient.showInAppMessages()
        if (purchasesUpdatedListener != null){
            purchasesUpdatedListener.onPurchasesUpdated(billingResult,purchases);
        }
        if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
            for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                mBillingCallback.onPurchaseUpdate(billingResult, purchases);
            }
        }
    }

    private void executeRunnable(Context context, Runnable runnable) {

        if (isBillingInit && billingClient != null && billingClient.isReady()) {
//            BillingResult billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.IN_APP_MESSAGING);
//            PL.i("IN_APP_MESSAGING isFeatureSupported:" + billingResult.toString());
//            BillingResult billingResult2 = billingClient.isFeatureSupported(BillingClient.FeatureType.PRODUCT_DETAILS);
//            PL.i("PRODUCT_DETAILS isFeatureSupported:" + billingResult.toString());
            runnable.run();
        } else {
            startServiceConnection(context, runnable);
        }
    }

//    public boolean isFeatureSupported(){
//       return billingClient.isFeatureSupported(BillingClient.FeatureType.IN_APP_MESSAGING).;
//    }

    private void startServiceConnection(Context context, final Runnable executeOnSuccess) {
        billingClient = BillingClient.newBuilder(context).setListener(this)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                int billingResponseCode = billingResult.getResponseCode();
                // Logic from ServiceConnection.onServiceConnected should be moved here.
                if (billingResponseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    isBillingInit = true;
                    PL.i( "onBillingSetupFinished -> success");
//                    callback.onStartUp(true);
                    if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
                        for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                            mBillingCallback.onStartUp(true, billingResult.getDebugMessage());
                        }
                    }
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                } else {
                    PL.i( "onBillingSetupFinished -> fail");
//                    callback.onStartUp(false);
                    if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
                        for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                            mBillingCallback.onStartUp(false, billingResult.getDebugMessage());
                        }
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Logic from ServiceConnection.onServiceDisconnected should be moved here.
                PL.i( "onBillingServiceDisconnected -> ");
                if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
                    for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                        mBillingCallback.onStartUp(false, "");
                    }
                }
            }
        });
    }


    /**
     * 消费购买
     *
     * 由于消耗请求偶尔会失败，因此您必须检查安全的后端服务器，确保所有购买令牌都未被使用过，这样您的应用就不会针对同一购买交易多次授予权利。
     * 或者，您的应用也可以等到您收到 Google Play 发来的成功消耗响应后再授予权利。如果您选择在 Google Play 发来成功消耗响应之前不让用户消耗所购商品，
     * 那么您必须非常小心，在消耗请求发出后时刻跟踪相应商品。
     *
     * @param purchase
     * @param isReplaceConsume 是否补单的消费
     */
    public void consumeAsync(Context context, Purchase purchase, boolean isReplaceConsume,ConsumeResponseListener consumeResponseListener) {
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, @NonNull String purchaseToken) {

                PL.i( "------consumeAsync finish--------");

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    PL.i( "consumeAsync purchaseToken -> " + purchaseToken);

                    if (consumeResponseListener != null){
                        consumeResponseListener.onConsumeResponse(billingResult,purchaseToken);
                    }

                    if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
                        for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                            mBillingCallback.onConsumeResult(context, billingResult, purchaseToken, purchase, isReplaceConsume);
                        }
                    }
                } else {
                    PL.i( "consumePurchase error code -> " + billingResult.getResponseCode());
                    PL.i( "consumePurchase error message -> " + billingResult.getDebugMessage());
                    if (consumeResponseListener != null){
                        consumeResponseListener.onConsumeResponse(billingResult,null);
                    }
                    if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
                        for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                            mBillingCallback.onConsumeResult(context, billingResult, purchaseToken, null, isReplaceConsume);
                        }
                    }
                }
            }
        };
        PL.i( "------consumeAsync start--------");
        billingClient.consumeAsync(consumeParams, listener);
    }

    /**
     * 消费多个购买
     *
     * @param purchases
     * @param isReplaceConsume 是否补单的消费
     */
//    public void consumePurchaseList(Context context, List<Purchase> purchases, boolean isReplaceConsume,ConsumeResponseListener consumeResponseListener) {
//        for (Purchase purchase : purchases) {
//            consumeAsync(context, purchase, isReplaceConsume,consumeResponseListener);
//        }
//    }

    /**
     * 查询商品详情
     */
    public void queryProductDetailsAsync(Context context, List<String> skuList, SkuDetailsResponseListener detailsResponseListener) {
        Runnable queryInventoryRunnable = new Runnable() {
            @Override
            public void run() {

                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                billingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                                // Process the result.

                                if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
                                    for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                                        mBillingCallback.onQuerySkuResult(context,skuDetailsList);
                                    }
                                }
                                if (detailsResponseListener != null){
                                    detailsResponseListener.onSkuDetailsResponse(billingResult,skuDetailsList);
                                }

                            }
                        }
                );
            }
        };
        executeRunnable(context, queryInventoryRunnable);
    }

    /**
     * 开始购买,需要传入sku查询商品详情
     */
    public void launchPurchaseFlow(Context context, String sku,String userId, String orderId,PurchasesUpdatedListener purchasesUpdatedListener) {

        this.purchasesUpdatedListener = purchasesUpdatedListener;

        queryProductDetailsAsync(context, Collections.singletonList(sku), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                PL.i( "queryProductDetailsAsync finish ");
                if (list != null && !list.isEmpty()) {
                    launchPurchaseFlow(context, userId, orderId, list.get(0));
                }else{
                    PL.i( "queryProductDetailsAsync finish fail,SkuDetails not find");
                    if (purchasesUpdatedListener != null) {
                        purchasesUpdatedListener.onPurchasesUpdated(billingResult, null);
                    }
                }
            }
        });

    }

    /**
     * 开始购买,直接传入商品详情
     */
    private void launchPurchaseFlow(Context context,String userId,String orderId, SkuDetails productDetails) {

        Runnable launchPurchaseRunnable = new Runnable() {
            @Override
            public void run() {
                PL.i( "productDetails -> " + productDetails.toString());

                // An activity reference from which the billing flow will be launched.
                // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(productDetails)
                        .setObfuscatedAccountId(userId)//64 个字符
                        .setObfuscatedProfileId(orderId)
                        .build();

                if (context instanceof Activity) {

                    PL.i("start launchBillingFlow");
                    BillingResult billingResult = billingClient.launchBillingFlow((Activity) context, billingFlowParams);
                    PL.i("start launchBillingFlow return:" + billingResult.getResponseCode());
                    if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
                        for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                            mBillingCallback.launchBillingFlowResult(context, billingResult);
                        }
                    }
                } else {
                    PL.i("context is not Activity error");
                    for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                        BillingResult billingResult = BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.ERROR).build();
                        mBillingCallback.launchBillingFlowResult(context, billingResult);
                    }
                }
//                            int responseCode = billingResult.getResponseCode();
//                            String responseMessage = billingResult.getDebugMessage();
//                            PL.i( "launchPurchaseFlow responseCode -> " + responseCode);
//                            PL.i( "launchPurchaseFlow responseMessage -> " + responseMessage);
            }
        };
        executeRunnable(context, launchPurchaseRunnable);
    }

    /**
     * 查询已购买
     * 在大多数情况下，您的应用会通过 PurchasesUpdatedListener 收到购买交易的通知。
     * 但在某些情况下，您的应用通过调用 BillingClient.queryPurchasesAsync() 得知购买交易
     *
     * 需验证购买交易，请先检查购买交易的状态是否为 PURCHASED。如果购买交易的状态为 PENDING，则您应按照处理待处理的交易中的说明处理购买交易。
     * 对于通过 onPurchasesUpdated() 或 queryPurchasesAsync() 接收的购买交易，您应在应用授予权利之前进一步验证购买交易以确保其合法性
     *
     * 一旦您验证了购买交易，您的应用就可以向用户授予权利了。授予权利后，您的应用必须确认购买交易。此确认会告知 Google Play 您已授予购买权。
     */
    public void queryPurchasesAsync(Context context, PurchasesResponseListener purchasesResponseListener) {
        Runnable queryPurchaseRunnable = new Runnable() {
            @Override
            public void run() {

                billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                        if (purchasesResponseListener != null){
                            purchasesResponseListener.onQueryPurchasesResponse(billingResult,list);
                        }
                    }
                });
            }
        };
        executeRunnable(context, queryPurchaseRunnable);
    }

    /**
     * 关闭BillingClient
     */
    public void destroy() {
        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
            billingClient = null;
            isBillingInit = false;
        }
    }

    /**
     * 功能回调
     */
    public interface BillingHelperStatusCallback {
//        /**
//         * BillingClient链接回调
//         * @param isSuccess
//         */
        void onStartUp(boolean isSuccess, String msg);

        /**
         * 查询商品Sku回调
         *
         * @param productDetails
         */
        void onQuerySkuResult(Context context, List<SkuDetails> productDetails);

        /**
         * 查询购买的回调
         */
        void queryPurchaseResult(Context context, List<Purchase> purchases);

        /**
         * 消费的回调
         */
        void onConsumeResult(Context context, BillingResult billingResult, @NonNull String purchaseToken, Purchase purchase, boolean isReplaceConsume);

        /**
         * 支付回调
         */
        void onPurchaseUpdate(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases);

        /**
         * 拉起支付页面的结果
         */
        void launchBillingFlowResult(Context context, BillingResult billingResult);
    }
}

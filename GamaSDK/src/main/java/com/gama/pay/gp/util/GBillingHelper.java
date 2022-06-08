package com.gama.pay.gp.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.core.base.utils.PL;
import com.mw.base.utils.GamaUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
     * @param purchase
     * @param isReplaceConsume 是否补单的消费
     */
    public void consumePurchase(Context context, Purchase purchase, boolean isReplaceConsume,ConsumeResponseListener consumeResponseListener) {
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
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    PL.i( "purchaseToken -> " + purchaseToken);

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

        billingClient.consumeAsync(consumeParams, listener);
    }

    /**
     * 消费多个购买
     *
     * @param purchases
     * @param isReplaceConsume 是否补单的消费
     */
    public void consumePurchaseList(Context context, List<Purchase> purchases, boolean isReplaceConsume,ConsumeResponseListener consumeResponseListener) {
        for (Purchase purchase : purchases) {
            consumePurchase(context, purchase, isReplaceConsume,consumeResponseListener);
        }
    }

    /**
     * 查询商品详情
     */
    public void queryProductDetailsAsync(Context context, List<String> skuList, ProductDetailsResponseListener detailsResponseListener) {
        Runnable queryInventoryRunnable = new Runnable() {
            @Override
            public void run() {
                List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
                if (skuList != null && !skuList.isEmpty()){

                    for (String skuId: skuList) {
                        productList.add(QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(skuId)
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build());
                    }

                }
                QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                        .setProductList(productList)
                        .build();

                billingClient.queryProductDetailsAsync(
                        params,
                        new ProductDetailsResponseListener() {
                            public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                                // Process the result
                                if (detailsResponseListener != null){
                                    detailsResponseListener.onProductDetailsResponse(billingResult,productDetailsList);
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
    public void launchPurchaseFlow(Context context, String sku, String orderId,PurchasesUpdatedListener purchasesUpdatedListener) {

        this.purchasesUpdatedListener = purchasesUpdatedListener;
        queryProductDetailsAsync(context, Collections.singletonList(sku), new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {

                PL.i( "queryProductDetailsAsync finish ");
                if (list != null && !list.isEmpty()) {
                    launchPurchaseFlow(context, orderId, list.get(0));
                }
            }
        });

    }

    /**
     * 开始购买,直接传入商品详情
     */
    private void launchPurchaseFlow(Context context,String orderId, ProductDetails productDetails) {

        Runnable launchPurchaseRunnable = new Runnable() {
            @Override
            public void run() {
                PL.i( "productDetails -> " + productDetails.toString());

                // An activity reference from which the billing flow will be launched.
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                                Arrays.asList(BillingFlowParams.ProductDetailsParams.newBuilder()
                                        // fetched via queryProductDetailsAsync
                                        .setProductDetails(productDetails)
                                        // to get an offer token, call ProductDetails.getOfferDetails()
                                        // for a list of offers that are available to the user
//                                        .setOfferToken(GamaUtil.getUid(context))
//                                        .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                        .build())
                        )
                        .build();

                if (context instanceof Activity) {
                    BillingResult billingResult = billingClient.launchBillingFlow((Activity) context, billingFlowParams);
                    if (mBillingCallbackList != null && !mBillingCallbackList.isEmpty()) {
                        for (BillingHelperStatusCallback mBillingCallback : mBillingCallbackList) {
                            mBillingCallback.launchBillingFlowResult(context, billingResult);
                        }
                    }
                } else {
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
     */
    public void queryPurchase(Context context, PurchasesResponseListener purchasesResponseListener) {
        Runnable queryPurchaseRunnable = new Runnable() {
            @Override
            public void run() {

                billingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                        new PurchasesResponseListener() {
                            public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> purchases) {
                                // Process the result
                                if (purchasesResponseListener != null){
                                    purchasesResponseListener.onQueryPurchasesResponse(billingResult,purchases);
                                }
                            }
                        }
                );
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
        void onQuerySkuResult(Context context, List<ProductDetails> productDetails);

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

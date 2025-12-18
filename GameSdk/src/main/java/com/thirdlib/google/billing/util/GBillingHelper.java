package com.thirdlib.google.billing.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.core.base.utils.PL;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * 处理Google支付3.0的帮助类
 */
public class GBillingHelper implements PurchasesUpdatedListener {
    private BillingClient billingClient;
    private static GBillingHelper gBillingHelper;
    //private ArrayList<BillingHelperStatusCallback> mBillingCallbackList = new ArrayList<>();
    private BillingHelperStatusCallback billingHelperStatusCallback;
    private boolean isBillingInit = false;

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
        this.billingHelperStatusCallback = callback;
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {

        PL.i( "billing onPurchasesUpdated.");//会被多种情况调用
        if (billingResult == null){
            if (billingHelperStatusCallback != null){
                billingHelperStatusCallback.handleError(4, "onPurchasesUpdated billingResult null");
            }
            return;
        }
        PL.i( "onPurchaseUpdate response code : " + billingResult.getResponseCode());
        PL.i( "onPurchaseUpdate response msg : " + billingResult.getDebugMessage());

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) { //支付成功，进行消费
            PL.i( "onPurchaseUpdate pay success.");

            if (billingHelperStatusCallback != null){
                billingHelperStatusCallback.onPurchaseUpdate(billingResult, purchases);
            }

        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) { //用户取消
            // Handle an error caused by a user cancelling the purchase flow.
            PL.i( "user cancelling the purchase flow");
            if (billingHelperStatusCallback != null){
                billingHelperStatusCallback.handleCancel("");
            }
        } else { //发生错误
            // Handle any other error codes.
            if (billingHelperStatusCallback != null){
                billingHelperStatusCallback.handleError(5,  "response code=" + billingResult.getResponseCode() + ", msg=" + billingResult.getDebugMessage());
            }
        }

    }

    private void startBillingService(Context context, BillingSetupCallback billingSetupCallback) {

        if (billingClient != null && billingClient.isReady() && isBillingInit) {
            PL.d("startServiceConnection billingClient isReady");
            if (billingSetupCallback != null){
                billingSetupCallback.onSuccess();
            }
            return;
        }
        if (billingClient != null){
            try {
                PL.d("startServiceConnection billingClient.endConnection()");
                billingClient.endConnection();
            } catch (Exception e) {
                PL.d("startServiceConnection billingClient.endConnection() exception");
                e.printStackTrace();
            }
        }
        PL.i( "startServiceConnection start");
        isBillingInit = false;
        billingClient = BillingClient.newBuilder(context)
//                .enablePendingPurchases()
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .setListener(this)
                .enableAutoServiceReconnection() //如果在服务断开连接时发出 API 调用，Play 结算库现在可以自动重新建立服务连接。这可能会导致 SERVICE_DISCONNECTED 响应减少，因为重新连接是在发出 API 调用之前在内部处理的。
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                int billingResponseCode = billingResult.getResponseCode();
                // Logic from ServiceConnection.onServiceConnected should be moved here.
                if (billingResponseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    isBillingInit = true;
                    PL.i( "startServiceConnection onBillingSetupFinished -> success");
                    if (billingSetupCallback != null){
                        billingSetupCallback.onSuccess();
                    }

                } else {
                    PL.i( "startServiceConnection onBillingSetupFinished -> fail");
                    isBillingInit = false;
                    if (billingHelperStatusCallback != null){
                        billingHelperStatusCallback.handleError(1, billingResult.getDebugMessage());
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Logic from ServiceConnection.onServiceDisconnected should be moved here.
                PL.i( "startServiceConnection onBillingServiceDisconnected -> ");
                isBillingInit = false;
                if (billingHelperStatusCallback != null){
                    billingHelperStatusCallback.handleError(1, "onBillingServiceDisconnected");
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
    public void consumeAsync(Activity activity, Purchase purchase, boolean isReplaceConsume,ConsumeResponseListener consumeResponseListener) {

        startBillingService(activity, new BillingSetupCallback() {
            @Override
            public void onSuccess() {

                // Verify the purchase.
                // Ensure entitlement was not already granted for this purchaseToken.
                // Grant entitlement to the user.
                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                PL.i( "------consumeAsync start--------");
                billingClient.consumeAsync(consumeParams, consumeResponseListener);
                PL.i( "------consumeAsync end--------");
            }
        });

    }


    /**
     * 查询商品详情
     */
    private void queryProductDetailsAsync(Context context, String productId, ProductDetailsResponseListener detailsResponseListener) {
        PL.i( "start queryProductDetailsAsync productId =>" + productId);
        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, detailsResponseListener);

    }

    /**
     * 开始购买,需要传入sku查询商品详情
     */
    public void launchPurchaseFlow(Activity activity, String sku,String userId, String orderId) {

        startBillingService(activity, new BillingSetupCallback() {
            @Override
            public void onSuccess() {

                queryProductDetailsAsync(activity, sku, new ProductDetailsResponseListener() {
                    @Override
                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull QueryProductDetailsResult queryProductDetailsResult) {

                        PL.i( "queryProductDetailsAsync finish ");
                        PL.i( "onQuerySkuResult error responseCode => " + billingResult.getResponseCode() + ",debugMessage => " + billingResult.getDebugMessage());
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

//                            for (ProductDetails productDetails : queryProductDetailsResult().getProductDetailsList()) {
//                                // Process success retrieved product details here.
//                            }
//
//                            for (UnfetchedProduct unfetchedProduct : queryproductDetailsResult.getUnfetchedProductList()) {
//                                // Handle any unfetched products as appropriate.
//                            }
                            List<ProductDetails> list = queryProductDetailsResult.getProductDetailsList();

                            if (list == null || list.isEmpty()){
                                PL.i( "queryProductDetailsAsync ProductDetail list empty");
                                if (billingHelperStatusCallback != null) {
                                    billingHelperStatusCallback.handleError(2, "can not find ProductDetail=>" + sku);
                                }

                            }else {

                                if (billingHelperStatusCallback != null) {
                                    billingHelperStatusCallback.onQuerySkuResult(list, sku);
                                }

                                launchPurchaseFlow(activity, userId, orderId, list.get(0));
                            }


                        }else{
                            PL.i( "queryProductDetailsAsync finish error => " + billingResult.getDebugMessage());
                            if (billingHelperStatusCallback != null) {
                                billingHelperStatusCallback.handleError(2, billingResult.getDebugMessage());
                            }
                        }


                    }

                });

            }
        });
    }

    /**
     * 开始购买,直接传入商品详情
     */
    private void launchPurchaseFlow(Activity activity, String userId, String orderId, ProductDetails productDetails) {

        PL.i( "productDetails -> " + productDetails.toString());

//                queryProductDetailsAsync 的回调会返回一个 List<ProductDetails>。每个 ProductDetails 项目都包含商品的相关信息（ID、商品名、类型等）。主要区别在于，
//                订阅商品现在还包含一个 List<ProductDetails.SubscriptionOfferDetails>，其中包含用户可以享受的所有优惠。
//                由于以前的 Play 结算库版本不支持新对象（订阅、基础方案、优惠等），因此新系统将每个订阅 SKU 转换为单个向后兼容的基础方案和优惠。
//                可用的一次性购买商品也改为使用 ProductDetails 对象。您可以使用 getOneTimePurchaseOfferDetails() 方法访问一次性购买商品的优惠详情。

        // Retrieve a value for "productDetails" by calling queryProductDetailsAsync()
        // Get the offerToken of the selected offer
//                String offerToken = productDetails
//                        .getSubscriptionOfferDetails()
//                        .get(0)
//                        .getOfferToken();
        // Set the parameters for the offer that will be presented
        // in the billing flow creating separate productDetailsParamsList variable
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = ImmutableList.of(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
//                                        .setOfferToken(offerToken)
                        .build()
        );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .setObfuscatedAccountId(userId)//64 个字符
                .setObfuscatedProfileId(orderId)
                .build();

        PL.i("start launchBillingFlow");
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        PL.i("start launchBillingFlow return:" + billingResult.getResponseCode());

        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            if (billingHelperStatusCallback != null){
                String msg = billingResult.getDebugMessage();
                if (TextUtils.isEmpty(msg)){
                    msg = "Google play billing error, please contact customer service";
                }
                billingHelperStatusCallback.handleError(3, msg);
            }
        }

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
    public void queryPurchasesAsync(Activity activity, boolean isPaying) {

        startBillingService(activity, new BillingSetupCallback() {
            @Override
            public void onSuccess() {

                PL.i("start queryPurchasesAsync");
                billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), new PurchasesResponseListener() {
                            public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> purchases) {
                                // Process the result
                                PL.i("queryPurchasesAsync end");
                                if (billingHelperStatusCallback != null){
                                    if (isPaying){
                                        billingHelperStatusCallback.queryPurchaseResultInPaying(purchases);
                                    }else {
                                        billingHelperStatusCallback.queryPurchaseResult(purchases);
                                    }
                                }
                            }
                        }
                );
            }
        });
    }

    /**
     * 关闭BillingClient
     */
    public void destroy() {
        if (billingClient != null && billingClient.isReady()) {
            PL.i("billingClient.endConnection");
            billingClient.endConnection();
            billingClient = null;
            isBillingInit = false;
        }
    }

    /**
     * 功能回调
     */
    public interface BillingHelperStatusCallback {

        void onQuerySkuResult(List<ProductDetails> productDetailsList, String productId);
        /**
         * 查询购买的回调
         */
        void queryPurchaseResult(List<Purchase> purchases);

        void queryPurchaseResultInPaying(List<Purchase> purchases);

        /**
         * 支付回调
         */
        void onPurchaseUpdate(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases);

        void handleError(int m, String msg);

        void handleCancel(String msg);
    }

    public interface BillingSetupCallback{
        void onSuccess();
    }
}

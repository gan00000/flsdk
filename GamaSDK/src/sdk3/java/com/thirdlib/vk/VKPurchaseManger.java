package com.thirdlib.vk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mw.sdk.R;

import java.util.Arrays;
import java.util.List;

import ru.rustore.sdk.billingclient.RuStoreBillingClient;
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory;
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult;
import ru.rustore.sdk.billingclient.model.purchase.Purchase;
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState;
import ru.rustore.sdk.billingclient.usecase.ProductsUseCase;
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase;
import ru.rustore.sdk.billingclient.utils.BillingRuStoreExceptionExtKt;
import ru.rustore.sdk.billingclient.utils.pub.RuStoreBillingClientExtKt;
import ru.rustore.sdk.core.exception.RuStoreException;
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult;

public class VKPurchaseManger {

    private static VKPurchaseManger vkPurchaseManger;
    private static RuStoreBillingClient ruStoreBillingClient;
    private boolean isPurchasesAvailability;
    private Context mContext;
    public PurchaseCallback purchaseCallback;

    public static VKPurchaseManger getInstance(){
        if (vkPurchaseManger == null){
            vkPurchaseManger = new VKPurchaseManger();
        }
        return vkPurchaseManger;
    }

    private static RuStoreBillingClient getBillingClient(Context context) {

        if (ruStoreBillingClient != null){
            return ruStoreBillingClient;
        }
        ruStoreBillingClient = RuStoreBillingClientFactory.INSTANCE.create(
                context,
                context.getString(R.string.mw_vk_appid),
                context.getString(R.string.sdk_game_code),
                null,
                null,
                true
        );
        return ruStoreBillingClient;
    }

    public void initBillingClient(Context context){
        checkPurchaseAvailiability(context);
    }

    public void onNewIntent(Context context,Intent intent) {
        getBillingClient(context).onNewIntent(intent);
    }

    public void checkPurchaseAvailiability(Context context) {
        RuStoreBillingClientExtKt.checkPurchasesAvailability(RuStoreBillingClient.Companion, context)
                .addOnSuccessListener(result -> {
                    if (result instanceof FeatureAvailabilityResult.Available) {
                        Log.w("RuStoreBillingClient", "Success calling checkPurchaseAvailiability - Available: " + result);
                        isPurchasesAvailability = true;
                    } else {
                        isPurchasesAvailability = false;
                        RuStoreException exception = ((FeatureAvailabilityResult.Unavailable) result).getCause();
                        BillingRuStoreExceptionExtKt.resolveForBilling(exception, context);
                        Log.w("RuStoreBillingClient", "Success calling checkPurchaseAvailiability - Unavailable: " + exception);
                    }
                }).addOnFailureListener(error -> {
                    isPurchasesAvailability = false;
                    Log.e("RuStoreBillingClient", "Error calling checkPurchaseAvailiability: " + error);
                });
    }

    public void getProducts(Context context) {
        this.mContext = context;
        ProductsUseCase productsUseCase = getBillingClient(context).getProducts();

        productsUseCase.getProducts(
                Arrays.asList(
                        "productId1",
                        "productId2",
                        "productId3"
                )).addOnSuccessListener(products -> {

        }).addOnFailureListener(throwable -> Log.e("RuStoreBillingClient", "Error calling getProducts cause: " + throwable));
    }

    public void queryPurchasesAsync(Context context) {

        this.mContext = context;
        PurchasesUseCase purchasesUseCase = getBillingClient(context).getPurchases();

        purchasesUseCase.getPurchases().addOnSuccessListener(purchases -> {

            if (purchaseCallback != null){
                purchaseCallback.onQueryPurchaseSucceed(purchases);
            }
        }).addOnFailureListener(throwable ->
                {
                    Log.e("RuStoreBillingClient", "Error calling getPurchases cause: " + throwable);
                    if (purchaseCallback != null){
                        purchaseCallback.onError(throwable.getMessage());
                    }
                }


        );
    }

    public void queryPurchasesAsyncInPaying(Context context) {

        this.mContext = context;
        PurchasesUseCase purchasesUseCase = getBillingClient(context).getPurchases();

        purchasesUseCase.getPurchases().addOnSuccessListener(purchases -> {

            if (purchaseCallback != null){
                purchaseCallback.onPayingQueryPurchaseSucceed(purchases);
            }
            /*for (Purchase purchase : purchases) {

                String purchaseId = purchase.getPurchaseId();
                if (purchaseId != null) {
                    assert purchase.getDeveloperPayload() != null;
                    Log.w("HOHOHO", purchase.getDeveloperPayload());
                    if (purchase.getPurchaseState() != null) {
                        if (purchase.getPurchaseState() == PurchaseState.CREATED ||
                                purchase.getPurchaseState() == PurchaseState.INVOICE_CREATED) {
                            deletePurchase(context, purchaseId);
                        } else if (purchase.getPurchaseState() == PurchaseState.PAID) {
                            confirmPurchase(context, purchaseId);
                        }
                    } else {
                        Log.e("HOHOHO", "PurchaseState is null");
                    }

                }

            }*/
//            purchases.forEach(purchase -> {
//            });
        }).addOnFailureListener(throwable ->
                {
                    Log.e("RuStoreBillingClient", "Error calling getPurchases cause: " + throwable);
                    if (purchaseCallback != null){
                        purchaseCallback.onError(throwable.getMessage());
                    }
                }


        );
    }

    public void purchaseProduct(Context context,String productId, String orderId, String developerPayload) {

        this.mContext = context;

        PurchasesUseCase purchasesUseCase = getBillingClient(context).getPurchases();

//        String developerPayload = "your_developer_payload";

        purchasesUseCase.purchaseProduct(productId, orderId, 1, developerPayload)
                .addOnSuccessListener(this::handlePaymentResult)
                .addOnFailureListener(throwable ->
                        {
                            Log.e("RuStoreBillingClient", "Error calling purchaseProduct cause: " + throwable);
                            if (purchaseCallback != null){
                                purchaseCallback.onError(throwable.getMessage());
                            }
                        }
                );
    }


    private void handlePaymentResult(PaymentResult paymentResult) {
        if (paymentResult instanceof PaymentResult.Success) {
            //confirmPurchase(this.mContext, ((PaymentResult.Success) paymentResult).getPurchaseId());
            if (purchaseCallback != null){
                purchaseCallback.onPurchaseSucceed(((PaymentResult.Success) paymentResult));
            }
        } else if (paymentResult instanceof PaymentResult.Failure) {
            deletePurchase(this.mContext, ((PaymentResult.Failure) paymentResult).getPurchaseId());
            if (purchaseCallback != null){
                purchaseCallback.onPurchaseFailed(null);
            }
        }
    }

    public void confirmPurchase(Context context, String purchaseId) {
        PurchasesUseCase purchasesUseCase = getBillingClient(context).getPurchases();

        purchasesUseCase.confirmPurchase(purchaseId)
                .addOnSuccessListener(unit -> {
                    //Toast.makeText(context, "Purchase confirmed", Toast.LENGTH_LONG).show();
                    if (purchaseCallback != null){
                        purchaseCallback.onConsumeFinished();
                    }
                }).addOnFailureListener(throwable -> {
                    Log.e("RuStoreBillingClient", "Error calling confirmPurchase cause: " + throwable);
                });
    }


    public void deletePurchase(Context context, String purchaseId) {
        PurchasesUseCase purchasesUseCase = getBillingClient(context).getPurchases();
        //To cancel a purchase, use the deletePurchase method.
        purchasesUseCase.deletePurchase(purchaseId)
                .addOnSuccessListener(unit -> {

                })
                .addOnFailureListener(throwable -> {
                    Log.e("RuStoreBillingClient", "Error calling deletePurchase cause: " + throwable);
                });
    }

    interface PurchaseCallback {
        /**
         * [PurchaseManager.startConnection]를 통해 InApp Purchase SDK와 원스토어 간 연결이 완료될 경우 호출됩니다.
         */
        void onPurchaseClientSetupFinished();

        /**
         * [PurchaseManager.consumeAsync] 가 성공했을 경우 호출됩니다.
         */
        void onConsumeFinished();

        void onPurchaseSucceed(PaymentResult paymentResult);

        void onQueryPurchaseSucceed(List<Purchase> purchases);
        void onPayingQueryPurchaseSucceed(List<Purchase> purchases);
        void onPurchaseFailed(PaymentResult paymentResult);
        void onError(String message);
    }
}

package com.thirdlib.vk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.core.base.utils.PL;
import com.thirdlib.ThirdModuleUtil;

import java.util.List;

import ru.rustore.sdk.pay.PurchaseInteractor;
import ru.rustore.sdk.pay.RuStorePayClient;
import ru.rustore.sdk.pay.model.AppUserId;
import ru.rustore.sdk.pay.model.DeveloperPayload;
import ru.rustore.sdk.pay.model.OrderId;
import ru.rustore.sdk.pay.model.PreferredPurchaseType;
import ru.rustore.sdk.pay.model.ProductId;
import ru.rustore.sdk.pay.model.ProductPurchaseParams;
import ru.rustore.sdk.pay.model.ProductPurchaseResult;
import ru.rustore.sdk.pay.model.ProductType;
import ru.rustore.sdk.pay.model.Purchase;
import ru.rustore.sdk.pay.model.PurchaseId;
import ru.rustore.sdk.pay.model.PurchaseStatus;
import ru.rustore.sdk.pay.model.Quantity;
import ru.rustore.sdk.pay.model.RuStorePaymentException;

public class VKPurchaseManger {

    private static VKPurchaseManger vkPurchaseManger;
    private static RuStorePayClient ruStoreBillingClient;
    private boolean isPurchasesAvailability;
    private Context mContext;
    public PurchaseCallback purchaseCallback;

    public static VKPurchaseManger getInstance(){
        if (vkPurchaseManger == null){
            vkPurchaseManger = new VKPurchaseManger();
        }
        return vkPurchaseManger;
    }

//    final static ExternalPaymentLoggerFactory externalPaymentLoggerFactory = new ExternalPaymentLoggerFactory() {
//        @Override
//        public ExternalPaymentLogger create(String tag) {
//            return new PaymentLogger(tag);
//        }
//    };
    final static boolean debugLogs = true;

    public static RuStorePayClient getBillingClient(Context context) {

        if (ruStoreBillingClient != null){
            return ruStoreBillingClient;
        }
//
//        ruStoreBillingClient = RuStoreBillingClientFactory.INSTANCE.create(
//                context,
//                context.getString(R.string.mw_vk_appid),
//                context.getString(R.string.sdk_game_code),
//                null,
//                externalPaymentLoggerFactory,
//                debugLogs
//        );
//        return ruStoreBillingClient;
        ruStoreBillingClient = RuStorePayClient.Companion.getInstance();
        return ruStoreBillingClient;
    }

    public void onCreate(Activity activity, Bundle savedInstanceState){
        if (savedInstanceState == null) {
            if (!ThirdModuleUtil.existRustoreModule()){
                return;
            }
//            getBillingClient(activity).onNewIntent(activity.getIntent());
            getBillingClient(activity).getIntentInteractor().proceedIntent(activity.getIntent());
        }
    }

    public void onNewIntent(Activity activity, Intent intent) {
        if (!ThirdModuleUtil.existRustoreModule()){
            return;
        }
        //        getBillingClient(activity).onNewIntent(intent);
        getBillingClient(activity).getIntentInteractor().proceedIntent(intent);

    }

    public void initBillingClient(Context context){
        checkPurchaseAvailiability(context);
    }

    public void checkPurchaseAvailiability(Context context) {
        /*RuStoreBillingClientExtKt.checkPurchasesAvailability(RuStoreBillingClient.Companion)
                .addOnSuccessListener(result -> {
                    if (result instanceof PurchaseAvailabilityResult.Available) {
                        Log.w("RuStoreBillingClient", "Success calling checkPurchaseAvailiability - Available: " + result);
                        isPurchasesAvailability = true;
                    } else {
                        isPurchasesAvailability = false;
                        RuStoreException exception = ((PurchaseAvailabilityResult.Unavailable) result).getCause();
                        BillingRuStoreExceptionExtKt.resolveForBilling(exception, context);
                        Log.w("RuStoreBillingClient", "Success calling checkPurchaseAvailiability - Unavailable: " + exception);
                    }
                }).addOnFailureListener(error -> {
                    isPurchasesAvailability = false;
                    Log.e("RuStoreBillingClient", "Error calling checkPurchaseAvailiability: " + error);
                });*/
    }

   /* public void getProducts(Context context) {
        this.mContext = context;
        ProductInteractor productInteractor = getBillingClient(context).getProductInteractor();

        productInteractor.getProducts(
                Arrays.asList(
                        "productId1",
                        "productId2",
                        "productId3"
                )).addOnSuccessListener(products -> {

        }).addOnFailureListener(throwable -> Log.e("RuStoreBillingClient", "Error calling getProducts cause: " + throwable));
    }*/

   /* public void queryPurchasesAsync(Context context) {

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
    }*/

    public void queryPurchasesAsyncInPaying(Context context) {

        this.mContext = context;
        if (purchaseCallback != null){
            purchaseCallback.onPayingQueryPurchaseSucceed(null);
        }
        /*PurchaseInteractor purchaseInteractor = getBillingClient(context).getPurchaseInteractor();

        purchaseInteractor.getPurchases(ProductType.CONSUMABLE_PRODUCT, PurchaseStatus.PAID).addOnSuccessListener(purchases -> {

            PL.d("calling getPurchases success");
            if (purchaseCallback != null){
                purchaseCallback.onPayingQueryPurchaseSucceed(purchases);
            }
        }).addOnFailureListener(throwable ->
                {
                    Log.e("RuStoreBillingClient", "Error calling getPurchases cause: " + throwable);
                    if (purchaseCallback != null){
                        purchaseCallback.onError(throwable.getMessage());
                    }
                }


        );*/
    }

    public void purchaseProduct(Context context,String productId, String orderId, String developerPayload, String userId) {

        this.mContext = context;

        PurchaseInteractor purchaseInteractor = getBillingClient(context).getPurchaseInteractor();

        ProductPurchaseParams params = new ProductPurchaseParams(new ProductId(productId), new Quantity(1), new OrderId(orderId), new DeveloperPayload(developerPayload), new AppUserId(userId), null);
//        purchaseInteractor.purchaseTwoStep(params)
        purchaseInteractor.purchase(params, PreferredPurchaseType.ONE_STEP)
                .addOnSuccessListener(result -> {
                    // Successful purchase result
                    PL.e("Successful purchase result");
                    handlePaymentResult(result);
                })
                .addOnFailureListener(throwable -> {
                    if (throwable instanceof RuStorePaymentException.ProductPurchaseException) {
                        // Handle product purchase error
                        PL.e("Handle product purchase error");
                    } else if (throwable instanceof RuStorePaymentException.ProductPurchaseCancelled) {
                        // Handle product purchase cancellation
                        PL.e("Handle product purchase cancellation");
                        if (purchaseCallback != null){
                            purchaseCallback.onCancel(throwable.getMessage());
                        }
                    } else {
                        // Handle other error
                        PL.e("Handle other error:" + throwable.getMessage());
                    }

                    if (throwable instanceof RuStorePaymentException.ProductPurchaseCancelled) {
                        if (purchaseCallback != null){
                            purchaseCallback.onCancel(throwable.getMessage());
                        }
                    }else {
                        if (purchaseCallback != null){
                            purchaseCallback.onError(throwable.getMessage());
                        }
                    }

                });

//        String developerPayload = "your_developer_payload";

//        purchasesUseCase.purchaseProduct(productId, orderId, 1, developerPayload)
//                .addOnSuccessListener(this::handlePaymentResult)
//                .addOnFailureListener(throwable ->
//                        {
//                            Log.e("RuStoreBillingClient", "Error calling purchaseProduct cause: " + throwable);
//                            if (purchaseCallback != null){
//                                purchaseCallback.onError(throwable.getMessage());
//                            }
//                        }
//                );
    }


    private void handlePaymentResult(ProductPurchaseResult paymentResult) {
        if (purchaseCallback != null){
            purchaseCallback.onPurchaseSucceed(paymentResult);
        }
        /*if (paymentResult instanceof PaymentResult.Success) {
            //confirmPurchase(this.mContext, ((PaymentResult.Success) paymentResult).getPurchaseId());
            if (purchaseCallback != null){
                purchaseCallback.onPurchaseSucceed(((PaymentResult.Success) paymentResult));
            }
        } else if (paymentResult instanceof PaymentResult.Failure) {
            deletePurchase(this.mContext, ((PaymentResult.Failure) paymentResult).getPurchaseId());
            if (purchaseCallback != null){
                purchaseCallback.onPurchaseFailed(null);
            }
        }*/
    }


//    Only purchases initiated using the two-stage payment scenario (i.e., with funds being held) require confirmation.
//    Such purchases, after successful holding, will have the status PurchaseStatus.PAID.
//    To debit funds from the buyer's card, the purchase must be confirmed. For this, you should use the confirmTwoStepPurchase method.
    public void confirmPurchase(Context context, String purchaseId) {
     /*   PurchasesUseCase purchasesUseCase = getBillingClient(context).getPurchases();

        purchasesUseCase.confirmPurchase(purchaseId)
                .addOnSuccessListener(unit -> {
                    //Toast.makeText(context, "Purchase confirmed", Toast.LENGTH_LONG).show();
                    if (purchaseCallback != null){
                        purchaseCallback.onConsumeFinished();
                    }
                }).addOnFailureListener(throwable -> {
                    Log.e("RuStoreBillingClient", "Error calling confirmPurchase cause: " + throwable);
                });*/

       /* PurchaseInteractor purchaseInteractor = getBillingClient(context).getPurchaseInteractor();

        purchaseInteractor.confirmTwoStepPurchase(new PurchaseId(purchaseId), null)
                .addOnSuccessListener(success -> {
                    // Process success
                    PL.d("confirmTwoStepPurchase success");
                    if (purchaseCallback != null) {
                        purchaseCallback.onConsumeFinished();
                    }
                })
                .addOnFailureListener(throwable -> {
                    // Process error
                    PL.e("confirmTwoStepPurchase failure");
                });*/
    }


   /* public void deletePurchase(Context context, String purchaseId) {
        *//*
        PurchasesUseCase purchasesUseCase = getBillingClient(context).getPurchases();
        //To cancel a purchase, use the deletePurchase method.
        purchasesUseCase.deletePurchase(purchaseId)
                .addOnSuccessListener(unit -> {

                })
                .addOnFailureListener(throwable -> {
                    Log.e("RuStoreBillingClient", "Error calling deletePurchase cause: " + throwable);
                });
        *//*
    }*/

/*    public void CheckingUserStatus(Context context){

        UserInteractor userInteractor = getBillingClient(context).getUserInteractor();
        userInteractor.getUserAuthorizationStatus()
                .addOnSuccessListener(status -> {
                    switch (status) {
                        case AUTHORIZED:
                            // Logic when the user is authorized in RuStore
                            break;
                        case UNAUTHORIZED:
                            // Logic when the user is NOT authorized in RuStore
                            break;
                    }
                })
                .addOnFailureListener(throwable -> {
                    // Error handling
                });
    }*/



    interface PurchaseCallback {
        /**
         * [PurchaseManager.startConnection]를 통해 InApp Purchase SDK와 원스토어 간 연결이 완료될 경우 호출됩니다.
         */
        void onPurchaseClientSetupFinished();

        /**
         * [PurchaseManager.consumeAsync] 가 성공했을 경우 호출됩니다.
         */
        void onConsumeFinished();

        void onPurchaseSucceed(ProductPurchaseResult paymentResult);

        void onQueryPurchaseSucceed(List<Purchase> purchases);
        void onPayingQueryPurchaseSucceed(List<Purchase> purchases);
        void onError(String message);
        void onCancel(String message);
    }
}

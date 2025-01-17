package com.mw.sdk.pay.nowgg.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gg.now.billingclient.api.AcknowledgePurchaseParams;
import gg.now.billingclient.api.AcknowledgePurchaseResponseListener;
import gg.now.billingclient.api.BillingClient;
import gg.now.billingclient.api.BillingClient.BillingResponse;
import gg.now.billingclient.api.BillingClient.FeatureType;
import gg.now.billingclient.api.BillingClient.SkuType;
import gg.now.billingclient.api.BillingClientStateListener;
import gg.now.billingclient.api.BillingFlowParams;
import gg.now.billingclient.api.BillingResult;
import gg.now.billingclient.api.Constants;
import gg.now.billingclient.api.ConsumeResponseListener;
import gg.now.billingclient.api.ProductDetails;
import gg.now.billingclient.api.ProductDetailsResponseListener;
import gg.now.billingclient.api.Purchase;
import gg.now.billingclient.api.Purchase.PurchasesResult;
import gg.now.billingclient.api.PurchasesResponseListener;
import gg.now.billingclient.api.PurchasesUpdatedListener;
import gg.now.billingclient.api.QueryProductDetailsParams;
import gg.now.billingclient.api.QueryPurchasesParams;
import gg.now.billingclient.api.SkuDetails;
import gg.now.billingclient.api.SkuDetailsParams;
import gg.now.billingclient.api.SkuDetailsResponseListener;
import gg.now.billingclient.util.BillingHelper;

/**
 * Handles all the interactions with billing service (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
public class BillingManager implements PurchasesUpdatedListener {
    // Default value of mBillingClientResponseCode until BillingManager was not yet initialized
    public static final int BILLING_MANAGER_NOT_INITIALIZED = -1;

    private static final String TAG = "NowggPay";

    //TODO Replace with your app's id.
    private static final String APP_ID = "5618";

    //TODO Replace with your inGameId.

    /*TODO BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    private static final String BASE_64_ENCODED_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAygWG1GhaNALZ9K6O+a6QKVEuQ9EGvUz2A6Ar2B0zBGe8hHwvdrr507SUpVkxF00NfZQxjljCkOFbOA/hvM+rZGkdZem2uG0O0x2q6d3ddGbwXjKSJrZumxsrtXtH4H8cwHGnVJbK5Jj1tEDijSCU3RF/Bl493PvaA479Suv4JRpaRSCUPRn1OzxXmtJaGyj4kFT51sYfwJ2ar0O4j98Be51wu7qcf/941CaWAyYJ6+heCahOEr4+85/njbLm35R+tz1aRpKIjerSw3fBX1OkfnCaruFPVtf1llsDlt2HE+ExcG0+lNGfDuw1yP2bVNODNl1mPKXloXPlsIOR28e2zwIDAQAB";

    private final BillingUpdatesListener mBillingUpdatesListener;
    private final Activity mActivity;
    /**
     * A reference to BillingClient
     **/
    private BillingClient mBillingClient;
    /**
     * True if billing service is connected now.
     */
    private boolean mIsServiceConnected;
    private Set<String> mTokensToBeConsumed;

    private Set<String> mTokensToBeAcknowledged;

    private int mBillingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED;

    public BillingManager(Activity activity, final BillingUpdatesListener updatesListener) {
        Log.d(TAG, "Creating Billing client.");
        mActivity = activity;
        mBillingUpdatesListener = updatesListener;
        mBillingClient = BillingClient.newBuilder(mActivity)
                .setAppId(APP_ID)
                .setListener(this).build();

        Log.d(TAG, "Starting setup.");

        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startServiceConnection(new Runnable() {
            @Override
            public void run() {
                // Notifying the listener that billing client is ready
                mBillingUpdatesListener.onBillingClientSetupFinished();
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                if (mBillingClientResponseCode == BillingClient.BillingResponse.OK) {
                    Log.d(TAG, "Setup successful. Querying inventory.");
//                    queryPurchasesAsync();
                }
            }
        });
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    @Override
    public void onPurchasesUpdated(int resultCode, List<Purchase> purchases) {
        if (resultCode == BillingResponse.OK) {
//            if (purchases == null) {
//                return;
//            }
//            for (Purchase purchase : purchases) {
//                handlePurchase(purchase);
//            }

            mBillingUpdatesListener.onPurchasesUpdated(purchases);

        } else {
            Log.i(TAG, "onPurchasesUpdated() - result not ok - " + Utils.getResponseString(resultCode));
//            Toast.makeText(mActivity, "Error: " + Utils.getResponseString(resultCode), Toast.LENGTH_SHORT).show();
            if (mBillingUpdatesListener != null){
                mBillingUpdatesListener.onPurchaseFailed("Error: " + Utils.getResponseString(resultCode));
            }
        }
    }

    /**
     * Start a purchase flow
     */
    public void initiatePurchaseFlow(final String skuId,
                                     final @SkuType String billingType,
                                     final String developerPayload

    ) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                        .setSku(skuId).setType(billingType).setDeveloperPayload(developerPayload).build();
                @BillingResponse int response = mBillingClient.launchBillingFlow(mActivity, purchaseParams);
                if (response != BillingResponse.OK) {
                    //Toast.makeText(mActivity, "Error: " + Utils.getResponseString(response), Toast.LENGTH_SHORT).show();
                    if (mBillingUpdatesListener != null){
                        mBillingUpdatesListener.onPurchaseFailed("Error: " + Utils.getResponseString(response));
                    }
                }
            }
        };

        executeServiceRequest(purchaseFlowRequest, false);
    }

    public Context getContext() {
        return mActivity;
    }

    /**
     * Clear the resources
     */
    public void destroy() {
        Log.d(TAG, "Destroying the manager.");

        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
    }

    public void querySkuDetailsAsync(final List<String> skuList,
                                     final SkuDetailsResponseListener listener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable queryRequest = new Runnable() {
            @Override
            public void run() {
                // Query the purchase async
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList);
                mBillingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode,
                                                             List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                            }
                        });
            }
        };

        executeServiceRequest(queryRequest, true);
    }

    public void consumeAsync(final String purchaseToken, ConsumeResponseListener consumeResponseListener) {
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
//        if (mTokensToBeConsumed == null) {
//            mTokensToBeConsumed = new HashSet<>();
//        } else if (mTokensToBeConsumed.contains(purchaseToken)) {
//            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
//            return;
//        }
//        mTokensToBeConsumed.add(purchaseToken);

        // Generating Consume Response listener
//        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
//            @Override
//            public void onConsumeResponse(@BillingResponse int responseCode, String purchaseToken) {
//                // If billing service was disconnected, we try to reconnect 1 time
//                // (feel free to introduce your retry policy here).
//                mBillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode, isPaying);
//            }
//        };

        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable consumeRequest = new Runnable() {
            @Override
            public void run() {
                // Consume the purchase async
                mBillingClient.consumeAsync(purchaseToken, consumeResponseListener);
            }
        };

        executeServiceRequest(consumeRequest, true);
    }

    public void acknowledgeAsync(String purchaseToken) {
        // If we've already scheduled to acknowledge this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        if (mTokensToBeAcknowledged == null) {
            mTokensToBeAcknowledged = new HashSet<>();
        } else if (mTokensToBeAcknowledged.contains(purchaseToken)) {
            Log.i(TAG, "Token was already scheduled to be acknowdledged - skipping...");
            return;
        }
        mTokensToBeAcknowledged.add(purchaseToken);

        // Generating Acknowledge Purchase Response listener
        final AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@BillingResponse int responseCode) {
                mBillingUpdatesListener.onAcknowledgeFinished(responseCode);
            }
        };

// Creating a runnable from the request to use it inside our connection retry policy below
        Runnable acknowledgeRequest = new Runnable() {
            @Override
            public void run() {
                // Acknowledge the purchase async

                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build();
                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        };

        executeServiceRequest(acknowledgeRequest, true);
    }


    /**
     * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
     * client connection response was not received yet.
     */
    public int getBillingClientResponseCode() {
        return mBillingClientResponseCode;
    }

    /**
     * Handles the purchase
     * <p>Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * </p>
     *
     * @param purchase Purchase to be handled
     */
//    private void handlePurchase(Purchase purchase) {
//        if (!mBillingClient.getStoreType().equals(Constants.XIAOMI) && !mBillingClient.getStoreType().equals(Constants.SAMSUNG) &&
//                !verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
//            Log.i(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
//            return;
//        }
//
//        Log.d(TAG, "Got a verified purchase: " + purchase);
//
//        List<Purchase> mPurchases = new ArrayList<>();
//        mPurchases.add(purchase);
//        mBillingUpdatesListener.onPurchasesUpdated(mPurchases);
//    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private void onQueryPurchasesFinished(PurchasesResult result) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.getResponseCode() != BillingResponse.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.getResponseCode()
                    + ") was bad - quitting");
            return;
        }

        Log.d(TAG, "Query inventory was successful.");

        // Update the UI and purchases inventory with new list of purchases
        onPurchasesUpdated(BillingResponse.OK, result.getPurchasesList());
    }

    /**
     * Checks if subscriptions are supported for current client
     * <p>Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     * </p>
     */
    public boolean areSubscriptionsSupported() {
        int responseCode = mBillingClient.isFeatureSupported(FeatureType.SUBSCRIPTIONS);
        if (responseCode != BillingResponse.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: " + responseCode);
        }
        return responseCode == BillingResponse.OK;
    }

    public void queryPurchasesAsync(boolean isPaying) {
        if (mBillingClient.isReady() == false) {
            Log.w(TAG, "queryPurchasesAsync() - client is not ready to query purchases.");
            if (mBillingUpdatesListener != null){
                mBillingUpdatesListener.onPurchaseFailed("queryPurchasesAsync() - client is not ready to query purchases.");
            }
            return;
        }
        else {
            Log.d(TAG, "queryPurchasesAsync() - client is ready to query purchases.");
        }

        QueryPurchasesParams params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build();
        mBillingClient.queryPurchasesAsync(params, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> list) {
//                if (billingResult.getResponseCode() == BillingResponse.OK) {
//                    //onPurchasesUpdated(billingResult.getResponseCode(), list);
//                } else {
//                    BillingHelper.logWarn(TAG, "queryPurchasesAsync() got an error response code: " + billingResult.getResponseCode() + ", error :" + Utils.getResponseString(billingResult.getResponseCode()));
//                }
                if (mBillingUpdatesListener != null){
                    if (isPaying){
                        mBillingUpdatesListener.onPayingQueryPurchaseSucceed(billingResult, list);
                    }else {
                        mBillingUpdatesListener.onQueryPurchaseSucceed(billingResult, list);
                    }
                }
            }
        });
    }

    public void queryProductDetailsAsync(final List<QueryProductDetailsParams.Product> productList,
                                         final ProductDetailsResponseListener listener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable queryRequest = new Runnable() {
            @Override
            public void run() {
                // Query the purchase async
                QueryProductDetailsParams.Builder params = QueryProductDetailsParams.newBuilder();
                params.setProductList(productList);
                mBillingClient.queryProductDetailsAsync(params.build(), new ProductDetailsResponseListener() {
                    @Override
                    public void onProductDetailsResponse(int billingResult, List<ProductDetails> productDetailsList) {
                        listener.onProductDetailsResponse(billingResult, productDetailsList);
                    }
                });
            }
        };

        executeServiceRequest(queryRequest, true);
    }

    public void startServiceConnection(final Runnable executeOnSuccess) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
                Log.d(TAG, "Setup finished. Response code: " + billingResponseCode);

                mBillingClientResponseCode = billingResponseCode;

                if (billingResponseCode == BillingResponse.OK) {
                    mIsServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
            }
        });
    }

    private void executeServiceRequest(Runnable runnable, Boolean isUiThread) {
        if (mIsServiceConnected) {
            if (isUiThread) {
                runnable.run();
            } else {
                new Thread(runnable).start();
            }
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable);
        }
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature, mBillingClient.getStoreType());
        } catch (IOException e) {
            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();

        //void onConsumeFinished(String token, @BillingResponse int result, boolean isPaying);

        void onPurchasesUpdated(List<Purchase> purchases);

        void onAcknowledgeFinished(@BillingClient.BillingResponse int result);

        void onPayingQueryPurchaseSucceed(BillingResult billingResult, List<Purchase>  purchases);

        void onQueryPurchaseSucceed(BillingResult billingResult, List<Purchase>  purchases);

        void onPurchaseFailed(String errorMsg);
    }
}

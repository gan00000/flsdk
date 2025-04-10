package com.thirdlib.xiaomi;

import android.app.Activity;
import android.util.Log;

import com.core.base.utils.PL;
import com.xiaomi.billingclient.api.BillingClient;
import com.xiaomi.billingclient.api.BillingClientStateListener;
import com.xiaomi.billingclient.api.BillingFlowParams;
import com.xiaomi.billingclient.api.BillingResult;
import com.xiaomi.billingclient.api.Purchase;
import com.xiaomi.billingclient.api.PurchasesUpdatedListener;
import com.xiaomi.billingclient.api.SkuDetailsParams;
import com.xiaomi.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;


public class XiaoMiPayManager {
    private static XiaoMiPayManager mXiaoMiPayManager;
    private BillingClient billingClient;

    private PurchaseCallback purchaseCallback;

    public void setPurchaseCallback(PurchaseCallback purchaseCallback) {
        this.purchaseCallback = purchaseCallback;
    }

    private boolean isSetupSuccess;

    public static XiaoMiPayManager getInstance(){
        if (mXiaoMiPayManager == null){
            mXiaoMiPayManager = new XiaoMiPayManager();
        }
        return mXiaoMiPayManager;
    }

    private void startConnectService(Activity activity, StartConnectionCallback connectionCallback){

            if (billingClient != null && isSetupSuccess && billingClient.isReady()){
                if (connectionCallback != null){
                    connectionCallback.success();
                }
                return;
            }

            if (billingClient == null){
                billingClient = BillingClient.newBuilder(activity).setListener(purchasesUpdatedListener).build();
                billingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingServiceDisconnected() {
                        PL.d("onBillingServiceDisconnected");
                        isSetupSuccess = false;
                        if (purchaseCallback != null){
                            purchaseCallback.onError(ReqType.Setup, "onBillingServiceDisconnected");
                        }
                    }

                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        int code = billingResult.getResponseCode();
                        PL.d("onBillingSetupFinished getResponseCode : " + code);
                        if (code == BillingClient.BillingResponseCode.OK) {
                            PL.d( "onBillingSetupFinished is ok =>" + Thread.currentThread().getName());
                            isSetupSuccess = true;
                            if (connectionCallback != null){
                                connectionCallback.success();
                            }
                        }else {
                            String msg = billingResult.getDebugMessage();
                            PL.d( "onBillingSetupFinished is not ok =>" + msg);
                            isSetupSuccess = false;
                            if (purchaseCallback != null){
                                purchaseCallback.onError(ReqType.Setup, msg);
                            }
                        }
                    }
                });
            }
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
            /*
             * 不可在此回调做耗时操作，避免出现ANR异常
             */
            int code = billingResult.getResponseCode();
            Log.d("TAG", "currentThread.name = " + Thread.currentThread().getName());
            Log.d("TAG", "onPurchasesUpdated.code = " + code);
            if (code == BillingClient.BillingResponseCode.PAYMENT_SHOW_DIALOG) {//可根据自己业务需要处理该状态，不可当做交易失败处理
                PL.d("XiaoMiPayManager 收银台启动");
            } else if (code == BillingClient.BillingResponseCode.OK) {
                PL.d("XiaoMiPayManager支付成功");
                if (purchaseCallback != null){
                    purchaseCallback.onPurchaseSucceed(list);
                }
            } else if (code == BillingClient.BillingResponseCode.USER_CANCELED) {
                PL.d("XiaoMiPayManager支付取消");
            } else {//其他失败状态  比如断网、服务异常等
                PL.d("XiaoMiPayManager支付 error =>" + billingResult.getDebugMessage());
                if (purchaseCallback != null){
                    purchaseCallback.onPurchaseFailed(list);
                    purchaseCallback.onError(ReqType.PurchasesUpdated, billingResult.getDebugMessage());
                }
            }
        }
    };

    /**
     * 查询商品
     */
    public void querySkuDetails(String productId, SkuDetailsResponseListener skuDetailsResponseListener) {
        Log.d("TAG", "querySkuDetails => " + productId);
        List<String> skuList = new ArrayList<>();//添加开发者真实skuId
        skuList.add(productId);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
//        billingClient.querySkuDetailsAsync(params.build(),
//                (billingResult, list) -> {
//                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                        if (list != null && list.size() > 0) {
//                        }
//                    } else {
//                        PL.d(productId + "查询失败 =>" + billingResult.getDebugMessage());
//                    }
//                });

        billingClient.querySkuDetailsAsync(params.build(), skuDetailsResponseListener);
    }

    /**
     * 下单 发起购买
     *
     * @param productId
     * obfuscatedAccountId 需要传递该用户在开发者侧的唯一Id，用于支付风控，为必传字段，最长200个字符。
     * obfuscatedProfileId 为开发者扩展字段，可以自由选择传入何值，交易结果会回传，最长200个字符。例如可以是开发者订单号，便于双方订单对应。
     * webHookUrl为开发者指定的webhook url；可以动态设置该笔交易开发者服务端回调通知地址；如果不设置，我们就用您在开发者站上配置的webhook地址发送服务端通知。
     *
     */
    public void launchBillingFlow(Activity activity, String productId, String mObfuscatedAccountId, String mObfuscatedProfileId, String mWebHookUrl) {
        PL.d("launchBillingFlow productId => " + productId);
        startConnectService(activity, () -> querySkuDetails(productId, (billingResult, list) -> {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (list != null && list.size() > 0) {

                    BillingFlowParams params = BillingFlowParams.newBuilder()
                            .setSkuDetails(list.get(0))
                            .setObfuscatedAccountId(mObfuscatedAccountId)//填写开发者真实的ObfuscatedAccountId、ObfuscatedProfileId、WebHookUrl
                            .setObfuscatedProfileId(mObfuscatedProfileId)
                            //.setWebHookUrl(mWebHookUrl)
                            .build();
                    billingClient.launchBillingFlow(activity, params);

                }else {
                    PL.d("querySkuDetails list is 0");
                }

            } else {
                PL.d(productId + "查询失败 =>" + billingResult.getDebugMessage());
            }

        }));

    }

    /**
     * 确认非消耗性商品
     *
     * @param token
     */
    private void acknowledgePurchase(String token) {
//        billingClient.acknowledgePurchase(token, billingResult -> {
//            progressBar.setVisibility(View.GONE);
//            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                resultTv.setText("确认成功");
//            } else {
//                resultTv.setText(billingResult.getDebugMessage());
//            }
//        });
    }

    /**
     * 确认消耗性商品
     *
     * @param token purchaseToken
     */
    public void onConsumePurchase(Activity activity, String token) {
        startConnectService(activity, () -> {

            billingClient.consumeAsync(token, (billingResult, str) -> {
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    resultTv.setText("确认成功，您可再次发起购买");
//                } else {
//                    resultTv.setText(billingResult.getDebugMessage());
//                }

                if (purchaseCallback != null){
                    purchaseCallback.onConsumeFinished();
                }
            });

        });

    }

    /**
     * 查询已支付，但未消耗的订单
     */
    public void queryPurchasesAsync(Activity activity, boolean isPaying) {
        PL.d("queryPurchasesAsync");
        startConnectService(activity, () -> billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchaseCallback != null){
                    if (isPaying){
                        purchaseCallback.onPayingQueryPurchaseSucceed(list);
                    }else {
                        purchaseCallback.onQueryPurchaseSucceed(list);
                    }
                }
            } else {
                PL.d("queryPurchasesAsync error =>" + billingResult.getDebugMessage());
                if (purchaseCallback != null){
                    if (isPaying){
                        purchaseCallback.onPayingQueryPurchaseSucceed(null);
                    }else {
                        purchaseCallback.onQueryPurchaseSucceed(null);
                    }
                }
            }
        }));

    }


    interface PurchaseCallback {

        void onConsumeFinished();

        void onPurchaseSucceed(List<Purchase> list);

        void onQueryPurchaseSucceed(List<Purchase> purchases);
        void onPayingQueryPurchaseSucceed(List<Purchase> purchases);
        void onPurchaseFailed(List<Purchase> list);
        void onError(ReqType type, String message);
    }

    enum ReqType {

        Setup,
        PurchasesUpdated,
    }

    interface StartConnectionCallback {
        void success();
    }
}

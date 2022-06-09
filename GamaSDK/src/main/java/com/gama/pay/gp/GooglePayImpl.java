package com.gama.pay.gp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.pay.IPay;
import com.gama.pay.IPayCallBack;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.gp.bean.req.PayReqBean;
import com.gama.pay.gp.bean.res.GPCreateOrderIdRes;
import com.gama.pay.gp.bean.res.GPExchangeRes;
import com.gama.pay.gp.constants.GooglePayDomainSite;
import com.gama.pay.gp.task.LoadingDialog;
import com.gama.pay.gp.util.GBillingHelper;
import com.gama.pay.gp.util.PayHelper;
import com.mw.base.bean.BasePayBean;
import com.mw.base.constant.GamaCommonKey;
import com.mw.base.utils.GamaUtil;
import com.mw.sdk.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gan on 2017/2/23.
 */

public class GooglePayImpl implements IPay, GBillingHelper.BillingHelperStatusCallback {
    private static final String TAG = GooglePayImpl.class.getSimpleName();

    private GBillingHelper mBillingHelper;

    private LoadingDialog loadingDialog;

    /**
     * 当次的商品详情
     */
    private SkuDetails skuDetails = null;

    /**
     * 当次的创单响应
     */
    private GPCreateOrderIdRes createOrderBean = null;

    /**
     * 创单的请求参数Bean
     */
    private GooglePayCreateOrderIdReqBean createOrderIdReqBean;

    private Activity activity;

    private IPayCallBack iPayCallBack;
    /**
     * 防止连续快速点击储值出现未知异常
     */
    private boolean isPaying = false;

    /**
     * 待消费列表,只有 PurchaseState.PURCHASED 的商品才能请求消费
     */
    private ArrayList<com.android.billingclient.api.Purchase> waitConsumeList = new ArrayList<>();


    private void callbackSuccess(Purchase purchase) {
        if (mBillingHelper != null) { //关闭页面前先移除callback，否则游戏的onResume会先于 GooglePayActivity2 的onDestroy执行
            mBillingHelper.removeBillingHelperStatusCallback(this);
        }

        if (loadingDialog != null) {
            loadingDialog.dismissProgressDialog();
        }

        if (iPayCallBack != null) {
            BasePayBean payBean = new BasePayBean();
            if (purchase != null) {
                try {
                    payBean.setOrderId(purchase.getOrderId());
                    payBean.setPackageName(purchase.getPackageName());
//                    payBean.setProductId(purchase.getProducts().get(0));
//                    payBean.setmItemType(purchase.getItemType());
                    payBean.setOriginPurchaseData(purchase.getOriginalJson());
                    payBean.setPurchaseState(purchase.getPurchaseState());
                    payBean.setPurchaseTime(purchase.getPurchaseTime());
                    payBean.setSignature(purchase.getSignature());
                    payBean.setDeveloperPayload(purchase.getDeveloperPayload());
                    payBean.setmToken(purchase.getPurchaseToken());
                    double price = skuDetails.getPriceAmountMicros() / 1000000.00;
                    payBean.setPrice(price);
                    payBean.setCurrency(skuDetails.getPriceCurrencyCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Bundle b = new Bundle();
            b.putInt(PAY_STATUS, PAY_SUCCESS);
            b.putSerializable("GooglePayCreateOrderIdReqBean", createOrderIdReqBean);
            b.putSerializable(GamaCommonKey.PURCHASE_DATA, payBean);
            iPayCallBack.success(b);
        }
    }

    private void callbackFail(String message) {
        if (mBillingHelper != null) { //关闭页面前先移除callback，否则游戏的onResume会先于 GooglePayActivity2 的onDestroy执行
            mBillingHelper.removeBillingHelperStatusCallback(this);
        }
        if (loadingDialog != null) {
            loadingDialog.dismissProgressDialog();
        }
        final Bundle b = new Bundle();
        b.putInt(PAY_STATUS, PAY_FAIL);
        if (!TextUtils.isEmpty(message)) {//提示错误信息

            loadingDialog.alert(message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    if (iPayCallBack != null) {
                        iPayCallBack.fail(b);
                    }
                }
            });

        } else {//用户取消

            if (iPayCallBack != null) {
                iPayCallBack.fail(b);
            }
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

        this.createOrderIdReqBean = null;

        PL.i("the jar version:" + BuildConfig.JAR_VERSION);//打印版本号

        if (activity == null) {
            PL.w("activity is null");
            return;
        }
        this.activity = activity;
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

        //由GooglePayActivity2传入
        this.createOrderIdReqBean = (GooglePayCreateOrderIdReqBean) payReqBean;
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(GooglePayDomainSite.GOOGLE_ORDER_CREATE);

        //创建Loading窗
        loadingDialog = new LoadingDialog(activity);
        if (this.createOrderIdReqBean.isInitOk()) {
            //开始Google储值
            googlePaySetUp();
        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
        isPaying = false;
        PL.w("google set not paying");
    }

    @Override
    public void onCreate(Activity activity) {
        PL.i( "onCreate");
        if (mBillingHelper == null) {
            PL.i( "instance GBillingHelper.");
            mBillingHelper = GBillingHelper.getInstance();
        } else {
            PL.i( "GBillingHelper not null.");
        }
        mBillingHelper.setBillingHelperStatusCallback(this);

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
        if (loadingDialog != null) {
            loadingDialog.dismissProgressDialog();
        }
        if (mBillingHelper != null) {
            //在销毁前再确保回调被移除，否则游戏的singletask会把支付activity杀死，导致回调没有被移除，游戏onresume时的查单会走这里的回调，导致再次创单
            mBillingHelper.removeBillingHelperStatusCallback(this);
            mBillingHelper = null;
        }
    }


    /**
     * <p>Title: googlePaySetUp</p> <p>Description: 启动远程服务</p>
     */
    private void googlePaySetUp() {

        loadingDialog.showProgressDialog();

//        如需验证购买交易，请先检查购买交易的状态是否为 PURCHASED。如果购买交易的状态为 PENDING，则您应按照处理待处理的交易中的说明处理购买交易。
//        对于通过 onPurchasesUpdated() 或 queryPurchasesAsync() 接收的购买交易，您应在应用授予权利之前进一步验证购买交易以确保其合法性
//        一旦您验证了购买交易，您的应用就可以向用户授予权利了。授予权利后，您的应用必须确认购买交易。此确认会告知 Google Play 您已授予购买权。

//        ====注意：如果您在三天内未确认购买交易，则用户会自动收到退款，并且 Google Play 会撤消该购买交易。====

        //1.先查询=>在提供待售商品之前，检查用户是否尚未拥有该商品。如果用户的消耗型商品仍在他们的商品库中，他们必须先消耗掉该商品，然后才能再次购买。
        mBillingHelper.queryPurchasesAsync(activity, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<com.android.billingclient.api.Purchase> list) {
                PL.i("queryPurchase finish");
                for (com.android.billingclient.api.Purchase purchase : list) {//查询是否为PURCHASED未消费商品
                    if(purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED){
                        //2.发送到服务器
                        //3.消费
                    }
                }
                if (activity == null){
                    callbackFail("");
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //4.创建订单
                        PayApi.requestCreateOrder(activity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
                            @Override
                            public void success(GPCreateOrderIdRes createOrderIdRes, String msg1) {
                                PL.i("requestCreateOrder finish success");
                                //5.开始购买
                                mBillingHelper.launchPurchaseFlow(activity.getApplicationContext(), createOrderIdReqBean.getProductId(),createOrderIdReqBean.getUserId(),
                                        createOrderIdRes.getOrderId(), new PurchasesUpdatedListener() {
                                            @Override
                                            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<com.android.billingclient.api.Purchase> purchasesList) {

                                                PL.i("onPurchasesUpdated finish...");
                                                if (purchasesList == null){
                                                    return;
                                                }
                                                //支付回调
                                                for (Purchase purchase : purchasesList) {
                                                    PL.i("onPurchasesUpdated = " + purchase.getPurchaseState());
                                                    if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                                                        //先消费,防止下次购买不了和三天过期退款
                                                        mBillingHelper.consumePurchase(activity, purchase, false, new ConsumeResponseListener() {
                                                            @Override
                                                            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                                                PL.i("onConsumeResponse => " + s);
                                                            }
                                                        });

                                                        //发送到服务器,发币
                                                        PayApi.requestSendStone(activity, purchase, new SFCallBack<GPExchangeRes>() {
                                                            @Override
                                                            public void success(GPExchangeRes result, String msg) {
                                                                PL.i("requestSendStone success => " + msg);
                                                            }

                                                            @Override
                                                            public void fail(GPExchangeRes result, String msg) {
                                                                PL.i("requestSendStone fail => " + msg);
//                                                                ToastUtils.toast(activity, msg);
                                                                callbackFail(result.getMessage());
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
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
        });
    }



    @Override
    public void onStartUp(boolean isSuccess, String msg) {
//        if (!isSuccess) {
//            PL.i( "onStartUp fail.");
//            callbackFail(msg);
//        } else {
//            PL.i( "onStartUp success.");
//        }
    }

    @Override
    public void onQuerySkuResult(Context context, List<SkuDetails> productDetails) {
//        if (list != null && list.size() == 1) {
//            skuDetails = list.get(0);
//            mBillingHelper.launchPurchaseFlow(activity, createOrderBean.getOrderId(), skuDetails);
//        } else {
//            PL.i( "onQuerySkuResult not current query. ");
//        }
    }

    @Override
    public void queryPurchaseResult(Context context, List<com.android.billingclient.api.Purchase> purchases) {
//        if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) { //查单成功
//            PL.i( "queryPurchaseResult response ok. ");
//            List<com.android.billingclient.api.Purchase> purchasesList = purchasesResult.getPurchasesList();
//            if (purchasesList != null && purchasesList.size() > 0) { //有未消费
//                for (com.android.billingclient.api.Purchase purchase : purchasesList) {
////                    PL.i( "queryPurchaseResult response purc toString" + purchase.toString());
////                    PL.i( "queryPurchaseResult response purc getOriginalJson" + purchase.getOriginalJson());
////                    PL.i( "queryPurchaseResult response purc getSku" + purchase.getSku());
////                    PL.i( "queryPurchaseResult response purc getPurchaseState" + purchase.getPurchaseState());
////                    PL.i( "queryPurchaseResult response purc getDeveloperPayload" + purchase.getDeveloperPayload());
////                    PL.i( "queryPurchaseResult response purc getPurchaseToken" + purchase.getPurchaseToken());
////                    PL.i( "queryPurchaseResult response purc getSignature" + purchase.getSignature());
////                    PL.i( "queryPurchaseResult response purc getOrderId" + purchase.getOrderId());
////                    PL.i( "queryPurchaseResult response purc getPurchaseTime" + purchase.getPurchaseTime());
//                    if (purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED) {
//                        PL.i( "queryPurchaseResult response ok, status purchased and start to consume.");
//                        mBillingHelper.consumePurchase(context, purchase, true);
//                    } else {
//                        PL.i( "queryPurchaseResult response ok, status not ok.");
//                    }
//                }
//            } else {
//                PL.i( "queryPurchaseResult response ok but no purchase to consume. ");
//            }
//        }
//        //
//        startPurchase();
    }

    @Override
    public void onConsumeResult(Context context, BillingResult billingResult, @NonNull String purchaseToken, com.android.billingclient.api.Purchase purchase, boolean isReplaceConsume) {
        if (billingResult != null) {
            PL.i( "onConsumeResult response code : " + billingResult.getResponseCode());
            if (TextUtils.isEmpty(billingResult.getDebugMessage())) {
                PL.i( "onConsumeResult response msg empty. ");
            } else {
                PL.i( "onConsumeResult response msg : " + billingResult.getDebugMessage());
            }
        }
       /* if (waitConsumeList != null && !waitConsumeList.isEmpty()) { //有未消费记录并且当次已经尝试过消费，不管是否成功都先移除
            PL.i( "onConsumeResult remove purchase regardless of consume or not. ");
            waitConsumeList.remove(purchase);
        }
        if (billingResult != null && billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) { //消费失败
            PL.i( "consume result:" + billingResult.getDebugMessage());
            if (!isReplaceConsume) { //正常购买消费失败
                PL.i( "onConsumeResult send stone fail.");
                callbackFail("Consuming purchase failed, please contact customer service.");
            }
        } else { //消费成功，发币
            PL.i( "onConsumeResult ok");
            if (isReplaceConsume) { //走补发请求，不阻塞购买
                PL.i( "onConsumeResult replacement.");
                requestReplace(purchase);
            } else {
                PL.i( "onConsumeResult send stone.");
//                requestSendStone(purchase);
            }
        }*/

    }

    @Override
    public void onPurchaseUpdate(@NonNull BillingResult billingResult, @Nullable List<com.android.billingclient.api.Purchase> purchases) {
        if (billingResult != null) {
            PL.i( "onPurchaseUpdate response code : " + billingResult.getResponseCode());
            if (TextUtils.isEmpty(billingResult.getDebugMessage())) {
                PL.i( "onPurchaseUpdate response msg empty. ");
            } else {
                PL.i( "onPurchaseUpdate response msg : " + billingResult.getDebugMessage());
            }
        }
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) { //支付成功，进行消费
            PL.i( "onPurchaseUpdate pay success, start consume.");
            com.android.billingclient.api.Purchase currentPurchase = null;
            boolean isPending = false;
            for (Purchase purchase : purchases) {
                PL.i( "onPurchaseUpdate purchase.getSku()=>" + purchase.toString());
                if (purchase.getSkus() != null && !purchase.getSkus().isEmpty() && purchase.getSkus().get(0).equals(createOrderIdReqBean.getProductId())) { //本次购买的商品
                    PL.i( "onPurchaseUpdate current pay product.");
                    if (currentPurchase == null) { //未有符合的当次购买商品记录
                        PL.i( "onPurchaseUpdate have no product meet.");
                        if (purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED) {
                            PL.i( "onPurchaseUpdate purchased.");
                            isPending = false;
                            currentPurchase = purchase;
                        } else if (purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PENDING) { //待支付状态
                            PL.i( "onPurchaseUpdate pending.");
                            isPending = true;
                        } else {
                            PL.i( "onPurchaseUpdate other status.");
                            isPending = false;
                        }
                    } else { //已经有一个符合当次商品的支付，将这个商品添加到补发列表
                        PL.i( "onPurchaseUpdate already have meet product, add to replace list.");
                        if (purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED) {
                            PL.i( "onPurchaseUpdate replace list purchased.");
                            waitConsumeList.add(purchase); //非本次购买的商品都添加到待消费列表，走补发流程
                        }
                    }
                } else {
                    PL.i( "onPurchaseUpdate not current pay, add to replace list.");
                    if (purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED) {
                        PL.i( "onPurchaseUpdate not current pay purchased.");
                        waitConsumeList.add(purchase); //非本次购买的商品都添加到待消费列表，走补发流程
                    }
                }
            }
            if (waitConsumeList.isEmpty()) { //没有可消费的商品
                PL.i( "onPurchaseUpdate Nothing waiting for consume.");
            } else { //有待补发的商品
                PL.i( "onPurchaseUpdate start replacement consume.");
                mBillingHelper.consumePurchaseList(activity, waitConsumeList, true,null);
            }
            if (currentPurchase != null) { //当次的购买消费
                PL.i( "onPurchaseUpdate start replacement consume.");
                mBillingHelper.consumePurchase(activity, currentPurchase, false,null);
            } else {
                if (isPending) { //当次购买的商品待付款，否则继续等待当次购买信息回调
                    callbackFail("Purchase is now pending, waiting for complete.");
                }/* else {
                    callbackFail("Something went wrong in purchase update, please contact customer service.");
                }*/
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) { //用户取消
            // Handle an error caused by a user cancelling the purchase flow.
            PL.i( "user cancelling the purchase flow");
            callbackFail(null);
        } else { //发生错误
            // Handle any other error codes.
            PL.i( "Purchases error code -> " + billingResult.getResponseCode());
            PL.i( "Purchases error message -> " + billingResult.getDebugMessage());
            callbackFail(billingResult.getDebugMessage());
//            if (SStringUtil.isNotEmpty(billingResult.getDebugMessage())){
//            }
        }
    }

    @Override
    public void launchBillingFlowResult(Context context, BillingResult billingResult) {
        if (billingResult != null) {
            PL.i( "launchBillingFlowResult response code : " + billingResult.getResponseCode() + "  :" + billingResult.toString());
            if (TextUtils.isEmpty(billingResult.getDebugMessage())) {
                PL.i( "launchBillingFlowResult response msg empty. ");
            } else {
                PL.i( "launchBillingFlowResult response msg : " + billingResult.getDebugMessage());
            }
        }
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            if (TextUtils.isEmpty(billingResult.getDebugMessage())) {
                callbackFail("Google play billing error, please contact customer service");
            } else {
                callbackFail(billingResult.getDebugMessage());
            }
        }
    }

    private void sendBroadcast(com.android.billingclient.api.Purchase mPurchase) {
        /*if (mBillingHelper != null && !activity.isFinishing()) {
            Context applicationContext = activity.getApplicationContext();
            mBillingHelper.querySkuDetailsAsync(activity, Collections.singletonList(mPurchase.getSku()), new GBillingHelper.BillingHelperStatusCallback() {
                @Override
                public void onStartUp(boolean isSuccess, String msg) {

                }

                @Override
                public void onQuerySkuResult(Context context, List<ProductDetails> list) {
                    if (list != null && list.size() == 1) {
                        try {
                            ProductDetails skuDetail = list.get(0);
                            Purchase purchase = new Purchase(BillingClient.ProductType.INAPP, mPurchase.getOriginalJson(), mPurchase.getSignature());
                            BasePayBean payBean = new BasePayBean();
                            if (purchase != null) {
                                payBean.setOrderId(purchase.getOrderId());
                                payBean.setPackageName(purchase.getPackageName());
                                payBean.setProductId(purchase.getSku());
                                payBean.setmItemType(purchase.getItemType());
                                payBean.setOriginPurchaseData(purchase.getOriginalJson());
                                payBean.setPurchaseState(purchase.getPurchaseState());
                                payBean.setPurchaseTime(purchase.getPurchaseTime());
                                payBean.setSignature(purchase.getSignature());
                                payBean.setDeveloperPayload(purchase.getDeveloperPayload());
                                payBean.setmToken(purchase.getToken());
                                double price = skuDetail.getOneTimePurchaseOfferDetails().getPriceAmountMicros() / 1000000.00;
                                payBean.setPrice(price);
                                payBean.setCurrency(skuDetail.getOneTimePurchaseOfferDetails().getPriceCurrencyCode());
                            }
                            Intent intent = new Intent(GooglePayHelper.ACTION_PAY_REPLACE_OK);
                            intent.putExtra(GamaCommonKey.PURCHASE_DATA, payBean);
                            intent.setPackage(activity.getPackageName());
                            applicationContext.sendBroadcast(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void queryPurchaseResult(Context context, List<com.android.billingclient.api.Purchase> purchases) {

                }

                @Override
                public void onConsumeResult(Context context, BillingResult billingResult, @NonNull String purchaseToken, com.android.billingclient.api.Purchase purchase, boolean isReplaceConsume) {

                }

                @Override
                public void onPurchaseUpdate(@NonNull BillingResult billingResult, @Nullable List<com.android.billingclient.api.Purchase> purchases) {

                }

                @Override
                public void launchBillingFlowResult(Context context, BillingResult billingResult) {

                }
            });
        }*/
    }

}

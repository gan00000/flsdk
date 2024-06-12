package com.mw.sdk.pay.gp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ThreadUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.out.bean.EventPropertie;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.api.PayApi;
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.bean.req.PayReqBean;
import com.mw.sdk.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.bean.res.GPExchangeRes;
import com.mw.sdk.api.task.LoadingDialog;
import com.mw.sdk.pay.gp.util.GBillingHelper;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.bean.res.BasePayBean;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.constant.ApiRequestMethod;
import com.thirdlib.td.TDAnalyticsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gan on 2017/2/23.
 */

public class GooglePayImpl implements IPay, GBillingHelper.BillingHelperStatusCallback {
    private static final String TAG = GooglePayImpl.class.getSimpleName();

    public static final String TAG_USER_CANCEL = "TAG_GOOGLE_PAY_USER_CANCEL";
    private GBillingHelper mBillingHelper;

    private LoadingDialog loadingDialog;

    /**
     * 当次的商品详情
     */
    private ProductDetails skuDetails = null;
    private Double skuAmount;

    /**
     * 当次的创单响应
     */
//    private GPCreateOrderIdRes createOrderBean = null;

    /**
     * 创单的请求参数Bean
     */
    private PayCreateOrderReqBean createOrderIdReqBean;

    private Activity mActivity;
//    private Context mContext;

    private IPayCallBack iPayCallBack;
    /**
     * 防止连续快速点击储值出现未知异常
     */
    private boolean isPaying = false;

    /**
     * 待消费列表,只有 PurchaseState.PURCHASED 的商品才能请求消费
     */
    private ArrayList<com.android.billingclient.api.Purchase> waitConsumeList = new ArrayList<>();


    private void callbackSuccess(Purchase purchase, GPExchangeRes gpExchangeRes) {
        isPaying = false;
        PL.i("google pay onConsumeResponse callbackSuccess");
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mBillingHelper != null) { //关闭页面前先移除callback，否则游戏的onResume会先于 GooglePayActivity2 的onDestroy执行
//            mBillingHelper.removeBillingHelperStatusCallback(this);
                    }
                    BasePayBean payBean = new BasePayBean();
                    if (purchase != null) {
                        try {
                            if (purchase.getAccountIdentifiers() != null && SStringUtil.isNotEmpty(purchase.getAccountIdentifiers().getObfuscatedProfileId())) {
                                payBean.setOrderId(purchase.getAccountIdentifiers().getObfuscatedProfileId());
                            }else {
                                payBean.setOrderId(purchase.getOrderId());
                            }
                            payBean.setTransactionId(purchase.getOrderId());
                            payBean.setPackageName(purchase.getPackageName());
                            payBean.setUsdPrice(skuAmount);
                            if (createOrderIdReqBean != null) {
                                payBean.setProductId(createOrderIdReqBean.getProductId());
                            }
//                    payBean.setmItemType(purchase.getItemType());
                            payBean.setOriginPurchaseData(purchase.getOriginalJson());
                            payBean.setPurchaseState(purchase.getPurchaseState());
                            payBean.setPurchaseTime(purchase.getPurchaseTime());
                            payBean.setSignature(purchase.getSignature());
                            payBean.setDeveloperPayload(purchase.getDeveloperPayload());
                            payBean.setmToken(purchase.getPurchaseToken());
                            if (skuDetails != null && skuDetails.getOneTimePurchaseOfferDetails() != null) {
                                double price = skuDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros() / 1000000.00;
                                payBean.setPrice(price);
                                payBean.setCurrency(skuDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode());
                            }

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

                    if (mBillingHelper != null) { //关闭页面前先移除callback，否则游戏的onResume会先于 GooglePayActivity2 的onDestroy执行
//            mBillingHelper.removeBillingHelperStatusCallback(this);
                    }
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

        //this.createOrderIdReqBean = null;
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
//        loadingDialog = new LoadingDialog(activity);

        //由GooglePayActivity2传入
        this.createOrderIdReqBean = (PayCreateOrderReqBean) payReqBean;
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(ApiRequestMethod.API_ORDER_CREATE);

        if (this.createOrderIdReqBean.isInitOk()) {
            //开始Google储值
            googlePayInActivity(activity);
        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
//        isPaying = false;
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

        if (mBillingHelper== null || mContext == null){
            return;
        }
        Handler mHandler = new Handler();
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        mBillingHelper.queryPurchasesAsync(mContext, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                PL.i("startQueryPurchase onQueryPurchasesResponse success");
                if (list == null) {
                    PL.i("startQueryPurchase onQueryPurchasesResponse success, purchase list is null");
                    return;
                }
                if (list.isEmpty()){
                    PL.i("startQueryPurchase onQueryPurchasesResponse success, purchase list is empty");
                    return;
                }
                for (com.android.billingclient.api.Purchase purchase : list) {//查询是否为PURCHASED未消费商品
                    if(purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED){
                        //2.发送到服务器
                        //发送到服务器,发币
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                PayApi.requestSendStone(mContext, purchase, true, new SFCallBack<GPExchangeRes>() {
                                    @Override
                                    public void success(GPExchangeRes result, String msg) {
                                        PL.i("startQueryPurchase requestSendStone success");
                                        //3.消费
                                        mBillingHelper.consumeAsync(mContext, purchase, false, new ConsumeResponseListener() {
                                            @Override
                                            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                                PL.i("startQueryPurchase onConsumeResponse => " + s);
                                            }
                                        });

                                    }

                                    @Override
                                    public void fail(GPExchangeRes result, String msg) {
                                        PL.i("startQueryPurchase requestSendStone fail => " + msg);
                                    }
                                });
                            }
                        });


                    }
                }
            }
        });
    }

    public void queryPreRegData(final Context mContext, ISdkCallBack iSdkCallBack) {

        if (mBillingHelper== null || mContext == null){
            if (iSdkCallBack != null){
                iSdkCallBack.failure();
            }
            return;
        }
        Handler mHandler = new Handler();
        PL.i("queryPreRegData...");
//        try {
//            Purchase purchase = new Purchase("{\"packageName\":\"com.wantang.xlzsg\",\"productId\":\"com.wantang.gwsg.yzcjl\",\"purchaseTime\":1684253534812,\"purchaseState\":0,\"purchaseToken\":\"giklhafklnbbdkfeceocpmhp.AO-J1OysnskiWzby8LmHDVegqwkIjD8k-e1xs8OVcDcfCConqSRqdjSehwL3RJweFQmHnd0ZMT4bwaj9xWVDyHydFLtqkaHwag\"}", "Tv3YvII6n66MAusut5PvLdLIlUSH85T/OaAA9jEyYTGsX3fqz5aPDZADJ35uRkiCTJqorlq3+pCnkKklCSReSeWfTgzyn7FJmUXIXHZ8RCB2NKFfhWYmROP8pPaX9eDeGv/IYloyazmp0Vo8GbTCQSUrjmdq4Tmke3cjxe9O6LGD+8PVX4WLuP5zuHITWRCHsYaaHM9UVzRRt28LUfrjGtuuYcdqeiW0i5rsLEIQbsQJNk4N1dwZ3/CjE+rs5NzcnDPilE4ej6EFcBCWiCtqLkPFjCpVCq7KZ3R/vUdqs4wMkJ1DWs8UUQNvcov6Bch6ez80GkVX68/Y1pqTPhMcOw==");
//            List<String> stringList = purchase.getProducts();
//        } catch (JSONException e) {
//
//        }
        mBillingHelper.queryPurchasesAsync(mContext, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                PL.i("queryPreRegData startQueryPurchase onQueryPurchasesResponse success");
                PL.i("is main thread=" + ThreadUtil.isUiThread());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null || list.isEmpty()){
                            PL.i("queryPreRegData onQueryPurchasesResponse success, purchase list is empty");
                            if (iSdkCallBack != null){
                                iSdkCallBack.failure();
                            }
                            return;
                        }
                        for (com.android.billingclient.api.Purchase purchase : list) {//查询是否为PURCHASED未消费商品

                            if(purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED){

                                if (!purchase.getProducts().isEmpty() && purchase.getProducts().get(0).contains(".yzcjl")){

                                    if (iSdkCallBack != null){
                                        iSdkCallBack.success();
                                    }
                                    mBillingHelper.consumeAsync(mContext, purchase, false, new ConsumeResponseListener() {
                                        @Override
                                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                            PL.i("queryPreRegData consumeAsync onConsumeResponse => " + s);
                                        }
                                    });
                                    return;
                                }
                            }
                        }
                        if (iSdkCallBack != null){
                            iSdkCallBack.failure();
                        }
                    }
                });

            }
        });
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

//        startQueryPurchase(activity.getApplicationContext());
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
        if (mBillingHelper != null) {
            //在销毁前再确保回调被移除，否则游戏的singletask会把支付activity杀死，导致回调没有被移除，游戏onresume时的查单会走这里的回调，导致再次创单
            mBillingHelper.removeBillingHelperStatusCallback(this);
            mBillingHelper.destroy();
            mBillingHelper = null;
        }
    }


    /**
     * <p>Title: googlePaySetUp</p> <p>Description: 启动远程服务</p>
     */
    private void googlePayInActivity(Activity activity) {

        loadingDialog.showProgressDialog();

//        如需验证购买交易，请先检查购买交易的状态是否为 PURCHASED。如果购买交易的状态为 PENDING，则您应按照处理待处理的交易中的说明处理购买交易。
//        对于通过 onPurchasesUpdated() 或 queryPurchasesAsync() 接收的购买交易，您应在应用授予权利之前进一步验证购买交易以确保其合法性
//        一旦您验证了购买交易，您的应用就可以向用户授予权利了。授予权利后，您的应用必须确认购买交易。此确认会告知 Google Play 您已授予购买权。

//        ====注意：如果您在三天内未确认购买交易，则用户会自动收到退款，并且 Google Play 会撤消该购买交易。====

        //1.先查询=>在提供待售商品之前，检查用户是否尚未拥有该商品。如果用户的消耗型商品仍在他们的商品库中，他们必须先消耗掉该商品，然后才能再次购买。
        Context context = activity.getApplicationContext();
        mBillingHelper.queryPurchasesAsync(context, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<com.android.billingclient.api.Purchase> list) {
                PL.i("queryPurchasesAsync finish");
                handleMultipleConsmeAsyncWithResend(list, activity, new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult3333, @NonNull String s) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //4.创建订单
                                PayApi.requestCreateOrder(activity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
                                    @Override
                                    public void success(GPCreateOrderIdRes createOrderIdRes, String msg1) {
                                        PL.i("requestCreateOrder finish success");

                                        EventPropertie eventPropertie = new EventPropertie();
                                        eventPropertie.setOrder_id(createOrderIdRes.getPayData().getOrderId());
                                        //eventPropertie.setPayment_name(createOrderIdRes.getPayData().getProductName());
                                        eventPropertie.setPay_amount(createOrderIdRes.getPayData().getAmount());
                                        eventPropertie.setProduct_id(createOrderIdReqBean.getProductId());
                                        eventPropertie.setPay_method("google");
                                        eventPropertie.setCurrency_type("USD");
                                        TDAnalyticsHelper.trackEvent("payment_submit",eventPropertie);

                                        //5.开始购买
                                        mBillingHelper.launchPurchaseFlow(activity, createOrderIdReqBean.getProductId(),createOrderIdReqBean.getUserId(),
                                                createOrderIdRes.getPayData().getOrderId(), new PurchasesUpdatedListener() {
                                                    @Override
                                                    public void onPurchasesUpdated(@NonNull BillingResult billingResult2, @Nullable List<com.android.billingclient.api.Purchase> purchasesList) {

                                                        PL.i("launchPurchaseFlow onPurchasesUpdated finish...");

                                                        if (billingResult2.getResponseCode() == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                                                            //成功回调
                                                            handlePurchase(purchasesList, createOrderIdRes, activity);
                                                        } else if (billingResult2.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                                                            // Handle an error caused by a user cancelling the purchase flow.
                                                        } else {
                                                            // Handle any other error codes.
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
        });
    }

    private void handlePurchase(@NonNull List<Purchase> purchasesList, GPCreateOrderIdRes createOrderIdRes, Activity activity) {

        skuAmount = createOrderIdRes.getPayData().getAmount();
        //支付回调
        for (Purchase purchase : purchasesList) {//这里其实只会有一笔
            PL.i("launchPurchaseFlow onPurchasesUpdated = " + purchase.getPurchaseState());
            if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                //发送到服务器,发币
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PayApi.requestSendStone(activity, purchase,false, new SFCallBack<GPExchangeRes>() {
                            @Override
                            public void success(GPExchangeRes gpExchangeRes, String msg) {
                                PL.i("launchPurchaseFlow requestSendStone success");

                                mBillingHelper.consumeAsync(activity, purchase, false, new ConsumeResponseListener() {
                                    @Override
                                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                        PL.i("launchPurchaseFlow onConsumeResponse => " + s);
                                        callbackSuccess(purchase, gpExchangeRes);
                                    }
                                });

                            }

                            @Override
                            public void fail(GPExchangeRes result, String msg) {
                                PL.i("launchPurchaseFlow requestSendStone fail => " + msg);
//
                                if (result != null && SStringUtil.isNotEmpty(result.getMessage())){
                                    callbackFail(result.getMessage());
                                }else {
                                    callbackFail("error");
                                }

                            }
                        });
                    }
                });

            }
        }
    }

    private int consumeFinish = 0;
    private int PurchaseState_PURCHASED_SIEZ = 0;
    private void handleMultipleConsmeAsyncWithResend(@NonNull List<Purchase> list, Activity activity, ConsumeResponseListener consumeResponseListener) {
        PL.i("------handleMultipleConsmeAsyncWithResend-----");
        consumeFinish = 0;
        PurchaseState_PURCHASED_SIEZ = 0;
        if (list==null || list.isEmpty() || activity == null){
            consumeResponseListener.onConsumeResponse(null,"");
            return;
        }

        for (Purchase purchase : list) {
            if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                PurchaseState_PURCHASED_SIEZ++;
            }
        }
        if (PurchaseState_PURCHASED_SIEZ == 0){ //0回调，没有需要消费的
            consumeResponseListener.onConsumeResponse(null,"");
            return;
        }
        for (Purchase purchase : list) {//查询是否为PURCHASED未消费商品
//            只有在状态为 PURCHASED 时，您才能授予权利。请使用 getPurchaseState()（而非 getOriginaljson()），并确保正确处理 PENDING 交易。
            if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                //2.发送到服务器
                //3.消费
                //这里为了用户能够正常下一笔充值，补发和消费并行进行，不卡用户充值
                mBillingHelper.consumeAsync(activity, purchase, true, new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                        consumeFinish = consumeFinish + 1;
                        PL.i("query purchase consumeAsync finish consumeFinish=" + consumeFinish);
                        if (consumeFinish == PurchaseState_PURCHASED_SIEZ){
                            consumeResponseListener.onConsumeResponse(null,"");
                        }
                    }
                });
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PayApi.requestSendStone(activity, purchase,true, new SFCallBack<GPExchangeRes>() {
                            @Override
                            public void success(GPExchangeRes result, String msg) {

                            }

                            @Override
                            public void fail(GPExchangeRes result, String msg) {

                            }
                        });
                    }
                });

            }
        }
    }


    @Override
    public void onStartUp(boolean isSuccess, String msg) {
        if (isSuccess) {
            PL.i( "billingClient.startConnection onStartUp success.");
        } else {
            PL.i( "billingClient.startConnection onStartUp fail.");
            callbackFail(msg + "");
        }
    }

    @Override
    public void onQuerySkuResult(Context context,BillingResult billingResult, List<ProductDetails> productDetailsList) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null && !productDetailsList.isEmpty()) {
            skuDetails = productDetailsList.get(0);
//            mBillingHelper.launchPurchaseFlow(activity, createOrderBean.getOrderId(), skuDetails);
        } else {
            PL.i( "onQuerySkuResult error responseCode => " + billingResult.getResponseCode() + ",debugMessage => " + billingResult.getDebugMessage());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
                PL.i("Feature not supported on onQuerySkuResult");
                callbackFail("Please update PlayStore app");
            }else {
                callbackFail("SkuDetails not find");
            }

        }
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
           /* for (Purchase purchase : purchases) {
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
            }*/
           /* if (waitConsumeList.isEmpty()) { //没有可消费的商品
                PL.i( "onPurchaseUpdate Nothing waiting for consume.");
            } else { //有待补发的商品
                PL.i( "onPurchaseUpdate start replacement consume.");
                mBillingHelper.consumePurchaseList(mActivity, waitConsumeList, true,null);
            }
            if (currentPurchase != null) { //当次的购买消费
                PL.i( "onPurchaseUpdate start replacement consume.");
                mBillingHelper.consumePurchase(mActivity, currentPurchase, false,null);
            } else {
                if (isPending) { //当次购买的商品待付款，否则继续等待当次购买信息回调
                    callbackFail("Purchase is now pending, waiting for complete.");
                }*//* else {
                    callbackFail("Something went wrong in purchase update, please contact customer service.");
                }*//*
            }*/
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) { //用户取消
            // Handle an error caused by a user cancelling the purchase flow.
            PL.i( "user cancelling the purchase flow");
            callbackFail(TAG_USER_CANCEL);
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

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
import com.thirdlib.tiktok.TTSdkHelper;

import java.util.ArrayList;
import java.util.List;

import gg.now.billingclient.util.BillingHelper;

/**
 * Created by gan on 2017/2/23.
 */

public class GooglePayImpl implements IPay, GBillingHelper.BillingHelperStatusCallback {

    public static final String TAG_USER_CANCEL = "TAG_GOOGLE_PAY_USER_CANCEL";

    private LoadingDialog loadingDialog;

    /**
     * 当次的商品详情
     */
    private ProductDetails skuDetails = null;
    private Double skuAmount;

    /**
     * 创单的请求参数Bean
     */
    private PayCreateOrderReqBean createOrderIdReqBean;

    private Activity mActivity;

    private IPayCallBack iPayCallBack;
    /**
     * 防止连续快速点击储值出现未知异常
     */
    private boolean isPaying = false;


    private void callbackSuccess(Purchase purchase, GPExchangeRes gpExchangeRes) {
        isPaying = false;
        PL.i("google pay onConsumeResponse callbackSuccess");
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

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
                                payBean.setCpOrderId(createOrderIdReqBean.getCpOrderId());
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
        GBillingHelper.getInstance().setBillingHelperStatusCallback(this);
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

        //创建Loading窗
        if(loadingDialog == null ||  this.mActivity != activity){
            dimissDialog();
            loadingDialog = new LoadingDialog(activity);
        }

        PL.w("google set paying...");
        isPaying = true;

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
            loadingDialog.showProgressDialog();
            //开始Google储值
            GBillingHelper.getInstance().queryPurchasesAsync(activity, true);

        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
//        isPaying = false;
      //  PL.w("google set not paying");
    }

    private void dimissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismissProgressDialog();
        }
        isPaying = false;
    }
    @Override
    public void startQueryPurchase(Activity activity) {

        this.mActivity = activity;
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        GBillingHelper.getInstance().setBillingHelperStatusCallback(this);
        GBillingHelper.getInstance().queryPurchasesAsync(activity, false);

    }

    public void queryPreRegData(final Activity activity, ISdkCallBack iSdkCallBack) {

        this.mActivity = activity;
     /*   if (mBillingHelper== null || mContext == null){
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
        */
    }
    @Override
    public void onCreate(Activity activity) {
        PL.i( "onCreate");
        this.mActivity = activity;
        GBillingHelper.getInstance().setBillingHelperStatusCallback(this);
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
//        if (mBillingHelper != null) {
//            //在销毁前再确保回调被移除，否则游戏的singletask会把支付activity杀死，导致回调没有被移除，游戏onresume时的查单会走这里的回调，导致再次创单
//            mBillingHelper.removeBillingHelperStatusCallback(this);
//            mBillingHelper.destroy();
//            mBillingHelper = null;
//        }
        GBillingHelper.getInstance().destroy();
    }


    /**
     * <p>Title: googlePaySetUp</p> <p>Description: 启动远程服务</p>
     */
    private void googlePayInActivity() {

//        如需验证购买交易，请先检查购买交易的状态是否为 PURCHASED。如果购买交易的状态为 PENDING，则您应按照处理待处理的交易中的说明处理购买交易。
//        对于通过 onPurchasesUpdated() 或 queryPurchasesAsync() 接收的购买交易，您应在应用授予权利之前进一步验证购买交易以确保其合法性
//        一旦您验证了购买交易，您的应用就可以向用户授予权利了。授予权利后，您的应用必须确认购买交易。此确认会告知 Google Play 您已授予购买权。

//        ====注意：如果您在三天内未确认购买交易，则用户会自动收到退款，并且 Google Play 会撤消该购买交易。====

        //1.先查询=>在提供待售商品之前，检查用户是否尚未拥有该商品。如果用户的消耗型商品仍在他们的商品库中，他们必须先消耗掉该商品，然后才能再次购买。
        PL.d("in googlePayInActivity");
        if (this.mActivity == null || createOrderIdReqBean == null){
            PL.d("createOrderIdReqBean is null");
            callbackFail("createOrderIdReqBean is null");
            return;
        }
        PL.d("start googlePayInActivity");
        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //4.创建订单
                PL.d("start googlePayInActivity PayApi.requestCreateOrder");
                PayApi.requestCreateOrder(mActivity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
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
                        TDAnalyticsHelper.trackEvent(mActivity,"payment_submit",eventPropertie);

                        TTSdkHelper.trackCheckout(mActivity.getApplicationContext(), createOrderIdRes.getPayData().getOrderId(), createOrderIdReqBean.getProductId(), createOrderIdRes.getPayData().getAmount());
                        skuAmount = createOrderIdRes.getPayData().getAmount();
                        //5.开始购买
                        GBillingHelper.getInstance().launchPurchaseFlow(mActivity, createOrderIdReqBean.getProductId(),createOrderIdReqBean.getUserId(),
                                createOrderIdRes.getPayData().getOrderId());
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

    private int consumeFinish = 0;
    private int PurchaseState_PURCHASED_SIEZ = 0;
    private void handleMultipleConsmeAsyncWithResend(@NonNull List<Purchase> list, Activity activity, ConsumeResponseListener consumeResponseListener) {
        PL.d("------handleMultipleConsmeAsyncWithResend-----");
        consumeFinish = 0;
        PurchaseState_PURCHASED_SIEZ = 0;
        if (list==null || list.isEmpty() || activity == null){
            PL.d("------handleMultipleConsmeAsyncWithResend list empty-----");
            consumeResponseListener.onConsumeResponse(null,"");
            return;
        }
        PL.d("------handleMultipleConsmeAsyncWithResend list=" + list.size());
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
                GBillingHelper.getInstance().consumeAsync(activity, purchase, true, new ConsumeResponseListener() {
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
                        PayApi.requestSendStone_Google(activity, purchase,true, new SFCallBack<GPExchangeRes>() {
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
    public void onQuerySkuResult(List<ProductDetails> productDetailsList, String productId) {

        if (productDetailsList != null && !productDetailsList.isEmpty()){
            skuDetails = productDetailsList.get(0);
        }

    }

    @Override
    public void queryPurchaseResult(List<Purchase> purchases) {

        PL.i("startQueryPurchase onQueryPurchasesResponse success");
        if (purchases == null) {
            PL.i("startQueryPurchase onQueryPurchasesResponse success, purchase list is null");
            return;
        }
        if (purchases.isEmpty()){
            PL.i("startQueryPurchase onQueryPurchasesResponse success, purchase list is empty");
            return;
        }
        if (this.mActivity == null){
            return;
        }

        for (com.android.billingclient.api.Purchase purchase : purchases) {//查询是否为PURCHASED未消费商品
            if(purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED){
                //2.发送到服务器
                //发送到服务器,发币
                this.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PayApi.requestSendStone_Google(mActivity, purchase, true, new SFCallBack<GPExchangeRes>() {
                            @Override
                            public void success(GPExchangeRes result, String msg) {
                                PL.i("startQueryPurchase requestSendStone success");
                                //3.消费
                                GBillingHelper.getInstance().consumeAsync(mActivity, purchase, true, new ConsumeResponseListener() {
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

    @Override
    public void queryPurchaseResultInPaying(List<Purchase> purchases) {

        PL.i("queryPurchaseResultInPaying finish");
        handleMultipleConsmeAsyncWithResend(purchases, this.mActivity, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult3333, @NonNull String s) {
                googlePayInActivity();
            }
        });

    }

    @Override
    public void onPurchaseUpdate(@NonNull BillingResult billingResult, @Nullable List<com.android.billingclient.api.Purchase> purchases) {

        PL.i("launchPurchaseFlow onPurchasesUpdated finish...");

        if (this.mActivity == null){
            callbackFail("onPurchaseUpdate mActivity is null");
            return;
        }
        //支付回调
        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (Purchase purchase : purchases) {//这里其实只会有一笔

                    /*注意： 只有在状态为 PURCHASED 时，您才能确认购买交易；即，在购买交易处于 PENDING 状态时，请勿确认该交易。
                    三天内确认购买交易。在购买交易处于 PENDING 状态时，请勿确认该交易。
                    当购买状态从“PENDING”转换为“PURCHASED”时，3 天的确认期限才会开始。*/
                    PL.i("launchPurchaseFlow onPurchasesUpdated = " + purchase.getPurchaseState());
                    if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                        //发送到服务器,发币
                        PayApi.requestSendStone_Google(mActivity, purchase,false, new SFCallBack<GPExchangeRes>() {
                            @Override
                            public void success(GPExchangeRes gpExchangeRes, String msg) {
                                PL.i("launchPurchaseFlow requestSendStone success");

                                GBillingHelper.getInstance().consumeAsync(mActivity, purchase, false, new ConsumeResponseListener() {
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
                }

            }
        });
    }

    @Override
    public void handleError(int m, String msg) {
        PL.i( "handleError isPaying=" +isPaying);
        if (mActivity != null && this.createOrderIdReqBean != null){//不是正在购买的时候，不提示错误弹框
            callbackFail(msg);
        }

    }

    @Override
    public void handleCancel(String msg) {
        PL.i( "pay handleCancel");
        dimissDialog();
    }
}

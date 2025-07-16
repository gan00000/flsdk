package com.thirdlib.xiaomi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.api.PayApi;
import com.mw.sdk.api.task.LoadingDialog;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.req.PayExchangeReqBean;
import com.mw.sdk.bean.req.PayReqBean;
import com.mw.sdk.bean.res.BasePayBean;
import com.mw.sdk.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.bean.res.GPExchangeRes;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.SdkUtil;
import com.xiaomi.billingclient.api.BillingResult;
import com.xiaomi.billingclient.api.ConsumeResponseListener;
import com.xiaomi.billingclient.api.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by gan on 2017/2/23.
 */

public class XiaomiPayImpl implements IPay, XiaoMiPayManager.PurchaseCallback {

    public static final String TAG_USER_CANCEL = "TAG_PAY_USER_CANCEL";

    private LoadingDialog loadingDialog;

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
        PL.i("XiaomiPayImpl callbackSuccess");
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    BasePayBean payBean = new BasePayBean();
                    if (purchase != null) {
                        try {
//                            JSONObject devPayload = new JSONObject(purchase.getDeveloperPayload());
//                            String userId = devPayload.optString("userId","");
//                            String roleId = devPayload.optString("roleId","");
//                            String orderId = devPayload.optString("orderId","");
//                            payBean.setOrderId(orderId);
//                            payBean.setTransactionId(purchase.getOrderId());
//                            payBean.setPackageName(purchase.getPackageName());
                            payBean.setUsdPrice(skuAmount);
                            if (createOrderIdReqBean != null) {
                                payBean.setProductId(createOrderIdReqBean.getProductId());
                            }
//                    payBean.setmItemType(purchase.getItemType());
//                            payBean.setOriginPurchaseData(purchase.getOriginalJson());
//                            payBean.setPurchaseState(purchase.getPurchaseState());
//                            payBean.setPurchaseTime(purchase.getPurchaseTime());
//                            payBean.setSignature(purchase.getSignature());
//                            payBean.setDeveloperPayload(purchase.getDeveloperPayload());
//                            payBean.setmToken(purchase.getPurchaseToken());
//                            if (skuDetails != null && skuDetails.getOneTimePurchaseOfferDetails() != null) {
//                                double price = skuDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros() / 1000000.00;
//                                payBean.setPrice(price);
//                                payBean.setCurrency(skuDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode());
//                            }

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

//        this.createOrderIdReqBean = null;

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

        PL.w("set paying...");
        isPaying = true;

        this.mActivity = activity;

//        if(activity != mActivity && purchaseManager != null){
//            purchaseManager.destroy();
//            purchaseManager = null;
//        }

        this.createOrderIdReqBean = (PayCreateOrderReqBean) payReqBean;
        this.createOrderIdReqBean.setMode(ChannelPlatform.Xiaomi.getChannel_platform());
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(ApiRequestMethod.API_ORDER_CREATE);

        if (this.createOrderIdReqBean.isInitOk()) {
            //开始储值,先查询有没有未消耗的商品
            loadingDialog.showProgressDialog();
            XiaoMiPayManager.getInstance().queryPurchasesAsync(activity, true);

        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
    }

    private void dimissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismissProgressDialog();
        }
        isPaying = false;
    }

    @Override
    public void startQueryPurchase(Activity activity) {

        if (activity == null){
            return;
        }
        this.mActivity = activity;
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        XiaoMiPayManager.getInstance().queryPurchasesAsync(activity, false);

    }

    public void queryPreRegData(final Activity activity, ISdkCallBack iSdkCallBack) {

    }

    @Override
    public void onCreate(Activity activity) {
        PL.i( "onCreate");
        this.mActivity = activity;
        XiaoMiPayManager.getInstance().setPurchaseCallback(this);
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
//        if (purchaseManager != null) {
//            purchaseManager.destroy();
//            purchaseManager = null;
//        }
    }


    /**
     * 查询消耗后开始购买
     */
    private void onePayInActivity(Activity activity) {

//        loadingDialog.showProgressDialog();

//        List productList = new ArrayList<>();
//        productList.add(this.createOrderIdReqBean.getProductId());

        PayApi.requestCreateOrder(activity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
            @Override
            public void success(GPCreateOrderIdRes createOrderIdRes, String msg1) {
                PL.i("requestCreateOrder finish success");

                if (createOrderIdRes == null || createOrderIdRes.getPayData() == null || SStringUtil.isEmpty(createOrderIdRes.getPayData().getOrderId())){
                    callbackFail("create orderId error");
                }else {

                    //5.开始购买
                    JSONObject devPayload = new JSONObject();
                    try {
                        skuAmount = createOrderIdRes.getPayData().getAmount();
                        devPayload.put("userId", createOrderIdReqBean.getUserId());
                        devPayload.put("roleId", createOrderIdReqBean.getRoleId());
                        devPayload.put("orderId", createOrderIdRes.getPayData().getOrderId());

                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackFail("devPayload error");
                        return;
                    }

                    XiaoMiPayManager.getInstance().launchBillingFlow(activity, createOrderIdReqBean.getProductId(), createOrderIdReqBean.getUserId(), devPayload.toString(), "");

                }

                dimissDialog();
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

    //=================================================================================================
    //=================================================================================================
    //=================================================================================================

    @Override
    public void onPurchaseSucceed(List<Purchase> purchases) {
        PL.i("onPurchaseSucceed");

        if (purchases == null || purchases.isEmpty()){
            return;
        }
        for (Purchase purchase : purchases) {

            String purchaseId = purchase.getOrderId();
            if (SStringUtil.isNotEmpty(purchaseId) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

                PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
                exchangeReqBean.setDataSignature(purchase.getSignature());
                //exchangeReqBean.setPurchaseData(purchase.getPurchaseToken());
                exchangeReqBean.setThirdPurchaseToken(purchase.getPurchaseToken());
                exchangeReqBean.setReissue("no");

                exchangeReqBean.setPurchaseId(purchaseId);
                exchangeReqBean.setProductId(purchase.getSkus().get(0));
                //exchangeReqBean.setInvoiceId(purchase.getInvoiceId());
                exchangeReqBean.setGoogleOrderId(purchaseId);
                exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_Xiaomi);

                try {
//      * obfuscatedAccountId 需要传递该用户在开发者侧的唯一Id，用于支付风控，为必传字段，最长200个字符。
//     * obfuscatedProfileId 为开发者扩展字段，可以自由选择传入何值，交易结果会回传，最长200个字符。例如可以是开发者订单号，便于双方订单对应。
                    JSONObject payLoadJsonObject = new JSONObject(purchase.getObfuscatedProfileId());
                    String userId = payLoadJsonObject.optString("userId");
                    String orderId = payLoadJsonObject.optString("orderId");
                    String roleId = payLoadJsonObject.optString("roleId");
                    exchangeReqBean.setUserId(userId);
                    exchangeReqBean.setOrderId(orderId);
                    exchangeReqBean.setRoleId(roleId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PayApi.requestCommonPaySendStone(mActivity, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
                    @Override
                    public void success(GPExchangeRes result, String msg) {
                        PL.i("requestCommonPaySendStone requestSendStone success => " + msg);

                        //3.消费
                        XiaoMiPayManager.getInstance().onConsumePurchase(mActivity, purchase.getPurchaseToken(), new ConsumeResponseListener() {
                            @Override
                            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                PL.i("onConsumeResponse finsh");
                            }
                        });
                        callbackSuccess(purchase, result);

                    }

                    @Override
                    public void fail(GPExchangeRes result, String msg) {
                        PL.i("requestCommonPaySendStone requestSendStone fail => " + msg);
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

    @Override
    public void onQueryPurchaseSucceed(List<Purchase> purchases) {//静默查询

        PL.i("onQueryPurchaseSucceed");

        if (purchases == null || purchases.isEmpty()){
            PL.i("onQueryPurchaseSucceed empty");
            return;
        }

        for (Purchase purchase : purchases) {

            String purchaseId = purchase.getOrderId();
            if (SStringUtil.isNotEmpty(purchaseId) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

                PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
                exchangeReqBean.setDataSignature(purchase.getSignature());
                //exchangeReqBean.setPurchaseData(purchase.getPurchaseToken());
                exchangeReqBean.setThirdPurchaseToken(purchase.getPurchaseToken());
                exchangeReqBean.setReissue("yes");

                exchangeReqBean.setPurchaseId(purchaseId);
                exchangeReqBean.setProductId(purchase.getSkus().get(0));
                //exchangeReqBean.setInvoiceId(purchase.getInvoiceId());
                exchangeReqBean.setGoogleOrderId(purchaseId);
                exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_Xiaomi);

                try {
//      * obfuscatedAccountId 需要传递该用户在开发者侧的唯一Id，用于支付风控，为必传字段，最长200个字符。
//     * obfuscatedProfileId 为开发者扩展字段，可以自由选择传入何值，交易结果会回传，最长200个字符。例如可以是开发者订单号，便于双方订单对应。
                    JSONObject payLoadJsonObject = new JSONObject(purchase.getObfuscatedProfileId());
                    String userId = payLoadJsonObject.optString("userId");
                    String orderId = payLoadJsonObject.optString("orderId");
                    String roleId = payLoadJsonObject.optString("roleId");
                    exchangeReqBean.setUserId(userId);
                    exchangeReqBean.setOrderId(orderId);
                    exchangeReqBean.setRoleId(roleId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PayApi.requestCommonPaySendStone(mActivity, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
                    @Override
                    public void success(GPExchangeRes result, String msg) {
                        PL.i("startQueryPurchase requestSendStone success => " + msg);
                    }

                    @Override
                    public void fail(GPExchangeRes result, String msg) {
                        PL.i("startQueryPurchase requestSendStone fail => " + msg);
                    }
                });
                XiaoMiPayManager.getInstance().onConsumePurchase(mActivity, purchase.getPurchaseToken(), new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                        PL.i("onConsumeResponse finsh");
                    }
                });

            }
        }
    }

    @Override
    public void onPayingQueryPurchaseSucceed(List<Purchase> purchases) {
        PL.d("onPayingQueryPurchaseSucceed");
        if (purchases == null || purchases.isEmpty()){
            PL.d("onQueryPurchaseSucceed empty");
            onePayInActivity(mActivity);
            return;
        }

        //购买信息
//        Purchase {
//            String orderId;//交易的唯一订单标识符
//            String purchaseToken;//购买令牌。
//            int purchaseState;//购买状态  PurchaseState.PENDING:待办  PurchaseState.PURCHASED：已购买
//            boolean acknowledged;//是否确认：true(已确认), false(未确认)
//            String obfuscatedAccountId;
//            String obfuscatedProfileId;
//            boolean autoRenewing;//订阅是否自动续订
//    ...
//        }
        for (Purchase purchase : purchases) {

            String purchaseId = purchase.getOrderId();
            if (SStringUtil.isNotEmpty(purchaseId) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

                PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
                exchangeReqBean.setDataSignature(purchase.getSignature());
                //exchangeReqBean.setPurchaseData(purchase.getPurchaseToken());
                exchangeReqBean.setThirdPurchaseToken(purchase.getPurchaseToken());
                exchangeReqBean.setReissue("yes");

                exchangeReqBean.setPurchaseId(purchaseId);
                exchangeReqBean.setProductId(purchase.getSkus().get(0));
                //exchangeReqBean.setInvoiceId(purchase.getInvoiceId());
                exchangeReqBean.setGoogleOrderId(purchaseId);
                exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_Xiaomi);

                try {
//      * obfuscatedAccountId 需要传递该用户在开发者侧的唯一Id，用于支付风控，为必传字段，最长200个字符。
//     * obfuscatedProfileId 为开发者扩展字段，可以自由选择传入何值，交易结果会回传，最长200个字符。例如可以是开发者订单号，便于双方订单对应。
                    JSONObject payLoadJsonObject = new JSONObject(purchase.getObfuscatedProfileId());
                    String userId = payLoadJsonObject.optString("userId");
                    String orderId = payLoadJsonObject.optString("orderId");
                    String roleId = payLoadJsonObject.optString("roleId");
                    exchangeReqBean.setUserId(userId);
                    exchangeReqBean.setOrderId(orderId);
                    exchangeReqBean.setRoleId(roleId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PayApi.requestCommonPaySendStone(mActivity, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
                    @Override
                    public void success(GPExchangeRes result, String msg) {
                        PL.i("startQueryPurchase requestSendStone success => " + msg);
                    }

                    @Override
                    public void fail(GPExchangeRes result, String msg) {
                        PL.i("startQueryPurchase requestSendStone fail => " + msg);
                    }
                });
                XiaoMiPayManager.getInstance().onConsumePurchase(mActivity, purchase.getPurchaseToken(), new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                        PL.i("onConsumeResponse finsh");
                    }
                });

            }
        }
        onePayInActivity(mActivity);
    }

    @Override
    public void onCancel(String msg) {
        dimissDialog();
    }

    @Override
    public void onError(XiaoMiPayManager.ReqType type, String message){
        PL.i("onError:" + message + ", isPaying=" + isPaying);
        if (mActivity != null && this.createOrderIdReqBean != null){//此时用户正在购买
            callbackFail("" + message);
        }
    }

    //=================================================================================================

}

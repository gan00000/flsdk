package com.thirdlib.onepay;

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
import com.gaa.sdk.auth.OnAuthListener;
import com.gaa.sdk.auth.SignInResult;
import com.gaa.sdk.iap.ConsumeListener;
import com.gaa.sdk.iap.IapResult;
import com.gaa.sdk.iap.ProductDetail;
import com.gaa.sdk.iap.ProductDetailsListener;
import com.gaa.sdk.iap.PurchaseClient;
import com.gaa.sdk.iap.PurchaseData;
import com.gaa.sdk.iap.PurchaseFlowParams;
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
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.SdkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Created by gan on 2017/2/23.
 */

public class OnestorePayImpl implements IPay, PurchaseManager.Callback {

    public static final String TAG_USER_CANCEL = "TAG_PAY_USER_CANCEL";
    private PurchaseManager purchaseManager;
    private AuthManager authManager;

    private LoadingDialog loadingDialog;

    /**
     * 当次的商品详情
     */
    private ProductDetail productDetail = null;
    private Double skuAmount;


    /**
     * 创单的请求参数Bean
     */
    private PayCreateOrderReqBean createOrderIdReqBean;

    private Activity mActivity;
    private Context mContext;

    private IPayCallBack iPayCallBack;
    /**
     * 防止连续快速点击储值出现未知异常
     */
    private boolean isPaying = false;

    private void callbackSuccess(PurchaseData purchase, GPExchangeRes gpExchangeRes) {

        PL.i("onestore pay onConsumeResponse callbackSuccess");
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    BasePayBean payBean = new BasePayBean();
                    if (purchase != null) {
                        try {
                            JSONObject devPayload = new JSONObject(purchase.getDeveloperPayload());
//                            String userId = devPayload.optString("userId","");
//                            String roleId = devPayload.optString("roleId","");
                            String orderId = devPayload.optString("orderId","");
                            payBean.setOrderId(orderId);
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

                    if (loadingDialog != null) {
                        loadingDialog.dismissProgressDialog();
                    }

                    if (iPayCallBack != null) {
                        iPayCallBack.success(payBean);
                    }
                }
            });
        }


    }

    private void callbackFail(String message) {

        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (loadingDialog != null) {
                        loadingDialog.dismissProgressDialog();
                    }

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

        this.createOrderIdReqBean = null;

        PL.i("the aar version info:" + SdkUtil.getSdkInnerVersion(activity) + "_" + BuildConfig.JAR_VERSION);//打印版本号

        if (activity == null) {
            PL.w("activity is null");
            return;
        }
        this.mActivity = activity;
        mContext = activity.getApplicationContext();
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

//        if(activity != mActivity && purchaseManager != null){
//            purchaseManager.destroy();
//            purchaseManager = null;
//        }

        if (purchaseManager == null){
            purchaseManager = new PurchaseManager(activity.getApplicationContext(), this);
        }
        if (authManager == null){
            authManager = new AuthManager(activity);
        }

        this.createOrderIdReqBean = (PayCreateOrderReqBean) payReqBean;
        this.createOrderIdReqBean.setMode("onestore");
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(ApiRequestMethod.API_ORDER_CREATE);

        //创建Loading窗
        loadingDialog = new LoadingDialog(activity);
        if (this.createOrderIdReqBean.isInitOk()) {
            //开始储值,先查询有没有未消耗的商品
//            onePayInActivity(activity);
            loadingDialog.showProgressDialog();
            purchaseManager.queryPurchasesAsync(true, PurchaseClient.ProductType.INAPP);

        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
        isPaying = false;
        PL.w("google set not paying");
    }


    @Override
    public void startQueryPurchase(Context mContext) {

        if (purchaseManager == null || mContext == null){
            return;
        }
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        mContext = mContext.getApplicationContext();
        purchaseManager.queryPurchasesAsync(false, PurchaseClient.ProductType.INAPP);

    }

    public void queryPreRegData(final Context mContext, ISdkCallBack iSdkCallBack) {

    }

    @Override
    public void onCreate(Activity activity) {
        PL.i( "onCreate");
        mContext = activity.getApplicationContext();
        if (purchaseManager == null){
            purchaseManager = new PurchaseManager(activity.getApplicationContext(), this);
        }

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
        if (purchaseManager != null) {
            purchaseManager.destroy();
            purchaseManager = null;
        }
    }


    /**
     * 查询消耗后开始购买
     */
    private void onePayInActivity(Activity activity) {

//        loadingDialog.showProgressDialog();

        List productList = new ArrayList<>();
        productList.add(this.createOrderIdReqBean.getProductId());
        purchaseManager.queryProductDetailAsync(productList, PurchaseClient.ProductType.INAPP, new ProductDetailsListener() {
            @Override
            public void onProductDetailsResponse(IapResult iapResult, List<ProductDetail> productDetailList) {

                if (iapResult != null && iapResult.isSuccess() && productDetailList != null && !productDetailList.isEmpty()){
                    ProductDetail productDetail = productDetailList.get(0);

                    PayApi.requestCreateOrder(activity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
                        @Override
                        public void success(GPCreateOrderIdRes createOrderIdRes, String msg1) {
                            PL.i("requestCreateOrder finish success");
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
                            PurchaseFlowParams purchaseFlowParams = PurchaseFlowParams.newBuilder()
                                    .setProductId(productDetail.getProductId())
                                    .setProductType(PurchaseClient.ProductType.INAPP)
                                    .setGameUserId(createOrderIdReqBean.getUserId())
                                    .setDeveloperPayload(devPayload.toString())
                                    .setQuantity(1)
                                    .build();

                            purchaseManager.launchPurchaseFlow(activity, purchaseFlowParams);

                            if (loadingDialog != null) {
                                loadingDialog.dismissProgressDialog();
                            }
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


                }else {
                    //queryProductDetailAsync fail
                    PL.i("queryProductDetailAsync fail " + iapResult.getMessage());
                    purchaseManager.handleIapResultError(iapResult);
                }
            }
        });

    }

    //=================================================================================================
    //=================================================================================================
    //=================================================================================================

    @Override
    public void onPurchaseClientSetupFinished() {
        PL.i("onPurchaseClientSetupFinished");
    }

    @Override
    public void onConsumeFinished(@NonNull PurchaseData purchaseData, @NonNull IapResult iapResult) {
        PL.i("startQueryPurchase onConsumeResponse => " + purchaseData.getPurchaseToken());
    }

    @Override
    public void onAcknowledgeFinished(@NonNull PurchaseData purchaseData, @NonNull IapResult iapResult) {

    }

    @Override
    public void onPurchaseSucceed(@NonNull List<? extends PurchaseData> purchases) {
        PL.i("onPurchaseSucceed");
        if (purchases == null || purchases.isEmpty()){
            PL.i("onPurchaseSucceed empty");
            callbackFail("PurchaseData empty");
            return;
        }
        for (PurchaseData purchaseData: purchases) {
            //补发币

            PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
            exchangeReqBean.setDataSignature(purchaseData.getSignature());
            exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
            exchangeReqBean.setReissue("no");

            exchangeReqBean.setGoogleOrderId(purchaseData.getOrderId());
            exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_ONESTORE);

            try {
                JSONObject payLoadJsonObject = new JSONObject(purchaseData.getDeveloperPayload());
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
                    //3.消费
                    purchaseManager.consumeAsync(purchaseData, new ConsumeListener() {
                        @Override
                        public void onConsumeResponse(IapResult iapResult, PurchaseData purchaseData) {

                        }
                    });

                    callbackSuccess(purchaseData, result);
                }

                @Override
                public void fail(GPExchangeRes result, String msg) {
                    PL.i("startQueryPurchase requestSendStone fail => " + msg);
                    if (result != null && SStringUtil.isNotEmpty(result.getMessage())){
                        callbackFail(result.getMessage());
                    }else {
                        callbackFail("error");
                    }
                }
            });

        }
    }

    @Override
    public void onQueryPurchaseSucceed(@NonNull List<? extends PurchaseData> purchases) {//默默查询是使用
        PL.i("onQueryPurchaseSucceed");

        if (purchases == null || purchases.isEmpty()){
            PL.i("onQueryPurchaseSucceed empty");
            return;
        }
        for (PurchaseData purchaseData: purchases) {
            //补发币

            PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(this.mContext);
            exchangeReqBean.setDataSignature(purchaseData.getSignature());
            exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
            exchangeReqBean.setReissue("yes");

            exchangeReqBean.setGoogleOrderId(purchaseData.getOrderId());
            exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_ONESTORE);
            try {
                JSONObject payLoadJsonObject = new JSONObject(purchaseData.getDeveloperPayload());
                String userId = payLoadJsonObject.optString("userId");
                String orderId = payLoadJsonObject.optString("orderId");
                String roleId = payLoadJsonObject.optString("roleId");
                exchangeReqBean.setUserId(userId);
                exchangeReqBean.setOrderId(orderId);
                exchangeReqBean.setRoleId(roleId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PayApi.requestCommonPaySendStone(this.mContext, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
                @Override
                public void success(GPExchangeRes result, String msg) {
                    PL.i("startQueryPurchase requestSendStone success => " + msg);
                    //3.消费
                    purchaseManager.consumeAsync(purchaseData, new ConsumeListener() {
                        @Override
                        public void onConsumeResponse(IapResult iapResult, PurchaseData purchaseData) {

                        }
                    });
                }

                @Override
                public void fail(GPExchangeRes result, String msg) {
                    PL.i("startQueryPurchase requestSendStone fail => " + msg);
                }
            });

        }
    }

    int consume_index;
    @Override
    public void onPayingQueryPurchaseSucceed(@NonNull List<? extends PurchaseData> purchases) {

        PL.i("onPayingQueryPurchaseSucceed");
        consume_index = 0;
        if (purchases == null || purchases.isEmpty()){
            PL.i("onQueryPurchaseSucceed empty");
            onePayInActivity(mActivity);
            return;
        }
        consume_index = purchases.size();
        for (PurchaseData purchaseData: purchases) {//正在购买的时候消耗
            //补发币

            PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
            exchangeReqBean.setDataSignature(purchaseData.getSignature());
            exchangeReqBean.setPurchaseData(purchaseData.getOriginalJson());
            exchangeReqBean.setReissue("yes");

            exchangeReqBean.setGoogleOrderId(purchaseData.getOrderId());
            exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_ONESTORE);
            try {
                JSONObject payLoadJsonObject = new JSONObject(purchaseData.getDeveloperPayload());
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

            //同时消耗掉
            purchaseManager.consumeAsync(purchaseData, new ConsumeListener() {
                @Override
                public void onConsumeResponse(IapResult iapResult, PurchaseData purchaseData) {
                    consume_index = consume_index - 1;
                    if (consume_index == 0){
                        //全部已经消耗结束
                        onePayInActivity(mActivity);
                    }
                }
            });

        }

    }

    @Override
    public void onPurchaseFailed(@NonNull IapResult iapResult) {
        PL.i("onPurchaseFailed");
        purchaseManager.handleIapResultError(iapResult);//处理iapResult
    }

    @Override
    public void onNeedLogin() {
        PL.i("onNeedLogin");
        if (authManager != null && mActivity != null && this.createOrderIdReqBean != null){

            authManager.launchSignInFlow(new OnAuthListener() {
                @Override
                public void onResponse(SignInResult signInResult) {
                    if (signInResult.isSuccessful()) {
                        PL.i("login success");
                    } else {
//                    _errorAuthResult.postValue(signInResult)
                        PL.i("onNeedLogin onResponse:" + signInResult.getMessage());
                    }
                    ToastUtils.toast(mActivity,"Please start buying again");
                }
            });

            if (loadingDialog != null) {
                loadingDialog.dismissProgressDialog();
            }
        }
    }

    @Override
    public void onNeedUpdate() {
        PL.i("onNeedUpdate");
        if (purchaseManager != null && mActivity != null){
            //完成更新/安装后，您需要应用适合开发人员的操作。
            purchaseManager.launchUpdateOrInstall(mActivity, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    ToastUtils.toast(mActivity,"Please start buying again");
                    return null;
                }
            });

            if (loadingDialog != null) {
                loadingDialog.dismissProgressDialog();
            }
        }
    }

    @Override
    public void onError(@NonNull String message) {
        PL.i("onError:" + message);
        if (mActivity != null && this.createOrderIdReqBean != null){//此时用户正在购买
            callbackFail("" + message);

        }
    }
}

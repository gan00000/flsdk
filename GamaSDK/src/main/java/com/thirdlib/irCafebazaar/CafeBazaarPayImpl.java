package com.thirdlib.irCafebazaar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.thirdlib.ThirdModuleUtil;

import ir.cafebazaar.poolakey.entity.PurchaseInfo;

/**
 * Created by gan on 2017/2/23.
 */

public class CafeBazaarPayImpl implements IPay {

    public static final String TAG_USER_CANCEL = "TAG_PAY_USER_CANCEL";
    private PoolakeyPayManager poolakeyPayManager;
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

    private void callbackSuccess( GPExchangeRes gpExchangeRes) {

        PL.i("rustore pay onConsumeResponse callbackSuccess");
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    BasePayBean payBean = new BasePayBean();
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

        PL.i("the aar version info:" + SdkUtil.getSdkInnerVersion(activity) + "_" + BuildConfig.JAR_VERSION);//打印版本号

        if (!ThirdModuleUtil.existBazaarModule()){
            PL.w("BazaarModule is unavailable");
            return;
        }
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

        this.createOrderIdReqBean = (PayCreateOrderReqBean) payReqBean;
        this.createOrderIdReqBean.setMode(ChannelPlatform.BAZAAR.getChannel_platform());
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(ApiRequestMethod.API_ORDER_CREATE);

        if (this.createOrderIdReqBean.isInitOk()) {
            //开始储值,先查询有没有未消耗的商品
            loadingDialog.showProgressDialog();
            if (poolakeyPayManager != null){

                if (activity instanceof AppCompatActivity){
                    poolakeyPayManager = new PoolakeyPayManager((AppCompatActivity)activity);
                }else {
                    callbackFail("need activity AppCompatActivity");
                    return;
                }

            }
            onePayInActivity(activity);

        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
        //isPaying = false;
        PL.w("bazaar set not paying");
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
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        this.mActivity = activity;

    }

    public void queryPreRegData(final Activity activity, ISdkCallBack iSdkCallBack) {

    }

    @Override
    public void onCreate(Activity activity) {
        PL.i( "onCreate");
        this.mActivity = activity;

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

        if (poolakeyPayManager != null){
            poolakeyPayManager.disconnect(activity);
            poolakeyPayManager = null;
        }
        dimissDialog();
    }


    /**
     * 查询消耗后开始购买
     */
    private void onePayInActivity(Activity activity) {

        PayApi.requestCreateOrder(activity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
            @Override
            public void success(GPCreateOrderIdRes createOrderIdRes, String msg1) {
                PL.i("requestCreateOrder finish success");

                if (createOrderIdRes == null || createOrderIdRes.getPayData() == null || SStringUtil.isEmpty(createOrderIdRes.getPayData().getOrderId())){
                    callbackFail("create orderId error");
                }else {

                    String devPayloadStr = createOrderIdRes.getPayData().getOrderId();//URLEncoder.encode(devPayload.toString());
                    String dynamicPriceToken = createOrderIdReqBean.getUserId() + "_" + devPayloadStr;
                    poolakeyPayManager.startPurchase(activity, createOrderIdReqBean.getProductId(), devPayloadStr, dynamicPriceToken, new PPPayCallback() {
                        @Override
                        public void succeed(@NonNull PurchaseInfo mPurchaseInfo) {
                            PL.i("startPurchase succeed");
                            if (mPurchaseInfo != null){
                                onPurchaseSucceed(mPurchaseInfo);
                            }else {
                                onError("");
                            }
                        }

                        @Override
                        public void fali(@NonNull String msg) {
                            onError(msg);
                        }

                        @Override
                        public void cancel(@NonNull String msg) {
                            onError(TAG_USER_CANCEL);
                        }
                    });
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

    public void onPurchaseSucceed(PurchaseInfo mPurchaseInfo) {
        PL.i("onPurchaseSucceed");
        if (mPurchaseInfo == null){
            callbackFail("ProductPurchaseResult is null");
            return;
        }
        String orderId = mPurchaseInfo.getOrderId();
        String mwOrderId = mPurchaseInfo.getPayload();
        String productId = mPurchaseInfo.getProductId();

        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
        exchangeReqBean.setDataSignature(mPurchaseInfo.getDataSignature());
        exchangeReqBean.setPurchaseData(mPurchaseInfo.getOriginalJson());
        exchangeReqBean.setGoogleOrderId(orderId);
        exchangeReqBean.setThirdPurchaseToken(mPurchaseInfo.getPurchaseToken());
        exchangeReqBean.setReissue("no");

        exchangeReqBean.setProductId(productId);
        exchangeReqBean.setOrderId(mwOrderId);//mw平台订单号
        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_BAZAAR);

        PayApi.requestCommonPaySendStone(mActivity, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
            @Override
            public void success(GPExchangeRes result, String msg) {
                PL.i("startQueryPurchase requestSendStone success => " + msg);
                //3.消费
                //VKPurchaseManger.getInstance().confirmPurchase(mActivity, purchaseId);
                callbackSuccess(result);
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


    public void onPurchaseFailed() {

    }

    public void onError(String message) {
        PL.i("onError:" + message);
        if (mActivity != null && this.createOrderIdReqBean != null){//此时用户正在购买
            callbackFail("" + message);
        }
    }

    //=================================================================================================

}

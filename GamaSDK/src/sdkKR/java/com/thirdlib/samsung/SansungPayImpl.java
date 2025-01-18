package com.thirdlib.samsung;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gaa.sdk.iap.ConsumeListener;
import com.gaa.sdk.iap.IapResult;
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
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.SdkUtil;
import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.IapHelper;
import com.samsung.android.sdk.iap.lib.listener.OnConsumePurchasedItemsListener;
import com.samsung.android.sdk.iap.lib.listener.OnGetOwnedListListener;
import com.samsung.android.sdk.iap.lib.listener.OnGetProductsDetailsListener;
import com.samsung.android.sdk.iap.lib.listener.OnPaymentListener;
import com.samsung.android.sdk.iap.lib.vo.ConsumeVo;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;
import com.samsung.android.sdk.iap.lib.vo.OwnedProductVo;
import com.samsung.android.sdk.iap.lib.vo.ProductVo;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gan on 2017/2/23.
 */

public class SansungPayImpl implements IPay{

    public static final String TAG_USER_CANCEL = "TAG_PAY_USER_CANCEL";

    private IapHelper  mIapHelper = null;
    private LoadingDialog loadingDialog;

    /**
     * 当次的商品详情
     */
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

    private void callbackSuccess(PurchaseVo _purchaseVo, GPExchangeRes gpExchangeRes) {

        PL.i("onestore pay onConsumeResponse callbackSuccess");
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    BasePayBean payBean = new BasePayBean();
                    if (_purchaseVo != null) {
                        try {
                            JSONObject devPayload = new JSONObject(_purchaseVo.getPassThroughParam());
//                            String userId = devPayload.optString("userId","");
//                            String roleId = devPayload.optString("roleId","");
                            String orderId = devPayload.optString("orderId","");
                            payBean.setOrderId(orderId);
                            payBean.setTransactionId(_purchaseVo.getOrderId());
                            payBean.setPackageName(mContext.getPackageName());
                            payBean.setUsdPrice(skuAmount);
                            if (createOrderIdReqBean != null) {
                                payBean.setProductId(createOrderIdReqBean.getProductId());
                            }
//                    payBean.setmItemType(purchase.getItemType());
                            payBean.setOriginPurchaseData(_purchaseVo.getJsonString());
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

        PL.w("google set paying...");
        isPaying = true;

        this.mActivity = activity;
        mContext = activity.getApplicationContext();

        if (mIapHelper == null){
            mIapHelper = IapHelper.getInstance( activity.getApplicationContext() );
            if (BuildConfig.DEBUG) {
                mIapHelper.setOperationMode(HelperDefine.OperationMode.OPERATION_MODE_TEST);
            }else {
                mIapHelper.setOperationMode(HelperDefine.OperationMode.OPERATION_MODE_PRODUCTION);
            }
        }

        this.createOrderIdReqBean = (PayCreateOrderReqBean) payReqBean;
        this.createOrderIdReqBean.setMode(ChannelPlatform.SAMSUNG.getChannel_platform());
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(ApiRequestMethod.API_ORDER_CREATE);

        if (this.createOrderIdReqBean.isInitOk()) {
            //开始储值,先查询有没有未消耗的商品
            onePayInActivity(activity);

        } else {
            ToastUtils.toast(activity, "please log in to the game first");
            callbackFail("can not find role info:" + this.createOrderIdReqBean.print());
        }
        //isPaying = false;
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

        if (mIapHelper == null || mContext == null){
            return;
        }
        PL.i("startQueryPurchase onQueryPurchasesResponse");
        this.mContext = mContext.getApplicationContext();
//        mIapHelper.getOwnedList(IapHelper.PRODUCT_TYPE_ITEM, new OnGetOwnedListListener() {
//            @Override
//            public void onGetOwnedProducts(ErrorVo errorVo, ArrayList<OwnedProductVo> arrayList) {
//
//            }
//        });

    }

    public void queryPreRegData(final Context mContext, ISdkCallBack iSdkCallBack) {

    }

    @Override
    public void onCreate(Activity activity) {
        PL.i( "onCreate");
        mContext = activity.getApplicationContext();
        if (mIapHelper == null){
            mIapHelper = IapHelper.getInstance( activity.getApplicationContext() );
            if (BuildConfig.DEBUG) {
                mIapHelper.setOperationMode(HelperDefine.OperationMode.OPERATION_MODE_TEST);
            }else {
                mIapHelper.setOperationMode(HelperDefine.OperationMode.OPERATION_MODE_PRODUCTION);
            }
        }

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
        if (mIapHelper != null) {
            mIapHelper.dispose();
            mIapHelper = null;
        }
    }


    /**
     * 查询消耗后开始购买
     */
    private void onePayInActivity(Activity activity) {

        loadingDialog.showProgressDialog();

        List productList = new ArrayList<>();
        productList.add(this.createOrderIdReqBean.getProductId());

        mIapHelper.getOwnedList(IapHelper.PRODUCT_TYPE_ITEM, new OnGetOwnedListListener() {
            @Override
            public void onGetOwnedProducts(ErrorVo _errorVo, ArrayList<OwnedProductVo> _ownedList) {
                PL.i("onGetOwnedProducts result");
                if( _errorVo != null) {
                    if (_errorVo.getErrorCode() == IapHelper.IAP_ERROR_NONE) {

                        String  mConsumablePurchaseIDs = "";

                        if (_ownedList != null) {
                            for (int i = 0; i < _ownedList.size(); i++) {
                                OwnedProductVo product = _ownedList.get(i);
                                PL.i(product.dump());
                                if (product.getIsConsumable()) {
                                    try {
                                        //无论如何查到都消耗掉，也补发
                                        mIapHelper.consumePurchasedItems(product.getPurchaseId(), new OnConsumePurchasedItemsListener() {
                                            @Override
                                            public void onConsumePurchasedItems(ErrorVo errorVo, ArrayList<ConsumeVo> arrayList) {
                                                PL.i(  "onGetOwnedProducts > onConsumePurchasedItems finish");
                                            }
                                        });

                                        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(activity);
//                                        exchangeReqBean.setDataSignature();
                                        exchangeReqBean.setPurchaseData(product.getJsonString());
                                        exchangeReqBean.setReissue("yes");
                                        exchangeReqBean.setThirdPurchaseToken(product.getPurchaseId());
                                        exchangeReqBean.setGoogleOrderId(product.getPaymentId());
                                        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_SAMSUNG);
                                        try {
                                            JSONObject payLoadJsonObject = new JSONObject(product.getPassThroughParam());
                                            String userId = payLoadJsonObject.optString("userId");
                                            String orderId = payLoadJsonObject.optString("orderId");
                                            String roleId = payLoadJsonObject.optString("roleId");
                                            exchangeReqBean.setUserId(userId);
                                            exchangeReqBean.setOrderId(orderId);
                                            exchangeReqBean.setRoleId(roleId);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        PayApi.requestCommonPaySendStone(activity, exchangeReqBean, new SFCallBack<GPExchangeRes>() {
                                            @Override
                                            public void success(GPExchangeRes result, String msg) {
                                                PL.i("onGetOwnedProducts requestSendStone success => " + msg);
                                                //3.消费
//                                                mIapHelper.consumePurchasedItems(product.getPurchaseId(), new OnConsumePurchasedItemsListener() {
//                                                    @Override
//                                                    public void onConsumePurchasedItems(ErrorVo errorVo, ArrayList<ConsumeVo> arrayList) {
//                                                        PL.i(  "onPayment > onConsumePurchasedItems finish");
//                                                    }
//                                                });
                                            }

                                            @Override
                                            public void fail(GPExchangeRes result, String msg) {
                                                PL.i("onGetOwnedProducts requestSendStone fail => " + msg);
                                            }
                                        });

                                    } catch (Exception e) {
                                        PL.e( "exception" + e);
                                    }
                                }

                            }
                        }

                    }
                    else
                    {
                        PL.i("onGetOwnedProducts ErrorCode [" + _errorVo.getErrorCode() +"]");
                        if(_errorVo.getErrorString()!=null){
                            PL.i("onGetOwnedProducts ErrorString[" + _errorVo.getErrorString() + "]");
                        }
                    }
                }

                //start pay
                createrOrderAndStartPurchaseProduct(createOrderIdReqBean.getProductId());
            }
        });

    }

    //=================================================================================================
    //=================================================================================================
    //=================================================================================================

    protected void createrOrderAndStartPurchaseProduct(String itemId) {
        PayApi.requestCreateOrder(this.mActivity, createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
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

                //dimissDialog();

                mIapHelper.startPayment(itemId, devPayload.toString(), new OnPaymentListener() {
                    @Override
                    public void onPayment(ErrorVo _errorVo, PurchaseVo _purchaseVo) {

                        if (_errorVo != null) {
                            if (_errorVo.getErrorCode() == IapHelper.IAP_ERROR_NONE) {
                                if (_purchaseVo != null) {//成功
                                    // ====================================================================

                                    PL.i(  _purchaseVo.dump());
                                    if (_purchaseVo.getIsConsumable()) {
                                        loadingDialog.showProgressDialog();
                                        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(mActivity);
//                                        exchangeReqBean.setDataSignature();
                                        exchangeReqBean.setPurchaseData(_purchaseVo.getJsonString());
                                        exchangeReqBean.setReissue("no");

                                        exchangeReqBean.setThirdPurchaseToken(_purchaseVo.getPurchaseId());
                                        exchangeReqBean.setGoogleOrderId(_purchaseVo.getPaymentId());
                                        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_SAMSUNG);
                                        try {
                                            JSONObject payLoadJsonObject = new JSONObject(_purchaseVo.getPassThroughParam());
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
                                                mIapHelper.consumePurchasedItems(_purchaseVo.getPurchaseId(), new OnConsumePurchasedItemsListener() {
                                                    @Override
                                                    public void onConsumePurchasedItems(ErrorVo errorVo, ArrayList<ConsumeVo> arrayList) {
                                                        PL.i(  "onPayment > onConsumePurchasedItems finish");
                                                    }
                                                });
                                                callbackSuccess(_purchaseVo, result);
                                            }

                                            @Override
                                            public void fail(GPExchangeRes result, String msg) {
                                                PL.i("startQueryPurchase requestSendStone fail => " + msg);
                                                callbackFail("onPayment > " + msg);
                                            }
                                        });

                                    }else {
                                        PL.e(  "onPayment > getIsConsumable: false");
                                        callbackFail("onPayment > getIsConsumable: false");
                                    }

                                } else{
                                    PL.e(  "onPayment > _purchaseVo: null");
                                    callbackFail("onPayment > _errorVo = null");
                                }
                            } else {//错误

                                String errMsg = "onPayment > ErrorCode [" + _errorVo.getErrorCode() + "]" ;
                                PL.e( errMsg);

                                if (_errorVo.getErrorString() != null) {
                                    errMsg = "onPayment > ErrorString[" + _errorVo.getErrorString() + "]";
                                    PL.e( errMsg);
                                }
                                callbackFail(errMsg);
                                // In case of network error from GalaxyStore 4.5.20.7 version and IAP SDK 6.1 version,
                                // IAP error popup is not displayed.
                                // As needed, the app can display network error to users.
//                            if (_errorVo.getErrorCode() == HelperDefine.IAP_ERROR_NETWORK_NOT_AVAILABLE) {
//                                //错误提示
//                                loadingDialog.alert(_errorVo.getErrorString(), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//
//                                        if (iPayCallBack != null) {
//                                            iPayCallBack.fail(null);
//                                        }
//                                    }
//                                });
//                            }else {
//
//                            }
                            }
                        }else {
                            PL.i(  "onPayment > _errorVo = null");
                            callbackFail("onPayment > _errorVo = null");
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
}

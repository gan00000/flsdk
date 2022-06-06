package com.mw.sdk.pay.gp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.base.bean.BasePayBean;
import com.mw.base.constant.GamaCommonKey;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.gp.bean.req.GoogleExchangeReqBean;
import com.mw.sdk.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.mw.sdk.pay.gp.bean.req.PayReqBean;
import com.mw.sdk.pay.gp.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.pay.gp.bean.res.GPExchangeRes;
import com.mw.sdk.pay.gp.constants.GooglePayContant;
import com.mw.sdk.pay.gp.constants.GooglePayDomainSite;
import com.mw.sdk.pay.gp.task.GoogleCreateOrderReqTask;
import com.mw.sdk.pay.gp.task.GoogleExchangeReqTask;
import com.mw.sdk.pay.gp.task.LoadingDialog;
import com.mw.sdk.pay.gp.util.IabHelper;
import com.mw.sdk.pay.gp.util.IabResult;
import com.mw.sdk.pay.gp.util.Inventory;
import com.mw.sdk.pay.gp.util.PayHelper;
import com.mw.sdk.pay.gp.util.Purchase;
import com.mw.base.utils.GamaUtil;
import com.mw.base.utils.SLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by gan on 2017/2/23.
 */

public class GooglePayImpl implements IPay {
    private static final String TAG = GooglePayImpl.class.getSimpleName();

    // The helper object
    private IabHelper mHelper;

    private LoadingDialog loadingDialog;

    /**
     * 创单的请求参数Bean
     */
    private GooglePayCreateOrderIdReqBean createOrderIdReqBean;

    private Activity activity;

    private IPayCallBack iPayCallBack;
    /**
     * 防止连续快速点击储值出现未知异常
     */
    boolean isPaying = false;
    private double usdPrice = 0;


    private void callbackSuccess(Purchase purchase){

        if (loadingDialog != null){
            loadingDialog.dismissProgressDialog();
        }

        if (iPayCallBack != null){
            BasePayBean payBean = new BasePayBean();
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

            payBean.setUsdPrice(usdPrice);
            payBean.setCpOrderId(createOrderIdReqBean.getCpOrderId());
            Bundle b = new Bundle();
            b.putInt(PAY_STATUS, PAY_SUCCESS);
            b.putSerializable(GooglePayCreateOrderIdReqBean_Key, createOrderIdReqBean);
            b.putSerializable(GamaCommonKey.PURCHASE_DATA, payBean);
            iPayCallBack.success(b);
        }
    }

    private void callbackFail(String message){
        usdPrice = 0;
        if (loadingDialog != null){
            loadingDialog.dismissProgressDialog();
        }
        final Bundle b = new Bundle();
        b.putInt(PAY_STATUS, PAY_FAIL);
        if (SStringUtil.isNotEmpty(message)){//提示错误信息

            loadingDialog.alert(message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    if (iPayCallBack != null){
                        iPayCallBack.fail(b);
                    }
                }
            });

        }else{//用户取消

            if (iPayCallBack != null){
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

        if (activity == null) {
            PL.w("activity is null");
            return;
        }
        this.activity = activity;
        if (payReqBean == null) {
            PL.w("payReqBean is null");
            return;
        }

        if (isPaying){
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
        if (GamaUtil.isInterfaceSurfixWithApp(activity)) {
            this.createOrderIdReqBean.setRequestMethod(GooglePayDomainSite.APP_SURFIX_GOOGLE_ORDER_CREATE);
        } else {
            this.createOrderIdReqBean.setRequestMethod(GooglePayDomainSite.GOOGLE_ORDER_CREATE);
        }
        //创建Loading窗
        loadingDialog = new LoadingDialog(activity);

        if (mHelper == null) {
            mHelper = new IabHelper(activity);
        }else if (mHelper.isAsyncInProgress()){
            onDestroy(activity);
            mHelper = new IabHelper(activity);
        }

        if (this.createOrderIdReqBean.isInitOk()){
            //开始Google储值
            googlePaySetUp();
        }else{
            ToastUtils.toast(activity,"please log in to the game first");
            callbackFail("please log in to the game first");
        }
        isPaying = false;
        PL.w("google set not paying");
    }

    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        handlerActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {
        if (mHelper != null) {
            SLog.logI("mHelper.dispose");
            mHelper.dispose();
        }
        mHelper = null;

    }


    /**
     * <p>Title: googlePaySetUp</p> <p>Description: 启动远程服务</p>
     */
    private void googlePaySetUp() {

        if (null == mHelper) {
            return;
        }
        loadingDialog.showProgressDialog();
        if (!mHelper.isSetupDone()) {
            PL.i(TAG, "初始化iabHelper开始");
            //进行Google支付的初始化，只能进行一次，如果已经初始化则不用再次初始化
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

                public void onIabSetupFinished(IabResult result) {
                    SLog.logI("startSetup onIabSetupFinished.");//No account found
                    if (!result.isSuccess()) {
                        ToastUtils.toast(activity.getApplicationContext(),"Your phone or Google account does not support In-app Billing");
                        callbackFail(result.getMessage());
                        return;
                    }
                    //开始从Google商店查询所有商品状态
                    mHelper.queryInventoryAsync(queryInventoryFinishedListener);

                }

            });
        } else {
            PL.i(TAG, "已经初始化iabHelper");
            //开始从Google商店查询所有商品状态
            mHelper.queryInventoryAsync(queryInventoryFinishedListener);

        }
    }


    private void handlerActivityResult(int requestCode, int resultCode, Intent data) {
//        loadingDialog.dismissProgressDialog();
        if (mHelper == null) {
            return;
        }
        if (mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            SLog.logI("onActivityResult handled by IABUtil. the result was related to a purchase flow and was handled");
        } else {
            SLog.logI("onActivityResult handled by IABUtil.the result was not related to a purchase");
        }
    }

    /**
     * <p>Title: startPurchase</p> <p>Description: 开始购买商品（储值）</p>
     */
    protected synchronized void startPurchase() {

        GoogleCreateOrderReqTask googleCreateOrderReqTask = new GoogleCreateOrderReqTask(createOrderIdReqBean);
        googleCreateOrderReqTask.setReqCallBack(new ISReqCallBack<GPCreateOrderIdRes>() {

            @Override
            public void success(GPCreateOrderIdRes createOrderIdRes, String rawResult) {
                if (createOrderIdRes != null && createOrderIdRes.isRequestSuccess() && !TextUtils.isEmpty(createOrderIdRes.getOrderId())) {
                   /* new SRequestAsyncTask(){

                        @Override
                        protected String doInBackground(String... params) {
                            mHelper.mQuerySkuDetails(createOrderIdReqBean.getProductId(), new IabHelper.QueryInventoryFinishedListener(){

                                @Override
                                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                    inv.getSkuDetails(createOrderIdReqBean.getProductId());
                                }
                            });
                            return null;
                        }
                    }.asyncExcute();*/
                    usdPrice = createOrderIdRes.getUsdPrice();
                    launchPurchase(createOrderIdRes);
                } else {
                    if (createOrderIdRes!=null && !TextUtils.isEmpty(createOrderIdRes.getMessage())){
                        ToastUtils.toast(activity,createOrderIdRes.getMessage());
                    }
                    callbackFail("create orderId error, please try again");
                }
            }

            @Override
            public void timeout(String code) {

//                ToastUtils.toast(activity, "connect timeout, please try again");
                callbackFail("connect timeout, please try again");
            }

            @Override
            public void noData() {
                callbackFail("server error, please try again");
            }

            @Override
            public void cancel() {

            }
        });
        googleCreateOrderReqTask.excute(GPCreateOrderIdRes.class);

    }


    private void launchPurchase(GPCreateOrderIdRes createOrderIdRes) {

        JSONObject mjson = new JSONObject();
        try {
            mjson.put("orderId", createOrderIdRes.getOrderId());
            mjson.put("paygpId", createOrderIdRes.getPaygpId());

            mjson.put("cpOrderId", createOrderIdReqBean.getCpOrderId());
            mjson.put("userId", createOrderIdReqBean.getUserId());
            mjson.put("gameCode", createOrderIdReqBean.getGameCode());
//			mjson.put("productId", createOrderIdReqBean.getProductId());
            mjson.put("serverCode", createOrderIdReqBean.getServerCode());
            mjson.put("roleId", createOrderIdReqBean.getRoleId());

        } catch (JSONException e) {
            SLog.logI("JSONException异常");
            e.printStackTrace();
        }
        String developerPayload = mjson.toString();
        if (developerPayload.length() > 256) {
            PL.i("developerPayload.length() > 256");
        }

        SLog.logI("developerPayload: " + developerPayload + " developerPayload length:" + developerPayload.length());
        SLog.logI("开始google购买流程launchPurchaseFlow");
        //developerPayload: optional argument to be sent back with the purchase information,最大256 characters.否则报错code:"IAB-DPTL"
        mHelper.launchPurchaseFlow(activity, createOrderIdReqBean.getProductId(), GooglePayContant.RC_REQUEST,
                new IabHelper.OnIabPurchaseFinishedListener() {
                    public void onIabPurchaseFinished(final IabResult result, final Purchase purchase) {

                        SLog.logI("onIabPurchaseFinished");

                        if (purchase == null || result.isFailure()) {
                            SLog.logI("purchase is null.");

                            //用户取消支付
                            if (result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED) {
                                PL.i("info: " + result.getMessage());
                                callbackFail("");//用户去掉不提示
                                return;
                            }
                            //进行定时查单
                            GooglePayHelper.getInstance().startQueryTask(activity);
                            callbackFail(result.getMessage());
//                            loadingDialog.complain(result.getMessage());
//                            ToastUtils.toast();
                        } else {
                            // TODO: 2018/7/6 测试上报代码
//                            Purchase p = null;
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("orderId", "orderId");
//                                jsonObject.put("packageName", "packageName");
//                                jsonObject.put("productId", "productId");
//                                jsonObject.put("purchaseTime", "purchaseTime");
//                                jsonObject.put("purchaseState", "purchaseState");
//                                jsonObject.put("developerPayload", "developerPayload");
//                                jsonObject.put("token", "token");
//                                jsonObject.put("", "");
//                                p = new Purchase("itemType", jsonObject.toString(), "signature");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            callbackSuccess(p);

                            GoogleExchangeReqBean exchangeReqBean = new GoogleExchangeReqBean(activity);
                            exchangeReqBean.setDataSignature(purchase.getSignature());
                            exchangeReqBean.setPurchaseData(purchase.getOriginalJson());

                            exchangeReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
                            exchangeReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
                            if(GamaUtil.isInterfaceSurfixWithApp(activity)) {
                                exchangeReqBean.setRequestMethod(GooglePayDomainSite.APP_SURFIX_GOOGLE_SEND);
                            } else {
                                exchangeReqBean.setRequestMethod(GooglePayDomainSite.GOOGLE_SEND);
                            }

                            /*SkuDetails skuDetails = activity.getSkuDetails();
                            if (skuDetails != null) {
                                exchangeReqBean.setPriceCurrencyCode(skuDetails.getPrice_currency_code());
                                exchangeReqBean.setPriceAmountMicros(skuDetails.getPrice_amount_micros());
                                exchangeReqBean.setProductPrice(skuDetails.getPrice());
                            }*/

                            GoogleExchangeReqTask googleExchangeReqTask = new GoogleExchangeReqTask(activity, exchangeReqBean);
                            googleExchangeReqTask.setReqCallBack(new ISReqCallBack<GPExchangeRes>() {

                                @Override
                                public void success(GPExchangeRes gpExchangeRes, String rawResult) {
                                    PL.i("exchange callback");
                                    // 消费
                                    if (mHelper != null && gpExchangeRes != null && gpExchangeRes.isRequestSuccess()) {
                                        PL.i("google pay consumeAsync");
                                        mHelper.consumeAsync(purchase, mlaunchPurchaseConsumeFinishedListener);
                                    } else {
                                        if (gpExchangeRes!=null && !TextUtils.isEmpty(gpExchangeRes.getMessage())){
                                            ToastUtils.toast(activity,gpExchangeRes.getMessage());
                                        }
                                        callbackFail("error, please contact customer service");
                                    }
                                }

                                @Override
                                public void timeout(String code) {
//                                    ToastUtils.toast(activity, "connect timeout, please try again");
                                    callbackFail("connect timeout, please try again");
                                }

                                @Override
                                public void noData() {
                                    callbackFail("server error, please try again");
                                }

                                @Override
                                public void cancel() {

                                }
                            });
                            googleExchangeReqTask.excute(GPExchangeRes.class);

                        }
                    }
                }, developerPayload);

    }

    /**
     * 购买的时候消费
     */
    private IabHelper.OnConsumeFinishedListener mlaunchPurchaseConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                SLog.logI("消费成功");
            } else {
                SLog.logI("消费失败");
            }
            callbackSuccess(purchase);
        }
    };

    /**
     * 查询商品状态的回调
     */
    private IabHelper.QueryInventoryFinishedListener queryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {

            handleQueryResult(result, inv);
//            startPurchase();//查询结束开始购买

        }
    };

    /**
     * 查询商品状态结果处理
     */
    private void handleQueryResult(IabResult result, Inventory inventory) {

        if (result.isFailure()) {
           // callbackFail();
            PL.i("query result:" + result.getMessage());
            SLog.logD("getQueryInventoryState is null");
        } else {

            SLog.logD("Query inventory was successful.");
            List<Purchase> purchaseList = inventory.getAllPurchases();

            if (null == purchaseList || purchaseList.isEmpty()) {
                //没有未消费的商品
              //  callbackFail();
                SLog.logD("purchases is empty");

            } else {

                SLog.logD("purchases size: " + purchaseList.size());
                for (Purchase mPurchase : purchaseList) {
                    SLog.logI("purchases sku: " + mPurchase.getSku());
                    if (mPurchase.getPurchaseState() == 2) {//退款订单
                        PL.i("refunded:属于退款订单");
                    } else {
                        //补发
                        replacement(mPurchase);
                    }

                }

                if (purchaseList.size() == 1) {
                    SLog.logD("mConsumeFinishedListener. 消费一个");
                    mHelper.consumeAsync(purchaseList.get(0), mConsumeFinishedListener);
                } else if (purchaseList.size() > 1) {
                    SLog.logD("mConsumeMultiFinishedListener.消费多个");
                    mHelper.consumeAsync(purchaseList, mConsumeMultiFinishedListener);
                }
                return;
            }
        }
        startPurchase();
    }

    /**
     * 补单
     */
    private void replacement(final Purchase mPurchase) {
        GoogleExchangeReqBean exchangeReqBean = new GoogleExchangeReqBean(activity);
        exchangeReqBean.setDataSignature(mPurchase.getSignature());
        exchangeReqBean.setPurchaseData(mPurchase.getOriginalJson());

        exchangeReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        exchangeReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));
        if(GamaUtil.isInterfaceSurfixWithApp(activity)) {
            exchangeReqBean.setRequestMethod(GooglePayDomainSite.APP_SURFIX_GOOGLE_SEND);
        } else {
            exchangeReqBean.setRequestMethod(GooglePayDomainSite.GOOGLE_SEND);
        }

        GoogleExchangeReqTask googleExchangeReqTask = new GoogleExchangeReqTask(activity, exchangeReqBean);
        googleExchangeReqTask.setReqCallBack(new ISReqCallBack<GPExchangeRes>() {

            @Override
            public void success(GPExchangeRes gpExchangeRes, String rawResult) {
                PL.i("exchange callback");
                try {//补发上报
                    Intent intent = new Intent(GooglePayHelper.ACTION_PAY_REPLACE_OK);
                    intent.putExtra(GooglePayContant.PURCHASE_OBJECT, mPurchase);
                    activity.sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }

            @Override
            public void cancel() {

            }
        });
        googleExchangeReqTask.excute(GPExchangeRes.class);
    }


    /**
     * 查询时 多个未消费
     */
    private IabHelper.OnConsumeMultiFinishedListener mConsumeMultiFinishedListener = new IabHelper.OnConsumeMultiFinishedListener() {

        @Override
        public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
            SLog.logD("Consume Multiple finished.");
            for (int i = 0; i < purchases.size(); i++) {
                if (results.get(i).isSuccess()) {
                    SLog.logD("sku: " + purchases.get(i).getSku() + " Consume finished success");
                } else {
                    SLog.logD("sku: " + purchases.get(i).getSku() + " Consume finished fail");
                    SLog.logD(purchases.get(i).getSku() + "consumption is not success, yet to be consumed.");
                }
            }
            SLog.logD("End consumption flow.");
            startPurchase();
        }
    };

    /**
     * 查询时 只有一个未消费
     */
    private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            SLog.logD("Consumption finished");
            if (null != purchase) {
                SLog.logD("Purchase: " + purchase.toString() + ", result: " + result);
            } else {
                SLog.logD("Purchase is null");
            }
            if (result.isSuccess()) {
                SLog.logD("Consumption successful.");
                if (purchase != null) {
                    SLog.logD("sku: " + purchase.getSku() + " Consume finished success");
                }
            } else {
                SLog.logD("consumption is not success, yet to be consumed.");
            }
            SLog.logD("End consumption flow.");

            startPurchase();
        }
    };


}

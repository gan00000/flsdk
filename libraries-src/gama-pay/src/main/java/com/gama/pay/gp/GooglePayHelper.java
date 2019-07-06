package com.gama.pay.gp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.gama.base.bean.BasePayBean;
import com.gama.base.constant.GamaCommonKey;
import com.gama.base.utils.SLog;
import com.gama.pay.gp.bean.req.GoogleExchangeReqBean;
import com.gama.pay.gp.bean.res.GPExchangeRes;
import com.gama.pay.gp.constants.GooglePayContant;
import com.gama.pay.gp.constants.GooglePayDomainSite;
import com.gama.pay.gp.task.GoogleExchangeReqTask;
import com.gama.pay.gp.util.IabHelper;
import com.gama.pay.gp.util.IabResult;
import com.gama.pay.gp.util.Inventory;
import com.gama.pay.gp.util.PayHelper;
import com.gama.pay.gp.util.Purchase;
import com.gama.pay.gp.util.SkuDetails;
import com.gama.pay.utils.GamaQueryProductListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GooglePayHelper {
    private static final String TAG = GooglePayHelper.class.getSimpleName();
    private static Handler mHandler;
    private GoogleQueryTask queryTask;
    private static GooglePayHelper payHelper;
    private IabHelper iabHelper;
    private boolean isForeground = true;
    private boolean isWorking = false;

    public static final String ACTION_PAY_REPLACE_OK = "com.gamamobi.PAY_REPLACE_OK";
    public static final String ACTION_PAY_QUERY_TASK_START = "com.gamamobi.PAY_QUERY_TASK_START";

    public static GooglePayHelper getInstance() {
        if (payHelper == null) {
            payHelper = new GooglePayHelper();
        }
        return payHelper;
    }

    private GooglePayHelper() {
    }

    /**
     * 查询单次未消费商品
     */
    public void queryConsumablePurchase(final Context context) {
        if(isWorking) {
            PL.i(TAG, "iab is working,ignore this request");
            return;
        }
        lockWorking("queryConsumablePurchase");
        if (iabHelper == null) {
            iabHelper = new IabHelper(context.getApplicationContext());
        }
        if (!iabHelper.isSetupDone()) {
            iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    PL.i(TAG, "startSetup onIabSetupFinished.");
                    if (!result.isSuccess()) {
                        PL.i(TAG, "Your phone or Google account does not support In-app Billing");
                        return;
                    }
                    //开始从Google商店查询所有商品状态
                    iabHelper.queryInventoryAsync(new MyQueryInventoryFinishedListener(context, iabHelper));
                }
            });
        } else {
            PL.e("iabhelper null !!!!!!");
        }
    }

    /**
     * 定时查询未消费商品
     */
    public void startQueryTask(Context context) {
        Intent intent = new Intent(ACTION_PAY_QUERY_TASK_START);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
        if (queryTask == null) {
            queryTask = new GoogleQueryTask(context);
        }
        if (mHandler != null) {
            SLog.logD(TAG, "Reset query task.");
            mHandler.removeCallbacksAndMessages(null);
        } else {
            mHandler = new Handler();
        }
        mHandler.post(queryTask);
    }

    /**
     * 停止定时查单
     */
    public void stopQueryTask() {
        PL.i(TAG, "stop query task!");
        recycleIab();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }

    /**
     * 查询商品状态的回调
     */
    private class MyQueryInventoryFinishedListener implements IabHelper.QueryInventoryFinishedListener {
        private Context mContext;
        private IabHelper mIabHelper;

        MyQueryInventoryFinishedListener(Context context, IabHelper iabHelper) {
            this.mContext = context;
            this.mIabHelper = iabHelper;
        }

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                PL.i(TAG, "query result:" + result.getMessage());
                SLog.logD(TAG, "getQueryInventoryState is null");
                recycleIab();
            } else {
                SLog.logD(TAG, "Query inventory was successful.");
                List<Purchase> purchaseList = inventory.getAllPurchases();
                if (null == purchaseList || purchaseList.isEmpty()) {
                    SLog.logD(TAG, "purchases is empty");
                    recycleIab();
                } else {
                    SLog.logD(TAG, "purchases size: " + purchaseList.size());
                    for (Purchase mPurchase : purchaseList) {
                        SLog.logI(TAG, "purchases sku: " + mPurchase.getSku());
                        if (mPurchase.getPurchaseState() == 2) {//退款订单
                            PL.i(TAG, "refunded:属于退款订单");
                        } else {
                            //补发
                            replacement(mContext, mPurchase);
                        }
                    }
                    if (purchaseList.size() == 1) {
                        SLog.logD(TAG, "mConsumeFinishedListener. 消费一个");
                        if (mIabHelper != null) {
                            mIabHelper.consumeAsync(purchaseList.get(0), new MyConsumeFinishedListener(mIabHelper));
                        }
                    } else if (purchaseList.size() > 1) {
                        SLog.logD(TAG, "mConsumeMultiFinishedListener.消费多个");
                        if (mIabHelper != null) {
                            mIabHelper.consumeAsync(purchaseList, new MyConsumeMultiFinishedListener(mIabHelper));
                        }
                    }
                }
            }
        }
    }

    /**
     * 补单
     */
    private void replacement(final Context context, final Purchase purchase) {
        GoogleExchangeReqBean exchangeReqBean = new GoogleExchangeReqBean(context);
        exchangeReqBean.setDataSignature(purchase.getSignature());
        exchangeReqBean.setPurchaseData(purchase.getOriginalJson());
        exchangeReqBean.setRequestUrl(PayHelper.getPreferredUrl(context));
        exchangeReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(context));
        exchangeReqBean.setRequestMethod(GooglePayDomainSite.google_send);
        GoogleExchangeReqTask googleExchangeReqTask = new GoogleExchangeReqTask(context, exchangeReqBean);
        googleExchangeReqTask.setReqCallBack(new ISReqCallBack<GPExchangeRes>() {

            @Override
            public void success(GPExchangeRes gpExchangeRes, String rawResult) {
                PL.i(TAG, "exchange callback");
                try {//补发上报
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
                    Intent intent = new Intent(ACTION_PAY_REPLACE_OK);
                    intent.putExtra(GamaCommonKey.PURCHASE_DATA, payBean);
                    intent.setPackage(context.getPackageName());
                    context.sendBroadcast(intent);
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
        });
        // TODO: 2018/7/5 测试支付上报时注释，不进行发币请求，正式版本必须去除注释
        googleExchangeReqTask.excute(GPExchangeRes.class);
    }

    /**
     * 查询时 多个未消费
     */
    private class MyConsumeMultiFinishedListener implements IabHelper.OnConsumeMultiFinishedListener {
        private IabHelper iabHelper;

        MyConsumeMultiFinishedListener(IabHelper iabHelper) {
            this.iabHelper = iabHelper;
        }

        @Override
        public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
            SLog.logD(TAG, "Consume Multiple finished.");
            for (int i = 0; i < purchases.size(); i++) {
                if (results.get(i).isSuccess()) {
                    SLog.logD(TAG, "sku: " + purchases.get(i).getSku() + " Consume finished success");
                } else {
                    SLog.logD(TAG, "sku: " + purchases.get(i).getSku() + " Consume finished fail");
                    SLog.logD(TAG, purchases.get(i).getSku() + "consumption is not success, yet to be consumed.");
                }
            }
            SLog.logD(TAG, "End consumption flow.");
            recycleIab();
        }
    }

    /**
     * 查询时 只有一个未消费
     */
    private class MyConsumeFinishedListener implements IabHelper.OnConsumeFinishedListener {
        private IabHelper iabHelper;

        MyConsumeFinishedListener(IabHelper iabHelper) {
            this.iabHelper = iabHelper;
        }

        public void onConsumeFinished(Purchase purchase, IabResult result) {
            SLog.logD(TAG, "Consumption finished");
            if (null != purchase) {
                SLog.logD(TAG, "Purchase: " + purchase.toString() + ", result: " + result);
            } else {
                SLog.logD(TAG, "Purchase is null");
            }
            if (result.isSuccess()) {
                SLog.logD(TAG, "Consumption successful.");
                if (purchase != null) {
                    SLog.logD(TAG, "sku: " + purchase.getSku() + " Consume finished success");
                }
            } else {
                SLog.logD(TAG, "consumption is not success, yet to be consumed.");
            }
            SLog.logD(TAG, "End consumption flow.");
            recycleIab();
        }
    }

    public void setForeground(boolean foreground) {
        isForeground = foreground;
    }

    /**
     * 回收IabHelper
     */
    private void recycleIab() {
        SLog.logD(TAG, "Start recycle IabHelper");
        if (iabHelper != null) {
            iabHelper.dispose();
        }
        iabHelper = null;
        isWorking = false;
    }

    private void lockWorking(String work) {
        if(isWorking) {
            PL.i(TAG, "iab is working!");
            return;
        }
        isWorking = true;
        PL.i(TAG, work + " begin.");
    }

    private class GoogleQueryTask implements Runnable {
        private Context mContext;

        GoogleQueryTask(Context context) {
            this.mContext = context;
        }

        @Override
        public void run() {
            Log.i(TAG, "Start timer query.");
            if (isForeground) {
                queryConsumablePurchase(mContext);
            } else {
                Log.i(TAG, "Application in background, stop query.");
            }
            mHandler.postDelayed(this, 60 * 1000 * 5);
        }
    }

    public void queryProductDetail(final Context context, final List<String> skus, final GamaQueryProductListener listener) {
        if(isWorking) {
            PL.i(TAG, "iab is working,ignore this request");
            return;
        }
        lockWorking("queryProductDetail");
        if (iabHelper == null) {
            iabHelper = new IabHelper(context.getApplicationContext());
        }
        if (!iabHelper.isSetupDone()) {
            iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    PL.i(TAG, "startSetup onIabSetupFinished.");
                    if (!result.isSuccess()) {
                        PL.i(TAG, "Your phone or Google account does not support In-app Billing");
                        if (listener != null) {
                            listener.onQueryResult(null);
                        }
                        return;
                    }

                    if (skus == null || skus.isEmpty()) {
                        PL.i(TAG, "query list is empty.");
                        if (listener != null) {
                            listener.onQueryResult(null);
                        }
                        return;
                    }

                    ArrayList<String> transList = new ArrayList<>();
                    String skusString = "";
                    for (String sku : skus) {
                        transList.add(sku);
                        skusString += sku + "\n";
                    }
                    PL.i(TAG, "query list is : " + skusString);

                    queryProductDetailBatch(context, transList, new HashMap<String, SkuDetails>(), new GamaQueryProductListener() {
                        @Override
                        public void onQueryResult(Map<String, SkuDetails> details) {
                            if (listener != null) {
                                listener.onQueryResult(details);
                            }
                            recycleIab();
                        }
                    });

                }
            });
        } else {
            recycleIab();
            if (listener != null) {
                listener.onQueryResult(null);
            }
        }
    }

    private void queryProductDetailBatch(final Context context, final List<String> skus, final Map<String, SkuDetails> allSkuDetail, final GamaQueryProductListener listener) {
        ArrayList<String> curSkus = new ArrayList<>(skus.subList(0, Math.min(15, skus.size())));//单次最多查询20个商品
        skus.removeAll(curSkus);
        if (iabHelper != null) {
            iabHelper.queryInventoryAsync(true, curSkus, new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    if (skus.size() > 0) {
                        if (listener != null && inventory != null) {
                            allSkuDetail.putAll(inventory.getAllSkuDetail());
                        }
                        queryProductDetailBatch(context, skus, allSkuDetail, listener);
                    } else {
                        if (listener != null) {
                            if(inventory != null) {
                                allSkuDetail.putAll(inventory.getAllSkuDetail());
                            }
                            listener.onQueryResult(allSkuDetail);
                        }
                    }
                }
            });

        } else {
            if (listener != null) {
                listener.onQueryResult(null);
            }
        }
    }

}

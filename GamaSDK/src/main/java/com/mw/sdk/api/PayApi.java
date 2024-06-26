package com.mw.sdk.api;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.core.base.callback.ISReqCallBack;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.api.task.GoogleCreateOrderReqTask;
import com.mw.sdk.api.task.GoogleExchangeReqTask;
import com.mw.sdk.bean.EventBean;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.req.PayExchangeReqBean;
import com.mw.sdk.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.bean.res.GPExchangeRes;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.utils.PayHelper;

public class PayApi {

    public static void requestCreateOrder(Activity activity, PayCreateOrderReqBean bean, SFCallBack<GPCreateOrderIdRes> sfCallBack) {

        GoogleCreateOrderReqTask googleCreateOrderReqTask = new GoogleCreateOrderReqTask(activity,bean);
        googleCreateOrderReqTask.setReqCallBack(new ISReqCallBack<GPCreateOrderIdRes>() {

            @Override
            public void success(GPCreateOrderIdRes createOrderIdRes, String rawResult) {
                if (createOrderIdRes != null && createOrderIdRes.isRequestSuccess() && createOrderIdRes.getPayData() != null && !TextUtils.isEmpty(createOrderIdRes.getPayData().getOrderId())) {
                    if (sfCallBack != null){
                        sfCallBack.success(createOrderIdRes,rawResult);
                    }
                } else {

                    if (sfCallBack != null){
                        sfCallBack.fail(createOrderIdRes,rawResult);
                    }
                }
            }

            @Override
            public void timeout(String code) {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void noData() {
            }

            @Override
            public void cancel() {

            }
        });
        googleCreateOrderReqTask.excute(GPCreateOrderIdRes.class);

    }

    /**
     * 发币
     */
    public static void requestSendStone(Context context,String reissue, SFCallBack<GPExchangeRes> sfCallBack) {

       /* PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(context);
        exchangeReqBean.setDataSignature(mPurchase.getSignature());
        exchangeReqBean.setPurchaseData(mPurchase.getOriginalJson());
        exchangeReqBean.setReissue(reissue);
        if (mPurchase.getAccountIdentifiers() != null) {
//            if (SStringUtil.isNotEmpty(mPurchase.getAccountIdentifiers().getObfuscatedAccountId())){
//                exchangeReqBean.setUserId(mPurchase.getAccountIdentifiers().getObfuscatedAccountId());
//            }
            if (SStringUtil.isNotEmpty(mPurchase.getAccountIdentifiers().getObfuscatedProfileId())){
                exchangeReqBean.setOrderId(mPurchase.getAccountIdentifiers().getObfuscatedProfileId());
            }
        }
        exchangeReqBean.setGoogleOrderId(mPurchase.getOrderId());

        exchangeReqBean.setRequestUrl(PayHelper.getPreferredUrl(context));
        exchangeReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(context));

        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_GOOGLE);

        GoogleExchangeReqTask googleExchangeReqTask = new GoogleExchangeReqTask(context, exchangeReqBean);
        googleExchangeReqTask.setReqCallBack(new ISReqCallBack<GPExchangeRes>() {

            @Override
            public void success(GPExchangeRes gpExchangeRes, String rawResult) {
                if (gpExchangeRes != null && gpExchangeRes.isRequestSuccess()) {
                    PL.i("google pay GoogleExchangeReqTask finish");
                    if (sfCallBack != null){
                        sfCallBack.success(gpExchangeRes,rawResult);
                    }
                    if (gpExchangeRes.getData() != null && SStringUtil.isNotEmpty(gpExchangeRes.getData().getOrderId())) {
//                        BasePayBean basePayBean = new BasePayBean();
//                        basePayBean.setOrderId(gpExchangeRes.getData().getOrderId());
//                        basePayBean.setProductId(gpExchangeRes.getData().getProductId());
//                        basePayBean.setServerTimestamp(gpExchangeRes.getData().getTimestamp());
//                        basePayBean.setUsdPrice(gpExchangeRes.getData().getAmount());

                        String orderId = gpExchangeRes.getData().getOrderId() == null ? "unknow" : gpExchangeRes.getData().getOrderId();
                        String productId = gpExchangeRes.getData().getProductId() == null ? "unknow" : gpExchangeRes.getData().getProductId();
                        double usdPrice = gpExchangeRes.getData().getAmount();
                        String serverTimestamp = gpExchangeRes.getData().getTimestamp();

                        SdkEventLogger.trackinPayEvent(context.getApplicationContext(), "", orderId, productId, usdPrice, serverTimestamp,true);
                        if (gpExchangeRes.getData().getEvents() != null && !gpExchangeRes.getData().getEvents().isEmpty()){
                            for (EventBean eventBean : gpExchangeRes.getData().getEvents()) {
                                if (SStringUtil.isNotEmpty(eventBean.getName()) && eventBean.getValue() > 0){
                                    SdkEventLogger.trackinPayEvent(context.getApplicationContext(), eventBean.getName(), orderId, productId, eventBean.getValue(), serverTimestamp,false);
                                }
                            }
                        }
                    }

                } else {
//                    if (gpExchangeRes!=null && !TextUtils.isEmpty(gpExchangeRes.getMessage())){
//                        ToastUtils.toast(activity,gpExchangeRes.getMessage());
//                    }
                    PL.i("GoogleExchangeReqTask failed");
                    if (sfCallBack != null){
                        sfCallBack.fail(gpExchangeRes,rawResult);
                    }
                }
            }

            @Override
            public void timeout(String code) {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void noData() {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void cancel() {
            }
        });
        googleExchangeReqTask.excute(GPExchangeRes.class);*/
    }


    public static void requestSendStone_huawei(Context context, String inAppPurchaseData, String inAppPurchaseDataSignature, String reissue, SFCallBack<GPExchangeRes> sfCallBack) {

        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(context);
        exchangeReqBean.setDataSignature(inAppPurchaseDataSignature);
        exchangeReqBean.setPurchaseData(inAppPurchaseData);
        exchangeReqBean.setReissue(reissue);

        exchangeReqBean.setRequestUrl(PayHelper.getPreferredUrl(context));
        exchangeReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(context));

        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_HW);

        GoogleExchangeReqTask googleExchangeReqTask = new GoogleExchangeReqTask(context, exchangeReqBean);
        googleExchangeReqTask.setReqCallBack(new ISReqCallBack<GPExchangeRes>() {

            @Override
            public void success(GPExchangeRes gpExchangeRes, String rawResult) {
                if (gpExchangeRes != null && gpExchangeRes.isRequestSuccess()) {
                    PL.i("huawei payment finish");
                    if (sfCallBack != null){
                        sfCallBack.success(gpExchangeRes,rawResult);
                    }
                } else {
//                    if (gpExchangeRes!=null && !TextUtils.isEmpty(gpExchangeRes.getMessage())){
//                        ToastUtils.toast(activity,gpExchangeRes.getMessage());
//                    }
                    PL.i("huawei payment failed");
                    if (sfCallBack != null){
                        sfCallBack.fail(gpExchangeRes,rawResult);
                    }
                }
            }

            @Override
            public void timeout(String code) {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void noData() {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void cancel() {
            }
        });
        googleExchangeReqTask.excute(GPExchangeRes.class);
    }


    public static void requestSendStone_qooapp(Context context, String inAppPurchaseData, String inAppPurchaseDataSignature, String algorithm, boolean reissue, SFCallBack<GPExchangeRes> sfCallBack) {

        PayExchangeReqBean exchangeReqBean = new PayExchangeReqBean(context);
        exchangeReqBean.setDataSignature(inAppPurchaseDataSignature);
        exchangeReqBean.setPurchaseData(inAppPurchaseData);
        exchangeReqBean.setAlgorithm(algorithm);
        if (reissue){
            exchangeReqBean.setReissue("yes");
        }else {
            exchangeReqBean.setReissue("no");
        }

        exchangeReqBean.setRequestUrl(PayHelper.getPreferredUrl(context));
        exchangeReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(context));

        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_QOOAPP);

        GoogleExchangeReqTask googleExchangeReqTask = new GoogleExchangeReqTask(context, exchangeReqBean);
        googleExchangeReqTask.setReqCallBack(new ISReqCallBack<GPExchangeRes>() {

            @Override
            public void success(GPExchangeRes gpExchangeRes, String rawResult) {
                if (gpExchangeRes != null && gpExchangeRes.isRequestSuccess()) {
                    PL.i("qooapp payment finish");
                    if (sfCallBack != null){
                        sfCallBack.success(gpExchangeRes,rawResult);
                    }
                } else {
//                    if (gpExchangeRes!=null && !TextUtils.isEmpty(gpExchangeRes.getMessage())){
//                        ToastUtils.toast(activity,gpExchangeRes.getMessage());
//                    }
                    PL.i("qooapp payment failed");
                    if (sfCallBack != null){
                        sfCallBack.fail(gpExchangeRes,rawResult);
                    }
                }
            }

            @Override
            public void timeout(String code) {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void noData() {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void cancel() {
            }
        });
        googleExchangeReqTask.excute(GPExchangeRes.class);
    }

    public static void requestCommonPaySendStone(Context context, PayExchangeReqBean exchangeReqBean, SFCallBack<GPExchangeRes> sfCallBack) {


        exchangeReqBean.setRequestUrl(PayHelper.getPreferredUrl(context));
        exchangeReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(context));

//        exchangeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_GOOGLE);

        GoogleExchangeReqTask googleExchangeReqTask = new GoogleExchangeReqTask(context, exchangeReqBean);
        googleExchangeReqTask.setReqCallBack(new ISReqCallBack<GPExchangeRes>() {

            @Override
            public void success(GPExchangeRes gpExchangeRes, String rawResult) {
                if (gpExchangeRes != null && gpExchangeRes.isRequestSuccess()) {
                    PL.i("pay GoogleExchangeReqTask finish");
                    if (sfCallBack != null){
                        sfCallBack.success(gpExchangeRes,rawResult);
                    }
                    if (gpExchangeRes.getData() != null && SStringUtil.isNotEmpty(gpExchangeRes.getData().getOrderId())) {
//                        BasePayBean basePayBean = new BasePayBean();
//                        basePayBean.setOrderId(gpExchangeRes.getData().getOrderId());
//                        basePayBean.setProductId(gpExchangeRes.getData().getProductId());
//                        basePayBean.setServerTimestamp(gpExchangeRes.getData().getTimestamp());
//                        basePayBean.setUsdPrice(gpExchangeRes.getData().getAmount());

                        String orderId = gpExchangeRes.getData().getOrderId() == null ? "unknow" : gpExchangeRes.getData().getOrderId();
                        String productId = gpExchangeRes.getData().getProductId() == null ? "unknow" : gpExchangeRes.getData().getProductId();
                        double usdPrice = gpExchangeRes.getData().getAmount();
                        String serverTimestamp = gpExchangeRes.getData().getTimestamp();

                        SdkEventLogger.trackinPayEvent(context.getApplicationContext(), "", orderId, productId, usdPrice, serverTimestamp,true);
                        if (gpExchangeRes.getData().getEvents() != null && !gpExchangeRes.getData().getEvents().isEmpty()){
                            for (EventBean eventBean : gpExchangeRes.getData().getEvents()) {
                                if (SStringUtil.isNotEmpty(eventBean.getName()) && eventBean.getValue() > 0){
                                    SdkEventLogger.trackinPayEvent(context.getApplicationContext(), eventBean.getName(), orderId, productId, eventBean.getValue(), serverTimestamp,false);
                                }
                            }
                        }
                    }

                } else {
//                    if (gpExchangeRes!=null && !TextUtils.isEmpty(gpExchangeRes.getMessage())){
//                        ToastUtils.toast(activity,gpExchangeRes.getMessage());
//                    }
                    PL.i("exchangeReqTask failed");
                    if (sfCallBack != null){
                        sfCallBack.fail(gpExchangeRes,rawResult);
                    }
                }
            }

            @Override
            public void timeout(String code) {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void noData() {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void cancel() {
            }
        });
        googleExchangeReqTask.excute(GPExchangeRes.class);
    }

}

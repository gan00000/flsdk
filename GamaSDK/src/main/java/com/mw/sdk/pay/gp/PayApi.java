package com.mw.sdk.pay.gp;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.android.billingclient.api.Purchase;
import com.core.base.callback.ISReqCallBack;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.pay.gp.bean.req.GoogleExchangeReqBean;
import com.mw.sdk.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.mw.sdk.pay.gp.bean.res.BasePayBean;
import com.mw.sdk.pay.gp.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.pay.gp.bean.res.GPExchangeRes;
import com.mw.sdk.pay.gp.task.GoogleCreateOrderReqTask;
import com.mw.sdk.pay.gp.task.GoogleExchangeReqTask;
import com.mw.sdk.pay.gp.util.PayHelper;

public class PayApi {

    public static void requestCreateOrder(Activity activity, GooglePayCreateOrderIdReqBean bean, SFCallBack<GPCreateOrderIdRes> sfCallBack) {

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
    public static void requestSendStone(Context context, final Purchase mPurchase,String reissue, SFCallBack<GPExchangeRes> sfCallBack) {

        GoogleExchangeReqBean exchangeReqBean = new GoogleExchangeReqBean(context);
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
                        BasePayBean basePayBean = new BasePayBean();
                        basePayBean.setOrderId(gpExchangeRes.getData().getOrderId());
                        basePayBean.setProductId(gpExchangeRes.getData().getProductId());
                        basePayBean.setServerTimestamp(gpExchangeRes.getData().getTimestamp());
                        basePayBean.setUsdPrice(gpExchangeRes.getData().getAmount());

                        SdkEventLogger.trackinPayEvent(context.getApplicationContext(), basePayBean);
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
        googleExchangeReqTask.excute(GPExchangeRes.class);
    }


    public static void requestSendStone_huawei(Context context, String inAppPurchaseData, String inAppPurchaseDataSignature, String reissue, SFCallBack<GPExchangeRes> sfCallBack) {

        GoogleExchangeReqBean exchangeReqBean = new GoogleExchangeReqBean(context);
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

        GoogleExchangeReqBean exchangeReqBean = new GoogleExchangeReqBean(context);
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

    public static void requestCommonPaySendStone(Context context, GoogleExchangeReqBean exchangeReqBean, SFCallBack<GPExchangeRes> sfCallBack) {


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
                        BasePayBean basePayBean = new BasePayBean();
                        basePayBean.setOrderId(gpExchangeRes.getData().getOrderId());
                        basePayBean.setProductId(gpExchangeRes.getData().getProductId());
                        basePayBean.setServerTimestamp(gpExchangeRes.getData().getTimestamp());
                        basePayBean.setUsdPrice(gpExchangeRes.getData().getAmount());

                        SdkEventLogger.trackinPayEvent(context.getApplicationContext(), basePayBean);
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

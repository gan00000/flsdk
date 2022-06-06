package com.gama.pay.gp;

import android.app.Activity;
import android.text.TextUtils;

import com.android.billingclient.api.Purchase;
import com.core.base.callback.ISReqCallBack;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.gama.pay.gp.bean.req.GoogleExchangeReqBean;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.gp.bean.res.GPCreateOrderIdRes;
import com.gama.pay.gp.bean.res.GPExchangeRes;
import com.gama.pay.gp.constants.GooglePayDomainSite;
import com.gama.pay.gp.task.GoogleCreateOrderReqTask;
import com.gama.pay.gp.task.GoogleExchangeReqTask;
import com.gama.pay.gp.util.PayHelper;

public class PayApi {

    public static void requestCreateOrder(Activity activity, GooglePayCreateOrderIdReqBean bean, SFCallBack<GPCreateOrderIdRes> sfCallBack) {

        GoogleCreateOrderReqTask googleCreateOrderReqTask = new GoogleCreateOrderReqTask(bean);
        googleCreateOrderReqTask.setReqCallBack(new ISReqCallBack<GPCreateOrderIdRes>() {

            @Override
            public void success(GPCreateOrderIdRes createOrderIdRes, String rawResult) {
                if (createOrderIdRes != null && createOrderIdRes.isRequestSuccess() && !TextUtils.isEmpty(createOrderIdRes.getOrderId())) {
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
     * 正常购买发币
     */
    public static void requestSendStone(Activity activity, final Purchase mPurchase, SFCallBack<GPExchangeRes> sfCallBack) {
        GoogleExchangeReqBean exchangeReqBean = new GoogleExchangeReqBean(activity);
        exchangeReqBean.setDataSignature(mPurchase.getSignature());
        exchangeReqBean.setPurchaseData(mPurchase.getOriginalJson());

        exchangeReqBean.setRequestUrl(PayHelper.getPreferredUrl(activity));
        exchangeReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(activity));

        exchangeReqBean.setRequestMethod(GooglePayDomainSite.GOOGLE_SEND);

        GoogleExchangeReqTask googleExchangeReqTask = new GoogleExchangeReqTask(activity, exchangeReqBean);
        googleExchangeReqTask.setReqCallBack(new ISReqCallBack<GPExchangeRes>() {

            @Override
            public void success(GPExchangeRes gpExchangeRes, String rawResult) {
                if (gpExchangeRes != null && gpExchangeRes.isRequestSuccess()) {
                    PL.i("google pay GoogleExchangeReqTask finish");
                    if (sfCallBack != null){
                        sfCallBack.success(gpExchangeRes,rawResult);
                    }
                } else {
                    if (gpExchangeRes!=null && !TextUtils.isEmpty(gpExchangeRes.getMessage())){
                        ToastUtils.toast(activity,gpExchangeRes.getMessage());
                    }
                    PL.i("GoogleExchangeReqTask failed");
                    if (sfCallBack != null){
                        sfCallBack.fail(gpExchangeRes,rawResult);
                    }
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
}

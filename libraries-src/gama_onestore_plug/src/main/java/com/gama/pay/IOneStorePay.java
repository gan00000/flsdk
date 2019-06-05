package com.gama.pay;

import android.app.Activity;

import com.core.base.callback.IGameLifeCycle;
import com.gama.pay.onestore.bean.req.PayReqBean;

/**
 * Created by gan on 2017/2/23.
 */

public interface IOneStorePay extends IGameLifeCycle{
    String PAY_STATUS = "status";
    int PAY_SUCCESS = 93;
    int PAY_FAIL = 94;

    void connectOneService();

    void checkIsBillingSupport();

    void queryPurchases();

    void launchPurchaseFlowAsync();

    void consumeAsync();

    /**
     * 进入Google储值流程
     */
    void startPay(Activity activity, PayReqBean payReqBean);

    /**
     * 设置Google储值的回调
     */
    void setIPayCallBack(IPayCallBack iPayCallBack);
}

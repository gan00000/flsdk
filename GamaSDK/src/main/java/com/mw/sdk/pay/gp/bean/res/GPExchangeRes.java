package com.mw.sdk.pay.gp.bean.res;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by ganyuanrong on 2017/2/23.
 */

public class GPExchangeRes extends BaseResponseModel {

    private String orderId;

    private String verification;

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}

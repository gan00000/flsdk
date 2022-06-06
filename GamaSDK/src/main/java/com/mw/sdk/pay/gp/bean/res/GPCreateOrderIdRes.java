package com.mw.sdk.pay.gp.bean.res;

import com.core.base.bean.BaseResponseModel;

/**
 * 请求创单接口返回的数据
 */
public class GPCreateOrderIdRes extends BaseResponseModel {

    private String orderId;
    private String paygpId;
    private double usdPrice;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaygpId() {
        return paygpId;
    }

    public void setPaygpId(String paygpId) {
        this.paygpId = paygpId;
    }

    public double getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(double usdPrice) {
        this.usdPrice = usdPrice;
    }
}

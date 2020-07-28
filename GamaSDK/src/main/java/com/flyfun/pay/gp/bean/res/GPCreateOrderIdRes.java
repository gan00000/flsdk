package com.flyfun.pay.gp.bean.res;

import com.core.base.bean.BaseResponseModel;

/**
 * 请求创单接口返回的数据
 */
public class GPCreateOrderIdRes extends BaseResponseModel {

    private String orderId;
    private String paygpId;


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
}

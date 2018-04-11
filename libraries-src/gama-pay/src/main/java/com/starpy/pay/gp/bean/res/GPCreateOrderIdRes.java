package com.starpy.pay.gp.bean.res;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by ganyuanrong on 2017/2/23.
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

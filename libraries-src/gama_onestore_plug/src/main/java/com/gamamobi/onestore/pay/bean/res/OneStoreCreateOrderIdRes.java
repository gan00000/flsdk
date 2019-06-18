package com.gamamobi.onestore.pay.bean.res;

import com.core.base.bean.BaseResponseModel;

/**
 * 请求创单接口返回的数据
 */
public class OneStoreCreateOrderIdRes extends BaseResponseModel {

    private String orderId;
    private String payId;
    private String developerPayload;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }
}

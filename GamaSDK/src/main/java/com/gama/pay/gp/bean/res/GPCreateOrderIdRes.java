package com.gama.pay.gp.bean.res;

import com.core.base.bean.BaseResponseModel;

/**
 * 请求创单接口返回的数据
 */
public class GPCreateOrderIdRes extends BaseResponseModel {

    private PayData data;

    public PayData getPayData() {
        return data;
    }

    public static class PayData{

        private String orderId;
        private String paymentId;


        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }
    }
}

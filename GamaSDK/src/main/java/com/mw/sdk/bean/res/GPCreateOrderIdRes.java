package com.mw.sdk.bean.res;

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
        private Double amount; //金额 usd
        private String currency = "USD";

        private String productName;
        private Double localAmount;//本地金额
        private String localCurrency;//本地货币


        //========================X7使用=====================
        private X7OrderBean x7Data;
        //========================X7使用 end=====================


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

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Double getLocalAmount() {
            return localAmount;
        }

        public void setLocalAmount(Double localAmount) {
            this.localAmount = localAmount;
        }

        public String getLocalCurrency() {
            return localCurrency;
        }

        public void setLocalCurrency(String localCurrency) {
            this.localCurrency = localCurrency;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public X7OrderBean getX7Data() {
            return x7Data;
        }

        public void setX7Data(X7OrderBean x7Data) {
            this.x7Data = x7Data;
        }
    }
}

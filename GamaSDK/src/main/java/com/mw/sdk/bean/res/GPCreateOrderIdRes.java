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
        private String extendsInfoData = "";

        private String gameSign = "";
        private String subject = "";
        private String gameAccessVersion = "";
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

        public String getExtendsInfoData() {
            return extendsInfoData;
        }

        public void setExtendsInfoData(String extendsInfoData) {
            this.extendsInfoData = extendsInfoData;
        }

        public String getGameSign() {
            return gameSign;
        }

        public void setGameSign(String gameSign) {
            this.gameSign = gameSign;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getGameAccessVersion() {
            return gameAccessVersion;
        }

        public void setGameAccessVersion(String gameAccessVersion) {
            this.gameAccessVersion = gameAccessVersion;
        }
    }
}

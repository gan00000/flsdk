package com.mw.sdk.bean.res;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by ganyuanrong on 2017/2/23.
 */

public class GPExchangeRes extends BaseResponseModel {

    @Override
    public boolean isRequestSuccess() {
        //2008表示已经发币
        return SUCCESS_CODE.equals(this.getCode()) || "2008".equals(this.getCode());
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{

        private String orderId;
        private double amount;
        private String timestamp;
        private String productId;

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

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }
    }
}

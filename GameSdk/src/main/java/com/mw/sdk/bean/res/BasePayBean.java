package com.mw.sdk.bean.res;

import java.io.Serializable;

public class BasePayBean implements Serializable {

    private String userId;
    private String mToken;
    private String orderId;//平台订单号，不是Google等
    private String packageName;
    private String productId;
    private long purchaseTime;
    private String developerPayload;
    private String transactionId;//金流订单号
    private int purchaseState;
    private String signature;
    private String originPurchaseData;
    private String cpOrderId;
    private double usdPrice;
    private double price;
    private String currency;

    private String serverTimestamp;
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getOriginPurchaseData() {
        return originPurchaseData;
    }

    public void setOriginPurchaseData(String originPurchaseData) {
        this.originPurchaseData = originPurchaseData;
    }

    public String getCpOrderId() {
        return cpOrderId;
    }

    public void setCpOrderId(String cpOrderId) {
        this.cpOrderId = cpOrderId;
    }

    public double getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(double usdPrice) {
        this.usdPrice = usdPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(String serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}

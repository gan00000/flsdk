package com.mw.sdk.pay.gp.bean.res;

import java.io.Serializable;

public class BasePayBean implements Serializable {

    private String mItemType;  // ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
    private String mToken;
    private String orderId;
    private String packageName;
    private String productId;
    private long purchaseTime;
    private String developerPayload;
    private String purchaseId;
    private int purchaseState;
    private String signature;
    private int recurringState;
    private String originPurchaseData;
    private String cpOrderId;
    private double usdPrice;
    private double price;
    private String currency;

    public String getmItemType() {
        return mItemType;
    }

    public void setmItemType(String mItemType) {
        this.mItemType = mItemType;
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

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
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

    public int getRecurringState() {
        return recurringState;
    }

    public void setRecurringState(int recurringState) {
        this.recurringState = recurringState;
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
}

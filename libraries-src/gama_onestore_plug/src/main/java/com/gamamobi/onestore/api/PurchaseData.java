//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gamamobi.onestore.api;

public class PurchaseData {
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

    private PurchaseData() {
    }

    private PurchaseData(PurchaseData other) {
        this.orderId = other.orderId;
        this.packageName = other.packageName;
        this.productId = other.productId;
        this.purchaseTime = other.purchaseTime;
        this.developerPayload = other.developerPayload;
        this.purchaseId = other.purchaseId;
        this.purchaseState = other.purchaseState;
        this.signature = other.signature;
        this.recurringState = other.recurringState;
        this.originPurchaseData = other.originPurchaseData;
    }

    public static PurchaseData.Builder builder() {
        return new PurchaseData.Builder();
    }

    public String getOrderId() {
        return this.orderId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getProductId() {
        return this.productId;
    }

    public long getPurchaseTime() {
        return this.purchaseTime;
    }

    public String getDeveloperPayload() {
        return this.developerPayload;
    }

    public String getPurchaseId() {
        return this.purchaseId;
    }

    public int getPurchaseState() {
        return this.purchaseState;
    }

    public String getSignature() {
        return this.signature;
    }

    public int getRecurringState() {
        return this.recurringState;
    }

    public String getPurchaseData() {
        return this.originPurchaseData;
    }

    public String toString() {
        return "[OrderId]: " + this.orderId + " [packageName]: " + this.packageName + " [productId]: " + this.productId + " [purchaseTime]: " + this.purchaseTime + " [developerPayload]: " + this.developerPayload + " [purchaseId]: " + this.purchaseId + " [purchaseState]: " + this.purchaseState + " [signature]: " + this.signature + " [recurringState]: " + this.recurringState + " [originPurchaseData]: " + this.originPurchaseData;
    }

    public static class Builder {
        final PurchaseData purchaseData = new PurchaseData();

        public Builder() {
        }

        public PurchaseData.Builder orderId(String orderId) {
            this.purchaseData.orderId = orderId;
            return this;
        }

        public PurchaseData.Builder packageName(String packageName) {
            this.purchaseData.packageName = packageName;
            return this;
        }

        public PurchaseData.Builder productId(String productId) {
            this.purchaseData.productId = productId;
            return this;
        }

        public PurchaseData.Builder purchaseTime(long purchaseTime) {
            this.purchaseData.purchaseTime = purchaseTime;
            return this;
        }

        public PurchaseData.Builder developerPayload(String developerPayload) {
            this.purchaseData.developerPayload = developerPayload;
            return this;
        }

        public PurchaseData.Builder purchaseId(String purchaseId) {
            this.purchaseData.purchaseId = purchaseId;
            return this;
        }

        public PurchaseData.Builder purchaseState(int purchaseState) {
            this.purchaseData.purchaseState = purchaseState;
            return this;
        }

        public PurchaseData.Builder signature(String signature) {
            this.purchaseData.signature = signature;
            return this;
        }

        public PurchaseData.Builder recurringState(int recurringState) {
            this.purchaseData.recurringState = recurringState;
            return this;
        }

        public PurchaseData.Builder purchaseData(String originPurchaseData) {
            this.purchaseData.originPurchaseData = originPurchaseData;
            return this;
        }

        public PurchaseData build() {
            return new PurchaseData(this.purchaseData);
        }
    }
}

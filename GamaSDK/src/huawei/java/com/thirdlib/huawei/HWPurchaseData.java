package com.thirdlib.huawei;

public class HWPurchaseData {

      private boolean autoRenewing;
      /**
       * 订单ID，唯一标识一笔需要收费的收据，由华为应用内支付服务器在创建订单以及订阅型商品续费时生成。
       *
       * 每一笔新的收据都会使用不同的orderId。
       */
      private String orderId;
      private String packageName;
      private long applicationId;
      private String applicationIdString;
      /**
       * 商品类别，取值包括：
       *
       * 0：消耗型商品
       * 1：非消耗型商品
       * 2：订阅型商品
       */
      private int kind;
      private String productId;
      private String productName;
      private long purchaseTime;
      /**
       * 订单交易状态。
       * -1：初始化
       * 0：已购买
       * 1：已取消
       * 2：已退款
       * 3：待处理
       */
      private int purchaseState;
      private String developerPayload;
      private String purchaseToken;
      /**
       * 消耗状态，仅一次性商品存在，取值包括：
       * 0：未消耗
       * 1：已消耗
       */
      private int consumptionState;
      private int confirmed;
      private int purchaseType;
      private String currency;
      private double price;
      private String country;
      private String payOrderId;
      private String payType;
      private String sdkChannel;

      public boolean isAutoRenewing() {
            return autoRenewing;
      }

      public void setAutoRenewing(boolean autoRenewing) {
            this.autoRenewing = autoRenewing;
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

      public long getApplicationId() {
            return applicationId;
      }

      public void setApplicationId(long applicationId) {
            this.applicationId = applicationId;
      }

      public String getApplicationIdString() {
            return applicationIdString;
      }

      public void setApplicationIdString(String applicationIdString) {
            this.applicationIdString = applicationIdString;
      }

      public int getKind() {
            return kind;
      }

      public void setKind(int kind) {
            this.kind = kind;
      }

      public String getProductId() {
            return productId;
      }

      public void setProductId(String productId) {
            this.productId = productId;
      }

      public String getProductName() {
            return productName;
      }

      public void setProductName(String productName) {
            this.productName = productName;
      }

      public long getPurchaseTime() {
            return purchaseTime;
      }

      public void setPurchaseTime(long purchaseTime) {
            this.purchaseTime = purchaseTime;
      }

      public int getPurchaseState() {
            return purchaseState;
      }

      public void setPurchaseState(int purchaseState) {
            this.purchaseState = purchaseState;
      }

      public String getDeveloperPayload() {
            return developerPayload;
      }

      public void setDeveloperPayload(String developerPayload) {
            this.developerPayload = developerPayload;
      }

      public String getPurchaseToken() {
            return purchaseToken;
      }

      public void setPurchaseToken(String purchaseToken) {
            this.purchaseToken = purchaseToken;
      }

      public int getConsumptionState() {
            return consumptionState;
      }

      public void setConsumptionState(int consumptionState) {
            this.consumptionState = consumptionState;
      }

      public int getConfirmed() {
            return confirmed;
      }

      public void setConfirmed(int confirmed) {
            this.confirmed = confirmed;
      }

      public int getPurchaseType() {
            return purchaseType;
      }

      public void setPurchaseType(int purchaseType) {
            this.purchaseType = purchaseType;
      }

      public String getCurrency() {
            return currency;
      }

      public void setCurrency(String currency) {
            this.currency = currency;
      }

      public double getPrice() {
            return price;
      }

      public void setPrice(double price) {
            this.price = price;
      }

      public String getCountry() {
            return country;
      }

      public void setCountry(String country) {
            this.country = country;
      }

      public String getPayOrderId() {
            return payOrderId;
      }

      public void setPayOrderId(String payOrderId) {
            this.payOrderId = payOrderId;
      }

      public String getPayType() {
            return payType;
      }

      public void setPayType(String payType) {
            this.payType = payType;
      }

      public String getSdkChannel() {
            return sdkChannel;
      }

      public void setSdkChannel(String sdkChannel) {
            this.sdkChannel = sdkChannel;
      }
}

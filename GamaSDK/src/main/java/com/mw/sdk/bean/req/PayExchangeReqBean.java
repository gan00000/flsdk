package com.mw.sdk.bean.req;

import android.content.Context;

import com.mw.sdk.bean.SGameBaseRequestBean;

public class PayExchangeReqBean extends SGameBaseRequestBean {

//	private String userId;
	private String orderId;
	private String googleOrderId;
	private String purchaseData;
	private String dataSignature;
	private String algorithm;

	private String priceCurrencyCode;
	private String priceAmountMicros;
	private String productPrice;
	private String reissue;//是否是补发调用

	private String thirdPurchaseToken;

	public String getThirdPurchaseToken() {
		return thirdPurchaseToken;
	}

	public void setThirdPurchaseToken(String thirdPurchaseToken) {
		this.thirdPurchaseToken = thirdPurchaseToken;
	}

	public String getReissue() {
		return reissue;
	}

	public void setReissue(String reissue) {
		this.reissue = reissue;
	}

	public String getPurchaseData() {
		return purchaseData;
	}

	public void setPurchaseData(String purchaseData) {
		this.purchaseData = purchaseData;
	}

	public String getDataSignature() {
		return dataSignature;
	}

	public void setDataSignature(String dataSignature) {
		this.dataSignature = dataSignature;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getPriceCurrencyCode() {
		return priceCurrencyCode;
	}

	public void setPriceCurrencyCode(String priceCurrencyCode) {
		this.priceCurrencyCode = priceCurrencyCode;
	}

	public String getPriceAmountMicros() {
		return priceAmountMicros;
	}

	public void setPriceAmountMicros(String priceAmountMicros) {
		this.priceAmountMicros = priceAmountMicros;
	}

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

//	public String getUserId() {
//		return userId;
//	}
//
//	public void setUserId(String userId) {
//		this.userId = userId;
//	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getGoogleOrderId() {
		return googleOrderId;
	}

	public void setGoogleOrderId(String googleOrderId) {
		this.googleOrderId = googleOrderId;
	}

	public PayExchangeReqBean(Context context) {
		super(context);
	}


}

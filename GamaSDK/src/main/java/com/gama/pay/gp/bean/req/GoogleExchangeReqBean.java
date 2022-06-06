package com.gama.pay.gp.bean.req;

import android.content.Context;

public class GoogleExchangeReqBean extends BPayReqBean {


	private String purchaseData;
	private String dataSignature;

	private String priceCurrencyCode;
	private String priceAmountMicros;
	private String productPrice;

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


	public GoogleExchangeReqBean(Context context) {
		super(context);
	}


}

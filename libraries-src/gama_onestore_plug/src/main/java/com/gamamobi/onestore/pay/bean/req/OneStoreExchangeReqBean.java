package com.gamamobi.onestore.pay.bean.req;

import android.content.Context;

import com.gamamobi.onestore.pay.constants.OneStoreContant;

public class OneStoreExchangeReqBean extends BPayReqBean {

	private String oneStoreOrderId;
	private String oneStorePackageName;
	private String oneStoreProductId;
	private long oneStorePurchaseTime;
	private String oneStorePeveloperPayload;
	private String oneStorePurchaseId;
	private int oneStorePurchaseState;
	private String oneStoreSignature;
	private int oneStoreRecurringState;
	private String oneStoreOriginPurchaseData;

	public String getOneStoreOrderId() {
		return oneStoreOrderId;
	}

	public void setOneStoreOrderId(String oneStoreOrderId) {
		this.oneStoreOrderId = oneStoreOrderId;
	}

	public String getOneStorePackageName() {
		return oneStorePackageName;
	}

	public void setOneStorePackageName(String oneStorePackageName) {
		this.oneStorePackageName = oneStorePackageName;
	}

	public String getOneStoreProductId() {
		return oneStoreProductId;
	}

	public void setOneStoreProductId(String oneStoreProductId) {
		this.oneStoreProductId = oneStoreProductId;
	}

	public long getOneStorePurchaseTime() {
		return oneStorePurchaseTime;
	}

	public void setOneStorePurchaseTime(long oneStorePurchaseTime) {
		this.oneStorePurchaseTime = oneStorePurchaseTime;
	}

	public String getOneStorePeveloperPayload() {
		return oneStorePeveloperPayload;
	}

	public void setOneStorePeveloperPayload(String oneStorePeveloperPayload) {
		this.oneStorePeveloperPayload = oneStorePeveloperPayload;
	}

	public String getOneStorePurchaseId() {
		return oneStorePurchaseId;
	}

	public void setOneStorePurchaseId(String oneStorePurchaseId) {
		this.oneStorePurchaseId = oneStorePurchaseId;
	}

	public int getOneStorePurchaseState() {
		return oneStorePurchaseState;
	}

	public void setOneStorePurchaseState(int oneStorePurchaseState) {
		this.oneStorePurchaseState = oneStorePurchaseState;
	}

	public String getOneStoreSignature() {
		return oneStoreSignature;
	}

	public void setOneStoreSignature(String oneStoreSignature) {
		this.oneStoreSignature = oneStoreSignature;
	}

	public int getOneStoreRecurringState() {
		return oneStoreRecurringState;
	}

	public void setOneStoreRecurringState(int oneStoreRecurringState) {
		this.oneStoreRecurringState = oneStoreRecurringState;
	}

	public String getOneStoreOriginPurchaseData() {
		return oneStoreOriginPurchaseData;
	}

	public void setOneStoreOriginPurchaseData(String oneStoreOriginPurchaseData) {
		this.oneStoreOriginPurchaseData = oneStoreOriginPurchaseData;
	}

	public OneStoreExchangeReqBean(Context context) {
		super(context);
		setPsid(OneStoreContant.PSID);
	}


}

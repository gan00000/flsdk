package com.mw.sdk.bean.req;

import android.content.Context;

/**
 * 用于请求创单接口的参数集
 */
public class PayCreateOrderReqBean extends PayReqBean {

	private String payValue = "0";
	private String productId;
	private String payType = "SDK";
	private String mode = "google";
	private String gameGuid = "";

	public PayCreateOrderReqBean(Context context) {
		super(context);
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getGameGuid() {
		return gameGuid;
	}

	public void setGameGuid(String gameGuid) {
		this.gameGuid = gameGuid;
	}

	/**
	 * @return the productId
	 */

	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	public void clear(){
		setProductId("");
		setExtra("");
		setGameCode("");
		setCpOrderId("");
		setServerCode("");
		setRoleId("");
	}


	public String getPayValue() {
		return payValue;
	}

	public void setPayValue(String payValue) {
		this.payValue = payValue;
	}
}

package com.gama.pay.onestore.bean.req;

import android.content.Context;

import com.gama.pay.onestore.constants.OneStoreContant;

/**
 * 用于请求创单接口的参数集
 */
public class OneStoreCreateOrderIdReqBean extends PayReqBean {

	private String payValue = "0";
	private String productId;
	private String payType = OneStoreContant.PAYTYPE;

	public OneStoreCreateOrderIdReqBean(Context context) {
		super(context);
		setPsid("90");
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

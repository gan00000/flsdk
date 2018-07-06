package com.gama.pay.gp.bean.req;

import android.content.Context;

import com.gama.pay.gp.constants.GooglePayContant;

/**
 * 用于请求创单接口的参数集
 */
public class GooglePayCreateOrderIdReqBean extends PayReqBean {

	private String payValue = "0";
	private String productId;
	private String payType = GooglePayContant.GOOGLEPAYTYPE;

	public GooglePayCreateOrderIdReqBean(Context context) {
		super(context);
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

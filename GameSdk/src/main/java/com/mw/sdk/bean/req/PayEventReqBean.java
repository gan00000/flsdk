package com.mw.sdk.bean.req;

import android.content.Context;

import com.core.base.utils.SStringUtil;
import com.mw.sdk.bean.SGameBaseRequestBean;

public class PayEventReqBean extends SGameBaseRequestBean {

	private String orderId;
	private double amount;

	public PayEventReqBean(Context context) {
		super(context);

	}


	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}

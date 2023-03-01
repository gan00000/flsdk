package com.mw.sdk.pay.gp.bean.req;

import android.content.Context;

import com.core.base.utils.SStringUtil;
import com.mw.sdk.pay.gp.constants.GooglePayContant;
import com.mw.base.bean.SGameBaseRequestBean;

public class PayReqBean extends SGameBaseRequestBean {

	private String payFrom = GooglePayContant.PAY_FROM;
	private String extra = "";
	/**
	 * 原厂订单号
	 */
	private String cpOrderId = "";

	private String psid = "61";

	public PayReqBean(Context context) {
		super(context);

		initm(context);

	}

	private void initm(Context context) {
	}

	public boolean isInitOk(){
		return !SStringUtil.hasEmpty(getUserId(),getGameCode(),getServerCode(),getRoleId());
//		return true;
	}

	public String print() {
		return "userId='" + getUserId() + '\'' +
				", serverCode='" + getServerCode() + '\'' +
				", roleId='" + getRoleId();
	}



	public String getPayFrom() {
		return payFrom;
	}

	public void setPayFrom(String payFrom) {
		this.payFrom = payFrom;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	/**
	 * 返回原厂订单号
	 */
	public String getCpOrderId() {
		return cpOrderId;
	}

	public void setCpOrderId(String cpOrderId) {
		this.cpOrderId = cpOrderId;
	}

	public String getPsid() {
		return psid;
	}

	public void setPsid(String psid) {
		this.psid = psid;
	}
}

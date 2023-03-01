package com.mw.sdk.pay.gp.bean.req;

import android.content.Context;

import com.mw.sdk.pay.gp.constants.GooglePayContant;

public class WebPayReqBean extends PayReqBean {

	private String payType = GooglePayContant.THIRDPAYTYPE;

	private String iswifi;

	private String appPlatFrom;

	/**
	 * 级别类型，此参数设定是否使用级别类型，比如有些游戏内置储值需要达到级别才可以看到第三方储值金流；
	 *	0：游戏内置储值跳转使用级别限制，根据等级控制显示储值金流；
	 *	1：游戏内置储值跳转不使用级别限制，仅看到第三方储值
	 */
//	private String levelType = "0";

	public WebPayReqBean(Context context) {
		super(context);
	}


	/**
	 * @return the payType
	 */
	public String getPayType() {
		return payType;
	}
	/**
	 * @param payType the payType to set
	 */
	public void setPayType(String payType) {
		this.payType = payType;
	}
	/**
	 * @return the iswifi
	 */
	public String getIswifi() {
		return iswifi;
	}
	/**
	 * @param iswifi the iswifi to set
	 */
	public void setIswifi(String iswifi) {
		this.iswifi = iswifi;
	}
	public String getAppPlatFrom() {
		return appPlatFrom;
	}
	public void setAppPlatFrom(String appPlatFrom) {
		this.appPlatFrom = appPlatFrom;
	}


//	public String getLevelType() {
//		return levelType;
//	}
//	public void setLevelType(String levelType) {
//		this.levelType = levelType;
//	}
}

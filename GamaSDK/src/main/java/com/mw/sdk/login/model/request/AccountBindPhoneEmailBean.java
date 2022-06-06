package com.mw.sdk.login.model.request;

import android.content.Context;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 接口请求参数实体</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class AccountBindPhoneEmailBean extends AccountLoginRequestBean {

	public AccountBindPhoneEmailBean(Context context) {
		super(context);
	}

	/**
	 * registPlatform 第三方登陆平台的标识符
	 */
	private String registPlatform;
	private String thirdPlatId;
	/**
	 * apps 关联应用的FB ID
	 */
	private String apps = "";
	private String tokenBusiness = "";//编码（标志明文密文）

	private String vfCode;

	private String userId;

	public String getVfCode() {
		return vfCode;
	}

	public void setVfCode(String vfCode) {
		this.vfCode = vfCode;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getRegistPlatform() {
		return registPlatform;
	}

	public void setRegistPlatform(String registPlatform) {
		this.registPlatform = registPlatform;
	}

	public String getThirdPlatId() {
		return thirdPlatId;
	}

	public void setThirdPlatId(String thirdPlatId) {
		this.thirdPlatId = thirdPlatId;
	}

	public String getApps() {
		return apps;
	}

	public void setApps(String apps) {
		this.apps = apps;
	}

	public String getTokenBusiness() {
		return tokenBusiness;
	}

	public void setTokenBusiness(String tokenBusiness) {
		this.tokenBusiness = tokenBusiness;
	}
}

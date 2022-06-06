package com.mw.sdk.login.model.request;

import android.content.Context;

import com.mw.base.bean.AdsRequestBean;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 接口请求参数实体</p>
* <p>Company:GanYuanrong</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class ThirdLoginRegRequestBean extends AdsRequestBean {

	public ThirdLoginRegRequestBean(Context context) {
		super(context);
	}

	private String thirdAccount;

	/**
	 * registPlatform 第三方登陆平台的标识符
	 */
	private String registPlatform;
	private String loginMode;
	private String thirdPlatId;
	private String thirdLoginId;

	/**
	 * apps 关联应用的FB ID
	 */
	private String apps = "";
	private String tokenBusiness = "";//编码（标志明文密文）

	/**
	 * fb服务端验证token
	 */
	private String fb_oauthToken;

	/**
	 * google服务端验证token
	 */
	private String googleIdToken;
	private String googleClientId;

	public String getRegistPlatform() {
		return registPlatform;
	}

	public void setRegistPlatform(String registPlatform) {
		this.registPlatform = registPlatform;
		this.loginMode = registPlatform;
	}

	public String getThirdPlatId() {
		return thirdPlatId;
	}

	public void setThirdPlatId(String thirdPlatId) {
		this.thirdPlatId = thirdPlatId;
		this.thirdLoginId = thirdPlatId;
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

	public String getFb_oauthToken() {
		return fb_oauthToken;
	}

	public void setFb_oauthToken(String fb_oauthToken) {
		this.fb_oauthToken = fb_oauthToken;
	}

	public String getGoogleIdToken() {
		return googleIdToken;
	}

	public void setGoogleIdToken(String googleIdToken) {
		this.googleIdToken = googleIdToken;
	}

	public String getGoogleClientId() {
		return googleClientId;
	}

	public void setGoogleClientId(String googleClientId) {
		this.googleClientId = googleClientId;
	}

	public String getThirdAccount() {
		return thirdAccount;
	}

	public void setThirdAccount(String thirdAccount) {
		this.thirdAccount = thirdAccount;
	}
}

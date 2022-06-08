package com.mw.sdk.login.model.request;

import android.content.Context;

import com.mw.base.bean.AdsRequestBean;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 用户登录请求参数实体</p>
* <p>Company:GanYuanrong</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class DeleteAccountRequestBean extends AdsRequestBean {

	public DeleteAccountRequestBean(Context context) {
		super(context);
	}

	private String userId;
	private String loginMode;
	private String thirdLoginId;


	public DeleteAccountRequestBean(Context context, String userId, String loginMode, String thirdLoginId) {
		super(context);
		this.userId = userId;
		this.loginMode = loginMode;
		this.thirdLoginId = thirdLoginId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLoginMode() {
		return loginMode;
	}

	public void setLoginMode(String loginMode) {
		this.loginMode = loginMode;
	}

	public String getThirdLoginId() {
		return thirdLoginId;
	}

	public void setThirdLoginId(String thirdLoginId) {
		this.thirdLoginId = thirdLoginId;
	}
}

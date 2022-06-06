package com.mw.sdk.login.model.request;

import android.content.Context;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 接口请求参数实体</p>
* <p>Company:GanYuanrong</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class AccountInjectionRequestBean extends AccountLoginRequestBean {

	private String userId;

	public AccountInjectionRequestBean(Context context) {
		super(context);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}
}

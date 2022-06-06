package com.mw.sdk.login.model.request;

import android.content.Context;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 接口请求参数实体</p>
* <p>Company:GanYuanrong</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class AccountRegRequestBean extends AccountLoginRequestBean {

	private String vfCode;

	public String getVfCode() {
		return vfCode;
	}

	public void setVfCode(String vfCode) {
		this.vfCode = vfCode;
	}
	public AccountRegRequestBean(Context context) {
		super(context);
	}
}

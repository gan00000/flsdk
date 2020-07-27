package com.gama.data.login.request;

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
	private String interfaces = "1";

	public String getVfCode() {
		return vfCode;
	}

	public void setVfCode(String vfCode) {
		this.vfCode = vfCode;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public AccountRegRequestBean(Context context) {
		super(context);
	}
}

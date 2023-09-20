package com.mw.sdk.bean.req;

import android.content.Context;

import com.mw.sdk.bean.AdsRequestBean;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 用户登录请求参数实体</p>
* <p>Company:GanYuanrong</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class FindPwdRequestBean extends AdsRequestBean {

	public FindPwdRequestBean(Context context) {
		super(context);
	}


	private String name;//用户账号名
	private String email;
	private String interfaces = "4";
	private String vfCode = "";
	private String verifyCode = "";
	private String newPwd = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVfCode() {
		return vfCode;
	}

	public void setVfCode(String vfCode) {
		this.vfCode = vfCode;
		this.verifyCode = vfCode;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
}
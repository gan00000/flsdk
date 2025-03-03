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
public class AccountLoginRequestBean extends AdsRequestBean {

	public AccountLoginRequestBean(Context context) {
		super(context);
	}


	private String name;//用户账号名
	private String pwd;
	private String loginId;//新添加
	private String password;//新添加
	private String newPwd;
	private String oldPwd;
	private String email;
	private String captcha; //验证码


//	public String getLoginId() {
//		return loginId;
//	}

//	public void setLoginId(String loginId) {
//		this.loginId = loginId;
//	}

//	public String getPassword() {
//		return password;
//	}

//	public void setPassword(String password) {
//		this.password = password;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? "" : name.trim().toLowerCase();
		this.loginId = this.name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		this.password = pwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email== null ? "": email.toLowerCase();
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
}

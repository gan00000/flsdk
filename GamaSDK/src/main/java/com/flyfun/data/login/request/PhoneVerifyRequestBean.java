package com.flyfun.data.login.request;

import android.content.Context;

import com.flyfun.base.bean.AdsRequestBean;

/**
* <p>Title: PhoneVerifyRequestBean</p>
* <p>Description: 手机验证请求参数实体</p>
* @author HuangShaoGuang
* @date 2019年11月27日
*/
public class PhoneVerifyRequestBean extends AdsRequestBean {

	public PhoneVerifyRequestBean(Context context) {
		super(context);
	}

	private String vfCode;
	private String thirdPlatId;
	private String registPlatform;
	private String email;

	public String getVfCode() {
		return vfCode;
	}

	public void setVfCode(String vfCode) {
		this.vfCode = vfCode;
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


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

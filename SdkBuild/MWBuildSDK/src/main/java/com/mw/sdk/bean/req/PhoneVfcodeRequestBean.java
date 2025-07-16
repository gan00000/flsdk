package com.mw.sdk.bean.req;

import android.content.Context;

import com.mw.sdk.bean.AdsRequestBean;

/**
 * 请求手机验证码的参数
 */
public class PhoneVfcodeRequestBean extends AdsRequestBean {

	private String email;

	public PhoneVfcodeRequestBean(Context context) {
		super(context);
	}

	/**
	 * 用到的接口名称，注册：1，绑定：2
	 */
	private String interfaces;//用户账号名

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

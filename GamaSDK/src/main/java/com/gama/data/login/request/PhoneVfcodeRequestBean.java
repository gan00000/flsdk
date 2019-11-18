package com.gama.data.login.request;

import android.content.Context;

import com.gama.base.bean.AdsRequestBean;

/**
 * 请求手机验证码的参数
 */
public class PhoneVfcodeRequestBean extends AdsRequestBean {

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

}

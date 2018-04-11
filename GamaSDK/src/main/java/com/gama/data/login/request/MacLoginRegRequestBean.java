package com.gama.data.login.request;

import android.content.Context;

import com.gama.base.bean.AdsRequestBean;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 接口请求参数实体</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class MacLoginRegRequestBean extends AdsRequestBean {

	public MacLoginRegRequestBean(Context context) {
		super(context);

	}


	/**
	 * registPlatform 第三方登陆平台的标识符
	 */
	private String registPlatform;

	public String getRegistPlatform() {
		return registPlatform;
	}

	public void setRegistPlatform(String registPlatform) {
		this.registPlatform = registPlatform;
	}
}

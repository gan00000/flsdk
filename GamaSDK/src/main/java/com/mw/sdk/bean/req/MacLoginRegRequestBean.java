package com.mw.sdk.bean.req;

import android.content.Context;

import com.mw.sdk.bean.AdsRequestBean;

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

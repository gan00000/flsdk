package com.mw.sdk.login.model.request;

import android.content.Context;

import com.mw.base.bean.SGameBaseRequestBean;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 接口请求参数实体</p>
* <p>Company:GanYuanrong</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class AccountBindInGameRequestBean extends SGameBaseRequestBean {

	public AccountBindInGameRequestBean(Context context) {
		super(context);
	}

	/**
	 * registPlatform 第三方登陆平台的标识符
	 */
	private String registPlatform;
	private String loginMode;

	private String thirdPlatId;
	private String thirdLoginId;

	private String name;//用户账号名
	private String loginId;//新添加

	private String pwd;
	private String password;//新添加

	public String getRegistPlatform() {
		return registPlatform;
	}

	public void setRegistPlatform(String registPlatform) {
		this.registPlatform = registPlatform;
		this.loginMode = registPlatform;
	}

	public String getThirdPlatId() {
		return thirdPlatId;
	}

	public void setThirdPlatId(String thirdPlatId) {
		this.thirdPlatId = thirdPlatId;
		this.thirdLoginId = thirdPlatId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.loginId = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		this.password = pwd;
	}
}

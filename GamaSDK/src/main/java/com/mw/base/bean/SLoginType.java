package com.mw.base.bean;

public class SLoginType {

	/**
	 * GS入
	 */
	public static final String LOGIN_TYPE_MG = "mg";
	/**
	 * facebook登入
	 */
	public static final String LOGIN_TYPE_FB = "fb";
	/**
	 * Google登入
	 */
	public static final String LOGIN_TYPE_GOOGLE = "google";
	public static final String LOGIN_TYPE_LINE = "line";
	public static final String LOGIN_TYPE_QooApp = "qooapp";
	public static final String LOGIN_TYPE_HUAWEI = "huawei";

	/**
	 * Twitter登入
	 */
	public static final String LOGIN_TYPE_TWITTER = "twitter";
	/**
	 * 用于mac登入，用于本地登录类型判断，回传原厂登录类型
	 */
	public static final String LOGIN_TYPE_GUEST = "visitor";
	/**
	 * 用于免注册登录传给服务端的登录标识
	 */
//	public static final String LOGIN_TYPE_UNIQUE = "visitor";

}

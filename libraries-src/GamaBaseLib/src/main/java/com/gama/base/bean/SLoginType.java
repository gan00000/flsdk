package com.gama.base.bean;

public class SLoginType {

	/**
	 * starpy登入
	 */
	// TODO: 2018/4/13 修改登录类型，待确认
	public static final String LOGIN_TYPE_STARPY = "starpy";
	/**
	 * facebook登入
	 */
	public static final String LOGIN_TYPE_FB = "fb";
	/**
	 * Google登入
	 */
	public static final String LOGIN_TYPE_GOOGLE = "google";
	/**
	 * 用于mac登入，用于本地登录类型判断，回传原厂登录类型
	 */
	public static final String LOGIN_TYPE_MAC = "mac";
	public static final String LOGIN_TYPE_UNIQUE = "unique";

	public static final int bind_unique = 1;
	public static final int bind_fb = 2;
	public static final int bind_google = 3;

}

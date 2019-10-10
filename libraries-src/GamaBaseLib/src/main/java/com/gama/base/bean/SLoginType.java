package com.gama.base.bean;

public class SLoginType {

	/**
	 * Gama登入
	 */
	public static final String LOGIN_TYPE_GAMA = "gamamobi";
	/**
	 * facebook登入
	 */
	public static final String LOGIN_TYPE_FB = "fb";
	/**
	 * Google登入
	 */
	public static final String LOGIN_TYPE_GOOGLE = "google";

	/**
	 * Twitter登入
	 */
	public static final String LOGIN_TYPE_TWITTER = "twitter";
	/**
	 * 原gamamobi登入
	 */
	public static final String LOGIN_TYPE_GMB = "gmb";
	/**
	 * 用于mac登入，用于本地登录类型判断，回传原厂登录类型
	 */
	public static final String LOGIN_TYPE_MAC = "mac";
	/**
	 * 用于免注册登录传给服务端的登录标识
	 */
	public static final String LOGIN_TYPE_UNIQUE = "unique";

	public static final int bind_unique = 1;
	public static final int bind_fb = 2;
	public static final int bind_google = 3;
	public static final int bind_twitter = 4;

}

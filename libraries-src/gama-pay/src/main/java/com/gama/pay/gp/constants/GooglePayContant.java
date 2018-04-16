package com.gama.pay.gp.constants;


public class GooglePayContant {
	
	public static final int RC_REQUEST = 10001;
	
	/**
	 * PAY_FROM payform 参数默认值
	 */
	public static final String PAY_FROM = "gamamobi";
	/**
	 * WIFI wifi参数值
	 */
	public static final String WIFI = "-111111";
	/**
	 * THIRDPAYTYPE 内嵌第三方paytype参数默认值
	 */
	public static final String THIRDPAYTYPE = "mobile";
	/**
	 * GOOGLEPAYTYPE google paytype参数默认值
	 */
	public static final String GOOGLEPAYTYPE = "SDK";
	
	/**
	 * PURCHASESUCCESS 回调购买成功标识
	 */
	public static final int PURCHASESUCCESS = 100000;
	/**
	 * PURCHASEFAILURE 回调购买失败标识
	 */
	public static final int PURCHASEFAILURE = -100000;
	
	/**
	 * GOOGLE_FILENAME 储值数据保存文件
	 */
	public static final String GOOGLE_FILENAME = "GOOGLE_WALLET.db";
	/**
	 * PURCHASE_DATA_ONE 保存最后google储值的一笔订单data
	 */
	public static final String PURCHASE_DATA_ONE = "GOOGLE_PURCHASE_DATA_ONE";
	/**
	 * PURCHASE_SIGN_ONE  保存最后google储值的一笔订单sign
	 */
	public static final String PURCHASE_SIGN_ONE = "GOOGLE_PURCHASE_SIGN_ONE";

}

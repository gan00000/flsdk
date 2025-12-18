package com.mw.sdk.utils;

import android.content.Context;
import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class PayHelper {
	/**
	 * 获取储值主域名
	 */
	public static String getPreferredUrl(Context payActivity){
		String preferredUrl = ResConfig.getPayPreferredUrl(payActivity);
		return checkUrl(preferredUrl);
	}
	/**
	 * 获取储值备用域名
	 */
	public static String getSpareUrl(Context payActivity){
		String spareUrl = ResConfig.getPaySpareUrl(payActivity);
		return checkUrl(spareUrl);
	}

	/**
	* <p>Title: checkUrl</p>
	* <p>Description: 判断是否为正确url</p>
	* @param url
	* @return
	*/
	public static String checkUrl(String url){
		if (TextUtils.isEmpty(url)) {
			return "";
		}
		try {
			URL checkUrl = new URL(url);//判断是否为正确url
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
		if (url.endsWith("/")) {
			return url;
		}else{
			url = url + "/";
		}
		return url;
	}
	
}

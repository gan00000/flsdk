package com.gama.pay.gp.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.SStringUtil;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;
import com.gama.pay.gp.bean.req.WebPayReqBean;
import com.gama.pay.gp.constants.GooglePayContant;

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

	public static WebPayReqBean buildWebPayBean(Context context, String cpOrderId, String extra){
		WebPayReqBean webPayReqBean = new WebPayReqBean(context);

		if (SStringUtil.isEmpty(webPayReqBean.getIswifi())) {
			String simOperator = "";
			if (ApkInfoUtil.isWifiAvailable(context)) {
				simOperator = GooglePayContant.WIFI;
			} else {
				simOperator = ApkInfoUtil.getSimOperator(context);
			}
			webPayReqBean.setIswifi(simOperator);
		}

		webPayReqBean.setUserId(GamaUtil.getUid(context));
		webPayReqBean.setCpOrderId(cpOrderId);
//		webPayReqBean.setRoleLevel(roleLevel);
		webPayReqBean.setExtra(extra);

		webPayReqBean.setPsid("62");
		webPayReqBean.setTimestamp(GamaUtil.getSdkTimestamp(context));

		return webPayReqBean;

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

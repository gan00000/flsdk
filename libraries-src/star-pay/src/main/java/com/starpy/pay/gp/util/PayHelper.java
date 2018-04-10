package com.starpy.pay.gp.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.SStringUtil;
import com.starpy.base.cfg.ResConfig;
import com.starpy.base.utils.StarPyUtil;
import com.starpy.pay.gp.bean.req.WebPayReqBean;
import com.starpy.pay.gp.constants.GooglePayContant;

import java.net.MalformedURLException;
import java.net.URL;

public class PayHelper {
	
	public static String getPreferredUrl(Activity payActivity){
		String preferredUrl = ResConfig.getPayPreferredUrl(payActivity);
		return checkUrl(preferredUrl);
	}
	
	public static String getSpareUrl(Activity payActivity){
		String spareUrl = ResConfig.getPaySpareUrl(payActivity);
		return checkUrl(spareUrl);
	}

	public static WebPayReqBean buildWebPayBean(Context context, String cpOrderId, String roleLevel, String extra){
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

		webPayReqBean.setUserId(StarPyUtil.getUid(context));
		webPayReqBean.setCpOrderId(cpOrderId);
		webPayReqBean.setRoleLevel(roleLevel);
		webPayReqBean.setExtra(extra);

		webPayReqBean.setPsid("62");

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

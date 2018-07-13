package com.core.base.js;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.core.base.SWebView;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.PL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Native2JS {

	Context context;
	String commonString = "";
	Map<String, String> map;
	SWebView sWebView;
	
	
	public Native2JS(Context context) {
		this.context = context;
	}
	
	public Native2JS(Context context, SWebView sWebView) {
		this.context = context;
		this.sWebView = sWebView;
	}

	/**
	 * @return the commonString
	 */
	@JavascriptInterface
	public String getCommonString() {
		return commonString;
	}
	
	@JavascriptInterface
	public String getCommonString(String key) {
		if (!TextUtils.isEmpty(key) && map != null && map.containsKey(key)) {
			return map.get(key);
		}
		return "";
	}
	
	
	/**
	 * @return the map
	 */
	public Map<String, String> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	/**
	 * @param commonString the commonString to set
	 */
	public void setCommonString(String commonString) {
		this.commonString = commonString;
	}

	
	@JavascriptInterface
	public String getPackageName(){
		return context.getPackageName();
	}
	
	@JavascriptInterface
	public String getVersionCode(){
		return ApkInfoUtil.getVersionCode(context);
	}
	
	@JavascriptInterface
	public String getDeviceType(){
		return ApkInfoUtil.getDeviceType();
	}
	@JavascriptInterface
	public String getAndroidId(){
		return ApkInfoUtil.getAndroidId(context);
	}
	
	@JavascriptInterface
	public String getImei(){
		//因Google警告，不再获取imei信息
		return "";
	}
	
	@JavascriptInterface
	public String getIp(){
		return ApkInfoUtil.getLocalIpAddress(context);
	}
	
	@JavascriptInterface
	public String getMac(){
		//因Google警告，不再获取imei信息
		return "";
	}
	
	@JavascriptInterface
	public String getOsVersion(){
		return ApkInfoUtil.getOsVersion();
	}
	
	@JavascriptInterface
	public String getVersionName(){
		return ApkInfoUtil.getVersionName(context);
	}
	

	@JavascriptInterface
	public void finishActivity(){
		if (context != null && context instanceof Activity) {
			PL.i("activity finish");
			((Activity)context).finish();
		}
	}
	
	@JavascriptInterface
	public void close(){
		finishActivity();
	}
	

	@JavascriptInterface
	public String getDeviceInfo(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("systemVersion", ApkInfoUtil.getOsVersion());
			jsonObject.put("mac", "");//因Google警告，不再获取imei信息
			jsonObject.put("deviceType", ApkInfoUtil.getDeviceType());
			jsonObject.put("imei", "");//因Google警告，不再获取imei信息
			jsonObject.put("ip", ApkInfoUtil.getLocalIpAddress(context));
			jsonObject.put("androidid", ApkInfoUtil.getAndroidId(context));
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@JavascriptInterface
	public void setSDKMsg(String msg){
		final String m = msg;
		if (sWebView != null) {
			sWebView.getHandler().post(new Runnable() {
				
				@Override
				public void run() {
					sWebView.jsCallBack(m);
				}
			});
			
		}
	}
	
}

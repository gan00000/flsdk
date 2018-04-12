package com.gama.base.cfg;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.utils.FileUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ResUtil;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.gama.base.utils.StarPyUtil;

public class ResConfig {

	//===========================================参数配置start===============================================
	//===========================================每個遊戲每個渠道都可能不一樣=======================================
	//===========================================参数配置start================================================

	
	/**
	 * 获取gameCode
	 * 
	 * @param context
	 * @return
	 */
	public static String getGameCode(Context context) {
		if (StarPyUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(StarPyUtil.getSdkCfg(context).getS_GameCode())){
			return StarPyUtil.getSdkCfg(context).getS_GameCode();
		}
//		return getResStringByName(context, "star_game_code");
		return getConfigInAssets(context, "star_game_code");
	}
	

	/**
	 * 获取秘钥
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppKey(Context context) {
		if (StarPyUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(StarPyUtil.getSdkCfg(context).getS_AppKey())){
			return StarPyUtil.getSdkCfg(context).getS_AppKey();
		}
		return getConfigInAssets(context, "star_app_key");
	}

	public static String getApplicationId(Context context) {
		return getConfigInAssets(context, "facebook_app_id");
	}


	public static String getGameLanguage(Context context) {
		String language = SPUtil.getSimpleString(context, StarPyUtil.STAR_PY_SP_FILE, StarPyUtil.STARPY_GAME_LANGUAGE);
//		if (TextUtils.isEmpty(language) && StarPyUtil.isMainland(context)) {
//			language = SGameLanguage.zh_CH.getLanguage();
//		}
		return language;
	}

	public static void saveGameLanguage(Context context,String language) {
		if (TextUtils.isEmpty(language)) {
			return;
		}
		SPUtil.saveSimpleInfo(context, StarPyUtil.STAR_PY_SP_FILE, StarPyUtil.STARPY_GAME_LANGUAGE,language);
	}
	
	public static String getGameLanguageLower(Context context){
		return getGameLanguage(context).toLowerCase();
	}
	public static String getGoogleClientId(Context context){
		return getConfigInAssets(context,"google_client_id");
	}



	//===========================================参数配置end===============================================	
	//===========================================参数配置end===============================================	
	//===========================================参数配置end===============================================	
	

	
	//===========================================域名获取start===============================================	
	//=========================================== 根据地区改变，同一地区的游戏不变===================================
	//===========================================域名获取start===============================================	

	/**
	 * 获取登录域名地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getLoginPreferredUrl(Context context) {
		if (StarPyUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(StarPyUtil.getSdkCfg(context).getS_Login_Pre_Url())){
			return StarPyUtil.getSdkCfg(context).getS_Login_Pre_Url();
		}
		return getConfigUrl(context, "star_py_login_pre_url");
	}

	public static String getLoginSpareUrl(Context context) {
		if (StarPyUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(StarPyUtil.getSdkCfg(context).getS_Login_Spa_Url())){
			return StarPyUtil.getSdkCfg(context).getS_Login_Spa_Url();
		}
		return getConfigUrl(context, "star_py_login_spa_url");
	}

	/**
	 * <p>Description: 获取储值域名</p>
	 * @param context
	 * @return
	 * @date 2015年2月5日
	 */
	public static String getPayPreferredUrl(Context context) {
		if (StarPyUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(StarPyUtil.getSdkCfg(context).getS_Pay_Pre_Url())){
			return StarPyUtil.getSdkCfg(context).getS_Pay_Pre_Url();
		}
		return getConfigUrl(context, "star_py_pay_pre_url");
	}
	public static String getPaySpareUrl(Context context) {
		if (StarPyUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(StarPyUtil.getSdkCfg(context).getS_Pay_Spa_Url())){
			return StarPyUtil.getSdkCfg(context).getS_Pay_Spa_Url();
		}
		return getConfigUrl(context, "star_py_pay_spa_url");
	}

	public static String getCsPreferredUrl(Context context) {
		return getCdnLocalUrl(context,"star_py_cs_pre_url");
	}
	public static String getCsSpareUrl(Context context) {
		return getCdnLocalUrl(context,"star_py_cs_spa_url");
	}

	public static String getActivityPreferredUrl(Context context) {
		if (StarPyUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(StarPyUtil.getSdkCfg(context).getS_Act_Pre_Url())){
			return StarPyUtil.getSdkCfg(context).getS_Act_Pre_Url();
		}
		return getConfigUrl(context, "star_py_act_pre_url");
	}
	public static String getActivitySpareUrl(Context context) {
		if (StarPyUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(StarPyUtil.getSdkCfg(context).getS_Act_Spa_Url())){
			return StarPyUtil.getSdkCfg(context).getS_Act_Spa_Url();
		}
		return getConfigUrl(context, "star_py_act_spa_url");
	}

	public static String getCdnPreferredUrl(Context context) {
		return getConfigUrl(context, "star_py_cdn_pre_url");
	}
	public static String getCdnSpareUrl(Context context) {
		return getConfigUrl(context, "star_py_cdn_spa_url");
	}

	public static boolean isInfringement(Context context){
		return SStringUtil.isEqual(getConfigInAssets(context, "star_infringement"),"true");
	}

	public static String getPayThirdMethod(Context context) {
		return getResStringByName(context, "star_pay_third_method");
	}



	/**
	 * <p>Description: 获取广告域名</p>
	 * @param context
	 * @return
	 * @date 2015年2月5日
	 */
	public static String getAdsPreferredUrl(Context context) {
		return getCdnLocalUrl(context, "star_ads_pre_url");
	}
	
	public static String getAdsSpareUrl(Context context) {
		return getCdnLocalUrl(context, "star_ads_spa_url");
	}

	public static String getPlatPreferredUrl(Context context) {
		return getCdnLocalUrl(context, "star_plat_pre_url");
	}

	public static String getPlatSpareUrl(Context context) {
		return getCdnLocalUrl(context, "star_plat_spa_url");
	}


	/**
	 * <p>Description: 先获取动态域名，然后获取本地域名，配置文件的key和本地的xml key需要一致</p>
	 * @param context
	 * @return
	 */
	public static String getCdnLocalUrl(Context context,String resName) {

		String url = StarPyUtil.getCfgValueByKey(context,resName,"");
		if (SStringUtil.isEmpty(url)){
			url = getConfigUrl(context, resName);
		}
		return url;
	}
	


//===========================================域名获取end===============================================	
//===========================================域名获取end===============================================	
//===========================================域名获取end===============================================	
	

	private static String getConfigUrl(Context context, String xmlSchemaName){

		String isGlobal = getConfigInAssets(context, "star_url_is_global");
		if ("100".equals(isGlobal)){
			xmlSchemaName = "g_" + xmlSchemaName;
		}

		String url = getResStringByName(context, xmlSchemaName);
		
//		if (TextUtils.isEmpty(url)) {
//			return "";
//		}
//		if (url.contains(".com") || url.contains("http") || url.contains("//")) {
//			return url;
//		}
		return url;
	}
	
	private static String getResStringByName(Context context, String xmlSchemaName){

		String xmlSchemaContent = "";
		try {
			xmlSchemaContent = ResUtil.findStringByName(context, xmlSchemaName);
		} catch (Exception e) {
			PL.w("String not find:" + xmlSchemaName);
			e.printStackTrace();
			return "";
		}
		return xmlSchemaContent;
	}

	private static String gameConfig = "";
	public static String getConfigInAssets(Context context, String key){

		if (SStringUtil.isEmpty(gameConfig)){
			gameConfig = FileUtil.readAssetsTxtFile(context,"gama/gama_game_config");
			PL.i("gameConfig:" + gameConfig);
		}
		String mVaule = "";
		if (SStringUtil.isNotEmpty(gameConfig)){
			mVaule = JsonUtil.getValueByKey(context,gameConfig,key,"");
		}
		if (SStringUtil.isEmpty(mVaule)){
			mVaule = getResStringByName(context, key);
		}
		return mVaule;
	}

}

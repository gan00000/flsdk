package com.gama.pay.onestore.bean.req;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;

public class BPayReqBean extends BaseReqeustBean {

	private String gameLanguage;
	private String accessToken;
	private String gameCode;

	/**
	 * 60 iOS   61 Google  62 web
	 */
	private String psid = "61";

	private String timestamp = System.currentTimeMillis() + "";

	private String signature = "";

	private String adId = "";//advertisingId

	public BPayReqBean(Context context) {
		super(context);

		init(context);

	}

	private void init(Context context) {

		adId = GamaUtil.getGoogleAdId(context);

		accessToken = GamaUtil.getSdkAccessToken(context);
		gameCode = ResConfig.getGameCode(context);
		gameLanguage = ResConfig.getGameLanguage(context);

		signature = SStringUtil.toMd5(ResConfig.getAppKey(context) + gameCode + timestamp);
	}

	public String getGameLanguage() {
		return gameLanguage;
	}

	public void setGameLanguage(String gameLanguage) {
		this.gameLanguage = gameLanguage;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setGameCode(String gameCode) {
		this.gameCode = gameCode;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getGameCode() {
		return gameCode;
	}

	public void setPsid(String psid) {
		this.psid = psid;
	}

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}
}

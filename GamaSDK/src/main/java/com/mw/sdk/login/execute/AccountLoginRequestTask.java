package com.mw.sdk.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.login.model.request.AccountLoginRequestBean;

public class AccountLoginRequestTask extends BaseLoginRequestTask {

	private AccountLoginRequestBean requestBean;
	
	public AccountLoginRequestTask(Context mContext, String userName, String password, String vfcode) {
		super(mContext);

		userName = userName.toLowerCase();
		password = password.trim();

		requestBean = new AccountLoginRequestBean(mContext);
		sdkBaseRequestBean = requestBean;
		requestBean.setName(userName);
		password = SStringUtil.toMd5(password);
		requestBean.setPwd(password);
		requestBean.setCaptcha(vfcode);
		requestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_LOGIN);
	}

	public AccountLoginRequestTask(Context mContext, String userName, String password) {
		super(mContext);

		userName = userName.toLowerCase();
		password = password.trim();

		requestBean = new AccountLoginRequestBean(mContext);
		sdkBaseRequestBean = requestBean;
		requestBean.setName(userName);
		password = SStringUtil.toMd5(password);
		requestBean.setPwd(password);
//		requestBean.setRequestMethod(GamaRequestMethod.GAMA_REQUEST_METHOD_LOGIN);
	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		requestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + requestBean.getTimestamp() +
				requestBean.getName() + requestBean.getGameCode()));

		return requestBean;
	}
}

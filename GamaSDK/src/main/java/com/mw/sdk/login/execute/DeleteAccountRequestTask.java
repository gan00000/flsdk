package com.mw.sdk.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.login.constant.ApiRequestMethod;
import com.mw.sdk.login.model.request.AccountLoginRequestBean;
import com.mw.sdk.login.model.request.DeleteAccountRequestBean;

public class DeleteAccountRequestTask extends BaseLoginRequestTask {

	private DeleteAccountRequestBean requestBean;

	public DeleteAccountRequestTask(Context mContext, String userId, String loginMode, String thirdLoginId,String loginAccessToken,String loginTimestamp) {
		super(mContext);


		requestBean = new DeleteAccountRequestBean(mContext, userId,loginMode,thirdLoginId);
		sdkBaseRequestBean = requestBean;
		sdkBaseRequestBean.setLoginAccessToken(loginAccessToken);
		sdkBaseRequestBean.setLoginTimestamp(loginTimestamp);
		requestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_DELETE_ACCOUNT);
	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		requestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + requestBean.getTimestamp() +
				requestBean.getUserId() + requestBean.getGameCode()));

		return requestBean;
	}
}

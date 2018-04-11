package com.starpy.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.starpy.data.login.request.AccountLoginRequestBean;

public class AccountLoginRequestTask extends BaseLoginRequestTask {

	private AccountLoginRequestBean requestBean;
	
	public AccountLoginRequestTask(Context mContext, String userName, String password) {
		super(mContext);

		userName = userName.toLowerCase();
		password = password.trim();

		requestBean = new AccountLoginRequestBean(mContext);
		sdkBaseRequestBean = requestBean;
		requestBean.setName(userName);
		password = SStringUtil.toMd5(password);
		requestBean.setPwd(password);

		requestBean.setRequestMethod("login");


	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		requestBean.setSignature(SStringUtil.toMd5(requestBean.getAppKey() + requestBean.getTimestamp() +
				requestBean.getName() + requestBean.getPwd() + requestBean.getGameCode()));

		return requestBean;
	}
}

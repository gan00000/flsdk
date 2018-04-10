package com.starpy.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.starpy.data.login.request.FindPwdRequestBean;

public class FindPwdRequestTask extends BaseLoginRequestTask {

	private FindPwdRequestBean pwdRequestBean;

	public FindPwdRequestTask(Context mContext, String userName, String email) {
		super(mContext);

		userName = userName.toLowerCase();

		pwdRequestBean = new FindPwdRequestBean(mContext);
		sdkBaseRequestBean = pwdRequestBean;
		pwdRequestBean.setName(userName);
		pwdRequestBean.setEmail(email);

		pwdRequestBean.setRequestMethod("findPwd");


	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		pwdRequestBean.setSignature(SStringUtil.toMd5(pwdRequestBean.getAppKey() + pwdRequestBean.getTimestamp() +
				pwdRequestBean.getName() + pwdRequestBean.getGameCode()));

		return pwdRequestBean;
	}
}

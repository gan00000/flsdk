package com.gama.data.login.execute;

import android.content.Context;
import android.telephony.gsm.GsmCellLocation;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.data.login.request.FindPwdRequestBean;

public class FindPwdRequestTask extends BaseLoginRequestTask {

	private FindPwdRequestBean pwdRequestBean;

	public FindPwdRequestTask(Context mContext, String userName, String email) {
		super(mContext);

		userName = userName.toLowerCase();

		pwdRequestBean = new FindPwdRequestBean(mContext);
		sdkBaseRequestBean = pwdRequestBean;
		pwdRequestBean.setName(userName);
		pwdRequestBean.setEmail(email);

		pwdRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_FIND_PASSWORD);


	}

	public FindPwdRequestTask(Context mContext, String userName, String areaCode, String phone) {
		super(mContext);

		userName = userName.toLowerCase();

		pwdRequestBean = new FindPwdRequestBean(mContext);
		sdkBaseRequestBean = pwdRequestBean;
		pwdRequestBean.setName(userName);
		pwdRequestBean.setPhoneAreaCode(areaCode);
		pwdRequestBean.setPhone(phone);

		pwdRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_FIND_PASSWORD);


	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		pwdRequestBean.setSignature(SStringUtil.toMd5(pwdRequestBean.getAppKey() + pwdRequestBean.getTimestamp() +
				pwdRequestBean.getName() + pwdRequestBean.getGameCode()));

		return pwdRequestBean;
	}
}

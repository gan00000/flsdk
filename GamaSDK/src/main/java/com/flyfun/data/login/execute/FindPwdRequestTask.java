package com.flyfun.data.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.flyfun.data.login.constant.ApiRequestMethod;
import com.flyfun.data.login.request.FindPwdRequestBean;

public class FindPwdRequestTask extends BaseLoginRequestTask {

	private FindPwdRequestBean pwdRequestBean;

	public FindPwdRequestTask(Context mContext, String userName, String email) {
		super(mContext);

		userName = userName.toLowerCase();

		pwdRequestBean = new FindPwdRequestBean(mContext);
		sdkBaseRequestBean = pwdRequestBean;
		pwdRequestBean.setName(userName);
		pwdRequestBean.setEmail(email);

//		pwdRequestBean.setRequestMethod(GamaRequestMethod.GAMA_REQUEST_METHOD_FIND_PASSWORD);


	}

	public FindPwdRequestTask(Context mContext, String userName, String areaCode, String phone, String vfCode) {
		super(mContext);

		userName = userName.toLowerCase();

		pwdRequestBean = new FindPwdRequestBean(mContext);
		sdkBaseRequestBean = pwdRequestBean;
		pwdRequestBean.setName(userName);
		pwdRequestBean.setVfCode(vfCode);

		pwdRequestBean.setPhoneAreaCode(areaCode);

		if (TextUtils.isEmpty(areaCode)){

			pwdRequestBean.setEmail(phone);

		}else {

			pwdRequestBean.setPhone(phone);
		}

		pwdRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_FIND_PASSWORD);


	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		pwdRequestBean.setSignature(SStringUtil.toMd5(pwdRequestBean.getAppKey() + pwdRequestBean.getTimestamp() +
				pwdRequestBean.getName() + pwdRequestBean.getGameCode()));

		return pwdRequestBean;
	}
}

package com.mw.sdk.api.task;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.bean.req.FindPwdRequestBean;

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

	public FindPwdRequestTask(Context mContext, String userName, String newPwd, String phone, String vfCode) {
		super(mContext);

		userName = userName.toLowerCase();

		pwdRequestBean = new FindPwdRequestBean(mContext);
		sdkBaseRequestBean = pwdRequestBean;
		pwdRequestBean.setName(userName);
		pwdRequestBean.setVfCode(vfCode);
		pwdRequestBean.setNewPwd(SStringUtil.toMd5(newPwd.trim()));

//		pwdRequestBean.setPhoneAreaCode(areaCode);

//		if (TextUtils.isEmpty(areaCode)){
//
//			pwdRequestBean.setEmail(phone);
//
//		}else {
//
//			pwdRequestBean.setPhone(phone);
//		}

		pwdRequestBean.setEmail(phone);

		pwdRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_FIND_PASSWORD);


	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		pwdRequestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + pwdRequestBean.getTimestamp() +
				pwdRequestBean.getEmail() + pwdRequestBean.getGameCode()));

		return pwdRequestBean;
	}
}

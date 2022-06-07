package com.mw.sdk.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.login.constant.ApiRequestMethod;
import com.mw.sdk.login.model.request.FindPwdRequestBean;

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
				pwdRequestBean.getName() + pwdRequestBean.getGameCode()));

		return pwdRequestBean;
	}
}

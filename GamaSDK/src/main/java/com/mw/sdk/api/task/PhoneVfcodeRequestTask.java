package com.mw.sdk.api.task;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.bean.req.PhoneVfcodeRequestBean;

public class PhoneVfcodeRequestTask extends BaseLoginRequestTask {

	private PhoneVfcodeRequestBean requestBean;

	/**
	 *
	 * @param mContext
	 * @param areaCode
	 * @param phone
	 * @param interfaceName 用到的接口名称，注册：1，绑定：2，手机验证：3 {@link ApiRequestMethod.RequestVfcodeInterface}
	 */
	public PhoneVfcodeRequestTask(Context mContext, String areaCode, String phone, String interfaceName) {
		super(mContext);

		requestBean = new PhoneVfcodeRequestBean(mContext);
		sdkBaseRequestBean = requestBean;
		requestBean.setPhone(phone);
		requestBean.setPhoneAreaCode(areaCode);
		requestBean.setInterfaces(interfaceName);
		requestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_GET_PHONT_VFCODE);

	}


	public PhoneVfcodeRequestTask(Context mContext, String email, String interfaceName) {
		super(mContext);

		requestBean = new PhoneVfcodeRequestBean(mContext);
		sdkBaseRequestBean = requestBean;
		requestBean.setEmail(email);
		requestBean.setInterfaces(interfaceName);
		requestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_GET_PHONT_VFCODE);

	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		requestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) +
				requestBean.getTimestamp() +
				requestBean.getEmail() +
				requestBean.getGameCode()));

		return requestBean;
	}

}

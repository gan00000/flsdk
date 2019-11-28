package com.gama.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.data.login.request.PhoneVfcodeRequestBean;

public class PhoneVfcodeRequestTask extends BaseLoginRequestTask {

	private PhoneVfcodeRequestBean requestBean;

	/**
	 *
	 * @param mContext
	 * @param areaCode
	 * @param phone
	 * @param interfaceName 用到的接口名称，注册：1，绑定：2，手机验证：3 {@link com.gama.data.login.constant.GSRequestMethod.RequestVfcodeInterface}
	 */
	public PhoneVfcodeRequestTask(Context mContext, String areaCode, String phone, String interfaceName) {
		super(mContext);

		requestBean = new PhoneVfcodeRequestBean(mContext);
		sdkBaseRequestBean = requestBean;
		requestBean.setPhone(phone);
		requestBean.setPhoneAreaCode(areaCode);
		requestBean.setInterfaces(interfaceName);
		requestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_GET_PHONT_VFCODE);

	}


	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();

		requestBean.setSignature(SStringUtil.toMd5(requestBean.getAppKey()
				+ requestBean.getTimestamp() +
				requestBean.getGameCode()
				+ requestBean.getPhone()));

		return requestBean;
	}

}

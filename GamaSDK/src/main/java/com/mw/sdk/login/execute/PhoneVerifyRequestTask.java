package com.mw.sdk.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.login.model.request.PhoneVerifyRequestBean;

public class PhoneVerifyRequestTask extends BaseLoginRequestTask {

	private PhoneVerifyRequestBean requestBean;

	public PhoneVerifyRequestTask(Context mContext, String area, String phone, String vfCode, String thirdId, String registPlatform) {
		super(mContext);
		requestBean = new PhoneVerifyRequestBean(mContext);
		sdkBaseRequestBean = requestBean;
		requestBean.setVfCode(vfCode);

		if (TextUtils.isEmpty(area)){

			requestBean.setEmail(phone);

		}else {

			requestBean.setPhone(phone);
		}
		requestBean.setPhoneAreaCode(area);
		requestBean.setThirdPlatId(thirdId);
		requestBean.setRegistPlatform(registPlatform);
		requestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_PHONE_VERIFY);
	}

	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();
		//签名，规则 AppKey + timestamp + gameCode + thirdPlatId + registPlatform
		requestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + requestBean.getTimestamp() +
				requestBean.getGameCode() + requestBean.getThirdPlatId() + requestBean.getRegistPlatform()));

		return requestBean;
	}
}

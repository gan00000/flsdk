package com.flyfun.data.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.flyfun.data.login.constant.GSRequestMethod;
import com.flyfun.data.login.request.PhoneVerifyRequestBean;

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
		requestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_PHONE_VERIFY);
	}

	@Override
	public BaseReqeustBean createRequestBean() {
		super.createRequestBean();
		//签名，规则 AppKey + timestamp + gameCode + thirdPlatId + registPlatform
		requestBean.setSignature(SStringUtil.toMd5(requestBean.getAppKey() + requestBean.getTimestamp() +
				requestBean.getGameCode() + requestBean.getThirdPlatId() + requestBean.getRegistPlatform()));

		return requestBean;
	}
}

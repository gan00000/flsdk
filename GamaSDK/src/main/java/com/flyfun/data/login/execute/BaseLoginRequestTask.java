/**
 * 
 */
package com.flyfun.data.login.execute;

import android.content.Context;

import com.core.base.request.AbsHttpRequest;
import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.flyfun.base.bean.SSdkBaseRequestBean;
import com.flyfun.base.cfg.ResConfig;

/**
 * <p>Title: BaseLoginRequestTask</p>
 * <p>Description: 请求接口对象封装类</p>
 * @author GanYuanrong
 * @date 2013年12月10日
 */
public class BaseLoginRequestTask extends AbsHttpRequest {

	protected Context context;

	protected SSdkBaseRequestBean sdkBaseRequestBean;

//	public void setSdkBaseRequestBean(SSdkBaseRequestBean sdkBaseRequestBean) {
//		this.sdkBaseRequestBean = sdkBaseRequestBean;
//	}


	public SSdkBaseRequestBean getSdkBaseRequestBean() {
		return sdkBaseRequestBean;
	}

	public BaseLoginRequestTask(Context context) {
		this.context = context;
	}


	@Override
	public BaseReqeustBean createRequestBean() {
		if (this.context == null) {
			PL.d("execute context is null");
			return null;
		}

		if (sdkBaseRequestBean == null){
			PL.d("sdkBaseRequestBean is null");
			return null;
		}
		if (SStringUtil.isEmpty(sdkBaseRequestBean.getRequestUrl())) {
			sdkBaseRequestBean.setRequestUrl(ResConfig.getLoginPreferredUrl(context));
		}
		if (SStringUtil.isEmpty(sdkBaseRequestBean.getRequestSpaUrl())) {
			sdkBaseRequestBean.setRequestSpaUrl(ResConfig.getLoginSpareUrl(context));
		}
		if (SStringUtil.isEmpty(sdkBaseRequestBean.getGameCode())) {
			sdkBaseRequestBean.setGameCode(ResConfig.getGameCode(context));
		}
		if (SStringUtil.isEmpty(sdkBaseRequestBean.getAppKey())) {
			sdkBaseRequestBean.setAppKey(ResConfig.getAppKey(context));
		}


		if(SStringUtil.isEmpty(sdkBaseRequestBean.getGameLanguage())){
			sdkBaseRequestBean.setGameLanguage(ResConfig.getGameLanguage(context));
		}

//		sdkBaseRequestBean.setReferrer(PyLoginHelper.takeReferrer(context, ""));


//		sdkBaseRequestBean.setAdvertisingId(GoogleUtil.getAdvertisingId(this.context));

		return sdkBaseRequestBean;
	}

	@Override
	public <T> void onHttpSucceess(T responseModel) {

	}

	@Override
	public void onTimeout(String result) {
		PL.i("onTimeout");
		ToastUtils.toast(context, "connect timeout, please try again");
	}

	@Override
	public void onNoData(String result) {
		PL.i("onNoData");
	}


}

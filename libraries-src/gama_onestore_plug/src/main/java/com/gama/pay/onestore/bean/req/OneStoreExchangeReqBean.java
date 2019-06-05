package com.gama.pay.onestore.bean.req;

import android.content.Context;

import com.gama.pay.onestore.constants.OneStoreContant;

public class OneStoreExchangeReqBean extends BPayReqBean {


	private String purchaseData;

	public String getPurchaseData() {
		return purchaseData;
	}

	public void setPurchaseData(String purchaseData) {
		this.purchaseData = purchaseData;
	}

	public OneStoreExchangeReqBean(Context context) {
		super(context);
		setPsid(OneStoreContant.PSID);
	}


}

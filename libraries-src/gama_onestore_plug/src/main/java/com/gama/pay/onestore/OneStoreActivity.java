package com.gama.pay.onestore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.core.base.SBaseActivity;
import com.gama.pay.IOneStorePay;
import com.gama.pay.IPayCallBack;
import com.gama.pay.IPayFactory;
import com.gama.pay.onestore.bean.req.OneStoreCreateOrderIdReqBean;

public class OneStoreActivity extends SBaseActivity {

	public static final String OneStorePayReqBean_Extra_Key = "OneStorePayReqBean_Extra_Key";
	public static final int OneStorePayResultCode = 92;


	private IOneStorePay iPay;
	private OneStoreCreateOrderIdReqBean oneStoreCreateOrderIdReqBean;

	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = this;

		iPay = IPayFactory.create(activity);
		iPay.onCreate(this);
		//设置Google储值的回调
		iPay.setIPayCallBack(new IPayCallBack() {

			@Override
			public void success(Bundle bundle) {
				setResultForPay(bundle);
				activity.finish();
			}

			@Override
			public void fail(Bundle bundle) {
				setResultForPay(bundle);
				activity.finish();
			}
		});

		Intent intent = getIntent();
		if (intent != null){
			oneStoreCreateOrderIdReqBean = (OneStoreCreateOrderIdReqBean) intent.getSerializableExtra(OneStorePayReqBean_Extra_Key);

			iPay.startPay(this, oneStoreCreateOrderIdReqBean);
		}
	}

	private void setResultForPay(Bundle bundle) {
		Intent resultIntent = new Intent();
		resultIntent.putExtras(bundle);
		setResult(OneStorePayResultCode,resultIntent);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		iPay.onActivityResult(this,requestCode,resultCode,data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		iPay.onDestroy(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		iPay.onResume(this);
	}
}

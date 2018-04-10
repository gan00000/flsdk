package com.starpy.pay.gp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.core.base.SBaseActivity;
import com.starpy.pay.IPay;
import com.starpy.pay.IPayCallBack;
import com.starpy.pay.IPayFactory;
import com.starpy.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;

public class GooglePayActivity2 extends SBaseActivity {

	public static final String GooglePayReqBean_Extra_Key = "GooglePayReqBean_Extra_Key";
	public static final int GooglePayReqeustCode = 90;
	public static final int GooglePayResultCode = 91;


	private IPay iPay;
	private GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean;

	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = this;

		iPay = IPayFactory.create(IPayFactory.PAY_GOOGLE);
		iPay.onCreate(this);

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
			googlePayCreateOrderIdReqBean = (GooglePayCreateOrderIdReqBean) intent.getSerializableExtra(GooglePayReqBean_Extra_Key);

			iPay.startPay(this,googlePayCreateOrderIdReqBean);
		}
	}

	private void setResultForPay(Bundle bundle) {
		Intent resultIntent = new Intent();
		resultIntent.putExtras(bundle);
		setResult(GooglePayResultCode,resultIntent);
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

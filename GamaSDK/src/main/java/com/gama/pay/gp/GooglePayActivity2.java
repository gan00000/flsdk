package com.gama.pay.gp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.core.base.SBaseActivity;
import com.facebook.CallbackManager;
import com.gama.pay.IPay;
import com.gama.pay.IPayCallBack;
import com.gama.pay.IPayFactory;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;

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
		// TODO: 2018/7/5  Facebook统计储值数据
		CallbackManager.Factory.create().onActivityResult(requestCode, resultCode, data);
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

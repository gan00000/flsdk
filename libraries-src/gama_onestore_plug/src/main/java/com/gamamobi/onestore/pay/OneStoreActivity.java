package com.gamamobi.onestore.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.core.base.SBaseActivity;
import com.gamamobi.onestore.IOneStorePay;
import com.gamamobi.onestore.IPayCallBack;
import com.gamamobi.onestore.IPayFactory;
import com.gamamobi.onestore.pay.bean.req.OneStoreCreateOrderIdReqBean;

public class OneStoreActivity extends SBaseActivity {

	public static final String OneStorePayReqBean_Extra_Key = "OneStorePayReqBean_Extra_Key";
	public static final int ONESTOREPAYRESULTCODE = 92;
	public static final int ONESTOREPAYREQEUSTCODE = 93;


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
		setResult(ONESTOREPAYRESULTCODE,resultIntent);
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

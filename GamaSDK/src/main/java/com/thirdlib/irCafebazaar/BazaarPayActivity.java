package com.thirdlib.irCafebazaar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.core.base.SBaseActivity;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.res.BasePayBean;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.IPayFactory;

public class BazaarPayActivity extends SBaseActivity {

    public static final String K_Extra_Data = "K_Extra_Data";
    public static final String K_Extra_Code = "K_Extra_Code";
	public static final int Result_Code_BazaarPay = 94;
	public static final int Request_Code_BazaarPay = 95;


	private IPay iPay;
	private PayCreateOrderReqBean payCreateOrderReqBean;

	private AppCompatActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = this;

		iPay = IPayFactory.create(activity);
		iPay.onCreate(this);
		//设置Google储值的回调
		iPay.setIPayCallBack(new IPayCallBack() {
			@Override
			public void success(BasePayBean basePayBean) {
                setResultForPay(1000, basePayBean);
			}

			@Override
			public void fail(BasePayBean basePayBean) {
                setResultForPay(1001, null);
			}

			@Override
			public void cancel(String msg) {
                setResultForPay(1002, null);
			}
		});

		Intent intent = getIntent();
		if (intent != null){
			payCreateOrderReqBean = (PayCreateOrderReqBean) intent.getSerializableExtra(K_Extra_Data);
            if (payCreateOrderReqBean != null) {
                iPay.startPay(this, payCreateOrderReqBean);
                return;
            }
        }
        finish();
	}

	private void setResultForPay(int code, BasePayBean payBean) {
		Intent resultIntent = new Intent();
        resultIntent.putExtra(K_Extra_Code, code);
        if (payBean != null) {
            resultIntent.putExtra(K_Extra_Data, payBean);
        }
        setResult(Result_Code_BazaarPay,resultIntent);

        activity.finish();
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

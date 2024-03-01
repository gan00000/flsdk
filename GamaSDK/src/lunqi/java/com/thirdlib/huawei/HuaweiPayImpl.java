package com.thirdlib.huawei;


import android.app.Activity;
import android.content.Intent;

import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.pay.IPayCallBack;

public class HuaweiPayImpl {

    public static final int HW_IAP_REQUEST_CODE_ENVREADY = 6660;
    public static final int HW_IAP_REQUEST_CODE_PURCHASE = 6661;

    private Activity mActivity;

    private String productId;
    private String cpOrderId;
    private String extra;


    public void setPayCallBack(IPayCallBack iPayCallBack) {
    }

    public HuaweiPayImpl(Activity mActivity) {
        this.mActivity = mActivity;
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    public void startPay(Activity activity, PayCreateOrderReqBean createOrderIdReqBean) {

    }

}

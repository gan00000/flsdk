package com.mw.sdk.pay;

import android.app.Activity;
import android.content.Context;

import com.core.base.callback.IGameLifeCycle;
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.bean.req.PayReqBean;

/**
 * Created by gan on 2017/2/23.
 */

public interface IPay extends IGameLifeCycle{
//    String PAY_STATUS = "status";
//    int PAY_SUCCESS = 93;
//    int PAY_FAIL = 94;

    public static final String SDK_MW_PAY_FILE = "sdk_mw_pay_file.xml";

    public static final String K_PAY_Extra_Data = "K_PAY_Extra_Data";
    public static final String K_PAY_Extra_Code = "K_PAY_Extra_Code";

    public static final String TAG_USER_CANCEL = "TAG_PAY_USER_CANCEL";
    public static final int TAG_PAY_SUCCESS= 1000;
    public static final int TAG_PAY_FAIL = 1001;
    public static final int TAG_PAY_CANCEL = 1002;
    /**
     * 进入Google储值流程
     */
    void startPay(Activity activity,PayReqBean payReqBean);
    void startQueryPurchase(Activity activity);

    /**
     * 设置Google储值的回调
     */
    void setIPayCallBack(IPayCallBack iPayCallBack);

    void queryPreRegData(Activity activity, ISdkCallBack iSdkCallBack);
}

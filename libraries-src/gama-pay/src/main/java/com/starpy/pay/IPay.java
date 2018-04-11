package com.starpy.pay;

import android.app.Activity;

import com.core.base.callback.IGameLifeCycle;
import com.starpy.pay.gp.bean.req.PayReqBean;

/**
 * Created by gan on 2017/2/23.
 */

public interface IPay extends IGameLifeCycle{

    public void startPay(Activity activity,PayReqBean payReqBean);

    public void setIPayCallBack(IPayCallBack iPayCallBack);
}

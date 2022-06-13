package com.gama.pay;

import android.os.Bundle;

import com.core.base.callback.ISCallBack;
import com.gama.pay.gp.bean.res.BasePayBean;
import com.gama.pay.gp.bean.res.GPExchangeRes;

/**
 * Created by gan on 2017/2/23.
 */

public interface IPayCallBack extends ISCallBack {

    void success(BasePayBean basePayBean);
    void fail(BasePayBean basePayBean);

}

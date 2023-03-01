package com.mw.sdk.pay;

import com.core.base.callback.ISCallBack;
import com.mw.sdk.pay.gp.bean.res.BasePayBean;

/**
 * Created by gan on 2017/2/23.
 */

public interface IPayCallBack extends ISCallBack {

    void success(BasePayBean basePayBean);
    void fail(BasePayBean basePayBean);

    void cancel(String msg);

}

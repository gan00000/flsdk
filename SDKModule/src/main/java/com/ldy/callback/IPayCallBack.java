package com.ldy.callback;

import com.ldy.sdk.pay.gp.res.BasePayBean;

/**
 * Created by gan on 2017/2/23.
 */

public interface IPayCallBack extends ISCallBack {

    void success(BasePayBean basePayBean);
    void fail(BasePayBean basePayBean);

    void cancel(String msg);

}

package com.mw.sdk.pay;

import com.mw.sdk.pay.gp.GooglePayImpl;

/**
 * Created by gan on 2017/2/23.
 */

public class IPayFactory {

    public static final int PAY_GOOGLE = 0;

    public static IPay create(int payTpye){
        return new GooglePayImpl();
    }
}

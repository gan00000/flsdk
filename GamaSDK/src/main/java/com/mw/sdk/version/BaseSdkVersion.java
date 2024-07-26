package com.mw.sdk.version;

import com.mw.sdk.pay.IPay;
import com.thirdlib.IThirdHelper;

public abstract class BaseSdkVersion {

    public IThirdHelper newNaverHelper(){
        return null;
    }
    public IPay newOneStorePay(){
        return null;
    }

    public IPay newSamsungPay() {
        return null;
    }

    public IPay newVKPay() {
        return null;
    }
}

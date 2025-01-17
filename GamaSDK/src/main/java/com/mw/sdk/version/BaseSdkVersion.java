package com.mw.sdk.version;

import com.core.base.utils.PL;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.nowgg.NowggPayImpl;
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

    public IPay newNowggPay(){

        try {
            Class<?> clazz = Class.forName("gg.now.billingclient.api.BillingClient");
            if (clazz == null){
                return null;
            }
        } catch (ClassNotFoundException e) {
            PL.w("nowgg pay module not exist.");
            return null;
        }

        return new NowggPayImpl();
    }
}

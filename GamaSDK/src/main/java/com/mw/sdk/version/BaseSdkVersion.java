package com.mw.sdk.version;

import com.core.base.utils.PL;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.nowgg.NowggPayImpl;
import com.thirdlib.IThirdHelper;
import com.thirdlib.ThirdModuleUtil;
import com.thirdlib.vk.VKPayImpl;
import com.thirdlib.xiaomi.XiaomiPayImpl;

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

        if (ThirdModuleUtil.existRustoreModule()){
            return new VKPayImpl();
        }
        return null;
    }

    public IPay newNowggPay(){

        if (ThirdModuleUtil.existNowggModule()){
            return new NowggPayImpl();
        }
        return null;
    }

    public IPay newXiaomiPay(){

        if (ThirdModuleUtil.existXiaomiModule()){
            return new XiaomiPayImpl();
        }
        return null;
    }
}

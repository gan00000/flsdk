package com.mw.sdk.pay;

import android.content.Context;

/**
 * Created by gan on 2017/2/23.
 */

public class IPayFactory {

//    public static final int PAY_GOOGLE = 0;

    public static IPay create(Context context){

//        String channel_platform = context.getResources().getString(R.string.channel_platform);
//        if(ChannelPlatform.ONESTORE.getChannel_platform().equals(channel_platform)) {
//            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
//            return sdkVersionUtil.newOneStorePay();
//        } else if (ChannelPlatform.SAMSUNG.getChannel_platform().equals(channel_platform)) {
//            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
//            return sdkVersionUtil.newSamsungPay();
//        } else {
//            return new GooglePayImpl();
//        }
        return null;

    }
}

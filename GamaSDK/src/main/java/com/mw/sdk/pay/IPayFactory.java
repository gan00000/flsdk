package com.mw.sdk.pay;

import android.content.Context;

import com.mw.base.utils.SdkVersionUtil;
import com.mw.sdk.R;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.pay.gp.GooglePayImpl;

/**
 * Created by gan on 2017/2/23.
 */

public class IPayFactory {

//    public static final int PAY_GOOGLE = 0;

    public static IPay create(Context context){
        return create(context, null);
    }
    public static IPay create(Context context, ChannelPlatform channelPlatform){

        String channel_platform = context.getResources().getString(R.string.channel_platform);
        if (channelPlatform != null){//代码设置的为主
            channel_platform = channelPlatform.getChannel_platform();
        }

        if(ChannelPlatform.ONESTORE.getChannel_platform().equals(channel_platform)) {
            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
            return sdkVersionUtil.newOneStorePay();
        } else if (ChannelPlatform.SAMSUNG.getChannel_platform().equals(channel_platform)) {
            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
            return sdkVersionUtil.newSamsungPay();
        }  else if (ChannelPlatform.VK.getChannel_platform().equals(channel_platform)) {
            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
            return sdkVersionUtil.newVKPay();
        }else if (ChannelPlatform.NOWGG.getChannel_platform().equals(channel_platform)) {
            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
            return sdkVersionUtil.newNowggPay();
        }else if (ChannelPlatform.Xiaomi.getChannel_platform().equals(channel_platform)) {
            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
            return sdkVersionUtil.newXiaomiPay();
        }else {
            return new GooglePayImpl();
        }

    }
}

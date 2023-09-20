package com.mw.sdk.pay;

import android.content.Context;

import com.mw.base.bean.SPayType;
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

        String channel_platform = context.getResources().getString(R.string.channel_platform);
        if(ChannelPlatform.ONESTORE.getChannel_platform().equals(channel_platform)) {
            SdkVersionUtil sdkVersionUtil = new SdkVersionUtil();
            return sdkVersionUtil.newOneStorePay();
        }else {
            return new GooglePayImpl();
        }

    }
}

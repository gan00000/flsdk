package com.mw.base.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;

import com.core.base.utils.SStringUtil;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.R;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.login.widget.v2.AgeQuaLayoutV2;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.version.BaseSdkVersion;
import com.thirdlib.IThirdHelper;
import com.thirdlib.never.NeverLogin;
import com.thirdlib.onepay.OnestorePayImpl;
import com.thirdlib.samsung.SansungPayImpl;

/**
 * Created by gan on 2017/2/7.
 */

public class SdkVersionUtil extends BaseSdkVersion {



    public static SLoginBaseRelativeLayout newAgeQualifiedView(Context context){

        return new AgeQuaLayoutV2(context);
    }

    @Override
    public IThirdHelper newNaverHelper() {
        return new NeverLogin();
    }

    @Override
    public IPay newOneStorePay() {
        return new OnestorePayImpl();
    }

    public IPay newSamsungPay() {
        return new SansungPayImpl();
    }
}

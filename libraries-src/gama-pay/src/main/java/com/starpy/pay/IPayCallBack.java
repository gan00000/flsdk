package com.starpy.pay;

import android.os.Bundle;

import com.core.base.callback.ISCallBack;

/**
 * Created by gan on 2017/2/23.
 */

public interface IPayCallBack extends ISCallBack {

    void success(Bundle bundle);
    void fail(Bundle bundle);

}

package com.gama.pay;

import android.app.Activity;

import com.gama.pay.onestore.OneStoreImpl;

/**
 * Created by gan on 2017/2/23.
 */

public class IPayFactory {

    public static IOneStorePay create(Activity activity){
        return new OneStoreImpl(activity);
    }
}

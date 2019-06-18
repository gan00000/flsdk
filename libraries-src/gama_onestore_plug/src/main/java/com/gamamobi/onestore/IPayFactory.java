package com.gamamobi.onestore;

import android.app.Activity;

import com.gamamobi.onestore.pay.OneStoreImpl;

/**
 * Created by gan on 2017/2/23.
 */

public class IPayFactory {

    public static IOneStorePay create(Activity activity){
        return new OneStoreImpl(activity);
    }
}

package com.mw.sdk.out;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class FlSdkFactory {

    public static IFLSDK create(){

        return new SdkImpl();
    }

}

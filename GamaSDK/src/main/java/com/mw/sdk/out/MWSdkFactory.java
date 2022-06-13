package com.mw.sdk.out;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class MWSdkFactory {

    public static IMWSDK create(){

        return new SdkImpl();
    }

}

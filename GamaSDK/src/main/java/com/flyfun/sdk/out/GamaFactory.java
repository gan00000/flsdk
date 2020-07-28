package com.flyfun.sdk.out;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class GamaFactory {

    public static IFLSDK create(){

        return new SdkImpl();
    }

}

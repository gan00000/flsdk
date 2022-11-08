package com.mw.sdk.out;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class MWSdkFactory {

    private static IMWSDK imwsdk;
    public synchronized static IMWSDK create(){
        if (imwsdk== null){
            imwsdk = new BaseSdkImpl();
        }
        return imwsdk;
        //return new BaseSdkImpl();
    }

}

package com.ldy.pub;

import com.ldy.sdk.pub.MWSdkImpl;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class MWSdkFactory {

    private static IMWSDK imwsdk;
    public synchronized static IMWSDK create(){
        if (imwsdk== null){
            imwsdk = new MWSdkImpl();
        }
        return imwsdk;
        //return new BaseSdkImpl();
    }

}

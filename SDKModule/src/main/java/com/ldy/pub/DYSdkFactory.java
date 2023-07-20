package com.ldy.pub;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class DYSdkFactory {

    private static IDYSDK IDYSDK;
    public synchronized static IDYSDK create(){
        if (IDYSDK == null){
            IDYSDK = new SdkImpl();
        }
        return IDYSDK;
        //return new SimpleSdkImpl();
    }

}

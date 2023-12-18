package com.ldy.pub;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class DYSdk {

    private static IDYSDK IDYSDK;
    public synchronized static IDYSDK getInstance(){
        if (IDYSDK == null){
            IDYSDK = new SdkImpl();
        }
        return IDYSDK;
        //return new SimpleSdkImpl();
    }

}

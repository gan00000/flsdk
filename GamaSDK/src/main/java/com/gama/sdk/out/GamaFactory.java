package com.gama.sdk.out;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class GamaFactory {

    public static IGama create(){

        return new GamaImpl();
    }

}

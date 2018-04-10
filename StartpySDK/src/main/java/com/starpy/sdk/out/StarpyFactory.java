package com.starpy.sdk.out;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public class StarpyFactory {

    public static IStarpy create(){

        return new StarpyImpl();
    }

}

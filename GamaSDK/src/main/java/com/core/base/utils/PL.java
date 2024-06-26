package com.core.base.utils;

import android.util.Log;

/**
 * Created by gan on 2016/11/28.
 */

public class PL {

    public final static String PL_LOG = "PL_LOG";

    public static void d(String format, Object... args){
        print(SdkLogLevel.Debug,format, args);
    }

    public static void d(String msg){
        print(SdkLogLevel.Debug, PL_LOG, msg);
    }

//    private static void d(String tag, String msg){
//        Log.d(tag,msg + "");
//    }

    public static void i(String msg){
        print(SdkLogLevel.Info, PL_LOG, msg);
    }
    public static void i(String format, Object... args){
        print(SdkLogLevel.Info,format, args);
    }

//    private static void i(String tag, String msg){
//        Log.i(tag,msg + "");
//    }

    public static void w(String msg){
        print(SdkLogLevel.Warn, PL_LOG, msg);
    }
    public static void w(String format, Object... args){
        print(SdkLogLevel.Warn,format, args);
    }

//    private static void e(String tag, String msg){
//        Log.e(tag,msg + "");
//    }

    public static void e(String msg){
        print(SdkLogLevel.Error, PL_LOG, msg);
    }
    public static void e(String format, Object... args){
        print(SdkLogLevel.Error,format, args);
    }

    private static void print(SdkLogLevel sdkLogLevel, String format, Object... args){
        if (args != null) {

            if (args.length == 0){
                print(sdkLogLevel,PL_LOG, format);
                return;
            }

            if (args.length > 1){
                print(sdkLogLevel, PL_LOG, String.format(format, args));
                return;
            }

            if (format.contains("%s")){
                print(sdkLogLevel, PL_LOG, String.format(format, args));
            }else {
                print(sdkLogLevel, format,args + "");
            }
        }else {
            print(sdkLogLevel,PL_LOG, format);
        }
    }

    private static void print(SdkLogLevel sdkLogLevel, String Tag, String msg){
            if (SdkLogLevel.Debug == sdkLogLevel){
                Log.d(Tag, msg);
            }else if (SdkLogLevel.Info == sdkLogLevel){
                Log.i(Tag, msg);
            }else if (SdkLogLevel.Warn == sdkLogLevel){
                Log.w(Tag, msg);
            }else if (SdkLogLevel.Error == sdkLogLevel){
                Log.e(Tag, msg);
            }else {
                Log.d(Tag, msg);
            }
    }

    public enum SdkLogLevel {
        Debug,
        Info,
        Warn,
        Error,
    }

}

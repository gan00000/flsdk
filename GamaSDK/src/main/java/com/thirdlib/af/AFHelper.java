package com.thirdlib.af;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.core.base.utils.PL;

import java.util.Map;

public class AFHelper {

    public static void applicationOnCreate(Application application){
        PL.i("AFHelper applicationOnCreate");

    }

    public static void activityOnCreate(Activity activity){

    }

    public static void logEvent(Context context, String eventName, Map<String, Object> map){

    }
}

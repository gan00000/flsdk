package com.starpy.sdk.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.widget.Scroller;

import com.starpy.sdk.FixedSpeedScroller;

import java.lang.reflect.Field;

/**
 * Created by gan on 2017/4/14.
 */

public class ViewPageUitl {

    public static void setScrollDuration(Context context, ViewPager mViewPager, Scroller scroller){
        /**
         * 通过反射来修改 ViewPager的mScroller属性
         */
        try {
            Class clazz = Class.forName("android.support.v4.view.ViewPager");
            Field f = clazz.getDeclaredField("mScroller");
            scroller = new FixedSpeedScroller(context,new LinearOutSlowInInterpolator());
            f.setAccessible(true);
            f.set(mViewPager,scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

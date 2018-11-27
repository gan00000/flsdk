package com.gama.thirdlib.facebook;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gan on 2016/12/21.
 */

public class FbSp {


    public static final String S_FACEBOOK_FILE = "S_FACEBOOK_FILE.XML";

    public static final String S_FB_TOKEN_FOR_BUSINESS = "S_FB_TOKEN_FOR_BUSINESS";
    public static final String S_FB_APP_BUSINESS_IDS = "S_FB_APP_BUSINESS_IDS";
    public static final String S_FB_LOGIN_ID = "S_FB_LOGIN_ID";

    public static void saveTokenForBusiness(Context context, String token_for_business){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(S_FB_TOKEN_FOR_BUSINESS,token_for_business).commit();
    }

    public static String getTokenForBusiness(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(S_FB_TOKEN_FOR_BUSINESS,"");
    }


    public static void saveAppsBusinessId(Context context, String appsBusinessId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(S_FB_APP_BUSINESS_IDS,appsBusinessId).commit();
    }

    public static String getAppsBusinessId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(S_FB_APP_BUSINESS_IDS,"");
    }

    public static void saveFbId(Context context, String fbId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(S_FB_LOGIN_ID,fbId).commit();
    }

    public static String getFbId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(S_FB_LOGIN_ID,"");
    }

    public static final String S_FB_GENDER = "S_FB_GENDER";
    public static final String S_FB_BIRTHDAY = "S_FB_BIRTHDAY";
    public static final String S_FB_NAME = "S_FB_NAME";

    public static void saveFbGender(Context context, String gender){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(S_FB_GENDER,gender).commit();
    }

    public static String getFbGender(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(S_FB_GENDER,"");
    }

    public static void saveFbBirthday(Context context, String birthday){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(S_FB_BIRTHDAY,birthday).commit();
    }

    public static String getFbBirthday(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(S_FB_BIRTHDAY,"");
    }

    public static void saveFbName(Context context, String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(S_FB_NAME,name).commit();
    }

    public static String getFbName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(S_FACEBOOK_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(S_FB_NAME,"");
    }

}

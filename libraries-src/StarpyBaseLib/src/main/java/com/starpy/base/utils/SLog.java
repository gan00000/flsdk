package com.starpy.base.utils;

import android.util.Log;

public class SLog {
	

	private static final String S_LOG_TAG = "S_LOG";

	private static boolean mDebugLog = false;
	private static boolean mInfo = false;

	public static void enableDebug(boolean debug){
		SLog.mDebugLog = debug;
	}
	
	public static void enableInfo(boolean mInfo){
		SLog.mInfo = mInfo;
	}
	
	public static  void logD(String msg) {
        if (mDebugLog) Log.i(S_LOG_TAG, msg + "");
    }
	
	public static  void logD(String tag, String msg) {
        if (mDebugLog) Log.i(tag, msg);
    }
	
	public static void logI(String msg) {
		if (mInfo) Log.i(S_LOG_TAG, msg + "");
	}
	
	public static void logI(String tag, String msg) {
		if (mInfo) Log.i(tag, msg);
	}
	
	public static void logE(String msg) {
		Log.e(S_LOG_TAG,  msg);
	}
	
	public static void logW(String msg) {
		Log.w(S_LOG_TAG, msg);
	}
	


}

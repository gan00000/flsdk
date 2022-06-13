package com.core.base.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtils {

	public static void toast(Context context, String msg) {
		if (context != null && !TextUtils.isEmpty(msg)) {
			toast(context,msg,Toast.LENGTH_SHORT);
		}
	}

	public static void toast(Context context, int stringId) {
		if (context != null) {
			toast(context,context.getString(stringId),Toast.LENGTH_SHORT);
		}
	}

	public static void toast(Context context, String msg, int time) {
		if (context != null) {
			Toast toast = Toast.makeText(context, msg + "", time);
			toast.setGravity(Gravity.CENTER,0,0);
			toast.show();
		}
	}

}

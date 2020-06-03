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
			Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
		}
	}

	public static void toast(Context context, int msg) {
		if (context != null) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	public static void toast(Context context, int msg, int time) {
		if (context != null) {
			if (time == Toast.LENGTH_LONG) {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public static void toast(Context context, String msg, int time) {
		if (context != null) {
			if (time == Toast.LENGTH_LONG) {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}

}

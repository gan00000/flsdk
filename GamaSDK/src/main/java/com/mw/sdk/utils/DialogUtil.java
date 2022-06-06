package com.mw.sdk.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mw.sdk.R;

/**
 * Created by gan on 2017/2/6.
 */

public class DialogUtil {

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.mw_sdk_loading, null);
        TextView msgTv = (TextView) v.findViewById(R.id.dialog_loading_text);
        msgTv.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.Gama_Theme_AppCompat_Dialog_NoTitle);
//		loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    public static Dialog createDialog(Context context, int layout) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layout, null);
        Dialog loadingDialog = new Dialog(context, R.style.Gama_Theme_AppCompat_Dialog_NoTitle);
		loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    public static Dialog createDialog(Context context, View contentView) {
        Dialog loadingDialog = new Dialog(context, R.style.Gama_Theme_AppCompat_Dialog_NoTitle);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    public static Dialog createDialog(Context context, View contentView,boolean cancelable,boolean outside) {
        Dialog loadingDialog = new Dialog(context, R.style.Gama_Theme_AppCompat_Dialog_NoTitle);
        loadingDialog.setCancelable(cancelable);
        loadingDialog.setCanceledOnTouchOutside(outside);
        loadingDialog.setContentView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
        return loadingDialog;
    }

}

package com.mw.sdk.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
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
        Dialog loadingDialog = new Dialog(context, R.style.Sdk_Theme_AppCompat_Dialog_NoTitle);
//		loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    public static Dialog createDialog(Context context, int layout) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layout, null);
        Dialog loadingDialog = new Dialog(context, R.style.Sdk_Theme_AppCompat_Dialog_NoTitle);
		loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局

        return loadingDialog;
    }

    public static Dialog createDialog(Context context, View contentView) {
        Dialog loadingDialog = new Dialog(context, R.style.Sdk_Theme_AppCompat_Dialog_NoTitle);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    public static Dialog createDialog(Context context, View contentView,boolean cancelable,boolean outside) {
        Dialog loadingDialog = new Dialog(context, R.style.Sdk_Theme_AppCompat_Dialog_NoTitle);
        loadingDialog.setCancelable(cancelable);
        loadingDialog.setCanceledOnTouchOutside(outside);
        loadingDialog.setContentView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
        return loadingDialog;
    }


    //alert

    public static void showAlert(Context context, String message) {
        if (TextUtils.isEmpty(message)){
            return;
        }
        Dialog dialog =  createDialog(context, message, "", "OK", false, new DialogCallback() {
            @Override
            public void onConfirm(DialogInterface dialog, int which) {

            }

            @Override
            public void onCancel(DialogInterface dialog, int which) {

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    public static Dialog createDialog(Context context, String message, String cancelText, String confirmText, DialogCallback callback) {
        return createDialog(context,message,cancelText,confirmText,true,callback);
    }

    public static Dialog createDialog(Context context, String message, String cancelText, String confirmText, boolean cancelable, DialogCallback callback) {
        AlertDialog.Builder builder = createBuilder(context);
        builder.setMessage(message);
        builder.setCancelable(cancelable);

        if (!TextUtils.isEmpty(cancelText)) {
            builder.setPositiveButton(cancelText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.onConfirm(dialog, which);
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(confirmText)) {
            builder.setNegativeButton(confirmText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.onCancel(dialog, which);
                    }
                }
            });
        }
        return builder.create();
    }

    public interface DialogCallback {
        void onConfirm(DialogInterface dialog, int which);
        void onCancel(DialogInterface dialog, int which);
    }

    public static AlertDialog.Builder createBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog);
        } else {
            return new AlertDialog.Builder(context);
        }
    }

}

package com.mw.sdk.api.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.core.base.utils.PL;

/**
 * Loading窗工具
 * Created by gan on 2017/2/23.
 */

public class LoadingDialog {

    private ProgressDialog progressDialog = null;

    private Activity mActivity;

    public LoadingDialog(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void showProgressDialog() {
        showProgressDialog("Loading");
    }
    /**
     * <p>Title: showProgressDialog</p>
     * <p>Description: 显示一个进度条</p>
     * @param message
     */
    public void showProgressDialog(String message) {


        if (mActivity == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mActivity);
        }

        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                PL.i("pay ProgressDialog Cancel");
                dialog.dismiss();
            }
        });
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                PL.i("pay progressDialog onDismiss");
            }
        });
        progressDialog.show();
    }

    /**
     * <p>Title: dismissProgressDialog</p>
     * <p>Description: 进度条消失</p>
     */
    public void dismissProgressDialog() {

        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * <p>Title: complain</p>
     * <p>Description: 弹出对话框</p>
     * @param message
     */
    public void complain(String message) {
        alert(message, null);
    }

    public void alert(final String message,final DialogInterface.OnClickListener listener) {
        try{
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    AlertDialog.Builder bld = new AlertDialog.Builder(mActivity);
                    bld.setMessage(message);
                    bld.setCancelable(false);
                    bld.setPositiveButton("OK", listener);

                    bld.create().show();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            PL.i("On progressDialog " + e.getStackTrace());
        }
    }
}

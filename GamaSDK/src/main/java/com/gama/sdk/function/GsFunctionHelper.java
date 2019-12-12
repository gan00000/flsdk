package com.gama.sdk.function;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.core.base.utils.PL;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.login.widget.v2.InGameBindLayoutV2;
import com.gama.sdk.out.GsFunctionType;
import com.gama.sdk.out.ISdkCallBack;

/**
 * 处理一些特殊功能的类
 */
public class GsFunctionHelper {
    private static final String TAG = GsFunctionHelper.class.getSimpleName();

    public static void openFunction(Context context, GsFunctionType type, ISdkCallBack callBack) {
        switch (type) {

            case BIND_ACCOUNT:
                startBind(context, callBack);
                break;
        }
    }

    private static void startBind(Context context, final ISdkCallBack callBack) {
        if(GamaUtil.isAccountLinked(context)) {
            PL.i(TAG, "current account already linked");
            if(callBack != null) {
                callBack.success();
            }
            return;
        }
        final Dialog d = new Dialog(context, android.R.style.Theme_Material_NoActionBar_Fullscreen);
        InGameBindLayoutV2 inGameBindLayoutV2 = new InGameBindLayoutV2(context, new ISdkCallBack() {
            @Override
            public void success() {
                if(callBack != null) {
                    callBack.success();
                }
                try {
                    d.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure() {
                if (d != null && d.isShowing()) {
                    try {
                        d.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(callBack != null) {
                    callBack.failure();
                }
            }
        });
        inGameBindLayoutV2.setLoginDialogV2(null);
        d.setContentView(inGameBindLayoutV2);
        d.setCanceledOnTouchOutside(false);
        d.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(callBack != null) {
                    callBack.failure();
                }
            }
        });
        d.show();
    }
}

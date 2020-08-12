package com.gama.sdk.function;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.bean.SLoginType;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.data.login.execute2.ThirdAccountBindRequestTaskV2;
import com.gama.data.login.execute2.ThirdAccountToBindRequestTaskV2;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.SProgressActivity;
import com.gama.sdk.login.widget.v2.InGameBindLayoutV2;
import com.gama.sdk.out.GsFunctionType;
import com.gama.sdk.out.ISdkCallBack;
import com.gama.sdk.utils.DialogUtil;
import com.gama.thirdlib.google.SGoogleSignIn;

/**
 * 处理一些特殊功能的类
 */
public class GsFunctionHelper {
    private static final String TAG = GsFunctionHelper.class.getSimpleName();
    public static final int REQUEST_CODE_TO_BIND_GOOGLE = 140;
    private static ISdkCallBack sdkCallBack;

    public static void openFunction(Context context, GsFunctionType type, ISdkCallBack callBack) {
        switch (type) {
            //游戏内绑定手机
            case BIND_ACCOUNT:
                startBind(context, callBack);
                break;
            //游戏内免注册绑Google
            case BIND_TO_GOOGLE:
                startBindGoogle(context, callBack);
                break;
        }
    }

    /**
     * 游戏内绑定手机
     */
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

    /**
     * 免注册绑Google
     */
    private static void startBindGoogle(final Context context, final ISdkCallBack callBack) {
        sdkCallBack = callBack;
        String previousLoginType = GamaUtil.getPreviousLoginType(context);
        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MAC, previousLoginType)) {
            Intent intent = new Intent(context, SProgressActivity.class);
            intent.setPackage(context.getPackageName());
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_TO_BIND_GOOGLE);
        } else {
            PL.i(TAG, "ignore bind request except for free login.");
            if (callBack != null) {
                callBack.failure();
            }
        }
    }

    public static void onActivityResult(final Context context, final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_TO_BIND_GOOGLE) {
            if (resultCode == Activity.RESULT_OK) {
                if (sdkCallBack != null) {
                    sdkCallBack.success();
                }
            } else {
                if (sdkCallBack != null) {
                    sdkCallBack.failure();
                }
            }
        }
    }
}

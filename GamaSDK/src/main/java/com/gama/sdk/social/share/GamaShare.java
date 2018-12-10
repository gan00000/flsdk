package com.gama.sdk.social.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.core.base.utils.ApkInstallUtil;
import com.core.base.utils.FileUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.facebook.messenger.MessengerUtils;
import com.gama.sdk.R;
import com.gama.sdk.out.GamaThirdPartyType;
import com.gama.sdk.out.ISdkCallBack;

public class GamaShare {

    private static ISdkCallBack iSdkCallBack;
    private static GamaShare gamaShare;

    private static final int SHARE_LINE = 60;
    private static final int SHARE_WHATSAPP = 61;

    public static void share(Activity activity, GamaThirdPartyType type, String message, String linkUrl, String picPath, ISdkCallBack callBack) {
        iSdkCallBack = callBack;
        switch (type) {
            case LINE:
                shareLine(activity, message, linkUrl, picPath);
                break;

            case WHATSAPP:
                shareWhatsapp(activity, message, linkUrl, picPath);
                break;
        }
    }

    private static void shareWhatsapp(Activity activity, String message, String linkUrl, String picPath) {
        if (shouldShareWithType(activity, GamaThirdPartyType.WHATSAPP)) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setPackage("com.whatsapp");
            if (!TextUtils.isEmpty(message)) {
                if (!TextUtils.isEmpty(linkUrl)) {
                    message = message + " " + linkUrl;
                }
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
            }
            if (!TextUtils.isEmpty(picPath)) { // 有读sdk权限及有图片路径
                Uri picUri = FileUtil.getContentUri(activity, picPath);

                sendIntent.putExtra(Intent.EXTRA_STREAM, picUri);
                sendIntent.setType("image/*");
            }
            activity.startActivityForResult(sendIntent, SHARE_WHATSAPP);
        } else {
            String tip = String.format(activity.getResources().getString(R.string.gama_toast_msg_app_not_install), "Whatsapp");
            ToastUtils.toast(activity, tip);
            PL.e("没有安装whatsapp");
            if(iSdkCallBack != null) {
                iSdkCallBack.failure();
            }
        }
    }

    private static void shareLine(Activity activity, String message, String linkUrl, String picPath) {
        if (shouldShareWithType(activity, GamaThirdPartyType.LINE)) {
            if (!TextUtils.isEmpty(picPath)) { // 有读sdk权限及有图片路径
                activity.startActivityForResult(
                        new Intent("android.intent.action.VIEW", Uri.parse("line://msg/image/"
                                + picPath)), SHARE_LINE);
            } else if (!TextUtils.isEmpty(message)) {
                if (!TextUtils.isEmpty(linkUrl)) {
                    message = message + " " + linkUrl;
                }
                activity.startActivityForResult(
                        new Intent("android.intent.action.VIEW", Uri.parse("line://msg/text/"
                                + message)), SHARE_LINE);
            }
        } else {
            String tip = String.format(activity.getResources().getString(R.string.gama_toast_msg_app_not_install), "Line");
            ToastUtils.toast(activity, tip);
            PL.e("没有安装line");
            if(iSdkCallBack != null) {
                iSdkCallBack.failure();
            }
        }
    }

    public static boolean shouldShareWithType(Activity activity, GamaThirdPartyType type) {
        switch (type) {
            case LINE:
                return ApkInstallUtil.isInstallApp(activity, "jp.naver.line.android");

            case WHATSAPP:
                return ApkInstallUtil.isInstallApp(activity, "com.whatsapp");

            case FACEBOOK_MESSENGER:
                return MessengerUtils.hasMessengerInstalled(activity);

            case FACEBOOK:
                return true;

            default:
                return false;
        }
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data){
        if(requestCode == SHARE_LINE) {
            if(iSdkCallBack != null) {
                iSdkCallBack.success();
            }
        } else if(requestCode == SHARE_WHATSAPP) {
            if(iSdkCallBack != null) {
                iSdkCallBack.success();
            }
        }
    }
}

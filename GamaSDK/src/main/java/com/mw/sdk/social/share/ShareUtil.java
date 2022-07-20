package com.mw.sdk.social.share;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.core.base.utils.ApkInstallUtil;
import com.core.base.utils.FileUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.facebook.messenger.MessengerUtils;
import com.mw.sdk.R;
import com.mw.sdk.out.ThirdPartyType;
import com.mw.sdk.out.ISdkCallBack;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class ShareUtil {

    private static ISdkCallBack iSdkCallBack;

    private static final int SHARE_LINE = 60;
    private static final int SHARE_WHATSAPP = 61;
    private static final int SHARE_TWITTER = 62;

    public static void share(Activity activity, ThirdPartyType type, String message, String linkUrl, String picPath, ISdkCallBack callBack) {
        iSdkCallBack = callBack;
        switch (type) {
            case LINE:
                shareLine(activity, message, linkUrl, picPath);
                break;

            case WHATSAPP:
                shareWhatsapp(activity, message, linkUrl, picPath);
                break;

            case TWITTER:
                startTwitterShare(activity, message, linkUrl, picPath);
        }
    }

    private static void shareWhatsapp(Activity activity, String message, String linkUrl, String picPath) {
        if (shouldShareWithType(activity, ThirdPartyType.WHATSAPP)) {
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
        if (shouldShareWithType(activity, ThirdPartyType.LINE)) {
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

    private static void startTwitterShare(Activity activity, String msg, String linkUrl, String picUrl) {
        Intent twitterIntent = createTwitterIntent(activity, msg, linkUrl, picUrl);
        if (twitterIntent != null) {
            PL.i("使用twitter客戶端分享");
            activity.startActivityForResult(twitterIntent, SHARE_TWITTER);
        } else {
            PL.i("使用web分享");
            Intent webIntent = createWebIntent(msg, linkUrl);
            activity.startActivityForResult(webIntent, SHARE_TWITTER);
        }
    }


    private static Intent createTwitterIntent(Activity activity, String msg, String linkUrl, String imageUri) {
        Intent intent = new Intent("android.intent.action.SEND");

        StringBuilder builder = new StringBuilder();

        if (!TextUtils.isEmpty(msg)) {
            builder.append(msg);
        }

        if (!TextUtils.isEmpty(linkUrl)) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(linkUrl);
        }

        intent.putExtra("android.intent.extra.TEXT", builder.toString());
        intent.setType("text/plain");

        if (!TextUtils.isEmpty(imageUri)) {
            Uri picUri = FileUtil.getContentUri(activity, imageUri);
            intent.putExtra("android.intent.extra.STREAM", picUri);
            intent.setType("image/jpeg");
        }

        PackageManager packManager = activity.getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);

                return intent;
            }
        }

        return null;
    }

    private static Intent createWebIntent(String msg, String linkUrl) {
        String url = TextUtils.isEmpty(linkUrl) ? "" : linkUrl;
        String message = TextUtils.isEmpty(msg) ? "" : msg;

        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                urlEncode(message), urlEncode(url));

        return new Intent("android.intent.action.VIEW", Uri.parse(tweetUrl));
    }

    public static String urlEncode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, "UTF8");
        } catch (UnsupportedEncodingException unlikely) {
            throw new RuntimeException(unlikely.getMessage(), unlikely);
        }
    }

    public static boolean shouldShareWithType(Activity activity, ThirdPartyType type) {
        switch (type) {
            case LINE:
                return ApkInstallUtil.isInstallApp(activity, "jp.naver.line.android");

            case WHATSAPP:
                return ApkInstallUtil.isInstallApp(activity, "com.whatsapp");

            case FACEBOOK_MESSENGER:
                return MessengerUtils.hasMessengerInstalled(activity);

            case FACEBOOK:
                return true;

            case TWITTER:
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
        } else if(requestCode == SHARE_TWITTER) {
            if(iSdkCallBack != null) {
                iSdkCallBack.success();
            }
        }
    }
}

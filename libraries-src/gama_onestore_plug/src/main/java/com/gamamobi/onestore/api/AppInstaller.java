//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gamamobi.onestore.api;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AppInstaller {
    private static final String TAG = AppInstaller.class.getSimpleName();
    public static final int minOssVer = 78;
    private static final int minOssVerForGA = 60800;
    private static final String ACTION_COREAPP_UPGRADE = "com.skt.skaf.A000Z00040.COREAPP.UPGRADE";
    private static final String ONE_STORE_DOWNLOAD_URL = "https://m.onestore.co.kr/mobilepoc/etc/downloadGuide.omp";
    private static final String KT_OLLEH_MARKET_EMBEDDED_PKG_NAME = "com.kt.olleh.storefront";
    private static final String KT_OLLEH_MARKET_INSTALLABLE_PKG_NAME = "com.kt.olleh.istore";
    private static final String LG_UPLUS_STORE_LEGACY_PKG_NAME = "android.lgt.appstore";
    private static final String LG_UPLUS_STORE_PKG_NAME = "com.lguplus.appstore";
    private static final String SK_TSTORE_PKG_NAME = "com.skt.skaf.A000Z00040";
    private static final String ONE_STORE_SERVICE_PACKAGE = "com.skt.skaf.OA00018282";
    private static final String PACKAGE_NAME_SAMSUNG_APPS = "com.sec.android.app.samsungapps";

    private AppInstaller() {
    }

    private static void requestServiceInstall(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.skt.skaf.A000Z00040.COREAPP.UPGRADE");
        if (VERSION.SDK_INT >= 12) {
            intent.setFlags(32);
        }

        if (VERSION.SDK_INT >= 26) {
            intent.setPackage(getInstalledMainStoreClientPkgName(context));
        }

        intent.putExtra("PACKAGE", "com.skt.skaf.OA00018282");
        intent.putExtra("CALLER", context.getPackageName());
        context.sendBroadcast(intent);
    }

    private static void requestMobileWebInstall(Context context) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://m.onestore.co.kr/mobilepoc/etc/downloadGuide.omp"));
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, 65536);
        if (activities.size() > 0) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "원스토어 서비스 설치/업데이트 페이지 이동 중 알 수 없는 오류가 발생하였습니다.", 0).show();
        }

    }

    private static boolean isValidShopClient(Context context) {
        String mainClientPkgName = getInstalledMainStoreClientPkgName(context);
        return !TextUtils.isEmpty(mainClientPkgName);
    }

    private static AppInstaller.STATE getOneStoreServiceState(Context context) {
        PackageInfo packageInfo = getPackageInfo(context, "com.skt.skaf.OA00018282", 128);
        if (packageInfo != null) {
            if (isInstalledFromGalaxyApps(context)) {
                return packageInfo.versionCode >= 60800 ? AppInstaller.STATE.INSTALLED : AppInstaller.STATE.NEED_UPDATE;
            } else {
                return packageInfo.versionCode >= 78 ? AppInstaller.STATE.INSTALLED : AppInstaller.STATE.NEED_UPDATE;
            }
        } else {
            return AppInstaller.STATE.NOT_INSTALLED;
        }
    }

    public static boolean isInstalledOneStoreService(Context context) {
        AppInstaller.STATE state = getOneStoreServiceState(context);
        return state == AppInstaller.STATE.INSTALLED;
    }

    private static String getInstalledMainStoreClientPkgName(Context context) {
        boolean isInstalledUserSktClient = false;
        Iterator var2 = getStoreClientMap().keySet().iterator();

        while(var2.hasNext()) {
            String clientPkgName = (String)var2.next();
            PackageInfo packageInfo = getPackageInfo(context, clientPkgName, 0);
            if (packageInfo != null && packageInfo.versionCode >= 50000 && packageInfo.applicationInfo.enabled) {
                if ((packageInfo.applicationInfo.flags & 1) != 0) {
                    return clientPkgName;
                }

                if (clientPkgName.equals("com.skt.skaf.A000Z00040")) {
                    isInstalledUserSktClient = true;
                }
            }
        }

        if (isInstalledUserSktClient) {
            return "com.skt.skaf.A000Z00040";
        } else {
            return "";
        }
    }

    private static PackageInfo getPackageInfo(Context context, String strPackageName, int flags) {
        if (TextUtils.isEmpty(strPackageName)) {
            return null;
        } else {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = null;

            try {
                packageInfo = manager.getPackageInfo(strPackageName, flags);
            } catch (Exception var6) {
                Log.e(TAG, var6.toString());
            }

            return packageInfo;
        }
    }

    private static Map<String, STORE_MARKET> getStoreClientMap() {
        Map<String, STORE_MARKET> marketMap = new HashMap();
        marketMap.put("com.kt.olleh.storefront", AppInstaller.STORE_MARKET.OLLEH_MARKET);
        marketMap.put("com.kt.olleh.istore", AppInstaller.STORE_MARKET.OLLEH_MARKET);
        marketMap.put("android.lgt.appstore", AppInstaller.STORE_MARKET.UPLUS_STORE);
        marketMap.put("com.lguplus.appstore", AppInstaller.STORE_MARKET.UPLUS_STORE);
        marketMap.put("com.skt.skaf.A000Z00040", AppInstaller.STORE_MARKET.T_STORE);
        return marketMap;
    }

    public static void updateOrInstall(Activity activity) {
        AppInstaller.STATE state = getOneStoreServiceState(activity);
        if (state != AppInstaller.STATE.INSTALLED) {
            showDialog(activity, state);
        }

    }

    private static void showDialog(final Activity activity, AppInstaller.STATE state) {
        String msg = "";
        String okStr = "";
        if (state == AppInstaller.STATE.NEED_UPDATE) {
            msg = "원스토어 서비스 업데이트 후 구매가 가능합니다.\n원스토어 서비스를 업데이트하시겠습니까?";
            okStr = "업데이트";
        } else {
            msg = "원스토어 서비스 설치 후 구매가 가능합니다.\n원스토어 서비스를 설치하시겠습니까?";
            okStr = "확인";
        }

        Builder alertDialog = new Builder(activity);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(okStr, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (AppInstaller.isValidShopClient(activity)) {
                    AppInstaller.requestServiceInstall(activity);
                } else {
                    AppInstaller.requestMobileWebInstall(activity);
                }

            }
        });
        alertDialog.setNegativeButton("취소", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d(AppInstaller.TAG, "설치/업데이트를 취소하였습니다.");
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private static boolean isInstalledFromGalaxyApps(Context context) {
        PackageManager pm = context.getPackageManager();
        return "com.sec.android.app.samsungapps".equals(pm.getInstallerPackageName(context.getPackageName()));
    }

    public static enum STATE {
        INSTALLED,
        NOT_INSTALLED,
        NEED_UPDATE;

        private STATE() {
        }
    }

    public static enum STORE_MARKET {
        T_STORE,
        OLLEH_MARKET,
        UPLUS_STORE;

        private STORE_MARKET() {
        }
    }
}

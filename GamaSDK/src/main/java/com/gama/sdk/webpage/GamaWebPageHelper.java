package com.gama.sdk.webpage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;

import com.core.base.utils.PL;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;

public class GamaWebPageHelper {

    public static void startService(Activity activity) {
        String servicesUrl = ResConfig.getServiceUrl(activity);
        if(TextUtils.isEmpty(servicesUrl)){
            return;
        }
        String url = addInfoToUrl(activity, servicesUrl);
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(Color.WHITE);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(activity, Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * gameCode、userId、accessToken、packageName、timestamp、serverCode、roleId、from (gamePage)
     * @param url
     * @return
     */
    private static String addInfoToUrl(Context context, String url) {
        if(TextUtils.isEmpty(url)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(url);
        builder.append("?")
                .append("gameCode=").append(ResConfig.getGameCode(context))
                .append("&userId=").append(GamaUtil.getUid(context))
                .append("&accessToken=").append(GamaUtil.getSdkAccessToken(context))
                .append("&packageName=").append(context.getPackageName())
                .append("&timestamp=").append(GamaUtil.getSdkTimestamp(context))
                .append("&serverCode=").append(GamaUtil.getServerCode(context))
                .append("&roleId=").append(GamaUtil.getRoleId(context))
                .append("&from=gamePage");
        PL.i("add info url : " + builder.toString());
        return builder.toString();
    }
}

package com.gama.sdk.webpage;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;

import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.gama.base.bean.unify.UnifiedSwitchResponseBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.excute.UnifiedSwitchRequestTask;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.SWebViewDialog;
import com.gama.sdk.out.GamaOpenWebType;
import com.gama.sdk.utils.DialogUtil;

public class GamaWebPageHelper {

    public static void openWebPage(Context context, GamaOpenWebType type, String url) {
        switch (type) {
            case CUSTOM_URL:
                    openWebPageWithDefaultDialog(context, url);
                break;

            case SERVICE:
                startService(context);
                break;

            case ANNOUNCEMENT:
                startAnnouncement(context);
                break;
        }
    }

    private static void startAnnouncement(final Context context) {
        UnifiedSwitchRequestTask requestTask = new UnifiedSwitchRequestTask(context, "notice");
        requestTask.setLoadDialog(DialogUtil.createLoadingDialog(context,"Loading..."));
        requestTask.setReqCallBack(new ISReqCallBack<UnifiedSwitchResponseBean>() {
            @Override
            public void success(UnifiedSwitchResponseBean responseBean, String rawResult) {
                if (responseBean != null){
                    if (responseBean.isRequestSuccess()) {
                        if(responseBean.getData() != null && responseBean.getData().getNotice() != null) {
                            String url = responseBean.getData().getNotice().getUrl();
                            openWebPageWithDefaultDialog(context, url);
                        }
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        requestTask.excute(UnifiedSwitchResponseBean.class);
    }

    public static void startService(Context context) {
        String servicesUrl = ResConfig.getServiceUrl(context);
        if(TextUtils.isEmpty(servicesUrl)){
            return;
        }
        String url = addInfoToUrl(context, servicesUrl);
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(Color.WHITE);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
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

    private static void openWebPageWithDefaultDialog(Context context, String url) {
        if(TextUtils.isEmpty(url)) {
            PL.e("Open Web Page url null");
            return;
        }
        try {
            SWebViewDialog sWebViewDialog = new SWebViewDialog(context, R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
            sWebViewDialog.setWebUrl(url);
            sWebViewDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//package com.mw.sdk.webpage;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.net.Uri;
//import android.text.TextUtils;
//
//import androidx.browser.customtabs.CustomTabsIntent;
//
//import com.core.base.callback.ISReqCallBack;
//import com.core.base.utils.PL;
//import com.mw.base.bean.unify.UnifiedSwitchResponseBean;
//import com.mw.base.cfg.ResConfig;
//import com.mw.base.excute.UnifiedSwitchRequestTask;
//import com.mw.base.utils.SdkUtil;
//import com.mw.sdk.R;
//import com.mw.sdk.SWebViewDialog;
//import com.mw.sdk.out.ISdkCallBack;
//import com.mw.sdk.utils.DialogUtil;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//
//public class GamaWebPageHelper {
//    private static ISdkCallBack iSdkCallBack;
//    private static boolean isLaunch = false;
//
//   /* public static SWebViewDialog openWebPage(Context context, GamaOpenWebType type, String url, ISdkCallBack callBack) {
//        iSdkCallBack = callBack;
//        switch (type) {
//            case CUSTOM_URL:
//                return openWebPageWithDefaultDialog(context, url);
//            // break;
//
//            case SERVICE:
//                startService(context);
//                break;
//
//            case ANNOUNCEMENT:
//                startAnnouncement(context);
//                break;
//        }
//        return null;
//    }*/
//
//    private static void startAnnouncement(final Context context) {
//        UnifiedSwitchRequestTask requestTask = new UnifiedSwitchRequestTask(context, "notice");
//        requestTask.setLoadDialog(DialogUtil.createLoadingDialog(context,"Loading..."));
//        requestTask.setReqCallBack(new ISReqCallBack<UnifiedSwitchResponseBean>() {
//            @Override
//            public void success(UnifiedSwitchResponseBean responseBean, String rawResult) {
//                if (responseBean != null){
//                    if (responseBean.isRequestSuccess()) {
//                        if(responseBean.getData() != null && responseBean.getData().getNotice() != null) {
//                            String url = responseBean.getData().getNotice().getUrl();
//                            openWebPageWithDefaultDialog(context, url);
//                        } else {
//                            if(iSdkCallBack != null) {
//                                iSdkCallBack.success();
//                            }
//                        }
//                    } else {
//                        if(iSdkCallBack != null) {
//                            iSdkCallBack.success();
//                        }
//                    }
//                } else {
//                    if(iSdkCallBack != null) {
//                        iSdkCallBack.success();
//                    }
//                }
//            }
//
//            @Override
//            public void timeout(String code) {
//                if(iSdkCallBack != null) {
//                    iSdkCallBack.success();
//                }
//            }
//
//            @Override
//            public void noData() {
//                if(iSdkCallBack != null) {
//                    iSdkCallBack.success();
//                }
//            }
//
//            @Override
//            public void cancel() {
//                if(iSdkCallBack != null) {
//                    iSdkCallBack.success();
//                }
//            }
//        });
//        requestTask.excute(UnifiedSwitchResponseBean.class);
//    }
//
//    public static void startService(Context context) {
//        String servicesUrl = ResConfig.getServiceUrl(context);
//        if(TextUtils.isEmpty(servicesUrl)){
//            PL.e("没有找到客服URL");
//            if(iSdkCallBack != null) {
//                iSdkCallBack.failure();
//            }
//            return;
//        }
//        String url = addInfoToUrl(context, servicesUrl);
//        try {
//            isLaunch = true;
//            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//            builder.setToolbarColor(Color.WHITE);
//            CustomTabsIntent customTabsIntent = builder.build();
//            customTabsIntent.launchUrl(context, Uri.parse(url));
//        } catch (Exception e) {
//            e.printStackTrace();
//            PL.e("打开customtab失败");
//            if(iSdkCallBack != null) {
//                iSdkCallBack.failure();
//            }
//        }
//
//    }
//
//    /**
//     * gameCode、userId、accessToken、packageName、timestamp、serverCode、roleId、from (gamePage)
//     * @param url
//     * @return
//     */
//    private static String addInfoToUrl(Context context, String url) {
//        if(TextUtils.isEmpty(url)) {
//            return null;
//        }
//        if(!url.endsWith("?")) {
//            url += "?";
//        }
//        StringBuilder builder = new StringBuilder(url);
//        try {
//            builder.append("gameCode=").append(URLEncoder.encode(ResConfig.getGameCode(context),"UTF-8"))
//                    .append("&userId=").append(URLEncoder.encode(SdkUtil.getUid(context),"UTF-8"))
//                    .append("&accessToken=").append(URLEncoder.encode(SdkUtil.getSdkAccessToken(context),"UTF-8"))
//                    .append("&packageName=").append(URLEncoder.encode(context.getPackageName(),"UTF-8"))
//                    .append("&timestamp=").append(URLEncoder.encode(SdkUtil.getSdkTimestamp(context),"UTF-8"))
//                    .append("&serverCode=").append(URLEncoder.encode(SdkUtil.getServerCode(context),"UTF-8"))
//                    .append("&roleId=").append(URLEncoder.encode(SdkUtil.getRoleId(context),"UTF-8"))
//                    .append("&roleName=").append(URLEncoder.encode(SdkUtil.getRoleName(context),"UTF-8"))
//                    .append("&roleLevel=").append(URLEncoder.encode(SdkUtil.getRoleLevel(context),"UTF-8"))
//                    .append("&from=gamePage");
//
//            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//                builder.append("&layout=transverse");
//            }else{
//                builder.append("&layout=vertical");
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        PL.i("add info url : " + builder.toString());
//        return builder.toString();
//    }
//
//    private static SWebViewDialog openWebPageWithDefaultDialog(Context context, String url) {
//        if(TextUtils.isEmpty(url)) {
//            PL.e("Open Web Page url null");
//            if(iSdkCallBack != null) {
//                iSdkCallBack.failure();
//            }
//            return null;
//        }
//        try {
//            url = addInfoToUrl(context, url);
//            SWebViewDialog sWebViewDialog = new SWebViewDialog(context, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
//            sWebViewDialog.setWebUrl(url);
//            sWebViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialogInterface) {
//                    if(iSdkCallBack != null) {
//                        iSdkCallBack.success();
//                    }
//                }
//            });
//            sWebViewDialog.show();
//            return sWebViewDialog;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static void onResume(Activity activity) {
//        if(isLaunch) {
//            isLaunch = false;
//            if(iSdkCallBack != null) {
//                iSdkCallBack.success();
//            }
//        }
//    }
//}

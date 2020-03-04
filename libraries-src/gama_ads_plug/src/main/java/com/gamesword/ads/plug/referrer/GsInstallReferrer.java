package com.gamesword.ads.plug.referrer;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.core.base.utils.PL;
import com.gama.base.utils.GamaUtil;

public class GsInstallReferrer {
    private static final String TAG  = GsInstallReferrer.class.getSimpleName();
    private  static InstallReferrerClient referrerClient;

    public static void initReferrerClient(final Context context, final GsInstallReferrerCallback callback) {
        if (referrerClient != null) {
            referrerClient = null;
        }
        referrerClient = InstallReferrerClient.newBuilder(context).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                GsInstallReferrerBean referrerBean = null;
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        ReferrerDetails response;
                        try {
                            response = referrerClient.getInstallReferrer();
                            String referrerUrl = response.getInstallReferrer();
                            if (!TextUtils.isEmpty(referrerUrl)) {
                                PL.i(TAG, "ref = " + referrerUrl);
                            }
                            long referrerClickTime = response.getReferrerClickTimestampSeconds();
                            long appInstallTime = response.getInstallBeginTimestampSeconds();
                            referrerBean = new GsInstallReferrerBean();
                            referrerBean.setAppInstallTime(appInstallTime + "");
                            referrerBean.setReferrerClickTime(referrerClickTime  + "");
                            referrerBean.setReferrerUrl(referrerUrl);
                            GamaUtil.saveReferrer(context, referrerUrl);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR:
                        //General errors caused by incorrect usage
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED:
                        //Play Store service is not connected now - potentially transient state.
                        break;
                }
                if (callback != null) {
                    callback.onResult(referrerBean);
                }
                referrerClient.endConnection();
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                if (callback != null) {
                    callback.onResult(null);
                }
                referrerClient.endConnection();
            }
        });
    }

    public interface GsInstallReferrerCallback {
        void onResult(GsInstallReferrerBean bean);
    }

}

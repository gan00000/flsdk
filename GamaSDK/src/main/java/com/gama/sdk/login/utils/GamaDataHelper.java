package com.gama.sdk.login.utils;

import android.content.Context;

import com.core.base.utils.SPUtil;
import com.gama.base.utils.GamaUtil;

public class GamaDataHelper {
    public static final String GAMA_SELECTED_PLATFORM = "gama_selected_platform";
    public static final String PLATFORM_EVATAR = "platform_evatar";
    public static final String PLATFORM_GAMAMOBI = "platform_gamamobi";

    public static void saveSelectPlatform(Context context, String platform) {
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE, GAMA_SELECTED_PLATFORM, platform);
    }

    public static String getGamaSelectedPlatform(Context context) {
        return SPUtil.getSimpleString(context, GamaUtil.GAMA_SP_FILE, GAMA_SELECTED_PLATFORM);
    }
}

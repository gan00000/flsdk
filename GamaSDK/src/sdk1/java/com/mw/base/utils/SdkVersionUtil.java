package com.mw.base.utils;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.core.base.cipher.DESCipher;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.FileUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mw.base.bean.PhoneInfo;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.bean.SLoginType;
import com.mw.base.cfg.ConfigBean;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.R;
import com.mw.sdk.login.model.AccountModel;
import com.mw.sdk.login.model.response.SLoginResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by gan on 2017/2/7.
 */

public class SdkVersionUtil extends BaseSdkVersion{


    public static void setAccountWithIcon(AccountModel accountModel, ImageView imageView, EditText editText){
        int imageResId = R.mipmap.mw_smail_icon;
        String showName = accountModel.getUserId();//accountModel.getThirdAccount();
//        if (SStringUtil.isEmpty(showName)){
//            showName = accountModel.getUserId();
//        }
        if (SLoginType.LOGIN_TYPE_FB.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.fb_smail_icon;
        }else  if (SLoginType.LOGIN_TYPE_GOOGLE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.google_smail_icon;
        }else  if (SLoginType.LOGIN_TYPE_GUEST.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.guest_smail_icon;
        }else if (SLoginType.LOGIN_TYPE_LINE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.line_smail_icon;
        }else if (SLoginType.LOGIN_TYPE_MG.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.mw_smail_icon;
            showName = accountModel.getAccount();
        }

        imageView.setImageResource(imageResId);
        // 使光标始终在最后位置
        try {
            if (SStringUtil.isNotEmpty(showName)) {
                editText.setText(showName);
//                Editable etable = editText.getText();
//                Selection.setSelection(etable, showName.length());
            }else{
                editText.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

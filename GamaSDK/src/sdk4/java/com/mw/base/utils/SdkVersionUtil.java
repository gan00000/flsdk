package com.mw.base.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;

import com.core.base.utils.SStringUtil;
import com.mw.base.bean.SLoginType;
import com.mw.sdk.R;
import com.mw.sdk.login.model.AccountModel;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.version.BaseSdkVersion;
/**
 * Created by gan on 2017/2/7.
 */

public class SdkVersionUtil extends BaseSdkVersion{


    public static void setAccountWithIcon(AccountModel accountModel, ImageView imageView, EditText editText){
        int imageResId = R.mipmap.mmplaygame_ac_login;
        String showName = accountModel.getUserId();//accountModel.getThirdAccount();
//        if (SStringUtil.isEmpty(showName)){
//            showName = accountModel.getUserId();
//        }
        if (SLoginType.LOGIN_TYPE_FB.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.mmplaygame_fb_img;
        }else  if (SLoginType.LOGIN_TYPE_GOOGLE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.mmplaygame_gp_img;
        }else  if (SLoginType.LOGIN_TYPE_GUEST.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.mmplaygame_vister_login;
        }else if (SLoginType.LOGIN_TYPE_LINE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.mmplaygame_le_login;
        }else if (SLoginType.LOGIN_TYPE_MG.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.mmplaygame_ac_login;
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

    public static SLoginBaseRelativeLayout newAgeQualifiedView(Context context){

        return null;
    }
}

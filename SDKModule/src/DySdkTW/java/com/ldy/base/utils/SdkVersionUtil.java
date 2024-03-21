package com.ldy.base.utils;

import android.widget.EditText;
import android.widget.ImageView;

import com.mybase.utils.SStringUtil;
import com.ldy.base.bean.SLoginType;
import com.ldy.sdk.R;
import com.ldy.sdk.login.model.AccountModel;

/**
 * Created by gan on 2017/2/7.
 */

public class SdkVersionUtil {


    public static void setAccountWithIcon(AccountModel accountModel, ImageView imageView, EditText editText){
        int imageResId = R.mipmap.sady_claustrorium37514;
        String showName = accountModel.getUserId();//accountModel.getThirdAccount();
//        if (SStringUtil.isEmpty(showName)){
//            showName = accountModel.getUserId();
//        }
        if (SLoginType.LOGIN_TYPE_FB.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.sady_corticoit62999;
        }else  if (SLoginType.LOGIN_TYPE_GOOGLE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.sady_phas46359;
        }else  if (SLoginType.LOGIN_TYPE_GUEST.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.sady_lowade93455;
        }else if (SLoginType.LOGIN_TYPE_LINE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.sady_panting23517;
        }else if (SLoginType.LOGIN_TYPE_MG.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.sady_claustrorium37514;
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

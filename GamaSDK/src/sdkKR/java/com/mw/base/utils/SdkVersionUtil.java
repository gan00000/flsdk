package com.mw.base.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;

import com.core.base.utils.SStringUtil;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.R;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.login.widget.v2.AgeQuaLayoutV2;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.version.BaseSdkVersion;
import com.thirdlib.IThirdHelper;
import com.thirdlib.never.NeverLogin;
import com.thirdlib.onepay.OnestorePayImpl;

/**
 * Created by gan on 2017/2/7.
 */

public class SdkVersionUtil extends BaseSdkVersion {


    public static void setAccountWithIcon(AccountModel accountModel, ImageView imageView, EditText editText){
        int imageResId = R.mipmap.img_persion_bg;
        String showName = accountModel.getUserId();//accountModel.getThirdAccount();
//        if (SStringUtil.isEmpty(showName)){
//            showName = accountModel.getUserId();
//        }
        if (SLoginType.LOGIN_TYPE_FB.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.icon_fb_2;
        }else  if (SLoginType.LOGIN_TYPE_GOOGLE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.icon_gp_2;
        }else  if (SLoginType.LOGIN_TYPE_GUEST.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.img_guest_2;
        }else if (SLoginType.LOGIN_TYPE_LINE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.icon_line_2;
        }else if (SLoginType.LOGIN_TYPE_TWITTER.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.icon_twitter_2;
        }else if (SLoginType.LOGIN_TYPE_NAVER.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.icon_naver;
        }else if (SLoginType.LOGIN_TYPE_MG.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.img_persion_bg;
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

        return new AgeQuaLayoutV2(context);
    }

    @Override
    public IThirdHelper newNaverHelper() {
        return new NeverLogin();
    }

    @Override
    public IPay newOneStorePay() {
        return new OnestorePayImpl();
    }
}

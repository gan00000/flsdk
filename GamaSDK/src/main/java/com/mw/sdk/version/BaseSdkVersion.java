package com.mw.sdk.version;

import android.widget.EditText;
import android.widget.ImageView;

import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.pay.IPay;
import com.mw.sdk.pay.nowgg.NowggPayImpl;
import com.thirdlib.IThirdHelper;
import com.thirdlib.ThirdModuleUtil;
import com.thirdlib.irCafebazaar.CafeBazaarPayImpl;
import com.thirdlib.vk.VKPayImpl;
import com.thirdlib.xiaomi.XiaomiPayImpl;

public abstract class BaseSdkVersion {

    public IThirdHelper newNaverHelper(){
        return null;
    }
    public IPay newOneStorePay(){
        return null;
    }

    public IPay newSamsungPay() {
        return null;
    }

    public IPay newVKPay() {

        if (ThirdModuleUtil.existRustoreModule()){
            return new VKPayImpl();
        }
        return null;
    }

    public IPay newNowggPay(){

        if (ThirdModuleUtil.existNowggModule()){
            return new NowggPayImpl();
        }
        return null;
    }

    public IPay newXiaomiPay(){

        if (ThirdModuleUtil.existXiaomiModule()){
            return new XiaomiPayImpl();
        }
        return null;
    }

    public IPay newBazaarPay(){

        if (ThirdModuleUtil.existBazaarModule()){
            return new CafeBazaarPayImpl();
        }
        return null;
    }

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
        }else if (SLoginType.LOGIN_TYPE_NOWGG.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.icon_nowgg;
        }else if (SLoginType.LOGIN_TYPE_HUAWEI.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.icon_huawei_2;
        }else if (SLoginType.LOGIN_TYPE_BAZAAR.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.icon_bazaar;
        }

        imageView.setImageResource(imageResId);
        // 使光标始终在最后位置
        try {
            if (editText != null){
                if (SStringUtil.isNotEmpty(showName)) {
                    editText.setText(showName);
//                Editable etable = editText.getText();
//                Selection.setSelection(etable, showName.length());
                }else{
                    editText.setText("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

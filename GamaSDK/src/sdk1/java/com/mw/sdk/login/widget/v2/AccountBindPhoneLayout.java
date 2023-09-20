package com.mw.sdk.login.widget.v2;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.bean.PhoneInfo;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.PhoneAreaCodeDialogHelper;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.out.ISdkCallBack;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class AccountBindPhoneLayout extends SLoginBaseRelativeLayout {

    private View contentView;

    private Button okBtn;

    private String account;
    private View iv_bind_phone_close;

    private View ll_bind_view;
    private TextView tv_area_code;
    private ImageView iv_area_code_select;
    private EditText et_input_phone_number;
    private EditText et_input_vf_code;
    private Button btn_find_get_vfcode;

    private View ll_has_bind_view;
    private TextView tv_area_code_2;
    private EditText et_input_phone_number_2;

    private PhoneAreaCodeDialogHelper phoneAreaCodeDialogHelper;

    private SFCallBack sfCallBack;


    public void setSFCallBack(SFCallBack sfCallBack) {
        this.sfCallBack = sfCallBack;
    }


    public AccountBindPhoneLayout(Context context) {
        super(context);

    }


    public AccountBindPhoneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountBindPhoneLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {


        return contentView;
    }

    @Override
    public void onViewVisible() {
        super.onViewVisible();
    }

    @Override
    public void onViewGone() {
        super.onViewGone();
    }

    @Override
    public void onViewRemove() {
        super.onViewRemove();
    }

    @Override
    public void refreshVfCode() {
        super.refreshVfCode();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }



}

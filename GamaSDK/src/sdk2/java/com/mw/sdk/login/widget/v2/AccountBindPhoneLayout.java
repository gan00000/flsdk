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
import com.mw.base.bean.PhoneInfo;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
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

        contentView = inflater.inflate(R.layout.mw_bind_phone_cp, null);

        okBtn = contentView.findViewById(R.id.btn_confirm);
        iv_bind_phone_close = contentView.findViewById(R.id.iv_bind_phone_close);

        ll_bind_view = contentView.findViewById(R.id.ll_bind_view);
        tv_area_code = contentView.findViewById(R.id.tv_area_code);

        iv_area_code_select = contentView.findViewById(R.id.iv_area_code_select);
        et_input_phone_number = contentView.findViewById(R.id.et_input_phone_number);
        et_input_vf_code = contentView.findViewById(R.id.et_input_vf_code);
        btn_find_get_vfcode = contentView.findViewById(R.id.btn_find_get_vfcode);

        ll_has_bind_view = contentView.findViewById(R.id.ll_has_bind_view);
        tv_area_code_2 = contentView.findViewById(R.id.tv_area_code_2);
        et_input_phone_number_2 = contentView.findViewById(R.id.et_input_phone_number_2);


        iv_bind_phone_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sBaseDialog != null) {
                    sBaseDialog.dismiss();
                }
            }
        });


        iv_area_code_select.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出区号选择
                if (phoneAreaCodeDialogHelper == null){
                    phoneAreaCodeDialogHelper = new PhoneAreaCodeDialogHelper((Activity) getContext());
                }
                phoneAreaCodeDialogHelper.showPhoneAreaCodeDialog(new PhoneAreaCodeDialogHelper.AreaCodeSelectCallback() {
                    @Override
                    public void select(PhoneInfo phoneInfo) {
                        tv_area_code.setText(phoneInfo.getValue());
                    }
                });
            }
        });
        btn_find_get_vfcode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求发送验证码
                String areaCode = tv_area_code.getText().toString();
                String phone = et_input_phone_number.getEditableText().toString();
                String vfCode = et_input_vf_code.getEditableText().toString();

                if (SStringUtil.isEmpty(areaCode)) {
                    ToastUtils.toast(getContext(),R.string.text_area_code_not_empty);
                    return;
                }
                if (SStringUtil.isEmpty(phone)) {
                    ToastUtils.toast(getContext(),R.string.text_phone_not_empty);
                    return;
                }
                Request.sendVfCode(getContext(),true, areaCode, phone, new ISdkCallBack() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void failure() {

                    }
                });
            }
        });

        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String areaCode = tv_area_code.getText().toString();
                String phone = et_input_phone_number.getEditableText().toString();
                String vfCode = et_input_vf_code.getEditableText().toString();

                if (SStringUtil.isEmpty(areaCode)) {
                    ToastUtils.toast(getContext(),R.string.text_area_code_not_empty);
                    return;
                }
                if (SStringUtil.isEmpty(phone)) {
                    ToastUtils.toast(getContext(),R.string.text_phone_not_empty);
                    return;
                }
                if (SStringUtil.isEmpty(vfCode)) {
                    ToastUtils.toast(getContext(),R.string.py_vfcode_empty);
                    return;
                }

                Request.bindPhone(getContext(),true, areaCode, phone, vfCode, new SFCallBack<String>() {
                    @Override
                    public void success(String result, String msg) {

                    }

                    @Override
                    public void fail(String result, String msg) {

                    }
                });

            }
        });

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

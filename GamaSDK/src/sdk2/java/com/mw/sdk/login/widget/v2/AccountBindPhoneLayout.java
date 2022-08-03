package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mw.sdk.R;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;

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

            }
        });


        iv_area_code_select.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出区号选择

            }
        });
        btn_find_get_vfcode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求发送验证码
            }
        });

        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

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

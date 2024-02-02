package com.mw.sdk.act;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.base.callback.SFCallBack;
import com.mw.sdk.R;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;

public class FloatPersionCenterView extends SLoginBaseRelativeLayout {

    private ImageView persionIconImageView;
    private TextView gameNameTextView;
    private TextView setverNameTextView;
    private TextView roleNameTextView;
    private TextView uidTextView;
    private TextView accountTextView;

    private View persionMainView;
    private  BindAccountLayout mBindAccountLayout;
    private FloatChangePwdLayout mFloatChangePwdLayout;

    public FloatPersionCenterView(Context context) {
        super(context);
    }

    public FloatPersionCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatPersionCenterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatPersionCenterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        View persionCenterView = layoutInflater.inflate(R.layout.float_personal_center, null);

        persionMainView = persionCenterView.findViewById(R.id.id_ll_persion_main_center);
        persionIconImageView = persionCenterView.findViewById(R.id.id_iv_game_icon);
        gameNameTextView = persionCenterView.findViewById(R.id.id_tv_game_name);
        uidTextView = persionCenterView.findViewById(R.id.id_tv_uid);
        setverNameTextView = persionCenterView.findViewById(R.id.id_tv_server_name);
        roleNameTextView = persionCenterView.findViewById(R.id.id_tv_role_name);
        accountTextView = persionCenterView.findViewById(R.id.id_tv_account);

        mBindAccountLayout = persionCenterView.findViewById(R.id.id_BindAccountLayout);
        mFloatChangePwdLayout = persionCenterView.findViewById(R.id.id_FloatChangePwdLayout);

        persionMainView.setVisibility(View.VISIBLE);
        mBindAccountLayout.setVisibility(View.GONE);
        mFloatChangePwdLayout.setVisibility(View.GONE);

        View changePwd = persionCenterView.findViewById(R.id.pc_tv_change_pwd);
        View updradeAccount = persionCenterView.findViewById(R.id.pc_tv_upgrade_account);

        updradeAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                persionMainView.setVisibility(View.GONE);
                mBindAccountLayout.setVisibility(View.VISIBLE);
                mFloatChangePwdLayout.setVisibility(View.GONE);
            }
        });
        changePwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                persionMainView.setVisibility(View.GONE);
                mBindAccountLayout.setVisibility(View.GONE);
                mFloatChangePwdLayout.setVisibility(View.VISIBLE);
            }
        });
        mBindAccountLayout.setSFCallBack(new SFCallBack() {
            @Override
            public void success(Object result, String msg) {

            }

            @Override
            public void fail(Object result, String msg) {
                if ("back".equals(msg)){
                    persionMainView.setVisibility(View.VISIBLE);
                    mBindAccountLayout.setVisibility(View.GONE);
                    mFloatChangePwdLayout.setVisibility(View.GONE);
                }
            }
        });

        mFloatChangePwdLayout.setSFCallBack(new SFCallBack() {
            @Override
            public void success(Object result, String msg) {

            }

            @Override
            public void fail(Object result, String msg) {
                if ("back".equals(msg)){
                    persionMainView.setVisibility(View.VISIBLE);
                    mBindAccountLayout.setVisibility(View.GONE);
                    mFloatChangePwdLayout.setVisibility(View.GONE);
                }
            }
        });

        return persionCenterView;
    }
}

package com.mw.sdk.act;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.google.gson.Gson;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
import com.mw.sdk.bean.res.FloatConfigData;
import com.mw.sdk.bean.res.FloatMenuResData;
import com.mw.sdk.callback.FloatCallback;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.SdkUtil;

public class FloatPersionCenterView extends SLoginBaseRelativeLayout {

    private ImageView persionIconImageView;
    private TextView gameNameTextView;
    private TextView setverNameTextView;
    private TextView roleNameTextView;
    private TextView uidTextView;
    private TextView accountTextView;

    private View persionMainView;
    private View accountLayoutView;
    private FloatBindAccountLayout mFloatBindAccountLayout;
    private FloatChangePwdLayout mFloatChangePwdLayout;

    private View updradeAccountView;
    private View changePwdView;
    private View delAccountView;
    private View switchAccountView;
    private View delAccountLayout;
    private Button delAccountCancelButton;
    private Button delAccountOkButton;

    private FloatConfigData floatConfigData;
    private FloatMenuResData floatMenuResData;

    private FloatCallback xFloatCallback;

    public void setFloatCallback(FloatCallback xFloatCallback) {
        this.xFloatCallback = xFloatCallback;
    }

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

        mFloatBindAccountLayout = persionCenterView.findViewById(R.id.id_BindAccountLayout);
        mFloatChangePwdLayout = persionCenterView.findViewById(R.id.id_FloatChangePwdLayout);

        accountLayoutView = persionCenterView.findViewById(R.id.pc_ll_account);

        persionMainView.setVisibility(View.VISIBLE);
        mFloatBindAccountLayout.setVisibility(View.GONE);
        mFloatChangePwdLayout.setVisibility(View.GONE);

        changePwdView = persionCenterView.findViewById(R.id.pc_tv_change_pwd);
        updradeAccountView = persionCenterView.findViewById(R.id.pc_tv_upgrade_account);
        delAccountView = persionCenterView.findViewById(R.id.pc_del_account);
        switchAccountView = persionCenterView.findViewById(R.id.pc_tv_switch_account);

        //清除账号页面
        delAccountLayout = persionCenterView.findViewById(R.id.id_layout_del_account);
        delAccountCancelButton = persionCenterView.findViewById(R.id.btn_delete_cancel);
        delAccountOkButton = persionCenterView.findViewById(R.id.btn_delete_confirm);

        updradeAccountView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                persionMainView.setVisibility(View.GONE);
                mFloatBindAccountLayout.setVisibility(View.VISIBLE);
                mFloatChangePwdLayout.setVisibility(View.GONE);
            }
        });
        changePwdView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                persionMainView.setVisibility(View.GONE);
                mFloatBindAccountLayout.setVisibility(View.GONE);
                mFloatChangePwdLayout.setVisibility(View.VISIBLE);
            }
        });

        switchAccountView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAccount();
            }
        });

        delAccountView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delAccountLayout.setVisibility(View.VISIBLE);
            }
        });
        delAccountCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delAccountLayout.setVisibility(View.GONE);
            }
        });
        delAccountOkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doDeleteAccount();
            }
        });

        mFloatBindAccountLayout.setSFCallBack(new SFCallBack() {
            @Override
            public void success(Object result, String msg) {
                updateUserInfoView(context, floatConfigData);
            }

            @Override
            public void fail(Object result, String msg) {
                if ("back".equals(msg)){
                    persionMainView.setVisibility(View.VISIBLE);
                    mFloatBindAccountLayout.setVisibility(View.GONE);
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
                    mFloatBindAccountLayout.setVisibility(View.GONE);
                    mFloatChangePwdLayout.setVisibility(View.GONE);
                }
            }
        });

        String floatCfgData = SdkUtil.getFloatCfgData(context);
        String menuResData = SdkUtil.getFloatMenuResData(context);

        if (SStringUtil.isNotEmpty(floatCfgData) && SStringUtil.isNotEmpty(menuResData)){
            floatConfigData = new Gson().fromJson(floatCfgData, FloatConfigData.class);
            floatMenuResData = new Gson().fromJson(menuResData, FloatMenuResData.class);

            if (floatConfigData != null && floatMenuResData != null && floatMenuResData.getData() != null) {
                gameNameTextView.setText(floatMenuResData.getData().getGameName());
                setverNameTextView.setText(floatMenuResData.getData().getServerName());
                roleNameTextView.setText(floatMenuResData.getData().getRoleName());
                //accountTextView.setText(floatMenuResData.getData().geta());
                uidTextView.setText(floatMenuResData.getData().getUserId());

                updateUserInfoView(context, floatConfigData);
            }

        }

        return persionCenterView;
    }

    private void updateUserInfoView(Context context, FloatConfigData floatConfigData) {
        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(context);

        if (sLoginResponse != null && sLoginResponse.getData() != null) {
            if (sLoginResponse.getData().isBind()){
                accountTextView.setText(sLoginResponse.getData().getLoginId());
                updradeAccountView.setVisibility(View.GONE);
                changePwdView.setVisibility(View.VISIBLE);
                accountLayoutView.setVisibility(View.VISIBLE);

            }else {
                updradeAccountView.setVisibility(View.VISIBLE);
                changePwdView.setVisibility(View.GONE);
                accountLayoutView.setVisibility(View.GONE);
            }
            Glide.with(this)
                    .load(floatConfigData.getGameIcon() + "?" + sLoginResponse.getData().getTimestamp())
                    .centerCrop()
                    .placeholder(ApkInfoUtil.getAppIcon(context))
                    .into(persionIconImageView);
        }
    }


    private void doDeleteAccount() {
        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());
        if (sLoginResponse == null || sLoginResponse.getData() == null){
            return;
        }
        Request.deleteAccout(getContext(), sLoginResponse.getData().getUserId(),
                sLoginResponse.getData().getLoginType(),
                sLoginResponse.getData().getThirdId(),
                sLoginResponse.getData().getToken(),
                sLoginResponse.getData().getTimestamp(), new SFCallBack<SLoginResponse>() {
                    @Override
                    public void success(SLoginResponse result, String msg) {
                        //此处需要退出账号
                        delAccountLayout.setVisibility(View.GONE);

                        switchAccount();
                    }

                    @Override
                    public void fail(SLoginResponse result, String msg) {

                    }
                });
    }


    private void switchAccount(){
        PL.i("sdk switch account");
        if (xFloatCallback != null){
            xFloatCallback.switchAccount("");
        }
    }

}

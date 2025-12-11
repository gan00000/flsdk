package com.mw.sdk.act;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.TimeUtil;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
import com.mw.sdk.bean.res.FloatMenuResData;
import com.mw.sdk.bean.res.MenuData;
import com.mw.sdk.callback.FloatCallback;
import com.mw.sdk.constant.FloatMenuType;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.ResConfig;
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
    private View referCcodeView;
    private EditText referCcodeEditText;
    private TextView referCcodeOkTextView, pc_refer_titleTextView;

    //    private FloatConfigData floatConfigData;
    private FloatMenuResData floatMenuResData;

    private MenuData myMenuData;

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

        //推荐码部分
        referCcodeView = persionCenterView.findViewById(R.id.pc_ll_refer_code);
        referCcodeEditText = persionCenterView.findViewById(R.id.pc_input_refer_code);
        referCcodeOkTextView = persionCenterView.findViewById(R.id.pc_refer_code_ok);
        pc_refer_titleTextView = persionCenterView.findViewById(R.id.pc_refer_title);
        pc_refer_titleTextView.setVisibility(View.GONE);
        referCcodeView.setVisibility(View.GONE);

        switchAccountView.setVisibility(View.GONE);
        delAccountView.setVisibility(View.GONE);
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

        referCcodeOkTextView.setOnClickListener(v -> {
            sendReferCode();
        });

        mFloatBindAccountLayout.setSFCallBack(new SFCallBack() {
            @Override
            public void success(Object result, String msg) {
                updateUserInfoView(context, floatMenuResData);
            }

            @Override
            public void fail(Object result, String msg) {
                if ("back".equals(msg)) {
                    persionMainView.setVisibility(View.VISIBLE);
                    mFloatBindAccountLayout.setVisibility(View.GONE);
                    mFloatChangePwdLayout.setVisibility(View.GONE);
                }
            }
        });

        mFloatChangePwdLayout.setSFCallBack(new SFCallBack() {
            @Override
            public void success(Object result, String msg) {

                persionMainView.setVisibility(View.VISIBLE);
                mFloatBindAccountLayout.setVisibility(View.GONE);
                mFloatChangePwdLayout.setVisibility(View.GONE);

            }

            @Override
            public void fail(Object result, String msg) {
                if ("back".equals(msg)) {
                    persionMainView.setVisibility(View.VISIBLE);
                    mFloatBindAccountLayout.setVisibility(View.GONE);
                    mFloatChangePwdLayout.setVisibility(View.GONE);
                }
            }
        });

//        String floatCfgData = SdkUtil.getFloatCfgData(context);
        floatMenuResData = SdkUtil.getFloatMenuResDataObj(context);

        if (floatMenuResData != null && floatMenuResData.getData() != null && floatMenuResData.getData().getUserInfo() != null) {
            gameNameTextView.setText(floatMenuResData.getData().getUserInfo().getGameName());
            setverNameTextView.setText(floatMenuResData.getData().getUserInfo().getServerName());
            roleNameTextView.setText(floatMenuResData.getData().getUserInfo().getRoleName());
            //accountTextView.setText(floatMenuResData.getData().geta());
            uidTextView.setText(floatMenuResData.getData().getUserInfo().getUserId());

            updateUserInfoView(context, floatMenuResData);

            if (floatMenuResData.getData().getMenuList() != null) {
                for (MenuData bbMenuData : floatMenuResData.getData().getMenuList()) {
                    if (FloatMenuType.MENU_TYPE_MY.equals(bbMenuData.getCode())) {
                        myMenuData = bbMenuData;
                        if (myMenuData.isDeleteAccount()) {
                            delAccountView.setVisibility(View.VISIBLE);
                        } else {
                            delAccountView.setVisibility(View.GONE);
                        }

                        if (myMenuData.getShowReferCode() == 1){
                            referCcodeView.setVisibility(View.GONE);
                        }

                        break;
                    }
                }
            }
        }

        return persionCenterView;
    }

    private void updateUserInfoView(Context context, FloatMenuResData floatConfigData) {

        if (floatConfigData == null || floatConfigData.getData() == null) {
            return;
        }

        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(context);
        //sLoginResponse.getData().setReferCode("1111133");

        if (sLoginResponse != null && sLoginResponse.getData() != null) {
            if (sLoginResponse.getData().isBind()) {
                accountTextView.setText(sLoginResponse.getData().getLoginId());
                updradeAccountView.setVisibility(View.GONE);
                changePwdView.setVisibility(View.VISIBLE);
                accountLayoutView.setVisibility(View.VISIBLE);

            } else {
                updradeAccountView.setVisibility(View.VISIBLE);
                changePwdView.setVisibility(View.GONE);
                accountLayoutView.setVisibility(View.GONE);
            }
            Glide.with(this)
                    .load(floatConfigData.getData().getGameIcon() + "?" + sLoginResponse.getData().getTimestamp())
                    .centerCrop()
                    .placeholder(ApkInfoUtil.getAppIcon(context))
                    .into(persionIconImageView);

            //显示
            if (ResConfig.isShowReferCode(context)
                    && sLoginResponse.getData().getRegTime() > TimeUtil.getTimestamp("2025-11-28 00:00:00")){
                referCcodeView.setVisibility(View.VISIBLE);
                if (SStringUtil.isNotEmpty(sLoginResponse.getData().getReferCode())) {
                    setReferCodeToView(sLoginResponse.getData().getReferCode());
                }
            }else {
                referCcodeView.setVisibility(View.GONE);
            }

        }
    }

    private void setReferCodeToView(String code) {
        referCcodeEditText.setText(code);
        referCcodeEditText.setEnabled(false);
        referCcodeEditText.setFocusable(false);
        referCcodeEditText.setFocusableInTouchMode(false);
        referCcodeOkTextView.setVisibility(View.GONE);
        pc_refer_titleTextView.setVisibility(View.VISIBLE);
    }


    private void doDeleteAccount() {
        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());
        if (sLoginResponse == null || sLoginResponse.getData() == null) {
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

    private void sendReferCode() {
        String referCode = referCcodeEditText.getText().toString().trim();
        if (TextUtils.isEmpty(referCode)){
            return;
        }
        Request.sendReferCode(getContext(), referCode, new SFCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel result, String msg) {

                setReferCodeToView(referCode);
            }

            @Override
            public void fail(BaseResponseModel result, String msg) {

            }
        });
    }

    private void switchAccount() {
        PL.i("sdk switch account");
        if (xFloatCallback != null) {
            xFloatCallback.switchAccount("");
        }
    }

}

package com.mw.sdk.login.widget.v2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.base.bean.PhoneInfo;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.R;
import com.mw.sdk.SBaseRelativeLayout;
import com.mw.sdk.api.Request;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.PhoneAreaCodeDialogHelper;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.out.ISdkCallBack;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private PhoneInfo currentPhoneInfo;
    private static final int TIME_OUT_SECONDS = 60;

    private Timer requestPhoneVfcodeTimer;
    /**
     * 剩余倒数时间
     */
    private int resetTime;

    private SFCallBack sfCallBack;

    private boolean isBindSuccess = false;

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
        et_input_phone_number_2.setEnabled(false);

        List<PhoneInfo> phoneInfos = SdkUtil.getPhoneInfo(getContext());
        if (phoneInfos != null && !phoneInfos.isEmpty()){
            currentPhoneInfo = phoneInfos.get(0);
            tv_area_code.setText(currentPhoneInfo.getValue());
        }

        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());
        if (sLoginResponse != null && sLoginResponse.getData() != null) {
            if (sLoginResponse.getData().isBindPhone()){
                ll_bind_view.setVisibility(GONE);
                ll_has_bind_view.setVisibility(VISIBLE);

                String tel = sLoginResponse.getData().getTelephone();
                String[] tels = tel.split("-");
                if (tels.length > 1){
                    tv_area_code_2.setText(tels[0]);
                    et_input_phone_number_2.setText(tels[1]);
                }
            }
        }

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
                        currentPhoneInfo = phoneInfo;
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

                if (currentPhoneInfo == null){
                    return;
                }

                if (!phone.matches(currentPhoneInfo.getPattern())){
                    ToastUtils.toast(getContext(),R.string.text_phone_not_match);
                    return;
                }

                Request.sendVfCode(getContext(), true, areaCode, phone, new SFCallBack<BaseResponseModel>() {
                    @Override
                    public void success(BaseResponseModel result, String msg) {
                        ToastUtils.toast(getContext(),R.string.text_vfcode_has_send);
                        startTimer();
                    }

                    @Override
                    public void fail(BaseResponseModel result, String msg) {
                        if (result != null){
                            ToastUtils.toast(getContext(), "" + result.getMessage());
                        }
                    }
                });
            }
        });

        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //已經綁定
                if (ll_has_bind_view.getVisibility() == VISIBLE){
                    if (sBaseDialog != null) {
                        sBaseDialog.dismiss();
                    }
                    return;
                }

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

                if (currentPhoneInfo == null){
                    return;
                }

                if (!phone.matches(currentPhoneInfo.getPattern())){
                    ToastUtils.toast(getContext(),R.string.text_phone_not_match);
                    return;
                }

                stopVfCodeTimer();
                Request.bindPhone(getContext(),true, areaCode, phone, vfCode, new SFCallBack<SLoginResponse>() {
                    @Override
                    public void success(SLoginResponse aaaSLoginResponse1, String msg) {
                        ToastUtils.toast(getContext(),R.string.text_phone_bind_success);
                        isBindSuccess = true;
//                        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());
//                        sLoginResponse.getData().setTelephone(areaCode + "-" + phone);
//                        sLoginResponse.getData().setBindPhone(true);
//                        SdkUtil.updateLoginData(getContext(), sLoginResponse);

                        if (sfCallBack != null) {
                            sfCallBack.success(SdkUtil.getCurrentUserLoginResponse(getContext()),"");
                        }


                        if (sBaseDialog != null) {
                            sBaseDialog.dismiss();
                        }
                    }

                    @Override
                    public void fail(SLoginResponse result, String msg) {

                        ToastUtils.toast(getContext(), "" + result.getMessage());
                        if (sfCallBack != null){
//                            sfCallBack.fail(null,"");
                        }
                    }
                });

            }
        });

        return contentView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.sBaseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopVfCodeTimer();
            }
        });
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

    private void startTimer(){

        if(requestPhoneVfcodeTimer == null) {
            requestPhoneVfcodeTimer = new Timer();
        }

        resetTime = TIME_OUT_SECONDS;
        requestPhoneVfcodeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(resetTime < 1) {
                    stopVfCodeTimer();
                    return;
                }
                if(resetTime > 0) {
                    resetTime--;
                    btn_find_get_vfcode.setClickable(false);
                    btn_find_get_vfcode.setText(resetTime + "");
                }
            }
        }, 300, 1000);

    }

    public void stopVfCodeTimer() {//根据需求充值计数器

        resetTime = 0;
        if(requestPhoneVfcodeTimer != null) {
            requestPhoneVfcodeTimer.cancel();
            requestPhoneVfcodeTimer = null;
        }
        btn_find_get_vfcode.setClickable(true);
        btn_find_get_vfcode.setText(R.string.py_register_account_get_vfcode);
    }

}

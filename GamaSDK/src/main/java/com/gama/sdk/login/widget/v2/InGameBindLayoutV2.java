package com.gama.sdk.login.widget.v2;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.bean.GamaAreaInfoBean;
import com.gama.base.bean.SLoginType;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.sdk.R;
import com.gama.sdk.SBaseRelativeLayout;
import com.gama.sdk.login.p.LoginPresenterImpl;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.out.ISdkCallBack;
import com.gama.thirdlib.facebook.FbSp;
import com.gama.thirdlib.google.SGoogleProxy;


public class InGameBindLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener, SBaseRelativeLayout.OperationCallback {

    private View contentView;
    private Button registerConfirm, gama_ingame_bind_btn_get_vfcode;

    /**
     * 手机、验证码
     */
    private EditText gama_ingame_bind_et_phone, gama_ingame_bind_et_vfcode;
    /**
     * 区号
     */
    private TextView gama_ingame_bind_tv_area;

    //选中的区域信息
    private GamaAreaInfoBean selectedBean;
//    private String loginType, thirdId;
    private ISdkCallBack callBack;

    LoginPresenterImpl iLoginPresenter = new LoginPresenterImpl();

    public InGameBindLayoutV2(Context context, ISdkCallBack callBack) {
        super(context);
        this.callBack = callBack;
    }

    public InGameBindLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InGameBindLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_ingame_bind_phone, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);

        gama_ingame_bind_et_phone = contentView.findViewById(R.id.gama_ingame_bind_et_phone);
        gama_ingame_bind_et_vfcode = contentView.findViewById(R.id.gama_ingame_bind_et_vfcode);
        gama_ingame_bind_tv_area = contentView.findViewById(R.id.gama_ingame_bind_tv_area);

        registerConfirm = contentView.findViewById(R.id.gama_ingame_bind_btn_confirm);
        gama_ingame_bind_btn_get_vfcode = contentView.findViewById(R.id.gama_ingame_bind_btn_get_vfcode);

        backView.setOnClickListener(this);
        registerConfirm.setOnClickListener(this);
        gama_ingame_bind_btn_get_vfcode.setOnClickListener(this);
        gama_ingame_bind_tv_area.setOnClickListener(this);

        setDefaultAreaInfo();

        return contentView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onClick(View v) {

        if (v == registerConfirm) {
            register();
        } else if (v == backView) {//返回键
            if(callBack != null) {
                callBack.failure();
            }
        } else if (v == gama_ingame_bind_btn_get_vfcode) {
            getVfcode();
        } else if (v == gama_ingame_bind_tv_area) {
            getAndShowArea();
        }

    }

    private void getAndShowArea() {
        iLoginPresenter.getAreaInfo((Activity) getContext());
    }

    private void register() {

        String areaCode = gama_ingame_bind_tv_area.getText().toString();
        if (TextUtils.isEmpty(areaCode)) {
            ToastUtils.toast(getActivity(), R.string.py_area_code_empty);
            return;
        }

        String phone = gama_ingame_bind_et_phone.getEditableText().toString().trim();
        if (!phone.matches(selectedBean.getPattern())) {
            ToastUtils.toast(getActivity(), R.string.py_phone_error);
            return;
        }

        String vfcode = gama_ingame_bind_et_vfcode.getEditableText().toString();
        if (TextUtils.isEmpty(vfcode)) {
            ToastUtils.toast(getActivity(), R.string.py_vfcode_empty);
            return;
        }

        String previousLoginType = GamaUtil.getPreviousLoginType(getContext());
        String loginType = previousLoginType;
        String thirdId = "";
        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MAC, previousLoginType)) {
            loginType = SLoginType.LOGIN_TYPE_UNIQUE;
            thirdId = GamaUtil.getGoogleAdId(getContext());
        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, previousLoginType)) {
            thirdId = FbSp.getFbId(getContext());
        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, previousLoginType)) {
            thirdId = GamaUtil.getGoogleId(getContext());
        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_TWITTER, previousLoginType)) {
            thirdId = GamaUtil.getTwitterId(getContext());
        }

        iLoginPresenter.inGamePhoneVerify((Activity) getContext(), areaCode, phone, vfcode, thirdId, loginType);
    }

    private void getVfcode() {
        String areaCode = gama_ingame_bind_tv_area.getText().toString();
        if (TextUtils.isEmpty(areaCode)) {
            ToastUtils.toast(getActivity(), R.string.py_area_code_empty);
            return;
        }
        String phone = gama_ingame_bind_et_phone.getEditableText().toString().trim();
        if (!phone.matches(selectedBean.getPattern())) {
            ToastUtils.toast(getActivity(), R.string.py_phone_error);
            return;
        }
        String interfaceName = GSRequestMethod.RequestVfcodeInterface.verify.getString();

        iLoginPresenter.getPhoneVfcode((Activity) getContext(), areaCode, phone, interfaceName);
    }

    @Override
    public void statusCallback(int operation) {
        if (TIME_LIMIT == operation) {
            gama_ingame_bind_btn_get_vfcode.setBackgroundResource(R.drawable.gama_ui_bg_btn_unclickable);
            gama_ingame_bind_btn_get_vfcode.setClickable(false);
        } else if (TIME_OUT == operation) {
            gama_ingame_bind_btn_get_vfcode.setBackgroundResource(R.drawable.bg_192d3f_46);
            gama_ingame_bind_btn_get_vfcode.setClickable(true);
            gama_ingame_bind_btn_get_vfcode.setText(R.string.py_ingame_account_link_get);
        } else if (BIND_OK == operation) {
            if(callBack != null) {
                callBack.success();
            }
        }
    }

    @Override
    public void dataCallback(Object o) {
        if (o instanceof GamaAreaInfoBean) {
            selectedBean = (GamaAreaInfoBean) o;
            String text = selectedBean.getValue();
            gama_ingame_bind_tv_area.setText(text);
        }
    }

    @Override
    public void alertTime(int remainTimeSeconds) {
        if(gama_ingame_bind_btn_get_vfcode.isClickable()) {
            gama_ingame_bind_btn_get_vfcode.setClickable(false);
        }
        gama_ingame_bind_btn_get_vfcode.setText(remainTimeSeconds + "s");
    }

    @Override
    protected void doSomething() {
        super.doSomething();
        iLoginPresenter.setOperationCallback(this);
        remainTimeSeconds = iLoginPresenter.getRemainTimeSeconds();
        if(remainTimeSeconds > 0) {
            gama_ingame_bind_btn_get_vfcode.setBackgroundResource(R.drawable.gama_ui_bg_btn_unclickable);
            gama_ingame_bind_btn_get_vfcode.setClickable(false);
            gama_ingame_bind_btn_get_vfcode.setText(remainTimeSeconds + "s");
        }
    }

    private void setDefaultAreaInfo() {
        selectedBean = new GamaAreaInfoBean();
        selectedBean.setValue(getResources().getString(R.string.py_default_area_num));
        selectedBean.setPattern(getResources().getString(R.string.py_default_area_num_pattern));
    }

}

package com.gama.sdk.login.widget.v2;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.thirdlib.google.SGoogleProxy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class PyAccountLoginV2 extends SLoginBaseRelativeLayout {

    private View contentView;

    private TextView loginMainLoginBtn;

    /**
     * 眼睛、保存密码、验证码
     */
    private ImageView eyeImageView, savePwdCheckBox, gama_login_iv_vfcode;

    /**
     * 密码、账号、验证码
     */
    private EditText loginPasswordEditText, loginAccountEditText, gama_login_et_vfcode;
    private String account;
    private String password;
    private View loginMainGoRegisterBtn;
    private View loginMainGoFindPwd;
    private View loginMainGoAccountCenter;
    private View loginMainGoChangePassword;
    private View vfcodeLayout;

    public PyAccountLoginV2(Context context) {
        super(context);

    }


    public PyAccountLoginV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PyAccountLoginV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.v2_gama_account_login, null);

        backView = contentView.findViewById(R.id.gama_head_iv_back);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toMainLoginView();
            }
        });

        vfcodeLayout = contentView.findViewById(R.id.gama_login_layout_vfcode);

        if(!GamaUtil.getVfcodeSwitchStatus(getContext())) { //没有开启验证码登入
            vfcodeLayout.setVisibility(GONE);
        } else {
            vfcodeLayout.setVisibility(VISIBLE);
        }

        loginMainGoRegisterBtn = contentView.findViewById(R.id.gama_login_tv_register);
        loginMainGoFindPwd = contentView.findViewById(R.id.gama_login_tv_forget_password);
        loginMainGoAccountCenter = contentView.findViewById(R.id.gama_login_tv_link);
        loginMainGoChangePassword = contentView.findViewById(R.id.gama_login_tv_change_password);

        eyeImageView = contentView.findViewById(R.id.gama_login_iv_eye);

        loginAccountEditText = contentView.findViewById(R.id.gama_login_et_account);
        loginPasswordEditText = contentView.findViewById(R.id.gama_login_et_password);
        gama_login_et_vfcode = contentView.findViewById(R.id.gama_login_et_vfcode);

        gama_login_iv_vfcode = contentView.findViewById(R.id.gama_login_iv_vfcode);
        gama_login_iv_vfcode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });

        loginMainLoginBtn = contentView.findViewById(R.id.gama_login_btn_confirm);

        savePwdCheckBox = contentView.findViewById(R.id.gama_login_iv_remember_account);

        savePwdCheckBox.setSelected(true);

        savePwdCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savePwdCheckBox.isSelected()) {
                    savePwdCheckBox.setSelected(false);
                } else {
                    savePwdCheckBox.setSelected(true);
                }
            }
        });

        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginActivity.replaceFragment(new AccountLoginFragment());
                sLoginDialogv2.toMainLoginView();
            }
        });

        loginMainGoRegisterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sLoginActivity.replaceFragmentBackToStack(new AccountRegisterFragment());

                sLoginDialogv2.toRegisterView(2);
            }
        });

        loginMainGoFindPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toFindPwdView();
            }
        });

        loginMainGoAccountCenter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toAccountManagerCenter();
            }
        });

        loginMainGoChangePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toChangePwdView();
            }
        });

        eyeImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eyeImageView.isSelected()) {
                    eyeImageView.setSelected(false);
                    // 显示为普通文本
                    loginPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    eyeImageView.setSelected(true);
                    // 显示为密码
                    loginPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // 使光标始终在最后位置
                Editable etable = loginPasswordEditText.getText();
                Selection.setSelection(etable, etable.length());
            }
        });

        loginMainLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        account = GamaUtil.getAccount(getContext());
        password = GamaUtil.getPassword(getContext());
        if (TextUtils.isEmpty(account)){
            account = GamaUtil.getMacAccount(getContext());
            password = GamaUtil.getMacPassword(getContext());
        }
        if (!TextUtils.isEmpty(account)){
            loginAccountEditText.setText(account);
            loginPasswordEditText.setText(password);
        }

        loadImage();

        return contentView;
    }

    private void login() {

        account = loginAccountEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_empty);
            return;
        }

        password = loginPasswordEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }

        if (SStringUtil.isEqual(account, password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
            return;
        }

        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getActivity(), R.string.py_account_error);
            return;
        }
        if (!GamaUtil.checkPassword(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_error);
            return;
        }

        String vfcode = gama_login_et_vfcode.getEditableText().toString().trim();
        if(TextUtils.isEmpty(vfcode)) {
            ToastUtils.toast(getActivity(), R.string.py_vfcode_empty);
            return;
        }

        sLoginDialogv2.getLoginPresenter().starpyAccountLogin(sLoginDialogv2.getActivity(),account,password, vfcode);

    }

    private void loadImage() {
        String vfcodeUrl = ResConfig.getLoginPreferredUrl(getContext()) + GSRequestMethod.GS_REQUEST_METHOD_VFCODE
                + "?timestamp=" + System.currentTimeMillis() + "&operatingSystem=android&uniqueId=" + SGoogleProxy.getAdvertisingId(getContext());
        PL.i(vfcodeUrl);
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //构建ImageRequest 实例
        final ImageRequest request = new ImageRequest(vfcodeUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                PL.i("response: " + response.toString());
                //给imageView设置图片
                gama_login_iv_vfcode.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //设置一张错误的图片，临时用ic_launcher代替
                gama_login_iv_vfcode.setImageResource(R.drawable.gama_title_sdk_bg);
            }
        });
        requestQueue.add(request);
    }
}

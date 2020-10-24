package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.core.base.utils.ToastUtils;
import com.flyfun.base.utils.GamaUtil;
import com.flyfun.sdk.login.model.AccountModel;
import com.flyfun.sdk.login.widget.SDKInputEditTextView;
import com.flyfun.sdk.login.widget.SDKInputType;
import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class PyAccountLoginV2 extends SLoginBaseRelativeLayout {

    private View contentView;

    private Button loginMainLoginBtn;

    /**
     * 眼睛、保存密码、验证码
     */
    private ImageView savePwdCheckBox;
    private CheckBox agreeCheckBox;

    /**
     * 密码、账号、验证码
     */
    private EditText loginPasswordEditText, loginAccountEditText;
    private String account;
    private String password;
    List<AccountModel> accountModels;
    private View loginMainGoRegisterBtn;
    private View loginMainGoFindPwd;
    private View loginMainGoAccountCenter;
    private View loginMainGoChangePassword;

    private View goTermView;

    private SDKInputEditTextView accountSdkInputEditTextView;
    private SDKInputEditTextView pwdSdkInputEditTextView;

    private RecyclerView historyAccountRv;
    View historyAccountListBtn;
    private CommonAdapter  historyAccountCommonAdapter;

    private View fbLoginView, bindAccountView, macLoginView, googleLoginView;

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


        accountSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_account_login_account);
        pwdSdkInputEditTextView = contentView.findViewById(R.id.sdkinputview_account_login_password);

        accountSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Account);
        pwdSdkInputEditTextView.setInputType(SDKInputType.SDKInputType_Password);

        loginMainGoRegisterBtn = contentView.findViewById(R.id.gama_login_tv_register);
        loginMainGoFindPwd = contentView.findViewById(R.id.gama_login_tv_forget_password);
        loginMainGoAccountCenter = contentView.findViewById(R.id.gama_login_tv_link);
        loginMainGoChangePassword = contentView.findViewById(R.id.gama_login_tv_change_password);


        loginAccountEditText = accountSdkInputEditTextView.getInputEditText();
        loginPasswordEditText = pwdSdkInputEditTextView.getInputEditText();

        historyAccountRv = contentView.findViewById(R.id.account_login_history_account_rv);
        historyAccountListBtn = contentView.findViewById(R.id.sdk_input_item_account_history);

        loginMainLoginBtn = contentView.findViewById(R.id.gama_login_btn_confirm);
        goTermView = contentView.findViewById(R.id.gama_gama_start_term_tv1);//跳轉服務條款
        agreeCheckBox = contentView.findViewById(R.id.gama_gama_start_term_cb1);//跳轉服務條款

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

        loginMainLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        goTermView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //服務條款
                sLoginDialogv2.toTermsV3View();
            }
        });
        fbLoginView = contentView.findViewById(R.id.fbLoginView);
        bindAccountView = contentView.findViewById(R.id.accountBindView);
        macLoginView = contentView.findViewById(R.id.guestLoginView);
        googleLoginView = contentView.findViewById(R.id.ggLoginView);

        fbLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
            }
        });
        bindAccountView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.toAccountManagerCenter();
            }
        });
        macLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginDialogv2.getLoginPresenter().macLogin(sLoginDialogv2.getActivity());
            }
        });
        googleLoginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //google+登录
                sLoginDialogv2.getLoginPresenter().googleLogin(sLoginDialogv2.getActivity());
            }
        });

        accountModels = new ArrayList<>();
        List<AccountModel> ams = GamaUtil.getAccountModels(getContext());
        accountModels.addAll(ams);
        if (accountModels != null && !accountModels.isEmpty()){//设置按照最好登录时间排序后的第一个账号
            AccountModel lastAccountModel = accountModels.get(0);
            account = lastAccountModel.getAccount();
            password = lastAccountModel.getPassword();

            if (accountModels.size() > 1){
                historyAccountListBtn.setVisibility(VISIBLE);
            }
        }

//        if (TextUtils.isEmpty(account)){
//            account = GamaUtil.getMacAccount(getContext());
//            password = GamaUtil.getMacPassword(getContext());
//        }
        if (!TextUtils.isEmpty(account)){ //显示记住的密码，待修改
            loginAccountEditText.setText(account);
            loginPasswordEditText.setText(password);
        }


        initHistoryRv();

        historyAccountListBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyAccountRv.getVisibility() == GONE){

                    historyAccountRv.setVisibility(VISIBLE);
                    historyAccountCommonAdapter.notifyDataSetChanged();
                }else {
                    historyAccountRv.setVisibility(GONE);
                }
            }
        });

        return contentView;
    }

    private void initHistoryRv() {

        if (accountModels == null){
            return;
        }

        historyAccountRv.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAccountCommonAdapter = new CommonAdapter<AccountModel>(this.getContext(), R.layout.sdk_login_history_account_item, accountModels)
        {

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }

            @Override
            protected void convert(final ViewHolder holder, final AccountModel accountModel, final int position) {
                holder.setText(R.id.history_account_item_text, accountModel.getAccount());
                holder.setOnClickListener(R.id.history_account_item_text, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginAccountEditText.setText(accountModel.getAccount());
                        loginPasswordEditText.setText(accountModel.getPassword());
                        historyAccountRv.setVisibility(GONE);
                    }
                });
                holder.setOnClickListener(R.id.history_account_item_delete_btn, new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AccountModel removeModel = accountModels.remove(position);
                        if (removeModel != null){

                            if (accountModels.isEmpty()){
                                historyAccountRv.setVisibility(GONE);
                                historyAccountListBtn.setVisibility(GONE);//删除就保存重新刷新数据
                                loginAccountEditText.setText("");
                                loginPasswordEditText.setText("");
                            }
                            GamaUtil.saveAccountModels(getContext(),accountModels);
                            List<AccountModel>  ams = GamaUtil.getAccountModels(getContext());
                            accountModels.clear();
                            accountModels.addAll(ams);
                            historyAccountCommonAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }

        };
        historyAccountRv.setAdapter(historyAccountCommonAdapter);
    }

    private void login() {

        if(!agreeCheckBox.isChecked()) {
            ToastUtils.toast(getContext(), R.string.gama_ui_term_not_read);
        }
        GamaUtil.saveStartTermRead(getContext(), true);

        account = loginAccountEditText.getEditableText().toString().trim();
        password = loginPasswordEditText.getEditableText().toString().trim();

        if (!accountSdkInputEditTextView.checkAccount()){
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getContext(), R.string.py_password_empty);
            return;
        }

        sLoginDialogv2.getLoginPresenter().starpyAccountLogin(sLoginDialogv2.getActivity(),account,password, "", savePwdCheckBox.isSelected());

    }

    /*private void loadImage() {
        String vfcodeUrl = ResConfig.getLoginPreferredUrl(getContext()) + GSRequestMethod.GS_REQUEST_METHOD_VFCODE
                + "?timestamp=" + System.currentTimeMillis() + "&operatingSystem=android&uniqueId=" + GamaUtil.getCustomizedUniqueId1AndroidId1Adid(getContext());
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
    }*/

    @Override
    public void refreshViewData() {
        super.refreshViewData();

        List<AccountModel>  ams = GamaUtil.getAccountModels(getContext());
        accountModels.clear();
        accountModels.addAll(ams);
        if (historyAccountCommonAdapter != null) {
            historyAccountCommonAdapter.notifyDataSetChanged();
        }
        if (accountModels != null && !accountModels.isEmpty()){
            AccountModel lastAccountModel = accountModels.get(0); //设置按照最好登录时间排序后的第一个账号
            account = lastAccountModel.getAccount();
            password = lastAccountModel.getPassword();

        }

        if (!TextUtils.isEmpty(account)){
            loginAccountEditText.setText(account);
            loginPasswordEditText.setText(password);
        }

        if (accountModels != null && accountModels.size() > 1){
            historyAccountListBtn.setVisibility(VISIBLE);
        }else{
            historyAccountListBtn.setVisibility(GONE);
        }
        if (historyAccountRv != null) {
            historyAccountRv.setVisibility(GONE);
        }

    }

    @Override
    public void refreshVfCode() {
        super.refreshVfCode();
        /*if (GamaUtil.getVfcodeSwitchStatus(getContext())) {
            loadImage();
        }*/
    }
}

package com.gama.sdk.plat.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.utils.StarPyUtil;
import com.gama.data.login.execute.ChangePwdRequestTask;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.R;
import com.gama.sdk.SSdkBaseFragment;
import com.gama.sdk.plat.PlatMainActivity;
import com.gama.sdk.plat.data.bean.response.UserBindInfoModel;
import com.gama.sdk.utils.DialogUtil;


public class StarpyAccountManagerFragment extends SSdkBaseFragment {

    private Dialog mDialog;

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    private UserBindInfoModel userBindInfoModel;

    private TextView accountTextView;
    private TextView bangPhoneTextView;
    private TextView phoneTipsTextView;
    private EditText oldPwdEditText;
    private EditText newPwdEditText;

    View changePwdView;
    View goChangePwdBtn;

    View confirmChangePwd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PL.d("onCreateView");

        View contentView = inflater.inflate(R.layout.plat_account_manage_starpy, container, false);

        TextView titleTextView = (TextView) contentView.findViewById(R.id.plat_title_tv);
        titleTextView.setText(title);

        contentView.findViewById(R.id.plat_title_close_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        accountTextView = (TextView) contentView.findViewById(R.id.plat_account);
        bangPhoneTextView = (TextView) contentView.findViewById(R.id.plat_phone_bang_tv);
        phoneTipsTextView = (TextView) contentView.findViewById(R.id.plat_phone_tips_tv);
        oldPwdEditText = (EditText) contentView.findViewById(R.id.plat_old_pwd_edt);
        newPwdEditText = (EditText) contentView.findViewById(R.id.plat_new_pwd_edt);

        changePwdView = contentView.findViewById(R.id.plat_change_pwd_layout);
        goChangePwdBtn = contentView.findViewById(R.id.plat_go_change_pwd_button);

        confirmChangePwd = contentView.findViewById(R.id.plat_confirm_btn);

        mDialog = DialogUtil.createLoadingDialog(getActivity(),"Loading...");

        accountTextView.setText(String.format(getString(R.string.plat_starpy_account),userBindInfoModel.getName()));

        if (SStringUtil.isNotEmpty(userBindInfoModel.getTelephone())){
            bangPhoneTextView.setText(getString(R.string.plat_already_bind_phone));
            bangPhoneTextView.setClickable(false);
            phoneTipsTextView.setText(userBindInfoModel.getTelephone());
        }else{

            bangPhoneTextView.setText(getString(R.string.plat_starpy_account_not_bind_phone));
            bangPhoneTextView.setClickable(true);
            phoneTipsTextView.setText(getString(R.string.plat_starpy_account_not_bind_phone_tips));

            bangPhoneTextView.setTextColor(getResources().getColor(R.color.e_ff0000));
            bangPhoneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PlatMainActivity)getActivity()).changeToBindPhoneGiftFragment(null);
                }
            });
        }

        //点击修改密码，展示修改密码
     goChangePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChangePwdBtn.setVisibility(View.GONE);
                changePwdView.setVisibility(View.VISIBLE);
            }
        });


        confirmChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwd();
            }
        });
        return contentView;

    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PL.d("onViewCreated -- tag:" + this.getTag());

    }

    public void setUserBindInfoModel(UserBindInfoModel userBindInfoModel) {
        this.userBindInfoModel = userBindInfoModel;
    }


    private void changePwd() {


        String password = oldPwdEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }

        String newPassword = newPwdEditText.getEditableText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            ToastUtils.toast(getActivity(), R.string.py_password_empty);
            return;
        }


        if (SStringUtil.isEqual(userBindInfoModel.getName(), newPassword)) {
            ToastUtils.toast(getActivity(), R.string.py_password_equal_account);
            return;
        }

        if (!StarPyUtil.checkAccount(userBindInfoModel.getName())) {
            ToastUtils.toast(getActivity(), R.string.py_account_error);
            return;
        }
        if (!StarPyUtil.checkPassword(newPassword)) {
            ToastUtils.toast(getActivity(), R.string.py_password_error);
            return;
        }

        if (SStringUtil.isEqual(password,newPassword)){
            ToastUtils.toast(getActivity(), R.string.py_old_equel_new_pwd);
            return;
        }

        changePwdTask(getActivity(),userBindInfoModel.getName(),password,newPassword);
//        ((PlatMainActivity)getTheContext()).getiLoginPresenter().changePwd(getTheContext(),userBindInfoModel.getName(),password,newPassword);
    }

    private void changePwdTask(final Activity activity, final String account, String oldPwd, String newPwd) {

        ChangePwdRequestTask changePwdRequestTask = new ChangePwdRequestTask(activity,account,oldPwd,newPwd);
        changePwdRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
        changePwdRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {

            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                        oldPwdEditText.setText("");
                        newPwdEditText.setText("");
                        goChangePwdBtn.setVisibility(View.VISIBLE);
                        changePwdView.setVisibility(View.GONE);


                    }else {

                        ToastUtils.toast(getActivity(), sLoginResponse.getMessage());
                    }

                } else {
                    ToastUtils.toast(getActivity(), R.string.py_error_occur);
                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });

        changePwdRequestTask.excute(SLoginResponse.class);

    }
}

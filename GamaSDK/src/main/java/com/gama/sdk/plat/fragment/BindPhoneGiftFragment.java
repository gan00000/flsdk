package com.gama.sdk.plat.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.AbsHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.base.bean.SGameBaseRequestBean;
import com.gama.base.bean.SSdkBaseRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.StarPyUtil;
import com.gama.data.login.execute.BaseLoginRequestTask;
import com.gama.data.login.request.AccountBindPhoneEmailBean;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.R;
import com.gama.sdk.SSdkBaseFragment;
import com.gama.sdk.plat.data.bean.response.GiftGetSuccessModel;
import com.gama.sdk.plat.data.bean.response.PhoneAreaCodeModel;
import com.gama.sdk.plat.data.bean.response.UserBindInfoModel;
import com.gama.sdk.plat.data.bean.response.UserHasGetGiftModel;
import com.gama.sdk.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class BindPhoneGiftFragment extends SSdkBaseFragment {

    private Dialog mDialog;

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    private UserBindInfoModel userBindInfoModel;

    private TextView accountTextView;

    //已经绑定
    private TextView bindPhoneNumTextView;
    private TextView bindPhoneGetGiftTextView;

    //未绑定
    private TextView phoneAreaCodeTextView;
    private EditText bindPhoneNumEditText;

    private TextView bindPhoneGetVfCodeTextView;
    private EditText bindPhoneVfCodeEditText;
    private TextView bindPhoneConfirmBindTextView;

    private TextView bindPhoneGetGiftTipsTextView;

    public UserHasGetGiftModel getUserHasGetGiftModel() {
        return userHasGetGiftModel;
    }

    public void setUserHasGetGiftModel(UserHasGetGiftModel userHasGetGiftModel) {
        this.userHasGetGiftModel = userHasGetGiftModel;
    }

    View plat_has_bind_phone_layout;
    View plat_bind_phone_layout;

    private UserHasGetGiftModel userHasGetGiftModel;

    private List<PhoneAreaCodeModel> phoneAreaCodeModels;

    public List<PhoneAreaCodeModel> getPhoneAreaCodeModels() {
        return phoneAreaCodeModels;
    }

    public void setPhoneAreaCodeModels(List<PhoneAreaCodeModel> phoneAreaCodeModels) {
        this.phoneAreaCodeModels = phoneAreaCodeModels;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PL.d("onCreateView");

        View contentView = inflater.inflate(R.layout.plat_phone_bind_gift, container, false);

        TextView titleTextView = (TextView) contentView.findViewById(R.id.plat_title_tv);
        titleTextView.setText(title);

        contentView.findViewById(R.id.plat_title_close_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        accountTextView = (TextView) contentView.findViewById(R.id.plat_account);
        bindPhoneNumTextView = (TextView) contentView.findViewById(R.id.bind_phone_num_tv);
        bindPhoneGetGiftTextView = (TextView) contentView.findViewById(R.id.bind_phone_get_gift_btn);

        phoneAreaCodeTextView = (TextView) contentView.findViewById(R.id.bind_phone_area_num);
        bindPhoneNumEditText = (EditText) contentView.findViewById(R.id.bind_phone_num_et);
        bindPhoneVfCodeEditText = (EditText) contentView.findViewById(R.id.bind_phone_vf_code_et);
        bindPhoneGetVfCodeTextView = (TextView) contentView.findViewById(R.id.bind_phone_get_vf_code_tv);
        bindPhoneConfirmBindTextView = (TextView) contentView.findViewById(R.id.bind_phone_confirm_bind);
        bindPhoneGetGiftTipsTextView = (TextView) contentView.findViewById(R.id.bind_phone_get_gift_tips);

        plat_has_bind_phone_layout = contentView.findViewById(R.id.plat_has_bind_phone_layout);
        plat_bind_phone_layout = contentView.findViewById(R.id.plat_bind_phone_layout);

        mDialog = DialogUtil.createLoadingDialog(getActivity(),"Loading...");


        if (userBindInfoModel == null){
            ToastUtils.toast(getActivity(),R.string.plat_get_data_error);
            plat_bind_phone_layout.setVisibility(View.GONE);
            plat_has_bind_phone_layout.setVisibility(View.GONE);
            bindPhoneGetGiftTextView.setVisibility(View.GONE);
        }else{

            if (userHasGetGiftModel == null){
                plat_bind_phone_layout.setVisibility(View.GONE);
                plat_has_bind_phone_layout.setVisibility(View.GONE);
                bindPhoneGetGiftTextView.setVisibility(View.GONE);
                bindPhoneNumTextView.setText(userBindInfoModel.getTelephone());

            }else if (userBindInfoModel.hasBindPhone()){

                if (userHasGetGiftModel.isReceive()){
                    plat_bind_phone_layout.setVisibility(View.GONE);
                    plat_has_bind_phone_layout.setVisibility(View.VISIBLE);
                    bindPhoneGetGiftTextView.setVisibility(View.GONE);
                    bindPhoneGetGiftTipsTextView.setText(getString(R.string.plat_has_get_gift) + " " + userHasGetGiftModel.getSerial());
                    bindPhoneNumTextView.setText(userBindInfoModel.getTelephone());
                }else {

                    plat_bind_phone_layout.setVisibility(View.GONE);
                    plat_has_bind_phone_layout.setVisibility(View.VISIBLE);
                    bindPhoneNumTextView.setText(userBindInfoModel.getTelephone());
                    bindPhoneGetGiftTipsTextView.setText(userHasGetGiftModel.getRewardName());

                }

            }else {

                if (userHasGetGiftModel.isReceive()){
                    plat_bind_phone_layout.setVisibility(View.GONE);
                    plat_has_bind_phone_layout.setVisibility(View.GONE);
                    bindPhoneGetGiftTipsTextView.setText(getString(R.string.plat_has_get_gift) + " " + userHasGetGiftModel.getSerial());
                }else {

                    plat_bind_phone_layout.setVisibility(View.VISIBLE);
                    plat_has_bind_phone_layout.setVisibility(View.GONE);

                    bindPhoneNumTextView.setText(userBindInfoModel.getTelephone());
                    bindPhoneGetGiftTipsTextView.setText(userHasGetGiftModel.getRewardName());

                }

            }
            initGetGiftLayout();
            initBindPhoneLayout();
            if (SStringUtil.isNotEmpty(userBindInfoModel.getName())){

                accountTextView.setText(String.format(getString(R.string.plat_starpy_account),userBindInfoModel.getName()));
            }else if (SStringUtil.isNotEmpty(userBindInfoModel.getFreeRegisterName())){
                accountTextView.setText(String.format(getString(R.string.plat_starpy_account),userBindInfoModel.getFreeRegisterName()));
            }else {
                accountTextView.setText(String.format(getString(R.string.plat_starpy_account),"unknown"));

            }
            if (phoneAreaCodeModels != null){
                phoneAreaCodeTextView.setText(phoneAreaCodeModels.get(0).getCode());
            }
        }

        return contentView;

    }

    //显示电话号码区号
    private void showPhoneAreaCode() {
        if (phoneAreaCodeModels == null){
            return;
        }
        final List<String> data = new ArrayList<String>();
        for (int i = 0; i < phoneAreaCodeModels.size(); i++) {
            data.add(phoneAreaCodeModels.get(i).getName() + "  " + phoneAreaCodeModels.get(i).getCode());
        }
        View listLayout = LayoutInflater.from(getActivity()).inflate(R.layout.plat_phone_area_code_list,null);
        ListView listView = (ListView) listLayout.findViewById(R.id.phone_area_num_list_id);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.plat_phone_area_code_list_item,R.id.area_code_item_id,data));

        final Dialog dialog = DialogUtil.createDialog(getActivity(),listLayout,true,true);
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PL.i(data.get(position) + "   position:" + position);
                dialog.dismiss();
                phoneAreaCodeTextView.setText(phoneAreaCodeModels.get(position).getCode());
            }
        });
    }

    private void initGetGiftLayout() {

        bindPhoneGetGiftTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestGetGift();
            }
        });
    }

    //领取礼包
    private void requestGetGift() {
        final SGameBaseRequestBean requestBean = new SGameBaseRequestBean(getActivity());
        requestBean.setCompleteUrl(ResConfig.getPlatPreferredUrl(getActivity()) + "app/giftbag/api/getPhoneBindGiftbag");
        AbsHttpRequest requestTask = new AbsHttpRequest() {
            @Override
            public BaseReqeustBean createRequestBean() {
                return requestBean;
            }
        };
        requestTask.setLoadDialog(mDialog);
        requestTask.setReqCallBack(new ISReqCallBack<GiftGetSuccessModel>() {
            @Override
            public void success(GiftGetSuccessModel getSuccessModel, String rawResult) {
                if (getSuccessModel != null){
                    PL.i(getSuccessModel.getMessage());
                    ToastUtils.toast(getActivity(),getSuccessModel.getMessage());
                    if (getSuccessModel.isRequestSuccess()) {
                        userHasGetGiftModel.setReceive(true);
                        userHasGetGiftModel.setSerial(getSuccessModel.getData());
                        bindPhoneGetGiftTextView.setVisibility(View.GONE);
                        bindPhoneNumTextView.setText(userBindInfoModel.getTelephone());
                        bindPhoneGetGiftTipsTextView.setText(getString(R.string.plat_has_get_gift) + " " + userHasGetGiftModel.getSerial());
                    }
                }

            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        requestTask.excute(GiftGetSuccessModel.class);
    }

    private void initBindPhoneLayout() {
        bindPhoneGetVfCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestVfCode();

            }
        });
        bindPhoneConfirmBindTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBindPhone();
//                changeShowHashBindPhone(null);
            }
        });

        phoneAreaCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneAreaCode();
            }
        });
    }

    private Timer countdowncTimer;
    private int count = 60;

    //獲取驗證碼
    private void requestVfCode() {

        String areaCode = phoneAreaCodeTextView.getText().toString().trim();
        String phone = bindPhoneNumEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toast(getActivity(), R.string.plat_phone_num_not_empty);
            return;
        }

        String phonePattern = "";
        for (PhoneAreaCodeModel phoneAreaCodeModel :
                phoneAreaCodeModels) {
            if (areaCode.equals(phoneAreaCodeModel.getCode())) {
                phonePattern = phoneAreaCodeModel.getPattern();
                break;
            }
        }
        if (SStringUtil.isNotEmpty(phonePattern)){
            if (!phone.matches(phonePattern)){
                ToastUtils.toast(getActivity(), R.string.plat_phone_num_format_error);
                return;
            }
        }

        final SSdkBaseRequestBean sSdkBaseRequestBean = new SSdkBaseRequestBean(getActivity());
        sSdkBaseRequestBean.setPhone(phone);
        sSdkBaseRequestBean.setPhoneAreaCode(areaCode);
        sSdkBaseRequestBean.setRequestMethod("acquireVfCode");
        BaseLoginRequestTask requestTask = new BaseLoginRequestTask(getActivity()) {
            @Override
            public BaseReqeustBean createRequestBean() {
                super.createRequestBean();
                sSdkBaseRequestBean.setSignature(SStringUtil.toMd5(sSdkBaseRequestBean.getAppKey() + sSdkBaseRequestBean.getTimestamp()
                + sSdkBaseRequestBean.getGameCode() + sSdkBaseRequestBean.getPhone()));
                return sSdkBaseRequestBean;
            }
        };
        requestTask.setSdkBaseRequestBean(sSdkBaseRequestBean);
        requestTask.setLoadDialog(mDialog);
        requestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null){
                    PL.i(sLoginResponse.getMessage());
                    ToastUtils.toast(getActivity(),sLoginResponse.getMessage());
                    if (sLoginResponse.isRequestSuccess()) {
                        startCountDownTimer();
                    }
                }

            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        requestTask.excute(SLoginResponse.class);
    }

    //验证码倒计时
    private void startCountDownTimer() {
        //获取验证码
        countdowncTimer = new Timer();//delay为long,period为long：从现在起过delay毫秒以后，每隔period毫秒执行一次。

        countdowncTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindPhoneGetVfCodeTextView.setText("(" + count +  ")");
                        bindPhoneGetVfCodeTextView.setClickable(false);
                        if (count <= 0){

                            if (countdowncTimer != null){
                                countdowncTimer.cancel();
                            }
                            bindPhoneGetVfCodeTextView.setClickable(true);
                            bindPhoneGetVfCodeTextView.setText(getResources().getString(R.string.plat_bind_phone_get_vf_code));
                        }
                        count--;
                    }
                });
            }
        },300,1000);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PL.d("onViewCreated -- tag:" + this.getTag());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (countdowncTimer != null){
            countdowncTimer.cancel();
        }
        if (mDialog != null){
            mDialog.dismiss();
        }
    }

    public void setUserBindInfoModel(UserBindInfoModel userBindInfoModel) {
        this.userBindInfoModel = userBindInfoModel;
    }


    //綁定手機
    private void requestBindPhone() {


        String areaCode = phoneAreaCodeTextView.getText().toString().trim();
        String phone = bindPhoneNumEditText.getEditableText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toast(getActivity(), R.string.plat_phone_num_not_empty);
            return;
        }
        String phonePattern = "";
        for (PhoneAreaCodeModel phoneAreaCodeModel :
                phoneAreaCodeModels) {
            if (areaCode.equals(phoneAreaCodeModel.getCode())) {
                phonePattern = phoneAreaCodeModel.getPattern();
                break;
            }
        }
        if (SStringUtil.isNotEmpty(phonePattern)){
            if (!phone.matches(phonePattern)){
                ToastUtils.toast(getActivity(), R.string.plat_phone_num_format_error);
                return;
            }
        }

        String vfCode = bindPhoneVfCodeEditText.getEditableText().toString().trim();

        if (TextUtils.isEmpty(vfCode)) {
            ToastUtils.toast(getActivity(), R.string.plat_vf_code_not_empty);
            return;
        }

        final AccountBindPhoneEmailBean sSdkBaseRequestBean = new AccountBindPhoneEmailBean(getActivity());
        sSdkBaseRequestBean.setPhone(phone);
        sSdkBaseRequestBean.setVfCode(vfCode);
        sSdkBaseRequestBean.setPhoneAreaCode(areaCode);
        sSdkBaseRequestBean.setUserId(StarPyUtil.getUid(getActivity()));
        sSdkBaseRequestBean.setRequestMethod("bindpe_phone_email");
        BaseLoginRequestTask requestTask = new BaseLoginRequestTask(getActivity()) {
            @Override
            public BaseReqeustBean createRequestBean() {
                super.createRequestBean();
                sSdkBaseRequestBean.setSignature(SStringUtil.toMd5(sSdkBaseRequestBean.getAppKey() + sSdkBaseRequestBean.getTimestamp()
                        + sSdkBaseRequestBean.getGameCode() + sSdkBaseRequestBean.getPhone()));
                return sSdkBaseRequestBean;
            }
        };
        requestTask.setSdkBaseRequestBean(sSdkBaseRequestBean);
        requestTask.setLoadDialog(mDialog);
        requestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null){
                    PL.i(sLoginResponse.getMessage());
                    ToastUtils.toast(getActivity(),sLoginResponse.getMessage());
                    if (sLoginResponse.isRequestSuccess()) {

                        changeShowHashBindPhone(sSdkBaseRequestBean);

                    }
                }

            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        requestTask.excute(SLoginResponse.class);

    }

    private void changeShowHashBindPhone(AccountBindPhoneEmailBean sSdkBaseRequestBean) {
        userBindInfoModel.setPhoneAreaCode(sSdkBaseRequestBean.getPhoneAreaCode());
        userBindInfoModel.setPhoneAreaCode(sSdkBaseRequestBean.getPhone());
        userBindInfoModel.setTelephone(sSdkBaseRequestBean.getPhoneAreaCode() + sSdkBaseRequestBean.getPhone());
        userBindInfoModel.setBindPhone(true);

        plat_bind_phone_layout.setVisibility(View.GONE);
        plat_has_bind_phone_layout.setVisibility(View.VISIBLE);

        bindPhoneNumTextView.setText(userBindInfoModel.getTelephone());
        bindPhoneGetGiftTipsTextView.setText(userHasGetGiftModel.getRewardName());
    }

}

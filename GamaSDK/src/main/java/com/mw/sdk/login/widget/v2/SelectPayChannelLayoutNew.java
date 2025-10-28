package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.mw.sdk.bean.res.PayChannelData;
import com.mw.sdk.bean.res.ToggleResult;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.login.adapter.PayChannelViewAdapter;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

import java.util.List;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class SelectPayChannelLayoutNew extends SLoginBaseRelativeLayout {

    private View contentView;

    private ImageView appiconImageView;
    private TextView uidTextView;
    private TextView amountTextView;
    private RecyclerView mRecyclerView;
    private PayChannelViewAdapter payChannelViewAdapter;

    private ToggleResult toggleResult;
    private List<PayChannelData> payChannelDatas;

    private SFCallBack<PayChannelData> sfCallBack;

    public void setSfCallBack(SFCallBack<PayChannelData> sfCallBack) {
        this.sfCallBack = sfCallBack;
        if (payChannelViewAdapter != null){
            payChannelViewAdapter.setSfCallBack(sfCallBack);
        }
    }

    public SelectPayChannelLayoutNew(Context context) {
        super(context);

    }

    public SelectPayChannelLayoutNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectPayChannelLayoutNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        PL.i("view onLayout");
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.mw_select_pay_channel_v2, null);

        backView = contentView.findViewById(R.id.iv_select_channel_close);
        mRecyclerView = contentView.findViewById(R.id.mw_rv_select_paychannel);
        appiconImageView = contentView.findViewById(R.id.iv_select_channel_appicon);
        uidTextView = contentView.findViewById(R.id.iv_select_channel_uid);
        amountTextView = contentView.findViewById(R.id.iv_select_channel_amount);

        appiconImageView.setImageDrawable(ApkInfoUtil.getAppIcon(getContext()));
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sBaseDialog != null){
                    sBaseDialog.dismiss();
                }
            }
        });

        //handleViewData();

        PL.d("layout onCreateView end");
        return contentView;
    }

    private void handleViewData() {

        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());
        if (toggleResult == null || toggleResult.getData() == null || toggleResult.getData().getChannelList() == null
                || sLoginResponse == null || sLoginResponse.getData() == null || SStringUtil.isEmpty(sLoginResponse.getData().getUserId())){
            return;
        }

        uidTextView.setText("uid:" + sLoginResponse.getData().getUserId());
        amountTextView.setText(toggleResult.getData().getProductName());

        //测试数据==============
//        payChannelDatas = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            PayChannelData payChannelData = new PayChannelData();
//            payChannelData.setChannelName("name=" + i);
//            payChannelDatas.add(payChannelData);
//        }
        //测试数据==============

        //正式数据
        payChannelDatas = toggleResult.getData().getChannelList();

        PayChannelData payNavtiveData = new PayChannelData();
        payNavtiveData.setViewType(2);
        payNavtiveData.setShowPayGG(false);
        payNavtiveData.setShowPayRutore(false);
        payNavtiveData.setShowPayXM(false);

        String channel_platform = ResConfig.getChannelPlatform(getContext());
        if (ChannelPlatform.GOOGLE.getChannel_platform().equals(channel_platform)){
            payNavtiveData.setShowPayGG(true);
            String payChannel = getContext().getResources().getString(R.string.mw_dialog_pay_add_type);
            if(payChannel.contains(ChannelPlatform.VK.getChannel_platform())){
                payNavtiveData.setShowPayRutore(true);
            }else {
                payNavtiveData.setShowPayRutore(false);
            }

            if(payChannel.contains(ChannelPlatform.Xiaomi.getChannel_platform())){
                payNavtiveData.setShowPayXM(true);
            }else {
                payNavtiveData.setShowPayXM(false);
            }
        }else {

            payNavtiveData.setShowPayGG(false);

            if (ChannelPlatform.VK.getChannel_platform().equals(channel_platform)){
                payNavtiveData.setShowPayRutore(true);
            }else {
                payNavtiveData.setShowPayRutore(false);
            }

            if(ChannelPlatform.Xiaomi.getChannel_platform().equals(channel_platform)){
                payNavtiveData.setShowPayXM(true);
            }else {
                payNavtiveData.setShowPayXM(false);
            }

        }

        if (payNavtiveData.isShowPayGG() || payNavtiveData.isShowPayRutore() || payNavtiveData.isShowPayXM()){
            payChannelDatas.add(payNavtiveData);
        }

        payChannelViewAdapter = new PayChannelViewAdapter(getContext(), payChannelDatas);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(payChannelViewAdapter);

    }

    @Override
    public void setDatas(Object data) {
        this.toggleResult = (ToggleResult) data;
        handleViewData();
    }
}

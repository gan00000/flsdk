package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.AppUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.mw.sdk.bean.res.PayChannelData;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.login.adapter.PayChannelViewAdapter;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

import java.util.ArrayList;
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
    private List<PayChannelData> payChannelDatas;

    private SFCallBack<ChannelPlatform> sfCallBack;

    public void setSfCallBack(SFCallBack<ChannelPlatform> sfCallBack) {
        this.sfCallBack = sfCallBack;
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

        contentView = inflater.inflate(R.layout.mw_select_pay_channel_newa, null);

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

        SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());
        if (sLoginResponse == null || sLoginResponse.getData() == null || SStringUtil.isEmpty(sLoginResponse.getData().getUserId())){
            return contentView;
        }

        uidTextView.setText("uid:" + sLoginResponse.getData().getUserId());
        amountTextView.setText("");

        payChannelDatas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PayChannelData payChannelData = new PayChannelData();
            payChannelData.setChannelName("name=" + i);
            payChannelDatas.add(payChannelData);
        }

        PayChannelData payChannelData2 = new PayChannelData();
        payChannelData2.setViewType(2);
        payChannelData2.setShowPayXM(false);
        payChannelData2.setShowPayGG(true);
        payChannelData2.setShowPayRutore(true);
        payChannelDatas.add(payChannelData2);

        payChannelViewAdapter = new PayChannelViewAdapter(getContext(), payChannelDatas);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(payChannelViewAdapter);


        return contentView;
    }
}

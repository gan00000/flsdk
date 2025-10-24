package com.mw.sdk.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mw.sdk.R;
import com.mw.sdk.bean.res.PayChannelData;

import java.util.List;

public class PayChannelViewAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<PayChannelData> payChannelDatas;
    private static final int ITEM_TYPE_NORMAL = 0;//普通类型
    private static final int ITEM_TYPE_SECTION = 2;//特殊类型

    public PayChannelViewAdapter(Context context, List<PayChannelData> infos) {
        this.mContext = context;
        this.payChannelDatas = infos;
    }

    @Override
    public int getItemViewType(int position) {
        PayChannelData payChannelData = payChannelDatas.get(position);
        return payChannelData.getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //根据不同类型来获取不同的ViewHolder，里面装载不同的布局
        if (viewType == ITEM_TYPE_SECTION) {
            return new PayNativeHolder(inflater.inflate(R.layout.mw_pay_channel_itme_type2, parent, false));
        } else {
            return new PayChannelHolder(inflater.inflate(R.layout.mw_pay_channel_itme_type1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        PayChannelData payChannelData = payChannelDatas.get(position);

        if (holder instanceof PayChannelHolder) {//普通类型ViewHolder

            PayChannelHolder viewHolder = (PayChannelHolder) holder;

            Glide.with(mContext)
            .load(payChannelData.getIcon())
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.mipmap.mw_icon_placeholder)
            .into(viewHolder.channelImageView);

            viewHolder.channelName.setText(payChannelData.getChannelName());
            viewHolder.channelDes.setText(payChannelData.getDescribe());

        } else if (holder instanceof PayNativeHolder) {//特殊类型ViewHolder
            PayNativeHolder payNativeHolder = (PayNativeHolder) holder;

            payNativeHolder.iv_select_channel_gg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            payNativeHolder.rl_select_channel_ru.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            payNativeHolder.rl_select_channel_xm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if (payChannelData.isShowPayGG()){
                payNativeHolder.iv_select_channel_gg.setVisibility(View.VISIBLE);
            }else {
                payNativeHolder.iv_select_channel_gg.setVisibility(View.GONE);
            }
            if (payChannelData.isShowPayRutore()){
                payNativeHolder.rl_select_channel_ru.setVisibility(View.VISIBLE);
            }else {
                payNativeHolder.rl_select_channel_ru.setVisibility(View.GONE);
            }
            if (payChannelData.isShowPayXM()){
                payNativeHolder.rl_select_channel_xm.setVisibility(View.VISIBLE);
            }else {
                payNativeHolder.rl_select_channel_xm.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (payChannelDatas == null){
            return 0;
        }
        return payChannelDatas.size();
    }

    //普通类型ViewHolder
    public static class PayChannelHolder extends RecyclerView.ViewHolder {
        ImageView channelImageView;
        TextView channelName;
        TextView channelDes;

        PayChannelHolder(@NonNull View itemView) {
            super(itemView);
            channelImageView = itemView.findViewById(R.id.iv_paychannel_icon);
            channelName = itemView.findViewById(R.id.tv_paychannel_name);
            channelDes = itemView.findViewById(R.id.tv_paychannel_des);
        }
    }

    //特殊类型ViewHolder
    public static class PayNativeHolder extends RecyclerView.ViewHolder {
        View payNativeView;
        View iv_select_channel_gg;
        View rl_select_channel_ru;
        View rl_select_channel_xm;

        PayNativeHolder(@NonNull View itemView) {
            super(itemView);
            payNativeView = itemView.findViewById(R.id.ll_channel_native);
            iv_select_channel_gg = itemView.findViewById(R.id.iv_select_channel_gg);
            rl_select_channel_ru = itemView.findViewById(R.id.rl_select_channel_ru);
            rl_select_channel_xm = itemView.findViewById(R.id.rl_select_channel_xm);
        }
    }
}

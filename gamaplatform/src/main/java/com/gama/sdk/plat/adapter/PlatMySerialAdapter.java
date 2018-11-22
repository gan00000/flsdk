package com.gama.sdk.plat.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.sdk.R;
import com.gama.sdk.callback.RecylerViewItemClickListener;
import com.gama.sdk.plat.data.bean.response.PlatMySerialModel;

import java.util.List;

/**
 * Created by gan on 2017/8/10.
 */

public class PlatMySerialAdapter extends RecyclerView.Adapter {

    private Activity activity;

    private LayoutInflater layoutInflater;

    public void setDataModelList(List<PlatMySerialModel> platMySerialModels) {
        this.platMySerialModels = platMySerialModels;
    }

    private List<PlatMySerialModel> platMySerialModels;

    public PlatMySerialAdapter(Activity activity) {
        this.activity = activity;
        layoutInflater = LayoutInflater.from(activity);
    }


    private RecylerViewItemClickListener recylerViewItemClickListener;

    public void setRecylerViewItemClickListener(RecylerViewItemClickListener recylerViewItemClickListener) {
        this.recylerViewItemClickListener = recylerViewItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new GiftCenterHolder(layoutInflater.inflate(R.layout.plat_my_serial_center_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        PlatMySerialModel platMySerialModel = platMySerialModels.get(i);
        if (platMySerialModel != null){
            ((GiftCenterHolder)viewHolder).timeTextView.setText(platMySerialModel.getTime());
            ((GiftCenterHolder)viewHolder).titleTextView.setText(platMySerialModel.getTitle());
            ((GiftCenterHolder)viewHolder).desTextView.setText(platMySerialModel.getRewardName());
            if (SStringUtil.isNotEmpty(platMySerialModel.getGiftbag())) {
                ((GiftCenterHolder)viewHolder).serialNmu.setText(activity.getResources().getString(R.string.plat_my_serial) + platMySerialModel.getGiftbag());
            }
        }
    }

    @Override
    public int getItemCount() {
        return platMySerialModels == null ? 0 : platMySerialModels.size();
    }

    private class GiftCenterHolder extends RecyclerView.ViewHolder{

        TextView serialNmu;
        TextView timeTextView;
        TextView titleTextView;
        TextView desTextView;


        public GiftCenterHolder(View contentView) {
            super(contentView);

            serialNmu = (TextView) contentView.findViewById(R.id.plat_serial_num_item_num);
            timeTextView = (TextView) contentView.findViewById(R.id.plat_serial_num_item_time);
            titleTextView = (TextView) contentView.findViewById(R.id.plat_serial_num_item_title);
            desTextView = (TextView) contentView.findViewById(R.id.plat_serial_num_item_des);
            serialNmu.setTextIsSelectable(true);
            contentView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
//                        cmb.setText(platMySerialModels.get(getLayoutPosition()).getGiftbag()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                        ClipData cd = ClipData.newPlainText(null,platMySerialModels.get(getLayoutPosition()).getGiftbag());
                        cmb.setPrimaryClip(cd);
                        //cm.getText();//获取粘贴信息
                        ToastUtils.toast(activity,String.format(activity.getString(R.string.plat_copy_success),platMySerialModels.get(getLayoutPosition()).getGiftbag()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}

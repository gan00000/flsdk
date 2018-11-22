package com.gama.sdk.plat.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gama.sdk.R;
import com.gama.sdk.callback.RecylerViewItemClickListener;
import com.gama.sdk.plat.data.bean.response.PlatMessageBoxModel;

import java.util.List;

/**
 * Created by gan on 2017/8/10.
 */

public class PlatMessageBoxAdapter extends RecyclerView.Adapter {

    private Activity activity;

    private LayoutInflater layoutInflater;

    public void setDataModelList(List<PlatMessageBoxModel> modelList) {
        this.models = modelList;
    }

    private List<PlatMessageBoxModel> models;

    public PlatMessageBoxAdapter(Activity activity) {
        this.activity = activity;
        layoutInflater = LayoutInflater.from(activity);
    }


    private RecylerViewItemClickListener recylerViewItemClickListener;

    public void setRecylerViewItemClickListener(RecylerViewItemClickListener recylerViewItemClickListener) {
        this.recylerViewItemClickListener = recylerViewItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new GiftCenterHolder(layoutInflater.inflate(R.layout.plat_message_box_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        PlatMessageBoxModel platMessageBoxModel = models.get(i);
        if (platMessageBoxModel != null){
            ((GiftCenterHolder)viewHolder).timeTextView.setText(platMessageBoxModel.getTime());
            ((GiftCenterHolder)viewHolder).titleTextView.setText(platMessageBoxModel.getTitle());
            if (platMessageBoxModel.isReadStatus()){
                ((GiftCenterHolder)viewHolder).layoutView.setBackgroundColor(activity.getResources().getColor(R.color.e_33ffffff));
                ((GiftCenterHolder)viewHolder).imageView.setImageResource(R.drawable.plat_message_box_item_has_read_icon_bg);

            }else {
                ((GiftCenterHolder)viewHolder).layoutView.setBackgroundColor(activity.getResources().getColor(R.color.e_33000000));
                ((GiftCenterHolder)viewHolder).imageView.setImageResource(R.drawable.plat_message_box_item_bg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    private class GiftCenterHolder extends RecyclerView.ViewHolder{

        View layoutView;
        TextView timeTextView;
        TextView titleTextView;
        ImageView imageView;


        public GiftCenterHolder(View contentView) {
            super(contentView);

            layoutView = contentView.findViewById(R.id.plat_message_box_item_layout);
            titleTextView = (TextView) contentView.findViewById(R.id.plat_message_box_item_title);
            timeTextView = (TextView) contentView.findViewById(R.id.plat_message_box_item_time);
            imageView = (ImageView) contentView.findViewById(R.id.plat_message_box_icon_bg);
            layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recylerViewItemClickListener != null){

                        recylerViewItemClickListener.onItemClick(PlatMessageBoxAdapter.this,getLayoutPosition(),layoutView);
                    }
                }
            });

        }
    }
}

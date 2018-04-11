package com.gama.sdk.plat.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gama.sdk.R;
import com.gama.sdk.callback.RecylerViewItemClickListener;
import com.gama.sdk.plat.data.bean.response.PlatGiftCenterModel;

import java.util.List;

/**
 * Created by gan on 2017/8/10.
 */

public class PlatGiftCenterAdapter extends RecyclerView.Adapter {

    private Activity activity;

    private LayoutInflater layoutInflater;

    public void setDataModelList(List<PlatGiftCenterModel> platGiftCenterModels) {
        this.platGiftCenterModels = platGiftCenterModels;
    }

    private List<PlatGiftCenterModel> platGiftCenterModels;

    public PlatGiftCenterAdapter(Activity activity) {
        this.activity = activity;
        layoutInflater = LayoutInflater.from(activity);
    }


    private RecylerViewItemClickListener recylerViewItemClickListener;

    public void setRecylerViewItemClickListener(RecylerViewItemClickListener recylerViewItemClickListener) {
        this.recylerViewItemClickListener = recylerViewItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new GiftCenterHolder(layoutInflater.inflate(R.layout.plat_gift_center_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        PlatGiftCenterModel platGiftCenterModel = platGiftCenterModels.get(i);
//        platGiftCenterModel.setTitle("aaaaaaaa");
        if (platGiftCenterModel != null){
            ((GiftCenterHolder)viewHolder).itemSimpleDraweeView.setImageURI(Uri.parse(platGiftCenterModel.getIcon()));


            ((GiftCenterHolder)viewHolder).timeTextView.setText(platGiftCenterModel.getTime());
            ((GiftCenterHolder)viewHolder).titleTextView.setText(platGiftCenterModel.getTitle());
            ((GiftCenterHolder)viewHolder).desTextView.setText(platGiftCenterModel.getRewardName());

            if (platGiftCenterModel.getIsReceive().equals("1")){

                ((GiftCenterHolder)viewHolder).getStatueTextView.setText(activity.getString(R.string.plat_gift_has_get));
                ((GiftCenterHolder)viewHolder).getStatueTextView.setBackground(activity.getResources().getDrawable(R.drawable.plat_gift_has_get));
                ((GiftCenterHolder)viewHolder).getStatueTextView.setClickable(false);

            }else if(platGiftCenterModel.getIsReceive().equals("2")){

                ((GiftCenterHolder)viewHolder).getStatueTextView.setText(activity.getString(R.string.plat_gift_has_get_finsh));
                ((GiftCenterHolder)viewHolder).getStatueTextView.setBackground(activity.getResources().getDrawable(R.drawable.plat_gift_has_get));
                ((GiftCenterHolder)viewHolder).getStatueTextView.setClickable(false);
            }else{
                ((GiftCenterHolder)viewHolder).getStatueTextView.setText(activity.getString(R.string.plat_click_get));
                ((GiftCenterHolder)viewHolder).getStatueTextView.setBackground(activity.getResources().getDrawable(R.drawable.plat_gift_click_get_bg));
                ((GiftCenterHolder)viewHolder).getStatueTextView.setClickable(true);

            }
        }
    }

    @Override
    public int getItemCount() {
        return platGiftCenterModels == null ? 0 : platGiftCenterModels.size();
    }

    private class GiftCenterHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView itemSimpleDraweeView;

        TextView getStatueTextView;
        TextView timeTextView;
        TextView titleTextView;
        TextView desTextView;

        public GiftCenterHolder(View contentView) {
            super(contentView);

            itemSimpleDraweeView = (SimpleDraweeView) contentView.findViewById(R.id.plat_gift_center_image_view);
            getStatueTextView = (TextView) contentView.findViewById(R.id.plat_gift_center_get_statue);
            timeTextView = (TextView) contentView.findViewById(R.id.plat_gift_center_time);
            titleTextView = (TextView) contentView.findViewById(R.id.plat_gift_center_title);
            desTextView = (TextView) contentView.findViewById(R.id.plat_gift_center_des);

            getStatueTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recylerViewItemClickListener != null){
                        recylerViewItemClickListener.onItemClick(PlatGiftCenterAdapter.this,getLayoutPosition(),getStatueTextView);
                    }
                }
            });

        }
    }
}

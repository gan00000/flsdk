package com.gama.sdk.plat.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gama.sdk.R;
import com.gama.sdk.callback.RecylerViewItemClickListener;
import com.gama.sdk.plat.data.bean.response.PlatInfoModel;

import java.util.List;

/**
 * Created by gan on 2017/8/16.
 */

public class PlatInfoRecyclerViewAdapter extends RecyclerView.Adapter {

    private Activity activity;

    private List<PlatInfoModel> dataModelList;

    private RecylerViewItemClickListener recylerViewItemClickListener;

    public void setRecylerViewItemClickListener(RecylerViewItemClickListener recylerViewItemClickListener) {
        this.recylerViewItemClickListener = recylerViewItemClickListener;
    }


    public PlatInfoRecyclerViewAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setDataModelList(List<PlatInfoModel> dataModelList) {
        this.dataModelList = dataModelList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new InfoHolder(activity.getLayoutInflater().inflate(R.layout.plat_information_list_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        PlatInfoModel platInfoModel = dataModelList.get(i);
        if (platInfoModel != null){
            ((InfoHolder)viewHolder).simpleDraweeView.setImageURI(Uri.parse(platInfoModel.getImage()));
        }
    }

    @Override
    public int getItemCount() {
        return dataModelList == null ? 0 : dataModelList.size();
    }

    private class InfoHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView simpleDraweeView;
        public InfoHolder(View itemView) {
            super(itemView);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.plat_info_image_view);
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recylerViewItemClickListener != null){
                        recylerViewItemClickListener.onItemClick(PlatInfoRecyclerViewAdapter.this,getLayoutPosition(),simpleDraweeView);
                    }
                }
            });

        }
    }
}

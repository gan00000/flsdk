package com.gama.sdk.plat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gama.sdk.R;
import com.gama.sdk.plat.data.bean.response.PlatMenuModel;

import java.util.List;

/**
 * Created by gan on 2017/8/10.
 */

public class PlatMenuGridViewAdapter extends BaseAdapter {

    private Activity activity;

    private LayoutInflater layoutInflater;

    public void setPlatMenuBeans(List<PlatMenuModel> platMenuBeans) {
        this.platMenuBeans = platMenuBeans;
    }

    private List<PlatMenuModel> platMenuBeans;

    public PlatMenuGridViewAdapter(Activity activity) {
        this.activity = activity;

        layoutInflater = LayoutInflater.from(activity);
    }


    @Override
    public int getCount() {
        return platMenuBeans == null ? 0 : platMenuBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return platMenuBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {

        PlatMenuViewHolder platMenuViewHolder;

        SimpleDraweeView itemSimpleDraweeView;
        TextView titleTextView;

        if (contentView == null) {
            contentView = layoutInflater.inflate(R.layout.plat_menu_item,viewGroup,false);

            itemSimpleDraweeView = (SimpleDraweeView) contentView.findViewById(R.id.plat_menu_item_image_view);
            titleTextView = (TextView) contentView.findViewById(R.id.plat_menu_item_title_view);

            platMenuViewHolder = new PlatMenuViewHolder();
            platMenuViewHolder.itemSimpleDraweeView = itemSimpleDraweeView;
            platMenuViewHolder.titleTextView = titleTextView;

            contentView.setTag(platMenuViewHolder);

        }else {
            platMenuViewHolder = (PlatMenuViewHolder) contentView.getTag();
            itemSimpleDraweeView = platMenuViewHolder.itemSimpleDraweeView;
            titleTextView = platMenuViewHolder.titleTextView;

        }

        PlatMenuModel platMenuBean = platMenuBeans.get(i);

        itemSimpleDraweeView.setImageURI(platMenuBean.getIcon());
        titleTextView.setText(platMenuBean.getName());

        return contentView;
    }

    private class PlatMenuViewHolder {

        View itemContentView;

        SimpleDraweeView itemSimpleDraweeView;
        TextView titleTextView;
    }
}

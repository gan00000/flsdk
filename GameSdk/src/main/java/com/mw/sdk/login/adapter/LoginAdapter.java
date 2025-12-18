package com.mw.sdk.login.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;

import java.util.List;

/**
 * Created by gan on 2017/4/12.
 */

public class LoginAdapter extends PagerAdapter {

    private Context context;

    private List<SLoginBaseRelativeLayout> viewPageList;

    public LoginAdapter(Context context, List<SLoginBaseRelativeLayout> viewPageList) {
        this.context = context;
        this.viewPageList = viewPageList;
    }


    @Override
    public int getCount() {
        return this.viewPageList == null ? 0 : this.viewPageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewPageList.get(position));//删除页卡
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewPageList.get(position), 0);//添加页卡
        return viewPageList.get(position);
    }
}

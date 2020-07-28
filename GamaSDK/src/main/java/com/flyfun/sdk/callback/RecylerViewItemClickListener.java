package com.flyfun.sdk.callback;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public interface RecylerViewItemClickListener {

    public void onItemClick(RecyclerView.Adapter adapter, int position, View itemView);
}

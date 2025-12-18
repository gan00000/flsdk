package com.mw.sdk.callback;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


public interface RecylerViewItemClickListener {

    public void onItemClick(RecyclerView.Adapter adapter, int position, View itemView);
}

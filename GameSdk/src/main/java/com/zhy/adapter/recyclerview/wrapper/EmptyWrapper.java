//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.zhy.adapter.recyclerview.wrapper;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.utils.WrapperUtils;

public class EmptyWrapper<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_EMPTY = 2147483646;
    private RecyclerView.Adapter mInnerAdapter;
    private View mEmptyView;
    private int mEmptyLayoutId;

    public EmptyWrapper(RecyclerView.Adapter adapter) {
        this.mInnerAdapter = adapter;
    }

    private boolean isEmpty() {
        return (this.mEmptyView != null || this.mEmptyLayoutId != 0) && this.mInnerAdapter.getItemCount() == 0;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (this.isEmpty()) {
            ViewHolder holder;
            if (this.mEmptyView != null) {
                holder = ViewHolder.createViewHolder(parent.getContext(), this.mEmptyView);
            } else {
                holder = ViewHolder.createViewHolder(parent.getContext(), parent, this.mEmptyLayoutId);
            }

            return holder;
        } else {
            return this.mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(this.mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            public int getSpanSize(GridLayoutManager gridLayoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (EmptyWrapper.this.isEmpty()) {
                    return gridLayoutManager.getSpanCount();
                } else {
                    return oldLookup != null ? oldLookup.getSpanSize(position) : 1;
                }
            }
        });
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        this.mInnerAdapter.onViewAttachedToWindow(holder);
        if (this.isEmpty()) {
            WrapperUtils.setFullSpan(holder);
        }

    }

    public int getItemViewType(int position) {
        return this.isEmpty() ? 2147483646 : this.mInnerAdapter.getItemViewType(position);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!this.isEmpty()) {
            this.mInnerAdapter.onBindViewHolder(holder, position);
        }
    }

    public int getItemCount() {
        return this.isEmpty() ? 1 : this.mInnerAdapter.getItemCount();
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    public void setEmptyView(int layoutId) {
        this.mEmptyLayoutId = layoutId;
    }
}

package com.gama.sdk.plat;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by gan on 2017/8/30.
 */

public class GiftCenterLayoutManager extends GridLayoutManager {

    public GiftCenterLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GiftCenterLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GiftCenterLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
//        int height = 0;
//        int childCount = getItemCount();
//        if (childCount > 0) {
//            for (int i = 0; i < childCount; i++) {
//                View child = recycler.getViewForPosition(i);
//                measureChild(child, widthSpec, heightSpec);
//                if (i % getSpanCount() == 0) {
//                    int measuredHeight = child.getMeasuredHeight() + getDecoratedBottom(child);
//                    height += measuredHeight;
//                }
//            }
//        }
//        setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), height);
    }
}

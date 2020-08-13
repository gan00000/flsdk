package com.flyfun.sdk.widget;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by txf on 2016/10/20.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int leftAndRight;
    private int buttom;

    public SpaceItemDecoration(int leftAndRight, int buttom) {
        this.leftAndRight = leftAndRight;
        this.buttom = buttom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = leftAndRight;
        outRect.right = leftAndRight;
        outRect.bottom = buttom;

        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildPosition(view) == 0)
//            outRect.top = space;
    }
}

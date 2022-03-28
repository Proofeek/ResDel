package ru.proofeek.resdel;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int mItemSize;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int itemSize) {
        this.spanCount = spanCount;
        mItemSize = itemSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column
        int spacing = (parent.getWidth() - ((spanCount * mItemSize))) / (spanCount +1);
        //Log.e("item size: ", String.valueOf(mItemSize));
        //Log.e("spacing1: ", String.valueOf(spacing));

        int rest = parent.getWidth() -(spanCount * mItemSize);
        //Log.e("ostalos: ", String.valueOf(rest));
        spacing = spacing + (rest / (spanCount +1));
        //Log.e("spacing2: ", String.valueOf(spacing));
        outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
        outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
    }
}

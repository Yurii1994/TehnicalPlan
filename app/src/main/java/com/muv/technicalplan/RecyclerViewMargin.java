package com.muv.technicalplan;


import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

public class RecyclerViewMargin  extends RecyclerView.ItemDecoration
{
    private int size;
    private int margin;

    public RecyclerViewMargin(@IntRange(from=0)int margin , @IntRange(from=0) int size ) {
        this.margin = margin;
        this.size = size - 1;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);
        if (position == 0)
        {
            outRect.right = margin;
            outRect.bottom = margin / 2;
            outRect.top = margin;
            outRect.left = margin;
        }
        else
        if (position == size)
        {
            outRect.right = margin;
            outRect.bottom = margin;
            outRect.top = margin / 2;
            outRect.left = margin;
        }
        else
        {
            outRect.right = margin;
            outRect.bottom = margin / 2;
            outRect.top = margin / 2;
            outRect.left = margin;
        }
    }
}
package com.example.gilcunningham.androidnewsreader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.gilcunningham.androidnewsreader.R;

/**
 * Created by gil.cunningham on 9/5/2016.
 * Divider for Headline CardViews
 */
public class HeadlineDivider extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    public HeadlineDivider(Context context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.headline_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}

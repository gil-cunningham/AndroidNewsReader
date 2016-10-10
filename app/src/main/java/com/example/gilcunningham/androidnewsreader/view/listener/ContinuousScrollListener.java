package com.example.gilcunningham.androidnewsreader.view.listener;

/**
 * Created by gil.cunningham on 9/15/2016.
 * Scroll listener which checks if a threshold is met
 */

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class ContinuousScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = ContinuousScrollListener.class.getSimpleName();

    private int mPreviousItemCount = 0; // The total number of items in the dataset after the last load
    private boolean mLoading = true; // True if we are still waiting for the last set of data to load.
    private int mVisibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.
    int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;

    private int mCurrentPage = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public ContinuousScrollListener(LinearLayoutManager linearLayoutManager) {
        mLinearLayoutManager = linearLayoutManager;
    }

    public ContinuousScrollListener(LinearLayoutManager linearLayoutManager, int visibleThreshold) {
        mLinearLayoutManager = linearLayoutManager;
        mVisibleThreshold = visibleThreshold;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        mVisibleThreshold = visibleThreshold;
    }

    /** reset scroll listener to original values **/
    public void reset() {
        mCurrentPage = 1;
        mPreviousItemCount = 0;
        mLoading = true;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        mVisibleItemCount = recyclerView.getChildCount();
        mTotalItemCount = mLinearLayoutManager.getItemCount();
        mFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (mLoading) {
            if (mTotalItemCount > mPreviousItemCount) {
                mLoading = false;
                mPreviousItemCount = mTotalItemCount;
            }
        }
        if (!mLoading && (mTotalItemCount - mVisibleItemCount)
                <= (mFirstVisibleItem + mVisibleThreshold)) {
            // End has been reached

            mCurrentPage++;

            onLoadMore(mCurrentPage);

            mLoading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}


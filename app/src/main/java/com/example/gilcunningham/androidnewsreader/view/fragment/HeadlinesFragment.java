package com.example.gilcunningham.androidnewsreader.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gilcunningham.androidnewsreader.ArticleDetailsActivity;
import com.example.gilcunningham.androidnewsreader.HeadlinesActivity;
import com.example.gilcunningham.androidnewsreader.view.listener.HeadlinesTouchListener;
import com.example.gilcunningham.androidnewsreader.R;
import com.example.gilcunningham.androidnewsreader.data.NewsArticle;
import com.example.gilcunningham.androidnewsreader.view.listener.ContinuousScrollListener;
import com.example.gilcunningham.androidnewsreader.view.HeadlineDivider;
import com.example.gilcunningham.androidnewsreader.view.adapter.HeadlinesAdapter;
import com.example.gilcunningham.androidnewsreader.view.listener.OnLoadMoreListener;

import java.util.List;

/**
 * Created by gil.cunningham on 9/12/2016.
 * Displays list of Headlines in a RecyclerView.
 * Provides ability to set or append new dataset to existing.
 * Implements a Continuous scroll listener to initiate callback to load more data.
 */
public class HeadlinesFragment extends Fragment {

    private RecyclerView mHeadlineRecyclerView;
    private HeadlinesAdapter mHeadlineAdapter;

    private List<NewsArticle> mNewsArticles;

    private ContinuousScrollListener mScrollListener;
    private OnLoadMoreListener mListener;

    /** set listener for onLoadMore() event **/
    public void setOnLoadOnMoreListener(OnLoadMoreListener listener) {
        mListener = listener;
    }

    @Override
    public void setArguments(Bundle args)
    {
        List<NewsArticle> newsArticles = args.getParcelableArrayList(HeadlinesActivity.NEWS_ARTICLES_EXTRA);

        /** first time date is set **/
        if (mNewsArticles == null) {
            mNewsArticles = newsArticles;
        } else {
            if (newsArticles.size() > 0) {

                /** represents a new dataset, reset scroll listener to initial values **/
                if (newsArticles.size() <= mNewsArticles.size() && mScrollListener != null) {
                    mScrollListener.reset();
                }

                /** clear existing and append new dataset **/
                mNewsArticles.clear();
                mNewsArticles.addAll(newsArticles);
            }
        }

        /** notify adapter dataset changed **/
        if (mHeadlineAdapter != null) {
            mHeadlineAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mHeadlineRecyclerView = (RecyclerView) inflater.inflate(R.layout.headlines_list, container, false);

        final Context ctx = mHeadlineRecyclerView.getContext();
        mHeadlineAdapter = new HeadlinesAdapter(ctx, mNewsArticles);
        RecyclerView.LayoutManager layoutMgr = new LinearLayoutManager(ctx);

        mHeadlineRecyclerView.setHasFixedSize(false);
        mHeadlineRecyclerView.setLayoutManager(layoutMgr);
        /** adds divider between card views **/
        mHeadlineRecyclerView.addItemDecoration(new HeadlineDivider(ctx));
        mHeadlineRecyclerView.setAdapter(mHeadlineAdapter);

        /** on headline tap/click, load details for article **/
        mHeadlineRecyclerView.addOnItemTouchListener(new HeadlinesTouchListener(ctx,
                mHeadlineRecyclerView, new HeadlinesTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NewsArticle article = mNewsArticles.get(position);

                Intent i = new Intent(ctx, ArticleDetailsActivity.class);
                i.putExtra(ArticleDetailsActivity.ARTICLE_EXTRA, article);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /** check remove existing continuous scroll listener **/
        if (mScrollListener != null) {
            mHeadlineRecyclerView.removeOnScrollListener(mScrollListener);
        }
        /** add continuous scroll listener **/
        mScrollListener = new NewsReaderContinuousScrollListener(
                (LinearLayoutManager) layoutMgr);
        mHeadlineRecyclerView.addOnScrollListener(mScrollListener);

        return mHeadlineRecyclerView;
    }

    /** impl for continous scrolling, passes next page number off to OnLoadMoreListener **/
    class NewsReaderContinuousScrollListener extends ContinuousScrollListener {

        public NewsReaderContinuousScrollListener(LinearLayoutManager layoutMgr) {
            super(layoutMgr);
        }

        public NewsReaderContinuousScrollListener(LinearLayoutManager layoutMgr, int visibleThreshold) {
            super(layoutMgr, visibleThreshold);
        }

        @Override
        public void onLoadMore(int nextPage) {
            if (mListener != null) {
                mListener.onLoadMore(nextPage);
            }
        }
    }
}

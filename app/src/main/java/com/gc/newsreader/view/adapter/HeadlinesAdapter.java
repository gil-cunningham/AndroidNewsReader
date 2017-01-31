package com.gc.newsreader.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.newsreader.R;
import com.gc.newsreader.data.NewsArticle;
import com.gc.newsreader.helper.CategoryHelper;
import com.gc.newsreader.helper.DateFormatHelper;

import java.util.List;

/**
 * Created by gil.cunningham on 9/13/2016.
 * View Holder for Headline Cards
 */
public class HeadlinesAdapter extends RecyclerView.Adapter<HeadlinesAdapter.ViewHolder> {

    private Context mContext;
    private List<NewsArticle> mDataset;

    /** Provide a reference to the views for each data item **/
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mHeadlineTxt;
        public TextView mHeadlineDateTxt;
        public ImageView mHeadlineCategoryImg;

        public ViewHolder(View v) {
            super(v);
            mHeadlineTxt = (TextView)v.findViewById(R.id.headline_text);
            mHeadlineDateTxt = (TextView)v.findViewById(R.id.headline_date);
            mHeadlineCategoryImg = (ImageView)v.findViewById(R.id.headline_category_img);
        }
    }

    public HeadlinesAdapter(Context context, List<NewsArticle> dataset) {
        mContext = context;
        mDataset = dataset;
    }

    /** Create new views (invoked by the layout manager) **/
    @Override
    public HeadlinesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_headline, parent, false);

        return new ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager) **/
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsArticle na = mDataset.get(position);

        holder.mHeadlineTxt.setText(na.getHeadline());
        holder.mHeadlineDateTxt.setText(DateFormatHelper.formatDate(na.getHeadlineDate(),"yyyy-MM-dd'T'HH:mm:ss","MM/dd/yyyy h:mm a", true));
        int resId = CategoryHelper.getHelper(mContext).getCategoryResourceDrawable(na.getCategory());
        holder.mHeadlineCategoryImg.setImageDrawable(ContextCompat.getDrawable(mContext, resId));
    }

    /** Return the size of dataset (invoked by the layout manager) **/
    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        }
        return 0;
    }
}

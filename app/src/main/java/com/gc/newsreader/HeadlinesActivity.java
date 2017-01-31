package com.gc.newsreader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gc.newsreader.data.NewsArticle;
import com.gc.newsreader.service.NewsReaderCallback;
import com.gc.newsreader.service.NewsReaderResponse;
import com.gc.newsreader.service.NewsReaderService2;
import com.gc.newsreader.view.NewsReaderViewPager;
import com.gc.newsreader.view.adapter.PagerAdapter;
import com.gc.newsreader.view.fragment.CategoriesFragment;
import com.gc.newsreader.view.fragment.HeadlinesFragment;
import com.gc.newsreader.view.fragment.SettingsFragment;
import com.gc.newsreader.view.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gil.cunningham on 9/7/2016.
 * Activity to display headlines for either todays articles or a search for articles
 */

public class HeadlinesActivity extends AppCompatActivity implements NewsReaderCallback,
        OnLoadMoreListener {

    public static final String NEWS_ARTICLES_EXTRA = "NEWS_ARTICLES_EXTRA";

    private static final int STATE_SEARCH_ARTICLES = 0;
    private static final int STATE_TODAYS_ARTICLES = 1;

    private ArrayList<NewsArticle> mTodaysNewsArticles;
    private ArrayList<NewsArticle> mSearchNewsArticles;

    private HeadlinesFragment mHeadlinesFragment;

    private NewsReaderViewPager mViewPager;

    private Timer mSearchDelayTimer;
    private long SEARCH_DELAY_TIME = 1000;
    private String mSearchText;

    private int mReaderState = STATE_TODAYS_ARTICLES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mTodaysNewsArticles = getIntent().getParcelableArrayListExtra(NEWS_ARTICLES_EXTRA);
        } else {
            mTodaysNewsArticles = savedInstanceState.getParcelableArrayList(NEWS_ARTICLES_EXTRA);
        }

        doActivityLayout();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(NEWS_ARTICLES_EXTRA, mTodaysNewsArticles);
    }

    /**
     * Callback with NewsReaderResponse for Todays Headlines
     */
    @Override
    public void handleTodaysNewsCallback(NewsReaderResponse newsReaderResponse) {

        ArrayList<NewsArticle> newsArticles = newsReaderResponse.getNewsArticles();

        /** append this dataset to existing **/
        if (newsReaderResponse.getPageNumber() > 0) {
            if (newsArticles.size() > 0) {
                mTodaysNewsArticles.addAll(newsArticles);
            }
        /** reset entire dataset **/
        } else {
            mTodaysNewsArticles = newsArticles;
        }

        /** update dataset with Headlines Fragment **/
        if (newsArticles.size() > 0) {
            Bundle b = new Bundle();
            b.putParcelableArrayList(NEWS_ARTICLES_EXTRA, mTodaysNewsArticles);
            mHeadlinesFragment.setArguments(b);
        }
    }

    /**
     * Callback with NewsReaderResponse for Search Articles
     */
    @Override
    public void handleSearchNewsCallback(NewsReaderResponse newsReaderResponse) {

        ArrayList<NewsArticle> newsArticles = newsReaderResponse.getNewsArticles();

        /** append this dataset to existing **/
        if (newsReaderResponse.getPageNumber() > 0) {
            if (newsArticles.size() > 0) {
                mSearchNewsArticles.addAll(newsArticles);
            }
        /** reset entire dataset **/
        } else {
            mSearchNewsArticles = newsArticles;
        }

        /** update dataset with Headlines Fragment **/
        if (newsArticles.size() > 0) {
            Bundle b = new Bundle();
            b.putParcelableArrayList(NEWS_ARTICLES_EXTRA, mSearchNewsArticles);
            mHeadlinesFragment.setArguments(b);
        }
    }

    /**
     * Loads next page of articles
     * Callback returns to handleTodaysNewsCallback() or handleSearchNewsCallback()
     */
    @Override
    public void onLoadMore(int nextPage) {

        /** check state and make appropriate request **/
        if (STATE_TODAYS_ARTICLES == mReaderState) {
            NewsReaderService2.newService(this, this)
                    .getTodaysNewsArticles(nextPage - 1);
        } else if (STATE_SEARCH_ARTICLES == mReaderState) {
            NewsReaderService2.newService(this, this)
                    .searchNewsArticles(mSearchText, nextPage - 1);
        }
    }
    /** initial layout of views **/
    private void doActivityLayout() {

        setContentView(R.layout.activity_headlines);

        /**
         * Handles search view text changes with delay time check after last char entered
         * to provide dynamic headlines loading (i.e. no need to tap Enter or Search)
         */
        final EditText searchView = (EditText) findViewById(R.id.search_view);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
                if (mSearchDelayTimer != null)
                    mSearchDelayTimer.cancel();
            }

            @Override
            public void afterTextChanged(final Editable s) {

                //avoid triggering event when text is too short
                if (s.length() >= 3) {

                    mSearchDelayTimer = new Timer();
                    mSearchDelayTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            mSearchText = searchView.getText().toString().trim();
                            NewsReaderService2.newService(HeadlinesActivity.this, HeadlinesActivity.this)
                                    .searchNewsArticles(mSearchText);

                            hideSoftInput(searchView);
                        }
                    }, SEARCH_DELAY_TIME);
                }
            }
        });

        /** clear search **/
        final ImageView searchBtn = (ImageView) findViewById(R.id.clear_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText searchView = (EditText) findViewById(R.id.search_view);
                searchView.setText("");

                showSoftInput(searchView);
            }
        });

        setupTabs();
        setupFab();
    }

    private void setupTabs() {

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        // set news articles list
        mHeadlinesFragment = new HeadlinesFragment();
        mHeadlinesFragment.setOnLoadOnMoreListener(this);

        // add Headlines Fragment and initiate with dataset
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(NEWS_ARTICLES_EXTRA, (ArrayList<NewsArticle>) mTodaysNewsArticles.clone());
        mHeadlinesFragment.setArguments(bundle);
        adapter.addFragment(mHeadlinesFragment, getResources().getString(R.string.tab_articles));

        // add Categories Fragment
        adapter.addFragment(new CategoriesFragment(), getResources().getString(R.string.tab_categories));
        // add Settings Fragment
        adapter.addFragment(new SettingsFragment(), getResources().getString(R.string.tab_settings));

        mViewPager = (NewsReaderViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.headlines_fab);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) fab.getLayoutParams();
            p.setMargins(0, 0, 0, 0); // get rid of margins since shadow area is now the margin
            fab.setLayoutParams(p);
        }
        setFabImage(fab);

        /**
         * Toggle between "search" and "todays" articles state
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FloatingActionButton fab = (FloatingActionButton) view;

                if (STATE_SEARCH_ARTICLES == mReaderState) {
                    mReaderState = STATE_TODAYS_ARTICLES;

                    // immediately display existing todays articles
                    Bundle b = new Bundle();
                    b.putParcelableArrayList(NEWS_ARTICLES_EXTRA, mTodaysNewsArticles);
                    mHeadlinesFragment.setArguments(b);

                    // reload todays articles
                    NewsReaderService2.newService(HeadlinesActivity.this, HeadlinesActivity.this)
                            .getTodaysNewsArticles();

                    // toggle tabs and search
                    findViewById(R.id.tabs).setVisibility(View.VISIBLE);
                    findViewById(R.id.search_toolbar).setVisibility(View.GONE);

                    // hide soft keys
                    EditText searchView = (EditText) findViewById(R.id.search_view);
                    if (searchView != null) {
                        hideSoftInput(searchView);
                    }
                    // re-enable paging
                    mViewPager.setEnabled(true);

                } else if (STATE_TODAYS_ARTICLES == mReaderState) {

                    mReaderState = STATE_SEARCH_ARTICLES;

                    // toggle tabs and search
                    findViewById(R.id.tabs).setVisibility(View.GONE);
                    findViewById(R.id.search_toolbar).setVisibility(View.VISIBLE);

                    // display search view
                    EditText searchView = (EditText) findViewById(R.id.search_view);
                    searchView.setText("");

                    // set focus to search view and display soft keys
                    if (searchView.requestFocus()) {
                        showSoftInput(searchView);
                    }
                    // disable paging
                    mViewPager.setEnabled(false);
                }

                setFabImage(fab);
            }
        });
    }

    /**
     * Set the FAB image based on state
     */
    private void setFabImage(FloatingActionButton fab) {
        if (STATE_SEARCH_ARTICLES == mReaderState) {
            Drawable closeSearchImg = ContextCompat.getDrawable(HeadlinesActivity.this, R.drawable.ic_close_search);
            fab.setImageDrawable(closeSearchImg);
        } else if (STATE_TODAYS_ARTICLES == mReaderState) {
            Drawable searchImg = ContextCompat.getDrawable(HeadlinesActivity.this, R.drawable.ic_search);
            fab.setImageDrawable(searchImg);
        }
    }

    private boolean hideSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private boolean showSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }


}

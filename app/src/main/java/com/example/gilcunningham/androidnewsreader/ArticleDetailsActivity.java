package com.example.gilcunningham.androidnewsreader;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gilcunningham.androidnewsreader.data.NewsArticle;
import com.example.gilcunningham.androidnewsreader.helper.DateFormatHelper;
import com.example.gilcunningham.androidnewsreader.helper.DisplayHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by gil.cunningham on 9/7/2016.
 * Activity to display author, date, and load article content
 */

public class ArticleDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ArticleDetailsActivity";

    public static final String ARTICLE_EXTRA = "ARTICLE_EXTRA";

    private NewsArticle mNewsArticle = null;
    private String mArticleDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        mNewsArticle = (NewsArticle) getIntent().getParcelableExtra(ARTICLE_EXTRA);

        doActivityLayout();
    }

    private void doActivityLayout() {
        setContentView(R.layout.activity_article_details);

        doLayoutAuthorDate();

        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /** set headline **/
        TextView articleHeadline = (TextView)findViewById(R.id.article_headline);
        articleHeadline.setText(mNewsArticle.getHeadline());

        /** adjust headline size **/
        Point p = DisplayHelper.getDisplaySizeInPixels(this);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)articleHeadline.getLayoutParams();
        layoutParams.width = (2 * p.x) / 3;
        articleHeadline.setLayoutParams(layoutParams);

        /** content type is web based - launch WebView to display content **/
        if (NewsArticle.ContentType.WEB == mNewsArticle.getmContentType()) {
            findViewById(R.id.article_details).setVisibility(View.GONE);

            WebView articleWebView = (WebView) findViewById(R.id.article_details_video);
            WebSettings webSettings = articleWebView.getSettings();
            webSettings.setBuiltInZoomControls(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            articleWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) { return (false); }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) { }
                @Override
                public void onPageFinished(WebView view, String url) { }
            });
            articleWebView.loadUrl(mNewsArticle.getWebUrl());
        }
        /** scrape content from page **/
        else {
            new ArticleDetailsLoader().execute(mNewsArticle.getWebUrl());
            findViewById(R.id.article_details_video).setVisibility(View.GONE);
        }
    }

    /** set article content **/
    private void finishActivitySetup() {
        TextView articleDetails = (TextView) findViewById(R.id.article_details);
        articleDetails.setText(mArticleDetails);
    }

    /** display simple error message with ability to reload page on tap **/
    private void finishActivitySetupWithError() {
        TextView errorLoadingDetails = (TextView)findViewById(R.id.error_loading_details);
        errorLoadingDetails.setVisibility(View.VISIBLE);

        errorLoadingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doActivityLayout();
            }
        });
    }

    private void doLayoutAuthorDate() {
        TextView articleDate = (TextView) findViewById((R.id.article_date));
        articleDate.setText(DateFormatHelper.formatDate(mNewsArticle.getHeadlineDate(),
                "yyyy-MM-dd'T'HH:mm:ss", "MMMM dd, yyyy h:mm a", true));

        TextView articleAuthor = (TextView) findViewById((R.id.article_author));
        articleAuthor.setText(this.getString(R.string.by_author) + " " + mNewsArticle.getAuthor());
    }

    /** Task to load article content **/
    private class ArticleDetailsLoader extends AsyncTask<String, Void, String> {

        String ERROR_LOADING_DETAILS = "BAD_RESPONSE";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                /** load page and extract contents of article **/
                Document doc = Jsoup.connect(params[0]).get();
                Elements articleBodyEls = doc.select("div.article-body");

                if (!articleBodyEls.isEmpty()) {
                    Element articleBodyEl = articleBodyEls.get(0);
                    Elements articleLineEls = articleBodyEl.select("p.p-block");
                    StringBuilder articleDetails = new StringBuilder();

                    for (int i = 0; i < articleLineEls.size(); i++) {
                        Element articleLineEl = articleLineEls.get(i);
                        articleDetails.append(articleLineEl.text() + "\n\n");
                    }

                    return articleDetails.toString();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return ERROR_LOADING_DETAILS;
        }

        @Override
        protected void onPostExecute(String result) {

            if (!ERROR_LOADING_DETAILS.equals(result)) {
                mArticleDetails = result;
                finishActivitySetup();
            }
            else {
                finishActivitySetupWithError();
            }
        }
    }
}
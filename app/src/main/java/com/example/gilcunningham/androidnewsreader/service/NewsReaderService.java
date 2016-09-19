package com.example.gilcunningham.androidnewsreader.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.gilcunningham.androidnewsreader.R;
import com.example.gilcunningham.androidnewsreader.data.NewsArticle;
import com.example.gilcunningham.androidnewsreader.helper.CategoryHelper;
import com.example.gilcunningham.androidnewsreader.network.NewsReaderRestClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by gil.cunningham on 9/14/2016.
 * Responsible for initiating NewsReader REST request and parsing response.
 * Returns to callback a NewsReaderResponse
 */
public class NewsReaderService {

    private static final String TAG = "NewsReaderService";

    private Context mContext;
    private NewsReaderCallback mCallback;

    private int mPage = 0;

    public static NewsReaderService newService(Context context, NewsReaderCallback callback) {

        return new NewsReaderService(context, callback);
    }

    private NewsReaderService(Context context, NewsReaderCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    public void getTodaysNewsArticles() {
        this.getTodaysNewsArticles(0);
    }

    public void getTodaysNewsArticles(int page) {
        mPage = page;
        mContext.registerReceiver(mReceiver, new IntentFilter(NewsReaderRestClient.TODAYS_HEADLINES_CALLBACK_INTENT));
        new NewsReaderRestClient(mContext).doGetTodaysNewsArticles(page);
    }

    public void searchNewsArticles(String search) {
        this.searchNewsArticles(search, 0);
    }

    public void searchNewsArticles(String search, int page) {
        mPage = page;
        mContext.registerReceiver(mReceiver, new IntentFilter(NewsReaderRestClient.SEARCH_NEWS_CALLBACK_INTENT));
        new NewsReaderRestClient(mContext).doSearchNewsArticles(search, page);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String response = intent.getStringExtra(NewsReaderRestClient.NEWS_ARTICLES_EXTRA);

                ArrayList<NewsArticle> newsArticles = parseForResult(response);

                /** handle search response **/
                if (NewsReaderRestClient.SEARCH_NEWS_CALLBACK_INTENT == intent.getAction()) {
                    mCallback.handleSearchNewsCallback(new NewsReaderResponse(newsArticles, mPage));
                }
                /** handle todays headlines response **/
                else if (NewsReaderRestClient.TODAYS_HEADLINES_CALLBACK_INTENT == intent.getAction()) {
                    mCallback.handleTodaysNewsCallback(new NewsReaderResponse(newsArticles, mPage));
                }
            }
            finally {
                mContext.unregisterReceiver(mReceiver);
            }
        }
    };

    private ArrayList<NewsArticle> parseForResult(String response) {

        ArrayList<NewsArticle> newsArticles = new ArrayList<NewsArticle>();

        try {
            JSONObject news = new JSONObject(response);
            JSONObject res = news.getJSONObject("response");
            JSONArray docs = res.getJSONArray("docs");

            for (int i = 0; i < docs.length(); i++) {

                NewsArticle.Builder builder = NewsArticle.newBuilder();

                try {
                    JSONObject docObj = docs.getJSONObject(i);

                    String webUrl = docObj.getString("web_url");
                    builder.setWebUrl(webUrl);

                    /** url containing video or interactive considered web based (i.e. load with WebView) **/
                    if (webUrl.contains("video") || webUrl.contains("interactive")) {
                        builder.setContentType(NewsArticle.ContentType.WEB);
                    }
                    /** print based (i.e. article content can be scraped) **/
                    else {
                        builder.setContentType(NewsArticle.ContentType.PRINT);
                    }

                    JSONObject headLineObj = docObj.getJSONObject("headline");
                    builder.setHeadline(headLineObj.getString("main"));

                    NewsArticle.Category category = CategoryHelper.getHelper(mContext)
                            .getCategoryFromString(docObj.getString("section_name"));
                    builder.setCategory(category);

                    String date = docObj.getString("pub_date");
                    builder.setHeadlineDate(date);

                    /**
                     * byline can be empty JSONArray or a JSONObject
                     * check type to determine how to parse author
                     */
                    Object check = docObj.get("byline");

                    if (check instanceof JSONObject) {

                        JSONObject byLineObj = (JSONObject)check;
                        JSONArray person = byLineObj.getJSONArray("person");

                        /** person JSONArray can be empty, default to organization as author (i.e. Reuters) **/
                        if (person.length() == 0) {
                            String organization = byLineObj.getString("organization");
                            builder.setAuthor(formatName(organization));
                        } else {
                            JSONObject personObj = person.getJSONObject(0);
                            /** found first and last name, use these to set author **/
                            if (personObj.has("firstname") && personObj.has("lastname")) {
                                String firstName = personObj.getString("firstname");
                                String lastName = personObj.getString("lastname");

                                builder.setAuthor(formatName(firstName + " " + lastName));
                            }
                            /** either first or last name missing, default to "original" to set author **/
                            else {
                                String original = byLineObj.getString("original").toLowerCase();
                                if (original.startsWith("by")) {
                                    original = original.substring(3);
                                }
                                builder.setAuthor(formatName(original));
                            }
                        }
                    }
                    else {
                        builder.setAuthor(mContext.getResources().getString(R.string.author_unknown));
                    }

                    newsArticles.add(builder.build());

                } catch (Exception ignored) {
                    Log.i(TAG, ignored.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return newsArticles;
    }

    /**
     * Utility method to convert a name (i.e. JOHN SMITH) to first letter capitalized
     * remaing chars lower case (i.e. result == John Smith)
     */

    private String formatName(String name) {

        StringTokenizer tokenizer = new StringTokenizer(name.trim(), " ");
        StringBuilder builder = new StringBuilder();

        while (tokenizer.hasMoreTokens()) {
            String tok = tokenizer.nextToken();

            if (tok.length() > 1) {
                tok = tok.substring(0, 1).toUpperCase() + tok.substring(1).toLowerCase();
            }
            builder.append(tok + " ");
        }
        return builder.substring(0, builder.length() - 1);
    }

    /**
     * Make sure to unregister receiver
     */
    @Override
    public void finalize() {
        if (mContext != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
            }
            catch (Exception ignored) {
            }
        }
    }
}

package com.example.gilcunningham.androidnewsreader.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gilcunningham.androidnewsreader.R;
import com.example.gilcunningham.androidnewsreader.helper.CategoryHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gil.cunningham on 9/9/2016.
 * REST client which handles assembling queries, making request, and broadcasting results
 */
public class NewsReaderRestClient extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsReaderRestClient";

    private static final String API_KEY_VAL = "a8457610b68381085a3fff38d6a36337:6:74255139";
    private static final String ARTICLE_SEARCH_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

    private static final String FIELD_LIST = "fl";
    private static final String FILTER_QUERY = "fq";
    private static final String SORT = "sort";
    private static final String BEGIN_DATE = "begin_date";
    private static final String END_DATE = "end_date";
    private static final String PAGE = "page";
    private static final String QUERY = "q";
    private static final String API_KEY = "api-key";
    private static final String NEWEST = "newest";
    private static final String SECTION_NAME="section_name";

    public static final String BAD_RESPONSE = "badResponse";

    /** Intents for broadcast receivers **/
    public static final String TODAYS_HEADLINES_CALLBACK_INTENT = "TODAYS_HEADLINES_CALLBACK_INTENT";
    public static final String SEARCH_NEWS_CALLBACK_INTENT = "SEARCH_NEWS_CALLBACK_INTENT";

    public static final String NEWS_ARTICLES_EXTRA = "NEWS_ARTICLES_EXTRA";

    private Context mContext;
    private String mIntentCallBack;

    private static String mFieldList = null;

    public NewsReaderRestClient(Context context)
    {
        mContext = context;

        if (mFieldList == null) {
            String[] temp = mContext.getResources().getStringArray(R.array.rest_query_filter);
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < temp.length; i++) {
                builder.append(temp[i] + ",");
            }
            mFieldList = builder.substring(0, builder.length() - 1);
        }
    }

    public void doGetTodaysNewsArticles() {
        doGetTodaysNewsArticles(0);
    }

    public void doGetTodaysNewsArticles(int page) {
        doGetNewsArticles(getTodaysNewsArticlesQueryParams(page), TODAYS_HEADLINES_CALLBACK_INTENT);
    }

    public void doSearchNewsArticles(String search) {
        doSearchNewsArticles(search, 0);
    }

    public void doSearchNewsArticles(String search, int page) {
        doGetNewsArticles(getSearchNewsArticlesQueryParams(search, page), SEARCH_NEWS_CALLBACK_INTENT);
    }

    private void doGetNewsArticles(Map<String,String> queryParams, String intentCallBack) {

        mIntentCallBack = intentCallBack;

        HttpUrl.Builder urlBuilder = HttpUrl.parse(ARTICLE_SEARCH_URL).newBuilder();

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            urlBuilder.addQueryParameter(key, value);
        }

        String url = urlBuilder.build().toString();

        execute(url);
    }

    private Map<String, String> getTodaysNewsArticlesQueryParams(int page)
    {
        String formattedDate = getyyyyMMddFormattedDate();

        HashMap<String,String> queryParams = new HashMap<String,String>();
        queryParams.put(FIELD_LIST, mFieldList);
        queryParams.put(FILTER_QUERY, SECTION_NAME + ":(" + CategoryHelper.getHelper(mContext).getCategorySectionList() + ")");
        queryParams.put(SORT, NEWEST);
        queryParams.put(BEGIN_DATE, formattedDate);
        queryParams.put(END_DATE, formattedDate);
        queryParams.put(PAGE, String.valueOf(page));
        queryParams.put(API_KEY, API_KEY_VAL);

        return queryParams;
    }

    private Map<String, String> getSearchNewsArticlesQueryParams(String search, int page)
    {
        HashMap<String,String> queryParams = new HashMap<String,String>();

        queryParams.put(FIELD_LIST, mFieldList);
        queryParams.put(FILTER_QUERY, SECTION_NAME + ":(" + CategoryHelper.getHelper(mContext).getCategorySectionList() + ")");
        queryParams.put(SORT, NEWEST);
        queryParams.put(END_DATE, getyyyyMMddFormattedDate());
        queryParams.put(QUERY, search);
        queryParams.put(PAGE, String.valueOf(page));
        queryParams.put(API_KEY, API_KEY_VAL);

        return queryParams;
    }

    /** date format per https://developer.nytimes.com/article_search_v2.json **/
    private String getyyyyMMddFormattedDate()
    {
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = sdf.format(d);

        return formattedDate;
    }

    @Override
    protected String doInBackground(String... strings) {

        OkHttpClient okClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(strings[0])
                .build();

        try (Response response = okClient.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception ioe) {
            Log.e(TAG, ioe.getMessage());
        }

        return BAD_RESPONSE;
    }

    @Override
    protected void onPostExecute(String res)
    {
        Intent intent = new Intent(mIntentCallBack);
        intent.putExtra(NEWS_ARTICLES_EXTRA, res);

        // broadcast the completion
        mContext.sendBroadcast(intent);
    }
}


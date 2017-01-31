package com.gc.newsreader.service;

import android.content.Context;

import com.gc.newsreader.helper.CategoryHelper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import timber.log.Timber;

//import static com.example.gilcunningham.androidnewsreader.network.NewsReaderRestClient.TODAYS_HEADLINES_CALLBACK_INTENT;

//import static com.example.gilcunningham.androidnewsreader.network.NewsReaderRestClient.SEARCH_NEWS_CALLBACK_INTENT;
//import static com.example.gilcunningham.androidnewsreader.network.NewsReaderRestClient.TODAYS_HEADLINES_CALLBACK_INTENT;

/**
 * Created by gil.cunningham on 1/27/2017.
 */

public class NewsReaderRestService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private static final String API_KEY_VAL = "a8457610b68381085a3fff38d6a36337:6:74255139";
    private static final String ARTICLE_SEARCH_URL = "https://api.nytimes.com/";

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

    static NewsReaderRestService mNewsReaderRestService = null;

    private NewsReaderService mNewsReaderService = null;
    private Context mCtx;

    private static final String FIELD_LIST_PARAMS = "headline,pub_date,section_name,multimedia,web_url,byline";
    //private static RestAdapter mRestAdapter;

    private static String versionName = "";
    private static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithoutExposeAnnotation()
            //.registerTypeAdapter(Date.class, new GsonUTCDateAdapter())
//            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .setPrettyPrinting()
            .create();

    public static NewsReaderRestService getService(Context ctx) {
        if (mNewsReaderRestService == null) {
            mNewsReaderRestService = new NewsReaderRestService(ctx);
        }

        return mNewsReaderRestService;
    }

    private NewsReaderRestService(Context ctx) {

        mCtx = ctx;

        // setup logger
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel( HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add logging as last interceptor
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ARTICLE_SEARCH_URL)
                //.client()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                //.callbackExecutor(Executor)
                .build();

        /**
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (Exception e) {
            Timber.e(e, "Package Name Not Found");
        }
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(API.getServer())
                .setClient(new OkClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String message) {
                        Timber.d(message);
                    }
                })
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (AuthManager.isSignedIn()) {
                            request.addHeader("Authorization", AuthManager.getHeaderToken());
                        }
                        request.addHeader("Content-Type", "application/json");
                        request.addHeader("Accept", "application/json");
                        request.addHeader("User-Agent", String.format(Locale.US, "CoachUp/%s (Android %s)", versionName, System.getProperty("http.agent")));
                    }
                })
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        Response r = cause.getResponse();
                        if (r != null) {

                            switch (r.getStatus()) {
                                case 401:
                                    ThreadingUtils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // FixMe: This logout should be moved to utility
                                                MainActivity.LogoutUtil.logout(true);
                                                Timber.e("Unauthorized");
                                            } catch (Exception e) {
                                                Timber.e(e, "Package Name Not Found");
                                            }
                                        }
                                    });
                                    break;
                                case 400:
                                case 403:
                                    try {
                                        final String s = new String(((TypedByteArray) cause.getResponse().getBody()).getBytes());
                                        Crashlytics.logException(new Throwable(s));
                                    } catch (Exception ignore) {
                                    }
                            }
                        }
                        return cause;
                    }
                });
        if (useAsyncTaskThreadpool) {
            builder.setExecutors(AsyncTask.THREAD_POOL_EXECUTOR,
                    new MainThreadExecutor());
        }
        restAdapter = builder.build();
        webHandlerInterface = restAdapter.create(WebHandlerInterface.class);
        **/

        mNewsReaderService = retrofit.create(NewsReaderService.class);
    }

    public void getTodaysArticles() {
        getTodaysArticles(0);
    }

    public void getTodaysArticles(int page) {
        doGetArticles(getTodaysNewsArticlesQueryParams(page));
    }

    public void searchNewsArticles(String search) {
        searchNewsArticles(search, 0);
    }

    public void searchNewsArticles(String search, int page) {
        doGetArticles(getSearchNewsArticlesQueryParams(search, page));
    }

    private void doGetArticles(Map<String,String> params) {

        //mIntentCallBack = intentCallBack;
        Timber.i("**********************************************************");
        Timber.i("**********************************************************");
        Timber.i("**********************************************************");




        try {

            Call<List<Object>> results = mNewsReaderService.getArticles(params);
            System.out.println("*** RESULTS = " + results);

            results.enqueue(new Callback<List<Object>>() {
                @Override
                public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                    Timber.i("*** ON RESPONSE ");
                    Timber.i("*** LIST = " + response.body());

                }

                @Override
                public void onFailure(Call<List<Object>> call, Throwable t) {
                    Timber.i("*** ON FAILURE");
                }
            });

            //Response<List<Object>> list = results.execute();



        }
        catch (Exception e) {
            e.printStackTrace();
        }

        /**
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ARTICLE_SEARCH_URL).newBuilder();

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            urlBuilder.addQueryParameter(key, value);
        }

        String url = urlBuilder.build().toString();

        execute(url);
        **/
    }

    private Map<String, String> getTodaysNewsArticlesQueryParams(int page)
    {
        String formattedDate = getyyyyMMddFormattedDate();

        HashMap<String,String> queryParams = new HashMap<String,String>();
        queryParams.put(FIELD_LIST, FIELD_LIST_PARAMS);
        queryParams.put(FILTER_QUERY, SECTION_NAME + ":(" + CategoryHelper.getHelper(mCtx).getCategorySectionList() + ")");
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

        queryParams.put(FIELD_LIST,FIELD_LIST_PARAMS);
        queryParams.put(FILTER_QUERY, SECTION_NAME + ":(" + CategoryHelper.getHelper(mCtx).getCategorySectionList() + ")");
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

    public interface NewsReaderService {

        @GET("/svc/search/v2/articlesearch.json")
        Call<List<Object>> getArticles(@QueryMap Map<String, String> params);

    }

    private static class GsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        private final DateFormat dateFormat;

        GsonUTCDateAdapter() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US); //This is the format needed
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); //This is the key line which converts the date to UTC which cannot be accessed with the default serializer
        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dateFormat.format(date));
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            try {
                return dateFormat.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    }

    private static Date toUtc(Date d) {
        TimeZone timeZoneMod = TimeZone.getDefault();
        long utc = d.getTime() - timeZoneMod.getOffset(d.getTime());
        return new Date(utc);
    }



}

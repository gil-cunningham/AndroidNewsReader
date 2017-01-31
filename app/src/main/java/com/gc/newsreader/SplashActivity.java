package com.gc.newsreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gc.newsreader.service.NewsReaderCallback;
import com.gc.newsreader.service.NewsReaderResponse;
import com.gc.newsreader.service.NewsReaderRestService;

/**
 * Created by gil.cunningham on 8/30/2016.
 */
public class SplashActivity extends AppCompatActivity implements NewsReaderCallback {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // load todays article headlines
        //NewsReaderService2.newService(this, this).getTodaysNewsArticles();
        NewsReaderRestService.getService(this).getTodaysArticles();
    }

    @Override
    public void handleTodaysNewsCallback(NewsReaderResponse newsReaderResponse) {

        Intent i = new Intent(getApplicationContext(), HeadlinesActivity.class);
        i.putParcelableArrayListExtra(HeadlinesActivity.NEWS_ARTICLES_EXTRA, newsReaderResponse.getNewsArticles());
        startActivity(i);
        finish();
    }

    @Override
    public void handleSearchNewsCallback(NewsReaderResponse newsReaderResponse) {

    }
}

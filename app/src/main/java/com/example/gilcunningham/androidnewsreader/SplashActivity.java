package com.example.gilcunningham.androidnewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.gilcunningham.androidnewsreader.service.NewsReaderResponse;
import com.example.gilcunningham.androidnewsreader.service.NewsReaderService;
import com.example.gilcunningham.androidnewsreader.service.NewsReaderCallback;

/**
 * Created by gil.cunningham on 8/30/2016.
 */
public class SplashActivity extends AppCompatActivity implements NewsReaderCallback {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // load todays article headlines
        NewsReaderService.newService(this, this).getTodaysNewsArticles();
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

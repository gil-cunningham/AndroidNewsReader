package com.gc.newsreader;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by gil.cunningham on 1/31/2017.
 */

public class NewsReaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("*** BuildConfig.DEBUG " + BuildConfig.DEBUG);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}

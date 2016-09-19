package com.example.gilcunningham.androidnewsreader.service;

/**
 * Created by gil.cunningham on 8/18/2016.
 * Callback for NewsReaderService
 */
public interface NewsReaderCallback {

    public void handleTodaysNewsCallback(NewsReaderResponse newsReaderResponse);

    public void handleSearchNewsCallback(NewsReaderResponse newsReaderResponse);

}

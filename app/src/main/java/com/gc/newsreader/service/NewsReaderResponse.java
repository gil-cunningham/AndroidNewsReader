package com.gc.newsreader.service;

import com.gc.newsreader.data.NewsArticle;

import java.util.ArrayList;

/**
 * Created by gil.cunningham on 9/14/2016.
 * Simple wrapper for a News Reader REST response and page# request
 */
public class NewsReaderResponse {

    private ArrayList<NewsArticle> mNewsArticles;
    private int mPageNumber;

    public NewsReaderResponse(ArrayList<NewsArticle> newsArticles, int pageNumber) {
        mNewsArticles = newsArticles;
        mPageNumber = pageNumber;
    }

    public ArrayList<NewsArticle> getNewsArticles() {
        return mNewsArticles;
    }

    public int getPageNumber() { return mPageNumber; }
}

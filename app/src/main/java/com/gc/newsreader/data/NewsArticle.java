package com.gc.newsreader.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gil.cunningham on 9/9/2016.
 * Parcelable data for a single news article
 * Provides Builder to set values
 */
public class NewsArticle implements Parcelable {

    public static enum Category {
        ARTS,
        BUSINESS,
        GAMES,
        HEALTH,
        MUSIC,
        POLITICS,
        SCIENCE,
        SPORTS,
        WORLD
    }

    /** indicates whether content is print or web based (i.e. load in WebView) **/
    public static enum ContentType {
        PRINT,
        WEB
    }

    private String mWebUrl = null;
    private String mHeadline = null;
    private String mHeadlineDate = null;
    private String mAuthor = null;
    private Category mCategory = null;
    private ContentType mContentType = null;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mWebUrl);
        parcel.writeString(mHeadline);
        parcel.writeString(mHeadlineDate);
        parcel.writeString(mAuthor);
        parcel.writeString(mCategory.name());
        parcel.writeString(mContentType.name());
    }

    public static final Creator<NewsArticle> CREATOR = new Creator<NewsArticle>() {

        public NewsArticle createFromParcel(Parcel in) {
            return new NewsArticle(in);
        }

        public NewsArticle[] newArray(int size) {
            return new NewsArticle[size];
        }
    };

    public NewsArticle(Builder builder) {
        mWebUrl = builder.webUrl;
        mHeadline = builder.headline;
        mHeadlineDate = builder.headlineDate;
        mAuthor = builder.author;
        mCategory = builder.category;
        mContentType = builder.contentType;
    }

    public NewsArticle(Parcel parcel) {
        mWebUrl = parcel.readString();
        mHeadline = parcel.readString();
        mHeadlineDate = parcel.readString();
        mAuthor = parcel.readString();
        mCategory = Category.valueOf(parcel.readString());
        mContentType = ContentType.valueOf(parcel.readString());
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getHeadlineDate() {
        return mHeadlineDate;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public Category getCategory() {
        return mCategory;
    }

    public ContentType getmContentType() {
        return mContentType;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String webUrl = null;
        private String headline = null;
        private String headlineDate = null;
        private String author = null;
        private Category category = null;
        private ContentType contentType = null;

        public NewsArticle build() {
            return new NewsArticle(this);
        }

        public Builder setWebUrl(String webUrl) {
            this.webUrl = webUrl;
            return this;
        }

        public Builder setHeadline(String headline) {
            this.headline = headline;
            return this;
        }

        public Builder setHeadlineDate(String headlineDate) {
            this.headlineDate = headlineDate;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder setContentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }
    }
}



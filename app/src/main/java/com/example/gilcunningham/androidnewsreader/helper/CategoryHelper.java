package com.example.gilcunningham.androidnewsreader.helper;

import android.content.Context;

import com.example.gilcunningham.androidnewsreader.R;
import com.example.gilcunningham.androidnewsreader.data.NewsArticle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gil.cunningham on 9/9/2016.
 * Helper class to assist in mapping category string (REST response) to Category value
 * and populating REST request with valid section_name category types
 */
public class CategoryHelper {

    private static CategoryHelper mHelper;

    private static String mSectionList = null;

    // these section_name values were taken from:
    // https://developer.nytimes.com/article_search_v2.json#/README
    // They were restricted to icons available

    private String[] SECTION_NAME_ARTS; // Arts
    private String[] SECTION_NAME_BUSINESS; // Business
    private String[] SECTION_NAME_GAMES; // Games
    private String[] SECTION_NAME_HEALTH; // Health
    private String[] SECTION_NAME_MUSIC; // Music
    private String[] SECTION_NAME_POLITICS; // Politics
    private String[] SECTION_NAME_SCIENCE; // Science
    private String[] SECTION_NAME_SPORTS; // Sports
    private String[] SECTION_NAME_WORLD; // World

    private static Map<String, NewsArticle.Category> CATEGORY_MAP = new HashMap<String, NewsArticle.Category>();

    public static CategoryHelper getHelper(Context ctx) {
        if (mHelper == null) {
            mHelper = new CategoryHelper(ctx);
        }

        return mHelper;
    }

    private CategoryHelper(Context ctx) {
        SECTION_NAME_ARTS = ctx.getResources().getStringArray(R.array.section_name_arts);
        SECTION_NAME_BUSINESS = ctx.getResources().getStringArray(R.array.section_name_business);
        SECTION_NAME_GAMES = ctx.getResources().getStringArray(R.array.section_name_games);
        SECTION_NAME_HEALTH = ctx.getResources().getStringArray(R.array.section_name_health);
        SECTION_NAME_MUSIC = ctx.getResources().getStringArray(R.array.section_name_music);
        SECTION_NAME_POLITICS = ctx.getResources().getStringArray(R.array.section_name_politics);
        SECTION_NAME_SCIENCE = ctx.getResources().getStringArray(R.array.section_name_science);
        SECTION_NAME_SPORTS = ctx.getResources().getStringArray(R.array.section_name_sports);
        SECTION_NAME_WORLD = ctx.getResources().getStringArray(R.array.section_name_world);

        initCategoryMap();
    }

    private void initCategoryMap() {
        // Arts
        for (int i = 0; i < SECTION_NAME_ARTS.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_ARTS[i], NewsArticle.Category.ARTS);
        }
        // Business
        for (int i = 0; i < SECTION_NAME_BUSINESS.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_BUSINESS[i], NewsArticle.Category.BUSINESS);
        }
        // Games
        for (int i = 0; i < SECTION_NAME_GAMES.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_GAMES[i], NewsArticle.Category.GAMES);
        }
        // Health
        for (int i = 0; i < SECTION_NAME_HEALTH.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_HEALTH[i], NewsArticle.Category.HEALTH);
        }
        // Music
        for (int i = 0; i < SECTION_NAME_MUSIC.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_MUSIC[i], NewsArticle.Category.MUSIC);
        }
        // Politics
        for (int i = 0; i < SECTION_NAME_POLITICS.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_POLITICS[i], NewsArticle.Category.POLITICS);
        }
        // Science
        for (int i = 0; i < SECTION_NAME_SCIENCE.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_SCIENCE[i], NewsArticle.Category.SCIENCE);
        }
        // Sports
        for (int i = 0; i < SECTION_NAME_SPORTS.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_SPORTS[i], NewsArticle.Category.SPORTS);
        }
        // World
        for (int i = 0; i < SECTION_NAME_WORLD.length; i++) {
            CATEGORY_MAP.put(SECTION_NAME_WORLD[i], NewsArticle.Category.WORLD);
        }
    }

    public NewsArticle.Category getCategoryFromString(String category) {
        return CATEGORY_MAP.get(category);
    }

    public String getCategorySectionList() {
        if (mSectionList == null) {
            String[][] sectionNames = {
                    SECTION_NAME_ARTS,
                    SECTION_NAME_BUSINESS,
                    SECTION_NAME_GAMES,
                    SECTION_NAME_HEALTH,
                    SECTION_NAME_MUSIC,
                    SECTION_NAME_POLITICS,
                    SECTION_NAME_SCIENCE,
                    SECTION_NAME_SPORTS,
                    SECTION_NAME_WORLD};

            StringBuilder sectionNamesBuilder = new StringBuilder();

            for (int i = 0; i < sectionNames.length; i++) {
                for (int x = 0; x < sectionNames[i].length; x++) {
                    sectionNamesBuilder.append("\"" + sectionNames[i][x] + "\" ");
                }
            }

            mSectionList = sectionNamesBuilder.substring(0, sectionNamesBuilder.length() - 1);
        }

        return mSectionList;
    }

    public int getCategoryResourceDrawable(NewsArticle.Category category) {
        switch (category) {
            case ARTS:
                return R.drawable.ic_arts;
            case BUSINESS:
                return R.drawable.ic_business;
            case GAMES:
                return R.drawable.ic_games;
            case HEALTH:
                return R.drawable.ic_health;
            case MUSIC:
                return R.drawable.ic_music;
            case POLITICS:
                return R.drawable.ic_politics;
            case SCIENCE:
                return R.drawable.ic_science;
            case SPORTS:
                return R.drawable.ic_sports;
            case WORLD:
                return R.drawable.ic_world;
            default:
                return R.drawable.blank_avatar;
        }
    }
}

package com.gc.newsreader.helper;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by gil.cunningham on 9/15/2016.
 * Utility for getting pixel and dp info about the Display
 */
public class DisplayHelper {

    public static Point getDisplaySizeInPixels(Activity act) {
        Display display = act.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        return p;
    }

    public static Point getDisplaySizeInDp(Activity act) {
        Point sizeInPixels = getDisplaySizeInPixels(act);

        float widthInDp = convertPixelsToDp(sizeInPixels.x);
        float heightInDp = convertPixelsToDp(sizeInPixels.y);

        Point p = new Point();
        p.x = (int)widthInDp;
        p.y = (int)heightInDp;

        return p;
    }
    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float getDisplayWidthInDp(Activity act)
    {
        Point sizeInDp = getDisplaySizeInDp(act);
        return sizeInDp.x;
    }

    public static float getDisplayHeightInDp(Activity act)
    {
        Point sizeInDp = getDisplaySizeInDp(act);
        return sizeInDp.y;
    }

}

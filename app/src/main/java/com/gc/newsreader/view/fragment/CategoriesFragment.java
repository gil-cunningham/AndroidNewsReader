package com.gc.newsreader.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.newsreader.R;

/**
 * Created by gil.cunningham on 9/12/2016.
 * Simple placeholder for Categories Fragment - TO DO
 */
public class CategoriesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.categories, container, false);

        return v;
    }
}

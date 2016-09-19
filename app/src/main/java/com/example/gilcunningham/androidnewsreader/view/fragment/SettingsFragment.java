package com.example.gilcunningham.androidnewsreader.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gilcunningham.androidnewsreader.R;

/**
 * Created by gil.cunningham on 9/12/2016.
 * Simple placeholder for Settings Fragment - TO DO
 */
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.settings, container, false);

        return v;
    }

}

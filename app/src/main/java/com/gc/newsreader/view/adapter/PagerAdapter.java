package com.gc.newsreader.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gil.cunningham on 9/12/2016.
 * Pager for tabs
 * Holds fragments
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) { return mFragments.get(position); }

    @Override
    public int getCount() { return mFragments.size(); }

    @Override
    public CharSequence getPageTitle(int position) { return mFragmentTitles.get(position); }
}
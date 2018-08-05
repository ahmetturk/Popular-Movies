package com.ahmetroid.popularmovies.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ahmetroid.popularmovies.ui.MoviesFragment;

import java.util.ArrayList;

public class MovieFragmentPager extends FragmentStatePagerAdapter {

    private ArrayList<PagerItem> mPagerItems;

    public MovieFragmentPager(FragmentManager fragmentManager, ArrayList<PagerItem> pagerItems) {
        super(fragmentManager);
        mPagerItems = pagerItems;
    }

    @Override
    public Fragment getItem(int position) {
        return MoviesFragment.newInstance(mPagerItems.get(position).getSortingCode());
    }

    @Override
    public int getCount() {
        return mPagerItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPagerItems.get(position).getTitle();
    }

}

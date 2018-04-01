package com.ahmetroid.popularmovies.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ahmetroid.popularmovies.ui.MovieListFragment;

import java.util.ArrayList;

public class MovieFragmentPager extends FragmentStatePagerAdapter {

    private ArrayList<PagerItem> mPagerItems;

    public MovieFragmentPager(FragmentManager fragmentManager, ArrayList<PagerItem> pagerItems) {
        super(fragmentManager);
        mPagerItems = pagerItems;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new MovieListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(MovieListFragment.SORTING_CODE,
                mPagerItems.get(position).getSortingCode());

        fragment.setArguments(bundle);

        return fragment;
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

package com.ahmetroid.popularmovies.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ahmetroid.popularmovies.MovieListFragment;

public class MovieListPager extends FragmentPagerAdapter {

    private Context mContext;

    public MovieListPager(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new MovieListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(MovieListFragment.SORTING, position);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "POPÜLER";
        } else if (position == 1) {
            title = "YÜKSEK PUANLI";
        } else if (position == 2) {
            title = "VİZYONDA";
        } else if (position == 3) {
            title = "YAKINDA";
        } else if (position == 4) {
            title = "FAVORİLER";
        }
        return title;
    }
}

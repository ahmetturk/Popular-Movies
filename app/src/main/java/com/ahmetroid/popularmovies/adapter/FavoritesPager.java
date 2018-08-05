package com.ahmetroid.popularmovies.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ahmetroid.popularmovies.ui.FavoritesFragment;

import java.util.ArrayList;

public class FavoritesPager extends MovieFragmentPager {
    public FavoritesPager(FragmentManager fragmentManager, ArrayList<PagerItem> pagerItems) {
        super(fragmentManager, pagerItems);
    }

    @Override
    public Fragment getItem(int position) {
        return FavoritesFragment.newInstance();
    }
}

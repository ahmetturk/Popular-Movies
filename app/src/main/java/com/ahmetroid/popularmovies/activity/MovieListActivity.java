package com.ahmetroid.popularmovies.activity;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.PagerItem;
import com.ahmetroid.popularmovies.utils.Codes;

import java.util.ArrayList;

public class MovieListActivity extends BaseActivity {

    @Override
    public ArrayList<PagerItem> getPagerItems() {
        ArrayList<PagerItem> list = new ArrayList<>(5);
        list.add(new PagerItem(getString(R.string.popular), Codes.POPULAR));
        list.add(new PagerItem(getString(R.string.top_rated), Codes.TOP_RATED));
        list.add(new PagerItem(getString(R.string.now_playing), Codes.NOW_PLAYING));
        list.add(new PagerItem(getString(R.string.upcoming), Codes.UPCOMING));
        list.add(new PagerItem(getString(R.string.favorites), Codes.FAVORITES));
        return list;
    }

    @Override
    public int getCheckedItem() {
        return R.id.nav_movie_list;
    }
}
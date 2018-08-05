package com.ahmetroid.popularmovies.ui;

import android.content.Context;
import android.content.Intent;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.PagerItem;
import com.ahmetroid.popularmovies.utils.Codes;

import java.util.ArrayList;

public class MovieListActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MovieListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public ArrayList<PagerItem> getPagerItems() {
        ArrayList<PagerItem> list = new ArrayList<>(4);
        list.add(new PagerItem(getString(R.string.popular), Codes.POPULAR));
        list.add(new PagerItem(getString(R.string.top_rated), Codes.TOP_RATED));
        list.add(new PagerItem(getString(R.string.now_playing), Codes.NOW_PLAYING));
        list.add(new PagerItem(getString(R.string.upcoming), Codes.UPCOMING));
        return list;
    }

    @Override
    public int getCheckedItem() {
        return R.id.nav_movie_list;
    }
}
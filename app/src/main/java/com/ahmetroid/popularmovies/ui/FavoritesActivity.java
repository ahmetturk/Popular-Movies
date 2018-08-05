package com.ahmetroid.popularmovies.ui;

import android.content.Context;
import android.content.Intent;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.FavoritesPager;
import com.ahmetroid.popularmovies.adapter.MovieFragmentPager;
import com.ahmetroid.popularmovies.adapter.PagerItem;
import com.ahmetroid.popularmovies.utils.Codes;

import java.util.ArrayList;

public class FavoritesActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, FavoritesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    ArrayList<PagerItem> getPagerItems() {
        ArrayList<PagerItem> list = new ArrayList<>(1);
        list.add(new PagerItem(getString(R.string.favorites), Codes.FAVORITES));
        return list;
    }

    @Override
    int getCheckedItem() {
        return R.id.nav_favorites;
    }

    @Override
    MovieFragmentPager getPager() {
        return new FavoritesPager(getSupportFragmentManager(), getPagerItems());
    }
}

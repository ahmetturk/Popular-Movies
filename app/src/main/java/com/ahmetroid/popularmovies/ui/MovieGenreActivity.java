package com.ahmetroid.popularmovies.ui;

import android.content.Context;
import android.content.Intent;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.PagerItem;
import com.ahmetroid.popularmovies.utils.Codes;

import java.util.ArrayList;

public class MovieGenreActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MovieGenreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public ArrayList<PagerItem> getPagerItems() {
        ArrayList<PagerItem> list = new ArrayList<>(17);
        list.add(new PagerItem(getString(R.string.action), Codes.ACTION));
        list.add(new PagerItem(getString(R.string.science_fiction), Codes.SCIENCE_FICTION));
        list.add(new PagerItem(getString(R.string.family), Codes.FAMILY));
        list.add(new PagerItem(getString(R.string.comedy), Codes.COMEDY));
        list.add(new PagerItem(getString(R.string.romance), Codes.ROMANCE));
        list.add(new PagerItem(getString(R.string.animation), Codes.ANIMATION));
        list.add(new PagerItem(getString(R.string.adventure), Codes.ADVENTURE));
        list.add(new PagerItem(getString(R.string.horror), Codes.HORROR));
        list.add(new PagerItem(getString(R.string.crime), Codes.CRIME));
        list.add(new PagerItem(getString(R.string.thriller), Codes.THRILLER));
        list.add(new PagerItem(getString(R.string.mystery), Codes.MYSTERY));
        list.add(new PagerItem(getString(R.string.drama), Codes.DRAMA));
        list.add(new PagerItem(getString(R.string.fantasy), Codes.FANTASY));
        list.add(new PagerItem(getString(R.string.war), Codes.WAR));
        list.add(new PagerItem(getString(R.string.western), Codes.WESTERN));
        list.add(new PagerItem(getString(R.string.history), Codes.HISTORY));
        list.add(new PagerItem(getString(R.string.music), Codes.MUSIC));
        return list;
    }

    @Override
    public int getCheckedItem() {
        return R.id.nav_movie_genre;
    }
}
